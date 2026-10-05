package pe.edu.upc.walletix.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pe.edu.upc.walletix.dtos.PresupuestoDto;
import pe.edu.upc.walletix.entities.Presupuesto;
import pe.edu.upc.walletix.entities.Usuario;
import pe.edu.upc.walletix.entities.Categoria;
import pe.edu.upc.walletix.servicesinterfaces.IUsuarioService;
import pe.edu.upc.walletix.servicesinterfaces.ICategoriaService;
import pe.edu.upc.walletix.servicesinterfaces.PresupuestoServiceInterface;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Tag(name = "Presupuestos", description = "Presupuestos mensuales del usuario por categoría")
@RestController
@RequestMapping("/presupuestos")
public class PresupuestoController {
    @Autowired
    private PresupuestoServiceInterface presupuestoService;

    @Autowired
    private IUsuarioService usuarioService;

    @Autowired
    private ICategoriaService categoriaService;

    @Operation(summary = "Listar los presupuestos activos")
    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USUARIO')")
    public ResponseEntity<List<PresupuestoDto>> listar() {
        ModelMapper mapper = new ModelMapper();
        List<PresupuestoDto> lista = presupuestoService.listar().stream()
                .map(presupuesto -> toDto(presupuesto, mapper))
                .collect(Collectors.toList());
        return ResponseEntity.ok(lista);
    }

    @Operation(summary = "Registrar un presupuesto mensual")
    @PostMapping("/web")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USUARIO')")
    public ResponseEntity<?> registrar(@RequestBody PresupuestoDto dto) {
        String error = validar(dto);
        if (error != null) return ResponseEntity.badRequest().body(error);

        Optional<Usuario> usuario = usuarioService.listId(dto.getIdUsuario());
        if (usuario.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
        Optional<Categoria> categoria = categoriaService.listId(dto.getIdCategoria());
        if (categoria.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Categoría no encontrada");

        Presupuesto presupuesto = toEntity(dto);
        presupuesto.setUsuario(usuario.get());
        presupuesto.setCategoria(categoria.get());
        Presupuesto registrado = presupuestoService.registrar(presupuesto);
        return ResponseEntity.status(HttpStatus.CREATED).body(toDto(registrado, new ModelMapper()));
    }

    @Operation(summary = "Buscar un presupuesto por su id")
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USUARIO')")
    public ResponseEntity<?> buscarPorId(@PathVariable int id) {
        Optional<Presupuesto> presupuesto = presupuestoService.buscarPorId(id);
        if (presupuesto.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Presupuesto no encontrado");
        return ResponseEntity.ok(toDto(presupuesto.get(), new ModelMapper()));
    }

    @Operation(summary = "Actualizar los datos de un presupuesto")
    @PutMapping("/actualiza")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USUARIO')")
    public ResponseEntity<String> actualizar(@RequestBody PresupuestoDto dto) {
        String error = validar(dto);
        if (error != null) return ResponseEntity.badRequest().body(error);

        Optional<Presupuesto> existente = presupuestoService.buscarPorId(dto.getIdPresupuesto());
        if (existente.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Presupuesto no encontrado");
        Optional<Usuario> usuario = usuarioService.listId(dto.getIdUsuario());
        if (usuario.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
        Optional<Categoria> categoria = categoriaService.listId(dto.getIdCategoria());
        if (categoria.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Categoría no encontrada");

        Presupuesto presupuesto = existente.get();
        presupuesto.setMes(dto.getMes());
        presupuesto.setAnio(dto.getAnio());
        presupuesto.setMontoAsignado(dto.getMontoAsignado());
        presupuesto.setUsuario(usuario.get());
        presupuesto.setCategoria(categoria.get());
        presupuestoService.actualizar(presupuesto);
        return ResponseEntity.ok("Presupuesto actualizado correctamente");
    }

    @Operation(summary = "Eliminar un presupuesto (borrado lógico)")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USUARIO')")
    public ResponseEntity<String> eliminar(@PathVariable int id) {
        if (presupuestoService.buscarPorId(id).isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Presupuesto no encontrado");
        }
        presupuestoService.eliminar(id);
        return ResponseEntity.ok("Presupuesto eliminado correctamente");
    }

    private Presupuesto toEntity(PresupuestoDto dto) {
        Presupuesto presupuesto = new Presupuesto();
        presupuesto.setIdPresupuesto(dto.getIdPresupuesto());
        presupuesto.setMes(dto.getMes());
        presupuesto.setAnio(dto.getAnio());
        presupuesto.setMontoAsignado(dto.getMontoAsignado());
        presupuesto.setEstado(1); // Todo registro nuevo empieza activo
        return presupuesto;
    }

    private PresupuestoDto toDto(Presupuesto presupuesto, ModelMapper mapper) {
        PresupuestoDto dto = mapper.map(presupuesto, PresupuestoDto.class);
        if (presupuesto.getUsuario() != null) dto.setIdUsuario(presupuesto.getUsuario().getIdUsuario());
        if (presupuesto.getCategoria() != null) dto.setIdCategoria(presupuesto.getCategoria().getIdCategoria());
        return dto;
    }

    private String validar(PresupuestoDto dto) {
        if (dto == null) return "Se requiere información del presupuesto";
        if (dto.getIdPresupuesto() < 0) return "El id del presupuesto no puede ser negativo";
        if (dto.getMes() < 1 || dto.getMes() > 12) return "El mes debe estar entre 1 y 12";
        if (dto.getAnio() <= 0) return "El año debe ser positivo";
        if (dto.getMontoAsignado() <= 0) return "El monto asignado debe ser mayor que cero";
        if (dto.getIdUsuario() <= 0) return "Se requiere un id de usuario válido";
        if (dto.getIdCategoria() <= 0) return "Se requiere un id de categoría válido";
        return null;
    }
}
