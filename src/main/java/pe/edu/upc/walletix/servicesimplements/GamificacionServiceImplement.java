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

    // Solo retos OFICIALES: un reto personal (que cualquiera crea de 1 día) no puede dar la medalla "Retador"
    private long retosSuperados(int idUsuario) {
        return usuarioDesafioRepository.findByUsuarioIdUsuarioAndEstadoOrderByIdUsuarioDesafioDesc(idUsuario, 1).stream()
                .filter(participacion -> "COMPLETADO".equals(participacion.getEstadoDesafio()))
                .filter(participacion -> participacion.getDesafio().esOficial())
                .count();
    }

    // Ids de los logros que el usuario ya tiene: UNA consulta en vez de una por cada logro
    private java.util.Set<Integer> logrosDe(Usuario usuario) {
        return usuarioLogroRepository.findByUsuarioIdUsuarioAndEstadoUsuarioLogro(usuario.getIdUsuario(), 1).stream()
                .map(usuarioLogro -> usuarioLogro.getLogro().getIdLogro())
                .collect(java.util.stream.Collectors.toSet());
    }

    // "Alcancía Iniciada" -> "alcancia iniciada"
    private static String clave(String nombre) {
        String sinTildes = Normalizer.normalize(nombre == null ? "" : nombre, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
        return sinTildes.trim().toLowerCase();
    }


    @Override
    public void sumarPuntos(Usuario usuario, int puntos) {
        if (puntos <= 0) {
            return;
        }
        // UPDATE atómico: dos peticiones a la vez no se pisan los puntos
        usuarioRepository.sumarPuntos(usuario.getIdUsuario(), puntos);
        usuario.setPuntosGamificacionUsuario(usuarioRepository.puntosGuardados(usuario.getIdUsuario()));
    }

    @Override
    public void quitarPuntos(Usuario usuario, int puntos) {
        if (puntos <= 0) {
            return;
        }
        usuarioRepository.sumarPuntos(usuario.getIdUsuario(), -puntos); // nunca baja de 0
        usuario.setPuntosGamificacionUsuario(usuarioRepository.puntosGuardados(usuario.getIdUsuario()));
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
        java.util.Set<Integer> yaTiene = logrosDe(usuario);
        for (Logro logro : logroRepository.findByEstadoLogro(1)) {
            Regla regla = reglas.get(clave(logro.getNombreLogro()));
            // Solo se calcula la regla de los logros que todavía no tiene (menos consultas a la base)
            if (regla == null || yaTiene.contains(logro.getIdLogro()) || regla.actual().applyAsLong(usuario) < regla.meta()) {
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
        java.util.Set<Integer> yaTiene = logrosDe(usuario);
        return logroRepository.findByEstadoLogro(1).stream().map(logro -> {
            Regla regla = reglas.get(clave(logro.getNombreLogro()));
            boolean desbloqueada = yaTiene.contains(logro.getIdLogro());
            Long meta = regla == null ? null : regla.meta();
            // Una medalla ya ganada se muestra completa (no hace falta calcular su avance)
            Long actual = regla == null ? null : desbloqueada ? meta : Math.min(regla.actual().applyAsLong(usuario), meta);
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
        notificacionService.registrarAutomatica(notificacion); // US25: respeta sus preferencias
    }
}
