package pe.edu.upc.walletix.servicesimplements;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upc.walletix.config.Reloj;
import pe.edu.upc.walletix.dtos.AprendeDTOs.*;
import pe.edu.upc.walletix.entities.*;
import pe.edu.upc.walletix.i18n.Mensajes;
import pe.edu.upc.walletix.repositories.*;
import pe.edu.upc.walletix.servicesinterfaces.IAprendeService;
import pe.edu.upc.walletix.servicesinterfaces.IGamificacionService;

import java.text.Normalizer;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AprendeServiceImplement implements IAprendeService {
    public static final String LECCION = "leccion";
    public static final String TIP = "tip";
    private static final int PALABRAS_POR_MINUTO = 200;

    @Autowired
    private IMicroleccionRepository microleccionRepository;
    @Autowired
    private IPreguntaQuizRepository preguntaQuizRepository;
    @Autowired
    private IIntentoQuizUsuarioRepository intentoQuizRepository;
    @Autowired
    private IFavoritoMicroleccionRepository favoritoRepository;
    @Autowired
    private IUsuarioDesafioRepository usuarioDesafioRepository;
    @Autowired
    private IGamificacionService gamificacionService;
    @Autowired
    private Mensajes mensajes;
    @Autowired
    private Reloj reloj;

    // Lo que el usuario ya hizo, cargado una sola vez por petición (menos consultas a la base)
    private record Avance(boolean desbloqueaContenido, Set<Integer> favoritos, Map<Integer, List<IntentoQuizUsuario>> intentos,
                          Map<Integer, Long> preguntas) {
    }

    private Avance avanceDe(Usuario usuario) {
        Set<Integer> favoritos = favoritoRepository.findByUsuarioIdUsuario(usuario.getIdUsuario()).stream()
                .map(favorito -> favorito.getMicroleccion().getIdMicroleccion()).collect(Collectors.toSet());
        Map<Integer, List<IntentoQuizUsuario>> intentos = intentoQuizRepository
                .findByUsuarioIdUsuarioAndEstadoIntentoQuizUsuario(usuario.getIdUsuario(), 1).stream()
                .collect(Collectors.groupingBy(intento -> intento.getMicroleccion().getIdMicroleccion()));
        Map<Integer, Long> preguntas = preguntaQuizRepository.findByEstadoPreguntaQuiz(1).stream()
                .collect(Collectors.groupingBy(pregunta -> pregunta.getMicroleccion().getIdMicroleccion(), Collectors.counting()));
        return new Avance(superoUnRetoOficial(usuario), favoritos, intentos, preguntas);
    }

    // US33: el contenido bloqueado se abre al superar un reto OFICIAL (uno personal se puede crear de 1 día)
    private boolean superoUnRetoOficial(Usuario usuario) {
        return usuarioDesafioRepository.findByUsuarioIdUsuarioAndEstadoOrderByIdUsuarioDesafioDesc(usuario.getIdUsuario(), 1).stream()
                .anyMatch(participacion -> RetoServiceImplement.COMPLETADO.equals(participacion.getEstadoDesafio())
                        && participacion.getDesafio().esOficial());
    }

    private boolean bloqueado(Microleccion microleccion, Avance avance) {
        return microleccion.isBloqueadoMicroleccion() && !avance.desbloqueaContenido();
    }

    private ContenidoResumenDTO resumen(Microleccion microleccion, Avance avance) {
        int id = microleccion.getIdMicroleccion();
        List<IntentoQuizUsuario> intentos = avance.intentos().getOrDefault(id, List.of());
        Integer mejor = intentos.stream().map(IntentoQuizUsuario::getPuntajeIntentoQuizUsuario).max(Integer::compare).orElse(null);
        boolean bloqueado = bloqueado(microleccion, avance);
        return new ContenidoResumenDTO(id, microleccion.getTituloMicroleccion(), microleccion.getDescripcionMicroleccion(),
                microleccion.getCategoriaEducativaMicroleccion(), microleccion.getTipoMicroleccion(),
                minutosLectura(microleccion.getContenidoMicroleccion()), avance.preguntas().getOrDefault(id, 0L).intValue(),
                LECCION.equals(microleccion.getTipoMicroleccion()) ? microleccion.getPuntosRecompensaMicroleccion() : 0,
                bloqueado, bloqueado ? mensajes.get("aprende.condicion.reto") : null,
                avance.favoritos().contains(id),
                intentos.stream().anyMatch(IntentoQuizUsuario::isAprobadoIntentoQuizUsuario), mejor);
    }

    static int minutosLectura(String contenido) {
        String texto = contenido == null ? "" : contenido.trim();
        int palabras = texto.isEmpty() ? 0 : texto.split("\\s+").length;
        return Math.max(1, (int) Math.ceil(palabras / (double) PALABRAS_POR_MINUTO));
    }

    // "Inversión" -> "inversion" (para buscar y filtrar sin importar tildes ni mayúsculas)
    static String normalizar(String texto) {
        return Normalizer.normalize(texto == null ? "" : texto, Normalizer.Form.NFD).replaceAll("\\p{M}", "")
                .toLowerCase().trim();
    }

    private Microleccion activa(int idMicroleccion) {
        return microleccionRepository.findByIdMicroleccionAndEstadoMicroleccion(idMicroleccion, 1)
                .orElseThrow(() -> new ReglaAprendeException(404, mensajes.get("microleccion.no.encontrada")));
    }

    // ---------- Consultas ----------

    @Override
    public List<ContenidoResumenDTO> listar(Usuario usuario, String categoria, String buscar, boolean soloFavoritos) {
        Avance avance = avanceDe(usuario);
        String categoriaBuscada = normalizar(categoria);
        String textoBuscado = normalizar(buscar);
        return microleccionRepository.findByEstadoMicroleccion(1).stream()
                .filter(m -> categoriaBuscada.isEmpty() || normalizar(m.getCategoriaEducativaMicroleccion()).equals(categoriaBuscada))
                .filter(m -> textoBuscado.isEmpty() || normalizar(m.getTituloMicroleccion() + " " + m.getDescripcionMicroleccion()
                        + " " + m.getCategoriaEducativaMicroleccion() + " " + m.getContenidoMicroleccion()).contains(textoBuscado))
                .filter(m -> !soloFavoritos || avance.favoritos().contains(m.getIdMicroleccion()))
                // Primero las lecciones y luego los consejos, cada grupo en el orden en que se crearon
                .sorted(Comparator.comparing((Microleccion m) -> !LECCION.equals(m.getTipoMicroleccion()))
                        .thenComparing(Microleccion::getIdMicroleccion))
                .map(m -> resumen(m, avance))
                .toList();
    }

    @Override
    public ContenidoDetalleDTO abrir(Usuario usuario, int idMicroleccion) {
        Microleccion microleccion = activa(idMicroleccion);
        Avance avance = avanceDe(usuario);
        if (bloqueado(microleccion, avance)) {
            // US33: el contenido no se envía; solo el mensaje con la condición para desbloquearlo
            throw new ReglaAprendeException(403, mensajes.get("aprende.bloqueado", mensajes.get("aprende.condicion.reto")));
        }
        return detalle(microleccion, avance);
    }

    private ContenidoDetalleDTO detalle(Microleccion microleccion, Avance avance) {
        List<PreguntaDTO> preguntas = LECCION.equals(microleccion.getTipoMicroleccion())
                ? preguntasDe(microleccion.getIdMicroleccion()).stream().map(AprendeServiceImplement::sinRespuesta).toList()
                : List.of();
        return new ContenidoDetalleDTO(resumen(microleccion, avance), microleccion.getContenidoMicroleccion(),
                microleccion.getTituloQuizMicroleccion(), microleccion.getPuntajeAprobatorioMicroleccion(), preguntas);
    }

    private List<PreguntaQuiz> preguntasDe(int idMicroleccion) {
        return preguntaQuizRepository.findByMicroleccionIdMicroleccionAndEstadoPreguntaQuiz(idMicroleccion, 1).stream()
                .sorted(Comparator.comparing(PreguntaQuiz::getIdPreguntaQuiz)).toList();
    }

    private static List<OpcionDTO> opciones(PreguntaQuiz pregunta) {
        List<OpcionDTO> opciones = new ArrayList<>();
        String[] textos = {pregunta.getOpcionAPreguntaQuiz(), pregunta.getOpcionBPreguntaQuiz(),
                pregunta.getOpcionCPreguntaQuiz(), pregunta.getOpcionDPreguntaQuiz()};
        String[] letras = {"a", "b", "c", "d"};
        for (int i = 0; i < textos.length; i++) {
            if (textos[i] != null && !textos[i].isBlank()) {
                opciones.add(new OpcionDTO(letras[i], textos[i]));
            }
        }
        return opciones;
    }

    private static PreguntaDTO sinRespuesta(PreguntaQuiz pregunta) {
        return new PreguntaDTO(pregunta.getIdPreguntaQuiz(), pregunta.getEnunciadoPreguntaQuiz(), opciones(pregunta));
    }

    @Override
    public ContenidoDetalleDTO tipDelDia(Usuario usuario) {
        Avance avance = avanceDe(usuario);
        List<Microleccion> tips = microleccionRepository.findByEstadoMicroleccion(1).stream()
                .filter(m -> TIP.equals(m.getTipoMicroleccion()) && !bloqueado(m, avance))
                .sorted(Comparator.comparing(Microleccion::getIdMicroleccion))
                .toList();
        if (tips.isEmpty()) {
            return null;
        }
        // El mismo consejo todo el día; al día siguiente, el que sigue
        return detalle(tips.get((int) (reloj.hoy().toEpochDay() % tips.size())), avance);
    }

    // ---------- Acciones ----------

    @Override
    @Transactional
    public ResultadoQuizDTO responderQuiz(Usuario usuario, int idMicroleccion, RespuestasQuizDTO datos) {
        Microleccion microleccion = activa(idMicroleccion);
        if (!LECCION.equals(microleccion.getTipoMicroleccion())) {
            throw new ReglaAprendeException(400, mensajes.get("solo.microlecciones.tipo.leccion.tienen"));
        }
        Avance avance = avanceDe(usuario);
        if (bloqueado(microleccion, avance)) {
            throw new ReglaAprendeException(403, mensajes.get("aprende.bloqueado", mensajes.get("aprende.condicion.reto")));
        }
        List<PreguntaQuiz> preguntas = preguntasDe(idMicroleccion);
        if (preguntas.isEmpty()) {
            throw new ReglaAprendeException(400, mensajes.get("aprende.quiz.sin.preguntas"));
        }
        Map<Integer, String> respuestas = datos == null || datos.respuestas() == null ? Map.of() : datos.respuestas();
        // US35: "Given que respondo TODAS las preguntas del quiz"
        for (PreguntaQuiz pregunta : preguntas) {
            String letra = respuestas.get(pregunta.getIdPreguntaQuiz());
            boolean valida = letra != null && opciones(pregunta).stream().anyMatch(o -> o.letra().equalsIgnoreCase(letra.trim()));
            if (!valida) {
                throw new ReglaAprendeException(400, mensajes.get("aprende.quiz.incompleto"));
            }
        }

        List<RetroalimentacionDTO> retroalimentacion = new ArrayList<>();
        int correctas = 0;
        for (PreguntaQuiz pregunta : preguntas) {
            String elegida = respuestas.get(pregunta.getIdPreguntaQuiz()).trim().toLowerCase();
            String correcta = pregunta.getOpcionCorrectaPreguntaQuiz().trim().toLowerCase();
            boolean acerto = elegida.equals(correcta);
            if (acerto) {
                correctas++;
            }
            String textoCorrecto = opciones(pregunta).stream().filter(o -> o.letra().equals(correcta))
                    .map(OpcionDTO::texto).findFirst().orElse("");
            retroalimentacion.add(new RetroalimentacionDTO(pregunta.getIdPreguntaQuiz(), pregunta.getEnunciadoPreguntaQuiz(),
                    elegida, correcta, textoCorrecto, acerto, pregunta.getExplicacionPreguntaQuiz()));
        }
        int puntaje = Math.round(correctas * 100f / preguntas.size());
        boolean aprobado = puntaje >= microleccion.getPuntajeAprobatorioMicroleccion();
        boolean yaHabiaAprobado = avance.intentos().getOrDefault(idMicroleccion, List.of()).stream()
                .anyMatch(IntentoQuizUsuario::isAprobadoIntentoQuizUsuario);
        boolean primeraVez = aprobado && !yaHabiaAprobado;

        IntentoQuizUsuario intento = new IntentoQuizUsuario();
        intento.setUsuario(usuario);
        intento.setMicroleccion(microleccion);
        intento.setPuntajeIntentoQuizUsuario(puntaje);
        intento.setAprobadoIntentoQuizUsuario(aprobado);
        intento.setEstadoIntentoQuizUsuario(1);
        intentoQuizRepository.save(intento);

        // Los puntos de la lección se ganan una sola vez (repetir el quiz sirve para repasar, no para sumar puntos)
        int puntos = primeraVez ? microleccion.getPuntosRecompensaMicroleccion() : 0;
        gamificacionService.sumarPuntos(usuario, puntos);
        List<String> logros = gamificacionService.revisarLogros(usuario); // "Lector Financiero", "Estudioso"
        return new ResultadoQuizDTO(puntaje, correctas, preguntas.size(), microleccion.getPuntajeAprobatorioMicroleccion(),
                aprobado, primeraVez, puntos, logros, retroalimentacion);
    }

    @Override
    @Transactional
    public FavoritoDTO alternarFavorito(Usuario usuario, int idMicroleccion) {
        Microleccion microleccion = activa(idMicroleccion);
        Optional<FavoritoMicroleccion> existente = favoritoRepository
                .findByUsuarioIdUsuarioAndMicroleccionIdMicroleccion(usuario.getIdUsuario(), idMicroleccion);
        if (existente.isPresent()) {
            favoritoRepository.delete(existente.get()); // US32 escenario 2: quitar de favoritos
        } else {
            favoritoRepository.save(new FavoritoMicroleccion(usuario, microleccion)); // US32 escenario 1
        }
        long total = favoritoRepository.findByUsuarioIdUsuario(usuario.getIdUsuario()).size();
        return new FavoritoDTO(idMicroleccion, existente.isEmpty(), total);
    }
}
