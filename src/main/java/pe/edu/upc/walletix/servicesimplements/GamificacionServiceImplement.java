package pe.edu.upc.walletix.servicesimplements;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.edu.upc.walletix.config.Reloj;
import pe.edu.upc.walletix.dtos.RetosDTOs.MedallaDTO;
import pe.edu.upc.walletix.entities.Logro;
import pe.edu.upc.walletix.entities.Notificacion;
import pe.edu.upc.walletix.entities.Usuario;
import pe.edu.upc.walletix.entities.UsuarioLogro;
import pe.edu.upc.walletix.i18n.Mensajes;
import pe.edu.upc.walletix.repositories.*;
import pe.edu.upc.walletix.servicesinterfaces.IGamificacionService;
import pe.edu.upc.walletix.servicesinterfaces.INotificacionService;

import java.math.BigDecimal;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.ToLongFunction;

@Service
public class GamificacionServiceImplement implements IGamificacionService {

    @Autowired
    private IUsuarioRepository usuarioRepository;
    @Autowired
    private ILogroRepository logroRepository;
    @Autowired
    private IUsuarioLogroRepository usuarioLogroRepository;
    @Autowired
    private IGastoRepository gastoRepository;
    @Autowired
    private IngresoRepository ingresoRepository;
    @Autowired
    private MetaAhorroRepository metaAhorroRepository;
    @Autowired
    private IIntentoQuizUsuarioRepository intentoQuizRepository;
    @Autowired
    private IUsuarioDesafioRepository usuarioDesafioRepository;
    @Autowired
    private IProgresoDesafioRepository progresoDesafioRepository;
    @Autowired
    private INotificacionService notificacionService;
    @Autowired
    private Mensajes mensajes;
    @Autowired
    private Reloj reloj;

    // Regla de un logro automático: cuánto lleva el usuario (actual) y cuánto necesita (meta)
    private record Regla(long meta, ToLongFunction<Usuario> actual) {
    }

    // Logros que la API desbloquea sola, buscados por su nombre (sin tildes ni mayúsculas).
    // Los que no estén aquí (ej. "Centinela Hormiga") siguen asignándose a mano por un ADMIN.
    private Map<String, Regla> reglas() {
        return Map.of(
                "primeros pasos", new Regla(10, u -> u.getSaldoActualUsuario() == null ? 0
                        : Math.min(10, u.getSaldoActualUsuario().max(BigDecimal.ZERO).longValue())),
                "primer registro", new Regla(1, u -> gastos(u) + ingresoRepository.countByUsuarioIdUsuarioAndEstado(u.getIdUsuario(), 1)),
                "primer gasto registrado", new Regla(1, this::gastos),
                "alcancia iniciada", new Regla(1, u -> metaAhorroRepository.countByUsuarioIdUsuarioAndEstado(u.getIdUsuario(), 1)),
                "gran ahorrador", new Regla(1, u -> metaAhorroRepository.contarCumplidas(u.getIdUsuario())),
                "constante", new Regla(7, u -> Niveles.racha(gastoRepository.fechasConGastos(u.getIdUsuario()), reloj.hoy())),
                "retador", new Regla(1, u -> retosSuperados(u.getIdUsuario())),
                "estudioso", new Regla(1, u -> intentoQuizRepository
                        .countByUsuarioIdUsuarioAndAprobadoIntentoQuizUsuarioTrueAndEstadoIntentoQuizUsuario(u.getIdUsuario(), 1)),
                "lector financiero", new Regla(1, u -> intentoQuizRepository
                        .countByUsuarioIdUsuarioAndEstadoIntentoQuizUsuario(u.getIdUsuario(), 1)));
    }

    private long gastos(Usuario usuario) {
        return gastoRepository.countByUsuarioIdUsuarioAndEstadoGasto(usuario.getIdUsuario(), 1);
    }

    private long retosSuperados(int idUsuario) {
        return usuarioDesafioRepository.findByUsuarioIdUsuarioAndEstadoOrderByIdUsuarioDesafioDesc(idUsuario, 1).stream()
                .filter(participacion -> "COMPLETADO".equals(participacion.getEstadoDesafio()))
                .count();
    }

    // "Alcancía Iniciada" -> "alcancia iniciada"
    private static String clave(String nombre) {
        String sinTildes = Normalizer.normalize(nombre == null ? "" : nombre, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
        return sinTildes.trim().toLowerCase();
    }

    private boolean tiene(Usuario usuario, Logro logro) {
        return usuarioLogroRepository.existsByUsuarioIdUsuarioAndLogroIdLogroAndEstadoUsuarioLogro(
                usuario.getIdUsuario(), logro.getIdLogro(), 1);
    }

    @Override
    public void sumarPuntos(Usuario usuario, int puntos) {
        if (puntos <= 0) {
            return;
        }
        usuario.setPuntosGamificacionUsuario(usuario.getPuntosGamificacionUsuario() + puntos);
        usuarioRepository.save(usuario);
    }

    // Un candado por usuario: si llegan dos peticiones a la vez (ej. al abrir la página),
    // la segunda espera a la primera y ya ve el logro entregado, así no se otorga dos veces.
    private final java.util.concurrent.ConcurrentHashMap<Integer, Object> candados = new java.util.concurrent.ConcurrentHashMap<>();

    @Override
    public List<String> revisarLogros(Usuario usuario) {
        synchronized (candados.computeIfAbsent(usuario.getIdUsuario(), id -> new Object())) {
            return revisarLogrosSinCompetencia(usuario);
        }
    }

    private List<String> revisarLogrosSinCompetencia(Usuario usuario) {
        List<String> nuevos = new ArrayList<>();
        Map<String, Regla> reglas = reglas();
        for (Logro logro : logroRepository.findByEstadoLogro(1)) {
            Regla regla = reglas.get(clave(logro.getNombreLogro()));
            if (regla == null || tiene(usuario, logro) || regla.actual().applyAsLong(usuario) < regla.meta()) {
                continue;
            }
            // Se desbloquea: queda registrado, suma sus puntos y llega una notificación a la campana
            UsuarioLogro usuarioLogro = new UsuarioLogro();
            usuarioLogro.setUsuario(usuario);
            usuarioLogro.setLogro(logro);
            usuarioLogro.setEstadoUsuarioLogro(1);
            usuarioLogroRepository.save(usuarioLogro);
            sumarPuntos(usuario, logro.getPuntosLogro());
            notificar(usuario, mensajes.get("logro.desbloqueado.titulo"),
                    mensajes.get("logro.desbloqueado.mensaje", logro.getNombreLogro(), String.valueOf(logro.getPuntosLogro())));
            nuevos.add(logro.getNombreLogro());
        }
        return nuevos;
    }

    @Override
    public List<MedallaDTO> medallas(Usuario usuario) {
        Map<String, Regla> reglas = reglas();
        return logroRepository.findByEstadoLogro(1).stream().map(logro -> {
            Regla regla = reglas.get(clave(logro.getNombreLogro()));
            boolean desbloqueada = tiene(usuario, logro);
            Long actual = regla == null ? null : Math.min(regla.actual().applyAsLong(usuario), regla.meta());
            Long meta = regla == null ? null : regla.meta();
            if (desbloqueada && meta != null) {
                actual = meta; // una medalla ya ganada se muestra completa
            }
            return new MedallaDTO(logro.getIdLogro(), logro.getNombreLogro(), logro.getDescripcionLogro(),
                    logro.getPuntosLogro(), desbloqueada, actual, meta);
        }).toList();
    }

    @Override
    public int rachaDeRetos(int idUsuario) {
        return Niveles.racha(progresoDesafioRepository.fechasConProgreso(idUsuario), reloj.hoy());
    }

    // Los textos de la tabla notificacion tienen un largo máximo (título 30, mensaje 100)
    private void notificar(Usuario usuario, String titulo, String mensaje) {
        Notificacion notificacion = new Notificacion();
        notificacion.setUsuario(usuario);
        notificacion.setTipo("logro");
        notificacion.setTitulo(titulo.length() <= 30 ? titulo : titulo.substring(0, 27) + "...");
        notificacion.setMensaje(mensaje.length() <= 100 ? mensaje : mensaje.substring(0, 97) + "...");
        notificacion.setEstadoNotificacion(false);
        notificacionService.registrar(notificacion);
    }
}
