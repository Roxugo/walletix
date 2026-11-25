package pe.edu.upc.walletix.servicesinterfaces;

import pe.edu.upc.walletix.dtos.RetosDTOs.MedallaDTO;
import pe.edu.upc.walletix.entities.Usuario;

import java.util.List;

// Puntos (XP), logros automáticos y vitrina de medallas
public interface IGamificacionService {
    // Suma puntos de gamificación al usuario
    void sumarPuntos(Usuario usuario, int puntos);

    // Revisa las reglas de los logros y desbloquea los que el usuario ya cumple
    // (suma sus puntos y le manda una notificación). Devuelve los nombres de los nuevos.
    List<String> revisarLogros(Usuario usuario);

    // Todos los logros con su estado (desbloqueado o no) y su avance
    List<MedallaDTO> medallas(Usuario usuario);

    // Días seguidos registrando progreso en retos
    int rachaDeRetos(int idUsuario);
}
