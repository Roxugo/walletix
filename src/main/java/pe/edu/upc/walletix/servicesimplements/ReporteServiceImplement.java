package pe.edu.upc.walletix.servicesimplements;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.ColumnText;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfPageEventHelper;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import pe.edu.upc.walletix.config.Reloj;
import pe.edu.upc.walletix.entities.Gasto;
import pe.edu.upc.walletix.entities.Ingreso;
import pe.edu.upc.walletix.entities.Usuario;
import pe.edu.upc.walletix.i18n.Mensajes;
import pe.edu.upc.walletix.repositories.IGastoRepository;
import pe.edu.upc.walletix.repositories.IngresoRepository;
import pe.edu.upc.walletix.servicesinterfaces.IReporteService;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

// US39: arma el PDF en el servidor con OpenPDF. Solo usa los movimientos del usuario que pidió el reporte.
@Service
public class ReporteServiceImplement implements IReporteService {
    public static final int MESES_MAXIMOS = 24;

    private static final Color VERDE = new Color(0x1b, 0x6d, 0x24);
    private static final Color GRIS = new Color(0x5f, 0x63, 0x68);
    private static final Color FONDO = new Color(0xee, 0xf4, 0xec);
    private static final Font TITULO = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, VERDE);
    private static final Font SUBTITULO = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, Color.BLACK);
    private static final Font NORMAL = FontFactory.getFont(FontFactory.HELVETICA, 9, Color.BLACK);
    private static final Font NEGRITA = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, Color.BLACK);
    private static final Font SUAVE = FontFactory.getFont(FontFactory.HELVETICA, 8, GRIS);

    @Autowired
    private IGastoRepository gastoRepository;
    @Autowired
    private IngresoRepository ingresoRepository;
    @Autowired
    private Mensajes mensajes;
    @Autowired
    private Reloj reloj;

    @Override
    public byte[] reportePdf(Usuario usuario, YearMonth desde, YearMonth hasta) {
        validarPeriodo(desde, hasta);
        LocalDate inicio = desde.atDay(1);
        LocalDate fin = hasta.atEndOfMonth();
        List<Gasto> gastos = gastoRepository.buscarPorUsuarioYRango(usuario.getIdUsuario(), inicio, fin);
        List<Ingreso> ingresos = ingresoRepository.buscarPorUsuarioYRango(usuario.getIdUsuario(), inicio, fin);
        // US39 escenario 2: "no hay información disponible"
        if (gastos.isEmpty() && ingresos.isEmpty()) {
            throw new ReglaReporteException(404, mensajes.get("reporte.sin.movimientos"));
        }
        return pdf(usuario, desde, hasta, gastos, ingresos);
    }

    private void validarPeriodo(YearMonth desde, YearMonth hasta) {
        if (desde.isAfter(hasta)) {
            throw new ReglaReporteException(400, mensajes.get("reporte.rango.invertido"));
        }
        if (hasta.isAfter(YearMonth.from(reloj.hoy()))) {
            throw new ReglaReporteException(400, mensajes.get("reporte.rango.futuro"));
        }
        if (desde.plusMonths(MESES_MAXIMOS - 1L).isBefore(hasta)) {
            throw new ReglaReporteException(400, mensajes.get("reporte.rango.largo", String.valueOf(MESES_MAXIMOS)));
        }
    }

    // ---------- Armado del PDF ----------

    private byte[] pdf(Usuario usuario, YearMonth desde, YearMonth hasta, List<Gasto> gastos, List<Ingreso> ingresos) {
        ByteArrayOutputStream salida = new ByteArrayOutputStream();
        Document documento = new Document(PageSize.A4, 40, 40, 40, 50);
        PdfWriter escritor = PdfWriter.getInstance(documento, salida);
        escritor.setPageEvent(new PiePagina(mensajes.get("reporte.pie")));
        documento.addTitle(mensajes.get("reporte.titulo"));
        documento.addAuthor("Walletix");
        documento.open();

        BigDecimal totalIngresos = ingresos.stream().map(i -> BigDecimal.valueOf(i.getMonto())).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalGastos = gastos.stream().map(Gasto::getMontoGasto).reduce(BigDecimal.ZERO, BigDecimal::add);
        List<Gasto> hormiga = gastos.stream().filter(g -> ReglasReto.rompe("sin_gastos_hormiga", g)).toList();
        BigDecimal totalHormiga = hormiga.stream().map(Gasto::getMontoGasto).reduce(BigDecimal.ZERO, BigDecimal::add);

        // Encabezado
        documento.add(new Paragraph(mensajes.get("reporte.titulo"), TITULO));
        documento.add(new Paragraph(usuario.getNombreUsuario() + " · " + usuario.getCorreoUsuario(), NEGRITA));
        documento.add(new Paragraph(desde.equals(hasta) ? mensajes.get("reporte.periodo.mes", mes(desde))
                : mensajes.get("reporte.periodo", mes(desde), mes(hasta)), NORMAL));
        // Java separa "10:33 AM" con un espacio especial (U+202F) que la fuente del PDF no tiene: se cambia por uno normal
        String generado = LocalDateTime.now().format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM, FormatStyle.SHORT)
                .withLocale(idioma())).replace(' ', ' ').replace(' ', ' ');
        documento.add(new Paragraph(mensajes.get("reporte.generado", generado), SUAVE));

        // Resumen
        titulo(documento, "reporte.resumen");
        PdfPTable resumen = tabla(new float[]{3, 2});
        fila(resumen, mensajes.get("reporte.total.ingresos"), soles(totalIngresos), false);
        fila(resumen, mensajes.get("reporte.total.gastos"), soles(totalGastos), false);
        fila(resumen, mensajes.get("reporte.diferencia"), soles(totalIngresos.subtract(totalGastos)), true);
        fila(resumen, mensajes.get("reporte.saldo.actual"), soles(usuario.getSaldoActualUsuario()), false);
        fila(resumen, mensajes.get("reporte.hormiga"), mensajes.get(hormiga.size() == 1 ? "reporte.hormiga.detalle.una" : "reporte.hormiga.detalle",
                soles(totalHormiga), String.valueOf(hormiga.size())), false);
        documento.add(resumen);

        // Por mes
        titulo(documento, "reporte.por.mes");
        PdfPTable meses = tabla(new float[]{3, 2, 2, 2});
        encabezado(meses, 1, "reporte.mes", "reporte.ingresos", "reporte.gastos", "reporte.diferencia");
        for (YearMonth mes = desde; !mes.isAfter(hasta); mes = mes.plusMonths(1)) {
            YearMonth actual = mes;
            BigDecimal ing = ingresos.stream().filter(i -> YearMonth.from(i.getFecha()).equals(actual))
                    .map(i -> BigDecimal.valueOf(i.getMonto())).reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal gas = gastos.stream().filter(g -> YearMonth.from(g.getFechaGasto()).equals(actual))
                    .map(Gasto::getMontoGasto).reduce(BigDecimal.ZERO, BigDecimal::add);
            celdas(meses, false, 1, mes(mes), soles(ing), soles(gas), soles(ing.subtract(gas)));
        }
        documento.add(meses);

        // Gastos por categoría
        if (!gastos.isEmpty()) {
            titulo(documento, "reporte.por.categoria");
            Map<String, BigDecimal> porCategoria = new LinkedHashMap<>();
            for (Gasto gasto : gastos) {
                porCategoria.merge(gasto.getCategoria().getNombreCategoria(), gasto.getMontoGasto(), BigDecimal::add);
            }
            PdfPTable categorias = tabla(new float[]{4, 2, 1});
            encabezado(categorias, 1, "reporte.categoria", "reporte.total", "reporte.porcentaje");
            porCategoria.entrySet().stream().sorted(Map.Entry.<String, BigDecimal>comparingByValue().reversed())
                    .forEach(e -> celdas(categorias, false, 1, e.getKey(), soles(e.getValue()), porcentaje(e.getValue(), totalGastos)));
            documento.add(categorias);
        }

        // Movimientos
        titulo(documento, "reporte.movimientos");
        PdfPTable movimientos = tabla(new float[]{2, 2, 4, 3, 2});
        movimientos.setHeaderRows(1); // el encabezado se repite en cada página
        encabezado(movimientos, 4, "reporte.fecha", "reporte.tipo", "reporte.descripcion", "reporte.categoria", "reporte.monto");
        List<Object[]> filas = new ArrayList<>();
        for (Ingreso ingreso : ingresos) {
            String descripcion = ingreso.getDescripcion() == null || ingreso.getDescripcion().isBlank() ? ingreso.getFuente() : ingreso.getDescripcion();
            filas.add(new Object[]{ingreso.getFecha(), mensajes.get("reporte.ingreso"), descripcion,
                    ingreso.getCategoria().getNombreCategoria(), BigDecimal.valueOf(ingreso.getMonto())});
        }
        for (Gasto gasto : gastos) {
            filas.add(new Object[]{gasto.getFechaGasto(), mensajes.get("reporte.gasto"), gasto.getDescripcionGasto(),
                    gasto.getCategoria().getNombreCategoria(), gasto.getMontoGasto().negate()});
        }
        filas.sort(Comparator.comparing(f -> (LocalDate) f[0]));
        DateTimeFormatter dia = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withLocale(idioma());
        for (Object[] f : filas) {
            celdas(movimientos, false, 4, ((LocalDate) f[0]).format(dia), (String) f[1], Objects.toString(f[2], ""), (String) f[3], soles((BigDecimal) f[4]));
        }
        documento.add(movimientos);

        documento.close();
        return salida.toByteArray();
    }

    private static Locale idioma() {
        return LocaleContextHolder.getLocale();
    }

    // "noviembre 2026" / "November 2026"
    private static String mes(YearMonth mes) {
        String nombre = mes.getMonth().getDisplayName(TextStyle.FULL, idioma());
        return nombre.substring(0, 1).toUpperCase(idioma()) + nombre.substring(1) + " " + mes.getYear();
    }

    // "S/ 1,234.50" (como en la web) y "-S/ 8.50" para los gastos
    static String soles(BigDecimal monto) {
        DecimalFormat formato = new DecimalFormat("#,##0.00", DecimalFormatSymbols.getInstance(Locale.US));
        BigDecimal valor = monto == null ? BigDecimal.ZERO : monto.setScale(2, RoundingMode.HALF_UP);
        return (valor.signum() < 0 ? "-S/ " : "S/ ") + formato.format(valor.abs());
    }

    private static String porcentaje(BigDecimal parte, BigDecimal total) {
        if (total.signum() == 0) {
            return "0%";
        }
        return parte.multiply(BigDecimal.valueOf(100)).divide(total, 1, RoundingMode.HALF_UP).stripTrailingZeros().toPlainString() + "%";
    }

    private void titulo(Document documento, String clave) {
        Paragraph parrafo = new Paragraph(mensajes.get(clave), SUBTITULO);
        parrafo.setSpacingBefore(14);
        parrafo.setSpacingAfter(6);
        documento.add(parrafo);
    }

    private static PdfPTable tabla(float[] anchos) {
        PdfPTable tabla = new PdfPTable(anchos);
        tabla.setWidthPercentage(100);
        return tabla;
    }

    private void encabezado(PdfPTable tabla, int primeraNumerica, String... claves) {
        for (int i = 0; i < claves.length; i++) {
            PdfPCell celda = new PdfPCell(new Phrase(mensajes.get(claves[i]), NEGRITA));
            celda.setBackgroundColor(FONDO);
            celda.setPadding(5);
            if (i >= primeraNumerica) {
                celda.setHorizontalAlignment(Element.ALIGN_RIGHT);
            }
            tabla.addCell(celda);
        }
    }

    private static void fila(PdfPTable tabla, String etiqueta, String valor, boolean destacar) {
        celdas(tabla, destacar, 1, etiqueta, valor);
    }

    // Las columnas desde "primeraNumerica" son montos o porcentajes: van a la derecha (la alineación
    // depende de la columna, no del texto: "Gastos hormiga (menos de S/ 10)" es una etiqueta)
    private static void celdas(PdfPTable tabla, boolean negrita, int primeraNumerica, String... textos) {
        for (int i = 0; i < textos.length; i++) {
            PdfPCell celda = new PdfPCell(new Phrase(textos[i], negrita ? NEGRITA : NORMAL));
            celda.setPadding(5);
            if (i >= primeraNumerica) {
                celda.setHorizontalAlignment(Element.ALIGN_RIGHT);
            }
            tabla.addCell(celda);
        }
    }

    // Pie de cada página: "Walletix · Reporte financiero · Página 2"
    private static class PiePagina extends PdfPageEventHelper {
        private final String texto;

        PiePagina(String texto) {
            this.texto = texto;
        }

        @Override
        public void onEndPage(PdfWriter escritor, Document documento) {
            ColumnText.showTextAligned(escritor.getDirectContent(), Element.ALIGN_CENTER,
                    new Phrase(texto + " " + escritor.getPageNumber(), SUAVE),
                    (documento.left() + documento.right()) / 2, documento.bottom() - 20, 0);
        }
    }
}
