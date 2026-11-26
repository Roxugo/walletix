package pe.edu.upc.walletix.securities;

import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

// Un candado por usuario para las acciones que dan puntos (registrar progreso, responder un quiz...).
// Si llegan dos peticiones del mismo usuario a la vez (ej. doble clic), la segunda espera a que la primera
// termine y se guarde, así ve el resultado y no se otorgan los puntos dos veces.
// Usuarios distintos no se esperan entre sí. (La API corre en una sola instancia de Render.)
@Component
public class CandadoUsuario {
    private final ConcurrentHashMap<Integer, Object> candados = new ConcurrentHashMap<>();

    // La acción debe llamar a un servicio @Transactional: su transacción se confirma antes de soltar el candado
    public <T> T ejecutar(int idUsuario, Supplier<T> accion) {
        synchronized (candados.computeIfAbsent(idUsuario, id -> new Object())) {
            return accion.get();
        }
    }
}
