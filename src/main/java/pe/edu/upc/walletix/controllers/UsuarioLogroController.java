package pe.edu.upc.walletix.controllers;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.upc.walletix.dtos.UsuarioLogroDTO;
import pe.edu.upc.walletix.entities.Logro;
import pe.edu.upc.walletix.entities.Usuario;
import pe.edu.upc.walletix.entities.UsuarioLogro;
import pe.edu.upc.walletix.servicesinterfaces.IUsuarioLogroService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/usuarioslogros")
public class UsuarioLogroController {
    @Autowired
    private IUsuarioLogroService hS;

    @GetMapping
    public ResponseEntity<List<UsuarioLogroDTO>> listar(){
        ModelMapper m= new ModelMapper();
        List<UsuarioLogroDTO> listalogros =hS.list().stream()
                .map(y->m.map(y, UsuarioLogroDTO.class))
                .collect(Collectors.toList());
        return ResponseEntity.ok(listalogros);
    }
    @PostMapping("/web")
    public ResponseEntity<?> registrar(@RequestBody UsuarioLogroDTO dto){
        ModelMapper m=new ModelMapper();
        UsuarioLogro c=m.map(dto, UsuarioLogro.class);
        UsuarioLogro cur= hS.insert(c);
        UsuarioLogroDTO responseDTO=m.map(cur, UsuarioLogroDTO.class);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }
    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable int id) {
        ModelMapper m = new ModelMapper();
        Optional<UsuarioLogro> mach = hS.listId(id);
        if (mach.isPresent()) {
            UsuarioLogroDTO dto = m.map(mach.get(), UsuarioLogroDTO.class);
            return ResponseEntity.ok(dto);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Logro no encontrado");
        }
    }
    @PutMapping("/actualiza")
    public ResponseEntity<String> actualizar(@RequestBody UsuarioLogroDTO dto) {
        Optional<UsuarioLogro> existente = hS.listId(dto.getIdUsersAchiev());
        if (existente.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Registro no encontrado");
        }

        UsuarioLogro ah = existente.get();

        Usuario u = new Usuario();
        u.setIdUser(dto.getIdUser());
        ah.setUsers(u);

        Logro a = new Logro();
        a.setIdAchievement(dto.getIdAchievement());
        ah.setAchievement(a);

        hS.update(ah);
        return ResponseEntity.ok("Registro actualizado correctamente");
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(@PathVariable int id) {
        Optional<UsuarioLogro> usersAchiev = hS.listId(id);
        if (usersAchiev.isPresent()) {
            hS.delete(id);
            return ResponseEntity.ok("Registro eliminado correctamente");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Registro no encontrado");
        }
    }
}
