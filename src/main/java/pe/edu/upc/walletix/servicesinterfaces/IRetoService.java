package pe.edu.upc.walletix.servicesinterfaces;

import pe.edu.upc.walletix.dtos.RetosDTOs.*;
import pe.edu.upc.walletix.entities.Usuario;

import java.util.List;

// Centro de Retos y Logros (US26-US29)
public interface IRetoService {
    // Nivel, efectividad, dinero blindado, racha y los retos del usuario (US26)
    ResumenRetosDTO resumen(Usuario usuario);

    // Retos oficiales vigentes o próximos, con los requisitos que le faltan al usuario (US27, US28)
    List<RetoDisponibleDTO> disponibles(Usuario usuario);

    // Unirse a un reto oficial; si falta algún requisito lanza ReglaRetoException (US27, US28)
    ParticipacionDTO unirse(Usuario usuario, int idDesafio);

    // "Registrar progreso diario": una sola vez por día (+20 XP). Al cumplir todos los días se supera.
    ProgresoRegistradoDTO registrarProgreso(Usuario usuario, int idUsuarioDesafio);

    ParticipacionDTO abandonar(Usuario usuario, int idUsuarioDesafio);

    // Reto personal: tipo, duración y meta (US29). Solo lo ve su creador y no da puntos de recompensa.
    ParticipacionDTO crearPersonal(Usuario usuario, RetoPersonalDTO datos);

    // Error de una regla de los retos, con el código HTTP que debe responder la API
    class ReglaRetoException extends RuntimeException {
        private final int status;

        public ReglaRetoException(int status, String mensaje) {
            super(mensaje);
            this.status = status;
        }

        public int getStatus() {
            return status;
        }
    }
}
