package pe.edu.upc.walletix.controllers;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.upc.walletix.dtos.UsersAchievDTO;
import pe.edu.upc.walletix.entities.Achievement;
import pe.edu.upc.walletix.entities.Usuario;
import pe.edu.upc.walletix.entities.UsersAchiev;
import pe.edu.upc.walletix.servicesinterfaces.IUsersAchievService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/usuarioslogros")
public class UsersAchievController {
    @Autowired
    private IUsersAchievService hS;

    @GetMapping
    public ResponseEntity<List<UsersAchievDTO>> listar(){
        ModelMapper m= new ModelMapper();
        List<UsersAchievDTO> listalogros =hS.list().stream()
                .map(y->m.map(y,UsersAchievDTO.class))
                .collect(Collectors.toList());
        return ResponseEntity.ok(listalogros);
    }
    @PostMapping("/web")
    public ResponseEntity<?> registrar(@RequestBody UsersAchievDTO dto){
        ModelMapper m=new ModelMapper();
        UsersAchiev c=m.map(dto, UsersAchiev.class);
        UsersAchiev cur= hS.insert(c);
        UsersAchievDTO responseDTO=m.map(cur,UsersAchievDTO.class);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }
    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable int id) {
        ModelMapper m = new ModelMapper();
        Optional<UsersAchiev> mach = hS.listId(id);
        if (mach.isPresent()) {
            UsersAchievDTO dto = m.map(mach.get(), UsersAchievDTO.class);
            return ResponseEntity.ok(dto);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Logro no encontrado");
        }
    }
    @PutMapping("/actualiza")
    public ResponseEntity<String> actualizar(@RequestBody UsersAchievDTO dto) {
        Optional<UsersAchiev> existente = hS.listId(dto.getIdUsersAchiev());
        if (existente.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Registro no encontrado");
        }

        UsersAchiev ah = existente.get();

        Usuario u = new Usuario();
        u.setIdUser(dto.getIdUser());
        ah.setUsers(u);

        Achievement a = new Achievement();
        a.setIdAchievement(dto.getIdAchievement());
        ah.setAchievement(a);

        hS.update(ah);
        return ResponseEntity.ok("Registro actualizado correctamente");
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(@PathVariable int id) {
        Optional<UsersAchiev> usersAchiev = hS.listId(id);
        if (usersAchiev.isPresent()) {
            hS.delete(id);
            return ResponseEntity.ok("Registro eliminado correctamente");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Registro no encontrado");
        }
    }
}
