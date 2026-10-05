package pe.edu.upc.walletix.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pe.edu.upc.walletix.dtos.LogroDTO;
import pe.edu.upc.walletix.entities.Logro;
import pe.edu.upc.walletix.servicesinterfaces.ILogroService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Tag(name = "Logros", description = "Logros que los usuarios pueden obtener")
@RestController
@RequestMapping("/logros")
public class LogroController {
    @Autowired
    private ILogroService logroService;

    @Operation(summary = "Listar los logros activos")
    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USUARIO')")
    public ResponseEntity<List<LogroDTO>> listar(){
        ModelMapper m= new ModelMapper();
        List<LogroDTO> listalogros = logroService.list().stream()
                .map(y->m.map(y, LogroDTO.class))
                .collect(Collectors.toList());
        return ResponseEntity.ok(listalogros);
    }
    @Operation(summary = "Registrar un logro (solo ADMIN)")
    @PostMapping("/web")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> registrar(@RequestBody LogroDTO dto){
        ModelMapper m=new ModelMapper();
        Logro c=m.map(dto, Logro.class);
        c.setEstadoLogro(1); // Siempre nace en 1 al registrar
        Logro cur= logroService.insert(c);
        LogroDTO responseDTO=m.map(cur, LogroDTO.class);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }
    @Operation(summary = "Buscar un logro por su id")
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USUARIO')")
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
    @Operation(summary = "Actualizar los datos de un logro (solo ADMIN)")
    @PutMapping("/actualiza")
    @PreAuthorize("hasAuthority('ADMIN')")
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
    @Operation(summary = "Eliminar un logro (borrado lógico, solo ADMIN)")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
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
