package pe.edu.upc.walletix.controllers;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.upc.walletix.dtos.UserDTO;
import pe.edu.upc.walletix.entities.Users;
import pe.edu.upc.walletix.servicesinterfaces.IUserService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/usuarios")
public class UserController {
    @Autowired
    private IUserService uS;

    @GetMapping
    public ResponseEntity<List<UserDTO>> listar(){
        ModelMapper m= new ModelMapper();
        List<UserDTO>listaUsuarios=uS.list().stream()
                .map(y->m.map(y,UserDTO.class))
                .collect(Collectors.toList());
        return ResponseEntity.ok(listaUsuarios);
    }
    @PostMapping("/web")
    public ResponseEntity<?> registrar(@RequestBody UserDTO dto){
        if (dto.getBirthdayUser().isAfter(java.time.LocalDate.now())) {
            return ResponseEntity.badRequest()
                    .body("La fecha de nacimiento no puede ser futura");
        }

        ModelMapper m=new ModelMapper();
        Users c=m.map(dto, Users.class);
        Users cur= uS.insert(c);
        UserDTO responseDTO=m.map(cur,UserDTO.class);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }
    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable int id) {
        ModelMapper m = new ModelMapper();
        Optional<Users> mach = uS.listId(id);
        if (mach.isPresent()) {
            UserDTO dto = m.map(mach.get(), UserDTO.class);
            return ResponseEntity.ok(dto);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Usuario no encontrado");
        }
    }
    @PutMapping("/actualiza")
    public ResponseEntity<String> actualizar(@RequestBody UserDTO dto) {
        if (dto.getBirthdayUser().isAfter(java.time.LocalDate.now())) {
            return ResponseEntity.badRequest()
                    .body("La fecha de nacimiento no puede ser futura");
        }
        Optional<Users> existente = uS.listId(dto.getIdUser());
        if (existente.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Usuario no encontrado");
        }
        Users us = existente.get();
        us.setNameUser(dto.getNameUser());
        us.setEmailUser(dto.getEmailUser());
        us.setPhoneUser(dto.getPhoneUser());
        us.setBirthdayUser(dto.getBirthdayUser());
        us.setSegmentUser(dto.getSegmentUser());
        us.setCurrentbalanceUser(dto.getCurrentbalanceUser());
        us.setGamificationpointsUser(dto.getGamificationpointsUser());
        uS.update(us);
        return ResponseEntity.ok("Usuario actualizado correctamente");
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(@PathVariable int id) {
        Optional<Users> machine = uS.listId(id);
        if (machine.isPresent()) {
            uS.delete(id);
            return ResponseEntity.ok("Usuario eliminado correctamente");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Usuario no encontrado");
        }
    }
}
