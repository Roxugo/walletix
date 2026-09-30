package pe.edu.upc.walletix.controllers;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.upc.walletix.dtos.NotificationDTO;
import pe.edu.upc.walletix.entities.Notifications;
import pe.edu.upc.walletix.entities.Users;
import pe.edu.upc.walletix.servicesinterfaces.INotificationService;
import pe.edu.upc.walletix.servicesinterfaces.IUserService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/notificaciones")
public class NotificationController {
    @Autowired
    private INotificationService nS;
    @Autowired
    private IUserService uS;

    @GetMapping
    public ResponseEntity<List<NotificationDTO>> listar(){
        ModelMapper m = new ModelMapper();
        List<NotificationDTO> lista = nS.list().stream()
                .map(y -> m.map(y, NotificationDTO.class))
                .collect(Collectors.toList());
        return ResponseEntity.ok(lista);
    }
    @PostMapping("/web")
    public ResponseEntity<?> registrar(@RequestBody NotificationDTO dto){
        Optional<Users> usuario = uS.listId(dto.getIdUser());
        if (usuario.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Usuario no encontrado");
        }

        ModelMapper m = new ModelMapper();
        Notifications n = m.map(dto, Notifications.class);
        n.setUser(usuario.get());
        Notifications cur = nS.insert(n);
        NotificationDTO responseDTO = m.map(cur, NotificationDTO.class);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }
    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable int id) {
        ModelMapper m = new ModelMapper();
        Optional<Notifications> n = nS.listId(id);
        if (n.isPresent()) {
            NotificationDTO dto = m.map(n.get(), NotificationDTO.class);
            return ResponseEntity.ok(dto);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Notificacion no encontrada");
        }
    }
    @PutMapping("/actualiza")
    public ResponseEntity<String> actualizar(@RequestBody NotificationDTO dto) {
        Optional<Notifications> existente = nS.listId(dto.getIdNotification());
        if (existente.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Notificacion no encontrada");
        }
        Optional<Users> usuario = uS.listId(dto.getIdUser());
        if (usuario.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Usuario no encontrado");
        }
        Notifications n = existente.get();
        n.setUser(usuario.get());
        n.setTypeNotification(dto.getTypeNotification());
        n.setTitleNotification(dto.getTitleNotification());
        n.setMessageNotification(dto.getMessageNotification());
        n.setReadNotification(dto.isReadNotification());
        nS.update(n);
        return ResponseEntity.ok("Notificacion actualizada correctamente");
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(@PathVariable int id) {
        Optional<Notifications> n = nS.listId(id);
        if (n.isPresent()) {
            nS.delete(id);
            return ResponseEntity.ok("Notificacion eliminada correctamente");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Notificacion no encontrada");
        }
    }
}