package pe.edu.upc.walletix.servicesimplements;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.edu.upc.walletix.entities.Gasto;
import pe.edu.upc.walletix.entities.Notificacion;
import pe.edu.upc.walletix.entities.Presupuesto;
import pe.edu.upc.walletix.i18n.Mensajes;
import pe.edu.upc.walletix.repositories.IGastoRepository;
import pe.edu.upc.walletix.repositories.PresupuestoRepository;
import pe.edu.upc.walletix.servicesinterfaces.IAlertaPresupuestoService;
import pe.edu.upc.walletix.servicesinterfaces.INotificacionService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Optional;

@Service
public class AlertaPresupuestoServiceImplement implements IAlertaPresupuestoService {
    @Autowired
    private PresupuestoRepository presupuestoRepository;
    @Autowired
    private IGastoRepository gastoRepository;
    @Autowired
    private INotificacionService notificacionService;
    @Autowired
    private Mensajes mensajes;

    // Montos con separador de miles y 2 decimales, ej. "1,250.50" (igual que la app)
    private String monto(BigDecimal valor) {
        return pe.edu.upc.walletix.config.Montos.formato(valor);
    }

    // Los textos de la tabla notificacion tienen un largo máximo (titulo 30, mensaje 100)
    private String recortar(String texto, int maximo) {
        return texto.length() <= maximo ? texto : texto.substring(0, maximo - 3) + "...";
    }

    @Override
    public Optional<String> revisar(Gasto gasto) {
        int idUsuario = gasto.getUsuario().getIdUsuario();
        int idCategoria = gasto.getCategoria().getIdCategoria();
        LocalDate fecha = gasto.getFechaGasto();

        // 1. ¿Hay un presupuesto activo para esa categoría en el mes del gasto?
        Optional<Presupuesto> presupuesto = presupuestoRepository
                .findFirstByUsuarioIdUsuarioAndCategoriaIdCategoriaAndMesAndAnioAndEstado(
                        idUsuario, idCategoria, fecha.getMonthValue(), fecha.getYear(), 1);
        if (presupuesto.isEmpty()) {
            return Optional.empty();
        }

        // 2. ¿Cuánto lleva gastado el usuario en esa categoría durante ese mes (incluido este gasto)?
        BigDecimal gastado = gastoRepository.sumarPorCategoriaYRango(
                idUsuario, idCategoria, fecha.withDayOfMonth(1), fecha.withDayOfMonth(fecha.lengthOfMonth()));
        BigDecimal asignado = new BigDecimal(Float.toString(presupuesto.get().getMontoAsignado()));
        if (gastado.compareTo(asignado) <= 0) {
            return Optional.empty();
        }

        // 3. Se superó: se avisa en la respuesta y queda guardado como notificación
        String alerta = mensajes.get("presupuesto.excedido",
                gasto.getCategoria().getNombreCategoria(), monto(gastado), monto(asignado));
        Notificacion notificacion = new Notificacion();
        notificacion.setUsuario(gasto.getUsuario());
        notificacion.setTitulo(recortar(mensajes.get("presupuesto.excedido.titulo"), 30));
        notificacion.setTipo("alerta");
        notificacion.setMensaje(recortar(alerta, 100));
        notificacion.setEstadoNotificacion(false); // no leída
        notificacionService.registrarAutomatica(notificacion); // US25: respeta sus preferencias
        return Optional.of(alerta);
    }
}
