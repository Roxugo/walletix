package pe.edu.upc.walletix.servicesimplements;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upc.walletix.config.Reloj;
import pe.edu.upc.walletix.dtos.RetosDTOs.*;
import pe.edu.upc.walletix.entities.*;
import pe.edu.upc.walletix.i18n.Mensajes;
import pe.edu.upc.walletix.repositories.IDesafioRepository;
import pe.edu.upc.walletix.repositories.IGastoRepository;
import pe.edu.upc.walletix.repositories.IProgresoDesafioRepository;
import pe.edu.upc.walletix.repositories.IUsuarioDesafioRepository;
import pe.edu.upc.walletix.servicesinterfaces.IGamificacionService;
import pe.edu.upc.walletix.servicesinterfaces.INotificacionService;
import pe.edu.upc.walletix.servicesinterfaces.IRetoService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class RetoServiceImplement implements IRetoService {
    public static final String EN_PROGRESO = "EN_PROGRESO";
    public static final String COMPLETADO = "COMPLETADO";
    public static final String VENCIDO = "VENCIDO";
    public static final String ABANDONADO = "ABANDONADO";

    // Tipos de reto personal (US29) con su descripción por defecto
    public static final Map<String, String> TIPOS = Map.of(
            "sin_delivery", "reto.tipo.sin_delivery",
            "sin_gastos_hormiga", "reto.tipo.sin_gastos_hormiga",
            "sin_compras_impulsivas", "reto.tipo.sin_compras_impulsivas",
            "transporte_inteligente", "reto.tipo.transporte_inteligente",
            "ahorro_libre", "reto.tipo.ahorro_libre");
    public static final int DURACION_MAXIMA = 90;
    private static final BigDecimal MONTO_MAXIMO = new BigDecimal("1000000");

    @Autowired
    private IDesafioRepository desafioRepository;
    @Autowired
    private IUsuarioDesafioRepository usuarioDesafioRepository;
    @Autowired
    private IProgresoDesafioRepository progresoDesafioRepository;
    @Autowired
    private IGamificacionService gamificacionService;
    @Autowired
    private INotificacionService notificacionService;
    @Autowired
    private Mensajes mensajes;
    @Autowired
    private IGastoRepository gastoRepository;
    @Autowired
    private Reloj reloj;

    // ---------- Consultas ----------

    @Override
    @Transactional
    public ResumenRetosDTO resumen(Usuario usuario) {
        List<UsuarioDesafio> participaciones = participacionesDe(usuario);
        participaciones.forEach(this::marcarSiVencio);

        List<ParticipacionDTO> activos = new ArrayList<>();
        List<ParticipacionDTO> historial = new ArrayList<>();
        long superados = 0;
        BigDecimal blindado = BigDecimal.ZERO;
        for (UsuarioDesafio participacion : participaciones) {
            ParticipacionDTO dto = aDTO(participacion);
            if (EN_PROGRESO.equals(participacion.getEstadoDesafio())) {
                activos.add(dto);
            } else {
                historial.add(dto);
            }
            if (COMPLETADO.equals(participacion.getEstadoDesafio())) {
                superados++;
                blindado = blindado.add(participacion.getDesafio().getMontoObjetivo());
            }
        }
        Niveles.Nivel nivel = Niveles.nivelDe(usuario.getPuntosGamificacionUsuario());
        return new ResumenRetosDTO(nivel.numero(), nivel.nombre(), usuario.getPuntosGamificacionUsuario(),
                nivel.xpInicio(), nivel.xpSiguiente(), superados, participaciones.size(), blindado,
                gamificacionService.rachaDeRetos(usuario.getIdUsuario()), activos, historial);
    }

    @Override
    public List<RetoDisponibleDTO> disponibles(Usuario usuario) {
        LocalDate hoy = reloj.hoy();
        return desafioRepository.findByEstado(1).stream()
                // Solo los oficiales que todavía no terminan (los personales se crean ya inscritos)
                .filter(desafio -> desafio.esOficial() && !desafio.getFechaFin().isBefore(hoy))
                .sorted((a, b) -> a.getFechaInicio().compareTo(b.getFechaInicio()))
                .map(desafio -> {
                    List<String> faltan = requisitosFaltantes(usuario, desafio);
                    return new RetoDisponibleDTO(desafio.getIdDesafio(), desafio.getTitulo(), desafio.getDescripcion(),
                            desafio.getTipoDesafio(), desafio.getMontoObjetivo(), desafio.getPuntosRecompensa(),
                            desafio.duracionEfectiva(), desafio.getFechaInicio(), desafio.getFechaFin(),
                            desafio.getEdadMinima(), usuarioDesafioRepository.contarUsuariosPorDesafio(desafio.getIdDesafio()),
                            faltan.isEmpty(), faltan);
                })
                .toList();
    }

    // US28: qué le falta al usuario para unirse (lista vacía = puede unirse)
    private List<String> requisitosFaltantes(Usuario usuario, Desafio desafio) {
        LocalDate hoy = reloj.hoy();
        List<String> faltan = new ArrayList<>();
        if (usuarioDesafioRepository.existsByUsuarioIdUsuarioAndDesafioIdDesafioAndEstadoAndEstadoDesafioIn(
                usuario.getIdUsuario(), desafio.getIdDesafio(), 1, List.of(EN_PROGRESO, COMPLETADO))) {
            faltan.add(mensajes.get("reto.ya.inscrito"));
        }
        if (desafio.getFechaInicio().isAfter(hoy)) {
            faltan.add(mensajes.get("reto.aun.no.empieza", fecha(desafio.getFechaInicio())));
        } else {
            long diasQueQuedan = ChronoUnit.DAYS.between(hoy, desafio.getFechaFin()) + 1;
            if (diasQueQuedan < desafio.duracionEfectiva()) {
                faltan.add(mensajes.get("reto.no.alcanzan.dias",
                        String.valueOf(desafio.duracionEfectiva()), fecha(desafio.getFechaFin())));
            }
        }
        int edad = usuario.getFechaNacimientoUsuario() == null ? 0
                : Period.between(usuario.getFechaNacimientoUsuario(), hoy).getYears();
        if (edad < desafio.getEdadMinima()) {
            faltan.add(mensajes.get("reto.edad.minima", String.valueOf(desafio.getEdadMinima()), String.valueOf(edad)));
        }
        BigDecimal saldoMinimo = desafio.getSaldoInicial() == null ? BigDecimal.ZERO : desafio.getSaldoInicial();
        BigDecimal saldo = usuario.getSaldoActualUsuario() == null ? BigDecimal.ZERO : usuario.getSaldoActualUsuario();
        if (saldo.compareTo(saldoMinimo) < 0) {
            faltan.add(mensajes.get("reto.saldo.minimo", saldoMinimo.setScale(2, RoundingMode.HALF_UP).toPlainString()));
        }
        return faltan;
    }

    // Fecha en el idioma de la petición: "3 ene 2027" (es-419) o "Jan 3, 2027" (en-US)
    private static String fecha(LocalDate fecha) {
        return fecha.format(java.time.format.DateTimeFormatter.ofLocalizedDate(java.time.format.FormatStyle.MEDIUM)
                .withLocale(org.springframework.context.i18n.LocaleContextHolder.getLocale()));
    }

    // ---------- Acciones ----------

    @Override
    @Transactional
    public ParticipacionDTO unirse(Usuario usuario, int idDesafio) {
        Desafio desafio = desafioRepository.findByIdDesafioAndEstado(idDesafio, 1)
                .filter(Desafio::esOficial)
                .orElseThrow(() -> new ReglaRetoException(404, mensajes.get("desafio.no.encontrado")));
        List<String> faltan = requisitosFaltantes(usuario, desafio);
        if (!faltan.isEmpty()) {
            throw new ReglaRetoException(400, mensajes.get("reto.requisitos.no.cumplidos", String.join(" · ", faltan)));
        }
        return aDTO(inscribir(usuario, desafio));
    }

    @Override
    @Transactional
    public ProgresoRegistradoDTO registrarProgreso(Usuario usuario, int idUsuarioDesafio) {
        UsuarioDesafio participacion = participacionPropia(usuario, idUsuarioDesafio);
        marcarSiVencio(participacion);
        if (!EN_PROGRESO.equals(participacion.getEstadoDesafio())) {
            throw new ReglaRetoException(409, mensajes.get("reto.no.esta.en.progreso"));
        }
        LocalDate hoy = reloj.hoy();
        // Validación del informe: una sola vez por día ("Ya registraste tu progreso de hoy")
        if (hoy.equals(participacion.getFechaUltimoProgreso())) {
            throw new ReglaRetoException(409, mensajes.get("reto.progreso.ya.registrado.hoy"));
        }
        Desafio desafio = participacion.getDesafio();
        // Verificación con los gastos de hoy: si ya registró un gasto que rompe el reto, el día no cuenta
        if (ReglasReto.seVerificaConGastos(desafio.getTipoDesafio())) {
            ReglasReto.primeroQueRompe(desafio.getTipoDesafio(),
                            gastoRepository.buscarPorUsuarioYRango(usuario.getIdUsuario(), hoy, hoy))
                    .ifPresent(gasto -> {
                        // 422 (y no 409, que es "ya registraste hoy"): la web muestra el gasto que rompió el día
                        throw new ReglaRetoException(422, mensajes.get("reto.dia.roto", detalle(gasto)));
                    });
        }
        int duracion = desafio.duracionEfectiva();
        int dias = participacion.getDiasCompletados() + 1;
        participacion.setDiasCompletados(dias);
        participacion.setFechaUltimoProgreso(hoy);
        participacion.setPorcentajeProgreso(porcentaje(dias, duracion));
        participacion.setMontoProgresoActual(ahorroEstimado(desafio.getMontoObjetivo(), dias, duracion));
        // Los +20 XP del progreso se ganan UNA vez al día aunque tenga varios retos: si no, bastaría con
        // crear (o abandonar y volver a unirse a) retos para sumar XP sin límite
        boolean yaGanoXpHoy = progresoDesafioRepository
                .existsByUsuarioDesafioUsuarioIdUsuarioAndFechaProgresoAndXpOtorgadoGreaterThan(usuario.getIdUsuario(), hoy, 0);
        int xp = yaGanoXpHoy ? 0 : Niveles.XP_POR_PROGRESO_DIARIO;
        progresoDesafioRepository.save(new ProgresoDesafio(participacion, hoy, xp));
        gamificacionService.sumarPuntos(usuario, xp);

        boolean completado = dias >= duracion;
        int recompensa = 0;
        if (completado) {
            participacion.setEstadoDesafio(COMPLETADO);
            // Solo los retos OFICIALES dan puntos de recompensa (así nadie se regala puntos con retos fáciles)
            recompensa = desafio.esOficial() ? desafio.getPuntosRecompensa() : 0;
            gamificacionService.sumarPuntos(usuario, recompensa);
            notificar(usuario, mensajes.get("reto.completado.titulo"),
                    mensajes.get("reto.completado.mensaje", desafio.getTitulo(), String.valueOf(recompensa)));
        }
        usuarioDesafioRepository.save(participacion);
        List<String> logros = gamificacionService.revisarLogros(usuario);
        return new ProgresoRegistradoDTO(xp, dias, duracion, completado, recompensa, logros);
    }

    @Override
    @Transactional
    public ParticipacionDTO abandonar(Usuario usuario, int idUsuarioDesafio) {
        UsuarioDesafio participacion = participacionPropia(usuario, idUsuarioDesafio);
        if (!EN_PROGRESO.equals(participacion.getEstadoDesafio())) {
            throw new ReglaRetoException(409, mensajes.get("reto.no.esta.en.progreso"));
        }
        participacion.setEstadoDesafio(ABANDONADO);
        usuarioDesafioRepository.save(participacion);
        return aDTO(participacion);
    }

    @Override
    @Transactional
    public ParticipacionDTO crearPersonal(Usuario usuario, RetoPersonalDTO datos) {
        String tipo = datos.tipo() == null ? "" : datos.tipo().trim().toLowerCase();
        if (!TIPOS.containsKey(tipo)) {
            throw new ReglaRetoException(400, mensajes.get("reto.tipo.invalido"));
        }
        if (datos.duracionDias() == null || datos.duracionDias() < 1 || datos.duracionDias() > DURACION_MAXIMA) {
            throw new ReglaRetoException(400, mensajes.get("reto.duracion.invalida", String.valueOf(DURACION_MAXIMA)));
        }
        if (datos.montoObjetivo() == null || datos.montoObjetivo().compareTo(BigDecimal.ZERO) <= 0
                || datos.montoObjetivo().compareTo(MONTO_MAXIMO) > 0) {
            throw new ReglaRetoException(400, mensajes.get("reto.monto.invalido"));
        }
        String titulo = datos.titulo() == null || datos.titulo().isBlank()
                ? mensajes.get(TIPOS.get(tipo)) : datos.titulo().trim();
        if (titulo.length() > 30) {
            throw new ReglaRetoException(400, mensajes.get("reto.titulo.largo"));
        }
        LocalDate hoy = reloj.hoy();
        Desafio desafio = new Desafio();
        desafio.setTitulo(titulo);
        desafio.setDescripcion(mensajes.get(datos.duracionDias() == 1 ? "reto.personal.descripcion.un.dia" : "reto.personal.descripcion",
                String.valueOf(datos.duracionDias()), datos.montoObjetivo().setScale(2, RoundingMode.HALF_UP).toPlainString()));
        desafio.setTipoDesafio(tipo);
        desafio.setDuracionDias(datos.duracionDias());
        desafio.setMontoObjetivo(datos.montoObjetivo());
        desafio.setPuntosRecompensa(0);
        desafio.setFechaInicio(hoy);
        desafio.setFechaFin(hoy.plusDays(datos.duracionDias() - 1L));
        desafio.setSaldoInicial(BigDecimal.ZERO);
        desafio.setSaldoProyectado(datos.montoObjetivo());
        desafio.setEdadMinima(0);
        desafio.setEstado(1);
        desafio.setUsuarioCreador(usuario);
        desafioRepository.save(desafio);
        return aDTO(inscribir(usuario, desafio)); // quien lo crea queda inscrito
    }

    @Override
    @Transactional
    public String revisarGasto(Usuario usuario, Gasto gasto) {
        List<String> perdidos = new ArrayList<>();
        for (UsuarioDesafio participacion : participacionesDe(usuario)) {
            String tipo = participacion.getDesafio().getTipoDesafio();
            if (!EN_PROGRESO.equals(participacion.getEstadoDesafio()) || !ReglasReto.seVerificaConGastos(tipo)
                    || !ReglasReto.rompe(tipo, gasto)) {
                continue;
            }
            // Solo importa si ese día ya estaba marcado como cumplido
            progresoDesafioRepository.findFirstByUsuarioDesafioIdUsuarioDesafioAndFechaProgreso(
                    participacion.getIdUsuarioDesafio(), gasto.getFechaGasto()).ifPresent(progreso -> {
                quitarDia(usuario, participacion, progreso);
                perdidos.add(participacion.getDesafio().getTitulo());
                int xpPerdido = progreso.xpOtorgadoEfectivo();
                notificar(usuario, mensajes.get("reto.dia.perdido.titulo"), xpPerdido > 0
                        ? mensajes.get("reto.dia.perdido.mensaje", participacion.getDesafio().getTitulo(), String.valueOf(xpPerdido))
                        : mensajes.get("reto.dia.perdido.mensaje.sin.xp", participacion.getDesafio().getTitulo()));
            });
        }
        return perdidos.isEmpty() ? null
                : mensajes.get("reto.dia.perdido.aviso", String.join(", ", perdidos), detalle(gasto));
    }

    // Deshace el progreso de ese día: el reto retrocede un día y se descuentan sus +20 XP
    private void quitarDia(Usuario usuario, UsuarioDesafio participacion, ProgresoDesafio progreso) {
        progresoDesafioRepository.delete(progreso);
        progresoDesafioRepository.flush();
        Desafio desafio = participacion.getDesafio();
        int duracion = desafio.duracionEfectiva();
        int dias = Math.max(0, participacion.getDiasCompletados() - 1);
        participacion.setDiasCompletados(dias);
        participacion.setPorcentajeProgreso(porcentaje(dias, duracion));
        participacion.setMontoProgresoActual(ahorroEstimado(desafio.getMontoObjetivo(), dias, duracion));
        // Si era el progreso de hoy, puede volver a registrarlo cuando ya no tenga ese gasto
        participacion.setFechaUltimoProgreso(progresoDesafioRepository
                .findFirstByUsuarioDesafioIdUsuarioDesafioOrderByFechaProgresoDesc(participacion.getIdUsuarioDesafio())
                .map(ProgresoDesafio::getFechaProgreso).orElse(null));
        usuarioDesafioRepository.save(participacion);
        gamificacionService.quitarPuntos(usuario, progreso.xpOtorgadoEfectivo()); // solo lo que ese día realmente dio
    }

    // "Rappi, S/ 25.00"
    private static String detalle(Gasto gasto) {
        String nombre = gasto.getComerciante() == null || gasto.getComerciante().getNombreComerciante() == null
                ? gasto.getDescripcionGasto() : gasto.getComerciante().getNombreComerciante();
        return nombre + ", S/ " + gasto.getMontoGasto().setScale(2, RoundingMode.HALF_UP).toPlainString();
    }

    // ---------- Apoyo ----------

    private List<UsuarioDesafio> participacionesDe(Usuario usuario) {
        return usuarioDesafioRepository.findByUsuarioIdUsuarioAndEstadoOrderByIdUsuarioDesafioDesc(usuario.getIdUsuario(), 1);
    }

    private UsuarioDesafio participacionPropia(Usuario usuario, int idUsuarioDesafio) {
        UsuarioDesafio participacion = usuarioDesafioRepository.findByIdUsuarioDesafioAndEstado(idUsuarioDesafio, 1)
                .orElseThrow(() -> new ReglaRetoException(404, mensajes.get("usuario.desafio.no.encontrado")));
        if (participacion.getUsuario().getIdUsuario() != usuario.getIdUsuario()) {
            throw new ReglaRetoException(403, mensajes.get("no.tiene.permiso.sobre.datos"));
        }
        return participacion;
    }

    private UsuarioDesafio inscribir(Usuario usuario, Desafio desafio) {
        UsuarioDesafio participacion = new UsuarioDesafio();
        participacion.setUsuario(usuario);
        participacion.setDesafio(desafio);
        participacion.setSaldoInicial(usuario.getSaldoActualUsuario() == null ? BigDecimal.ZERO : usuario.getSaldoActualUsuario());
        participacion.setMontoProgresoActual(BigDecimal.ZERO);
        participacion.setPorcentajeProgreso(BigDecimal.ZERO);
        participacion.setEstadoDesafio(EN_PROGRESO);
        participacion.setDiasCompletados(0);
        participacion.setFechaInscripcion(reloj.hoy());
        participacion.setEstado(1);
        return usuarioDesafioRepository.save(participacion);
    }

    // Si ya pasó la fecha fin sin cumplir todos los días, el reto queda VENCIDO
    private void marcarSiVencio(UsuarioDesafio participacion) {
        if (EN_PROGRESO.equals(participacion.getEstadoDesafio())
                && participacion.getDesafio().getFechaFin().isBefore(reloj.hoy())) {
            participacion.setEstadoDesafio(VENCIDO);
            usuarioDesafioRepository.save(participacion);
        }
    }

    private static BigDecimal porcentaje(int dias, int duracion) {
        return BigDecimal.valueOf(Math.min(100.0, dias * 100.0 / duracion)).setScale(2, RoundingMode.HALF_UP);
    }

    // Ahorro estimado: la meta repartida entre los días del reto ("día 5 de 7, ahorro estimado S/ 140")
    private static BigDecimal ahorroEstimado(BigDecimal meta, int dias, int duracion) {
        return meta.multiply(BigDecimal.valueOf(Math.min(dias, duracion)))
                .divide(BigDecimal.valueOf(duracion), 2, RoundingMode.HALF_UP);
    }

    private ParticipacionDTO aDTO(UsuarioDesafio participacion) {
        Desafio desafio = participacion.getDesafio();
        int duracion = desafio.duracionEfectiva();
        int dias = participacion.getDiasCompletados();
        LocalDate hoy = reloj.hoy();
        long restantes = Math.max(0, ChronoUnit.DAYS.between(hoy, desafio.getFechaFin()) + 1);
        return new ParticipacionDTO(participacion.getIdUsuarioDesafio(), desafio.getIdDesafio(), desafio.getTitulo(),
                desafio.getDescripcion(), desafio.getTipoDesafio(), desafio.esOficial(), dias, duracion,
                porcentaje(dias, duracion), ahorroEstimado(desafio.getMontoObjetivo(), dias, duracion),
                desafio.getMontoObjetivo(), desafio.esOficial() ? desafio.getPuntosRecompensa() : 0,
                participacion.getEstadoDesafio(), hoy.equals(participacion.getFechaUltimoProgreso()),
                desafio.getFechaFin(), restantes);
    }

    private void notificar(Usuario usuario, String titulo, String mensaje) {
        Notificacion notificacion = new Notificacion();
        notificacion.setUsuario(usuario);
        notificacion.setTipo("reto");
        notificacion.setTitulo(titulo.length() <= 30 ? titulo : titulo.substring(0, 27) + "...");
        notificacion.setMensaje(mensaje.length() <= 100 ? mensaje : mensaje.substring(0, 97) + "...");
        notificacion.setEstadoNotificacion(false);
        notificacionService.registrarAutomatica(notificacion); // US25: respeta sus preferencias
    }
}
