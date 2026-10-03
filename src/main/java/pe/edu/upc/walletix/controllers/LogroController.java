package pe.edu.upc.walletix.controllers;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.upc.walletix.dtos.LogroDTO;
import pe.edu.upc.walletix.entities.Logro;
import pe.edu.upc.walletix.servicesinterfaces.ILogroService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/logros")
public class LogroController {
    @Autowired
    private ILogroService logroService;

    @GetMapping
    public ResponseEntity<List<LogroDTO>> listar(){
        ModelMapper m= new ModelMapper();
        List<LogroDTO> listalogros = logroService.list().stream()
                .map(y->m.map(y, LogroDTO.class))
                .collect(Collectors.toList());
        return ResponseEntity.ok(listalogros);
    }
    @PostMapping("/web")
    public ResponseEntity<?> registrar(@RequestBody LogroDTO dto){
        ModelMapper m=new ModelMapper();
        Logro c=m.map(dto, Logro.class);
        Logro cur= logroService.insert(c);
        LogroDTO responseDTO=m.map(cur, LogroDTO.class);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }
    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable int id) {
        ModelMapper m = new ModelMapper();
        Optional<Logro> mach = logroService.listId(id);
        if (mach.isPresent()) {
            LogroDTO dto = m.map(mach.get(), LogroDTO.class);
            return ResponseEntity.ok(dto);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Logro no encontrado");
        }
    }
    @PutMapping("/actualiza")
    public ResponseEntity<String> actualizar(@RequestBody LogroDTO dto) {
        Optional<Logro> existente = logroService.listId(dto.getIdLogro());
        if (existente.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Logro no encontrado");
        }
        Logro ac = existente.get();
        ac.setNombreLogro(dto.getNombreLogro());
        ac.setDescripcionLogro(dto.getDescripcionLogro());
        ac.setUrlIconoLogro(dto.getUrlIconoLogro());
        ac.setPuntosLogro(dto.getPuntosLogro());
        logroService.update(ac);
        return ResponseEntity.ok("Logro actualizado correctamente");
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(@PathVariable int id) {
        Optional<Logro> achievement = logroService.listId(id);
        if (achievement.isPresent()) {
            logroService.delete(id);
            return ResponseEntity.ok("Logro eliminado correctamente");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Logro no encontrado");
        }
    }
}
