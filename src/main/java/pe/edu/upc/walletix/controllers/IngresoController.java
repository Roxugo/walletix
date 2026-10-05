package pe.edu.upc.walletix.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pe.edu.upc.walletix.dtos.IngresoDto;
import pe.edu.upc.walletix.entities.Ingreso;
import pe.edu.upc.walletix.entities.Usuario;
import pe.edu.upc.walletix.entities.Categoria;
import pe.edu.upc.walletix.servicesinterfaces.IUsuarioService;
import pe.edu.upc.walletix.servicesinterfaces.ICategoriaService;
import pe.edu.upc.walletix.servicesinterfaces.IngresoServiceInterface;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Tag(name = "Ingresos", description = "Registro y consulta de los ingresos del usuario")
@RestController
@RequestMapping("/ingresos")
public class IngresoController {
    @Autowired
    private IngresoServiceInterface ingresoService;

    @Autowired
    private IUsuarioService usuarioService;

    @Autowired
    private ICategoriaService categoriaService;

    @Operation(summary = "Listar los ingresos activos")
    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USUARIO')")
    public ResponseEntity<List<IngresoDto>> listar() {
        ModelMapper mapper = new ModelMapper();
        List<IngresoDto> lista = ingresoService.listar().stream()
                .map(ingreso -> toDto(ingreso, mapper))
                .collect(Collectors.toList());
        return ResponseEntity.ok(lista);
    }

    @Operation(summary = "Registrar un ingreso")
    @PostMapping("/web")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USUARIO')")
    public ResponseEntity<?> registrar(@RequestBody IngresoDto dto) {
        String error = validar(dto);
        if (error != null) return ResponseEntity.badRequest().body(error);

        Optional<Usuario> usuario = usuarioService.listId(dto.getIdUsuario());
        if (usuario.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
        Optional<Categoria> categoria = categoriaService.listId(dto.getIdCategoria());
        if (categoria.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Categoría no encontrada");

        Ingreso ingreso = toEntity(dto);
        ingreso.setUsuario(usuario.get());
        ingreso.setCategoria(categoria.get());
        Ingreso registrado = ingresoService.registrar(ingreso);
        return ResponseEntity.status(HttpStatus.CREATED).body(toDto(registrado, new ModelMapper()));
    }

    @Operation(summary = "Buscar un ingreso por su id")
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USUARIO')")
    public ResponseEntity<?> buscarPorId(@PathVariable int id) {
        Optional<Ingreso> ingreso = ingresoService.buscarPorId(id);
        if (ingreso.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Ingreso no encontrado");
        return ResponseEntity.ok(toDto(ingreso.get(), new ModelMapper()));
    }

    @Operation(summary = "Actualizar los datos de un ingreso")
    @PutMapping("/actualiza")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USUARIO')")
    public ResponseEntity<String> actualizar(@RequestBody IngresoDto dto) {
        String error = validar(dto);
        if (error != null) return ResponseEntity.badRequest().body(error);

        Optional<Ingreso> existente = ingresoService.buscarPorId(dto.getIdIngreso());
        if (existente.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Ingreso no encontrado");
        Optional<Usuario> usuario = usuarioService.listId(dto.getIdUsuario());
        if (usuario.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
        Optional<Categoria> categoria = categoriaService.listId(dto.getIdCategoria());
        if (categoria.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Categoría no encontrada");

        Ingreso ingreso = existente.get();
        ingreso.setMonto(dto.getMonto());
        ingreso.setFecha(dto.getFecha());
        ingreso.setTipoIngreso(dto.getTipoIngreso());
        ingreso.setFrecuencia(dto.getFrecuencia());
        ingreso.setFuente(dto.getFuente());
        ingreso.setDescripcion(dto.getDescripcion());
        ingreso.setUsuario(usuario.get());
        ingreso.setCategoria(categoria.get());
        ingresoService.actualizar(ingreso);
        return ResponseEntity.ok("Ingreso actualizado correctamente");
    }

    @Operation(summary = "Eliminar un ingreso (borrado lógico)")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USUARIO')")
    public ResponseEntity<String> eliminar(@PathVariable int id) {
        if (ingresoService.buscarPorId(id).isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Ingreso no encontrado");
        }
        ingresoService.eliminar(id);
        return ResponseEntity.ok("Ingreso eliminado correctamente");
    }

    private Ingreso toEntity(IngresoDto dto) {
        Ingreso ingreso = new Ingreso();
        ingreso.setIdIngreso(dto.getIdIngreso());
        ingreso.setMonto(dto.getMonto());
        ingreso.setFecha(dto.getFecha());
        ingreso.setTipoIngreso(dto.getTipoIngreso());
        ingreso.setFrecuencia(dto.getFrecuencia());
        ingreso.setFuente(dto.getFuente());
        ingreso.setDescripcion(dto.getDescripcion());
        ingreso.setEstado(1); // Todo registro nuevo empieza activo
        return ingreso;
    }

    private IngresoDto toDto(Ingreso ingreso, ModelMapper mapper) {
        IngresoDto dto = mapper.map(ingreso, IngresoDto.class);
        if (ingreso.getUsuario() != null) dto.setIdUsuario(ingreso.getUsuario().getIdUsuario());
        if (ingreso.getCategoria() != null) dto.setIdCategoria(ingreso.getCategoria().getIdCategoria());
        return dto;
    }

    private String validar(IngresoDto dto) {
        if (dto == null) return "Se requiere información del ingreso";
        if (dto.getIdIngreso() < 0) return "El id del ingreso no puede ser negativo";
        if (dto.getMonto() <= 0) return "El monto debe ser mayor que cero";
        if (dto.getFecha() == null) return "Se requiere una fecha";
        if (dto.getTipoIngreso() == null || dto.getTipoIngreso().isBlank()) return "Se requiere el tipo de ingreso";
        if (dto.getFrecuencia() == null || dto.getFrecuencia().isBlank()) return "Se requiere la frecuencia";
        if (dto.getFuente() == null || dto.getFuente().isBlank()) return "Se requiere la fuente";
        if (dto.getIdUsuario() <= 0) return "Se requiere un id de usuario válido";
        if (dto.getIdCategoria() <= 0) return "Se requiere un id de categoría válido";
        return null;
    }
}
