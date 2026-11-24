package pe.edu.upc.walletix.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pe.edu.upc.walletix.dtos.PresupuestoDto;
import pe.edu.upc.walletix.entities.Presupuesto;
import pe.edu.upc.walletix.entities.Usuario;
import pe.edu.upc.walletix.entities.Categoria;
import pe.edu.upc.walletix.i18n.Mensajes;
import pe.edu.upc.walletix.servicesinterfaces.IUsuarioService;
import pe.edu.upc.walletix.servicesinterfaces.ICategoriaService;
import pe.edu.upc.walletix.servicesinterfaces.PresupuestoServiceInterface;
import pe.edu.upc.walletix.securities.UsuarioActual;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Tag(name = "Presupuestos", description = "Presupuestos mensuales del usuario por categoría")
@RestController
@RequestMapping("/presupuestos")
public class PresupuestoController {
    @Autowired
    private Mensajes mensajes;
    // Usuario que inició sesión: un USUARIO solo trabaja con sus datos, un ADMIN con todos
    @Autowired
    private UsuarioActual usuarioActual;
    @Autowired
    private PresupuestoServiceInterface presupuestoService;

    @Autowired
    private IUsuarioService usuarioService;

    @Autowired
    private ICategoriaService categoriaService;

    @Operation(summary = "Listar los presupuestos activos")
    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USUARIO')")
    public ResponseEntity<List<PresupuestoDto>> listar(Authentication autenticacion) {
        // Un USUARIO solo ve sus propios registros; un ADMIN ve todos
        boolean esAdmin = usuarioActual.esAdmin(autenticacion);
        int idActual = usuarioActual.id(autenticacion);
        ModelMapper mapper = new ModelMapper();
        List<PresupuestoDto> lista = presupuestoService.listar().stream()
                .filter(presupuesto -> esAdmin || presupuesto.getUsuario().getIdUsuario() == idActual)
                .map(presupuesto -> toDto(presupuesto, mapper))
                .collect(Collectors.toList());
        return ResponseEntity.ok(lista);
    }

    @Operation(summary = "Registrar un presupuesto mensual")
    @PostMapping("/web")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USUARIO')")
    public ResponseEntity<?> registrar(@RequestBody PresupuestoDto dto, Authentication autenticacion) {
        String error = validar(dto);
        if (error != null) return ResponseEntity.badRequest().body(error);
        if (!usuarioActual.puedeGestionar(autenticacion, dto.getIdUsuario())) return ResponseEntity.status(HttpStatus.FORBIDDEN).body(mensajes.get("no.tiene.permiso.sobre.datos"));

        Optional<Usuario> usuario = usuarioService.listId(dto.getIdUsuario());
        if (usuario.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).body(mensajes.get("usuario.no.encontrado"));
        Optional<Categoria> categoria = categoriaService.listId(dto.getIdCategoria());
        if (categoria.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).body(mensajes.get("categoria.no.encontrada"));
        if (!categoria.get().getTipoCategoria().equals("gasto")) return ResponseEntity.badRequest().body(mensajes.get("categoria.debe.ser.tipo.gasto"));
        if (!usuarioActual.categoriaDisponible(categoria.get(), dto.getIdUsuario())) return ResponseEntity.badRequest().body(mensajes.get("categoria.no.pertenece.usuario"));
        // Un solo presupuesto por categoría en cada mes (0 = no hay presupuesto que excluir porque es nuevo)
        if (presupuestoService.existeDuplicado(dto.getIdUsuario(), dto.getIdCategoria(), dto.getMes(), dto.getAnio(), 0)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(mensajes.get("presupuesto.duplicado"));
        }

        Presupuesto presupuesto = toEntity(dto);
        presupuesto.setUsuario(usuario.get());
        presupuesto.setCategoria(categoria.get());
        Presupuesto registrado = presupuestoService.registrar(presupuesto);
        return ResponseEntity.status(HttpStatus.CREATED).body(toDto(registrado, new ModelMapper()));
    }

    @Operation(summary = "Buscar un presupuesto por su id")
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USUARIO')")
    public ResponseEntity<?> buscarPorId(@PathVariable int id, Authentication autenticacion) {
        Optional<Presupuesto> presupuesto = presupuestoService.buscarPorId(id);
        if (presupuesto.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).body(mensajes.get("presupuesto.no.encontrado"));
        if (!usuarioActual.puedeGestionar(autenticacion, presupuesto.get().getUsuario().getIdUsuario())) return ResponseEntity.status(HttpStatus.FORBIDDEN).body(mensajes.get("no.tiene.permiso.sobre.datos"));
        return ResponseEntity.ok(toDto(presupuesto.get(), new ModelMapper()));
    }

    @Operation(summary = "Actualizar los datos de un presupuesto")
    @PutMapping("/actualiza")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USUARIO')")
    public ResponseEntity<String> actualizar(@RequestBody PresupuestoDto dto, Authentication autenticacion) {
        String error = validar(dto);
        if (error != null) return ResponseEntity.badRequest().body(error);

        Optional<Presupuesto> existente = presupuestoService.buscarPorId(dto.getIdPresupuesto());
        if (existente.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).body(mensajes.get("presupuesto.no.encontrado"));
        // Debe ser dueño del registro y no puede pasárselo a otro usuario
        if (!usuarioActual.puedeGestionar(autenticacion, existente.get().getUsuario().getIdUsuario())) return ResponseEntity.status(HttpStatus.FORBIDDEN).body(mensajes.get("no.tiene.permiso.sobre.datos"));
        if (!usuarioActual.puedeGestionar(autenticacion, dto.getIdUsuario())) return ResponseEntity.status(HttpStatus.FORBIDDEN).body(mensajes.get("no.tiene.permiso.sobre.datos"));
        Optional<Usuario> usuario = usuarioService.listId(dto.getIdUsuario());
        if (usuario.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).body(mensajes.get("usuario.no.encontrado"));
        Optional<Categoria> categoria = categoriaService.listId(dto.getIdCategoria());
        if (categoria.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).body(mensajes.get("categoria.no.encontrada"));
        if (!categoria.get().getTipoCategoria().equals("gasto")) return ResponseEntity.badRequest().body(mensajes.get("categoria.debe.ser.tipo.gasto"));
        if (!usuarioActual.categoriaDisponible(categoria.get(), dto.getIdUsuario())) return ResponseEntity.badRequest().body(mensajes.get("categoria.no.pertenece.usuario"));
        // Al editar se ignora este mismo presupuesto; solo choca con OTRO de la misma categoría y mes
        if (presupuestoService.existeDuplicado(dto.getIdUsuario(), dto.getIdCategoria(), dto.getMes(), dto.getAnio(), dto.getIdPresupuesto())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(mensajes.get("presupuesto.duplicado"));
        }

        Presupuesto presupuesto = existente.get();
        presupuesto.setMes(dto.getMes());
        presupuesto.setAnio(dto.getAnio());
        presupuesto.setMontoAsignado(dto.getMontoAsignado());
        presupuesto.setUsuario(usuario.get());
        presupuesto.setCategoria(categoria.get());
        presupuestoService.actualizar(presupuesto);
        return ResponseEntity.ok(mensajes.get("presupuesto.actualizado.correctamente"));
    }

    @Operation(summary = "Eliminar un presupuesto (borrado lógico)")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USUARIO')")
    public ResponseEntity<String> eliminar(@PathVariable int id, Authentication autenticacion) {
        if (presupuestoService.buscarPorId(id).isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(mensajes.get("presupuesto.no.encontrado"));
        }
        if (!usuarioActual.puedeGestionar(autenticacion, presupuestoService.buscarPorId(id).get().getUsuario().getIdUsuario())) return ResponseEntity.status(HttpStatus.FORBIDDEN).body(mensajes.get("no.tiene.permiso.sobre.datos"));
        presupuestoService.eliminar(id);
        return ResponseEntity.ok(mensajes.get("presupuesto.eliminado.correctamente"));
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

    // Límites de un presupuesto: desde el año actual hasta 5 años después, y hasta S/ 1 000 000
    private static final int ANIOS_A_FUTURO = 5;
    private static final float MONTO_MAXIMO = 1_000_000f;

    private String validar(PresupuestoDto dto) {
        if (dto == null) return mensajes.get("requiere.informacion.presupuesto");
        if (dto.getIdPresupuesto() < 0) return mensajes.get("id.presupuesto.no.puede.ser");
        if (dto.getMes() < 1 || dto.getMes() > 12) return mensajes.get("mes.debe.estar.entre.1");
        int anioActual = LocalDate.now().getYear();
        if (dto.getAnio() < anioActual || dto.getAnio() > anioActual + ANIOS_A_FUTURO) {
            return mensajes.get("anio.presupuesto.fuera.de.rango",
                    String.valueOf(anioActual), String.valueOf(anioActual + ANIOS_A_FUTURO));
        }
        if (dto.getMontoAsignado() <= 0) return mensajes.get("monto.asignado.debe.ser.mayor");
        if (dto.getMontoAsignado() > MONTO_MAXIMO) return mensajes.get("monto.asignado.maximo");
        if (dto.getIdUsuario() <= 0) return mensajes.get("requiere.id.usuario.valido");
        if (dto.getIdCategoria() <= 0) return mensajes.get("requiere.id.categoria.valido");
        return null;
    }
}
