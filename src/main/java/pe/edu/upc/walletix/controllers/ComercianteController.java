package pe.edu.upc.walletix.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pe.edu.upc.walletix.dtos.ComercianteDTO;
import pe.edu.upc.walletix.entities.Categoria;
import pe.edu.upc.walletix.entities.Comerciante;
import pe.edu.upc.walletix.servicesinterfaces.ICategoriaService;
import pe.edu.upc.walletix.servicesinterfaces.IComercianteService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Tag(name = "Comercios", description = "Comercios donde se realizan los gastos")
@RestController
@RequestMapping("/comercios")
public class ComercianteController {
    @Autowired
    private IComercianteService comercianteService;
    @Autowired
    private ICategoriaService categoriaService;

    private boolean estaVacio(String texto) {
        return texto == null || texto.isBlank();
    }

    private String validar(ComercianteDTO comercianteDTO) {
        if (estaVacio(comercianteDTO.getNombreComerciante())) {
            return "El nombre es obligatorio";
        }
        if (estaVacio(comercianteDTO.getUrlLogoComerciante())) {
            return "La URL del logo es obligatoria";
        }
        return null;
    }


    @Operation(summary = "Listar los comercios activos")
    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USUARIO')")
    public ResponseEntity<List<ComercianteDTO>> listar() {
        ModelMapper modelMapper = new ModelMapper();
        List<ComercianteDTO> listaComerciantes = comercianteService.list().stream()
                .map(comerciante -> modelMapper.map(comerciante, ComercianteDTO.class))
                .collect(Collectors.toList());
        return ResponseEntity.ok(listaComerciantes);
    }

    @Operation(summary = "Registrar un comercio (solo ADMIN)")
    @PostMapping("/web")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> registrar(@RequestBody ComercianteDTO comercianteDTO) {
        String error = validar(comercianteDTO);
        if (error != null) {
            return ResponseEntity.badRequest().body(error);
        }
        Optional<Categoria> categoria = categoriaService.listId(comercianteDTO.getIdCategoria());
        if (categoria.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Categoría no encontrada");
        }
        ModelMapper modelMapper = new ModelMapper();
        Comerciante nuevoComerciante = modelMapper.map(comercianteDTO, Comerciante.class);
        nuevoComerciante.setCategoria(categoria.get());
        nuevoComerciante.setEstadoComerciante(1); // Siempre nace en 1 al registrar
        Comerciante comercianteRegistrado = comercianteService.insert(nuevoComerciante);
        return ResponseEntity.status(HttpStatus.CREATED).body(modelMapper.map(comercianteRegistrado, ComercianteDTO.class));
    }

    @Operation(summary = "Buscar un comercio por su id")
    @GetMapping("/{idComerciante}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USUARIO')")
    public ResponseEntity<?> buscarPorId(@PathVariable int idComerciante) {
        ModelMapper modelMapper = new ModelMapper();
        Optional<Comerciante> comerciante = comercianteService.listId(idComerciante);
        if (comerciante.isPresent()) {
            return ResponseEntity.ok(modelMapper.map(comerciante.get(), ComercianteDTO.class));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Comercio no encontrado");
        }
    }

    @Operation(summary = "Actualizar los datos de un comercio (solo ADMIN)")
    @PutMapping("/actualiza")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<String> actualizar(@RequestBody ComercianteDTO comercianteDTO) {
        String error = validar(comercianteDTO);
        if (error != null) {
            return ResponseEntity.badRequest().body(error);
        }
        Optional<Comerciante> comercianteExistente = comercianteService.listId(comercianteDTO.getIdComerciante());
        if (comercianteExistente.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Comercio no encontrado");
        }
        Optional<Categoria> categoria = categoriaService.listId(comercianteDTO.getIdCategoria());
        if (categoria.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Categoría no encontrada");
        }
        Comerciante comerciante = comercianteExistente.get();
        comerciante.setCategoria(categoria.get());
        comerciante.setNombreComerciante(comercianteDTO.getNombreComerciante());
        comerciante.setUrlLogoComerciante(comercianteDTO.getUrlLogoComerciante());
        comercianteService.update(comerciante);
        return ResponseEntity.ok("Comercio actualizado correctamente");
    }

    @Operation(summary = "Eliminar un comercio (borrado lógico, solo ADMIN)")
    @DeleteMapping("/{idComerciante}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<String> eliminar(@PathVariable int idComerciante) {
        Optional<Comerciante> comerciante = comercianteService.listId(idComerciante);
        if (comerciante.isPresent()) {
            // No se elimina un catálogo del que todavía dependen registros activos
            if (comercianteService.tieneRegistrosActivos(idComerciante)) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("No se puede eliminar: el comercio tiene gastos activos");
            }
            comercianteService.delete(idComerciante);
            return ResponseEntity.ok("Comercio eliminado correctamente");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Comercio no encontrado");
        }
    }
}
