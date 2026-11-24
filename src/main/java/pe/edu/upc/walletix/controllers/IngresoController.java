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
import pe.edu.upc.walletix.dtos.IngresoDto;
import pe.edu.upc.walletix.entities.Ingreso;
import pe.edu.upc.walletix.entities.Usuario;
import pe.edu.upc.walletix.entities.Categoria;
import pe.edu.upc.walletix.i18n.Mensajes;
import pe.edu.upc.walletix.servicesinterfaces.IUsuarioService;
import pe.edu.upc.walletix.servicesinterfaces.ICategoriaService;
import pe.edu.upc.walletix.servicesinterfaces.ISaldoService;
import pe.edu.upc.walletix.servicesinterfaces.IngresoServiceInterface;
import pe.edu.upc.walletix.securities.UsuarioActual;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Tag(name = "Ingresos", description = "Registro y consulta de los ingresos del usuario")
@RestController
@RequestMapping("/ingresos")
public class IngresoController {
    @Autowired
    private Mensajes mensajes;
    // Usuario que inició sesión: un USUARIO solo trabaja con sus datos, un ADMIN con todos
    @Autowired
    private UsuarioActual usuarioActual;
    @Autowired
    private IngresoServiceInterface ingresoService;

    @Autowired
    private IUsuarioService usuarioService;

    // US12: los ingresos suman al saldo del usuario
    @Autowired
    private ISaldoService saldoService;

    // El monto del ingreso es float; se pasa por texto para no arrastrar decimales de más (1500.1 -> 1500.1)
    private BigDecimal comoDecimal(float monto) {
        return new BigDecimal(Float.toString(monto));
    }

    @Autowired
    private ICategoriaService categoriaService;

    @Operation(summary = "Listar los ingresos activos")
    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USUARIO')")
    public ResponseEntity<List<IngresoDto>> listar(Authentication autenticacion) {
        // Un USUARIO solo ve sus propios registros; un ADMIN ve todos
        boolean esAdmin = usuarioActual.esAdmin(autenticacion);
        int idActual = usuarioActual.id(autenticacion);
        ModelMapper mapper = new ModelMapper();
        List<IngresoDto> lista = ingresoService.listar().stream()
                .filter(ingreso -> esAdmin || ingreso.getUsuario().getIdUsuario() == idActual)
                .map(ingreso -> toDto(ingreso, mapper))
                .collect(Collectors.toList());
        return ResponseEntity.ok(lista);
    }

    @Operation(summary = "Registrar un ingreso (suma al saldo del usuario)")
    @PostMapping("/web")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USUARIO')")
    @Transactional // el ingreso y el saldo se guardan juntos (todo o nada)
    public ResponseEntity<?> registrar(@RequestBody IngresoDto dto, Authentication autenticacion) {
        String error = validar(dto);
        if (error != null) return ResponseEntity.badRequest().body(error);
        if (!usuarioActual.puedeGestionar(autenticacion, dto.getIdUsuario())) return ResponseEntity.status(HttpStatus.FORBIDDEN).body(mensajes.get("no.tiene.permiso.sobre.datos"));

        Optional<Usuario> usuario = usuarioService.listId(dto.getIdUsuario());
        if (usuario.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).body(mensajes.get("usuario.no.encontrado"));
        Optional<Categoria> categoria = categoriaService.listId(dto.getIdCategoria());
        if (categoria.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).body(mensajes.get("categoria.no.encontrada"));
        if (!categoria.get().getTipoCategoria().equals("ingreso")) return ResponseEntity.badRequest().body(mensajes.get("categoria.debe.ser.tipo.ingreso"));
        if (!usuarioActual.categoriaDisponible(categoria.get(), dto.getIdUsuario())) return ResponseEntity.badRequest().body(mensajes.get("categoria.no.pertenece.usuario"));

        Ingreso ingreso = toEntity(dto);
        ingreso.setUsuario(usuario.get());
        ingreso.setCategoria(categoria.get());
        Ingreso registrado = ingresoService.registrar(ingreso);
        // US12: el ingreso se suma al saldo disponible
        saldoService.aplicar(usuario.get(), comoDecimal(registrado.getMonto()));
        return ResponseEntity.status(HttpStatus.CREATED).body(toDto(registrado, new ModelMapper()));
    }

    @Operation(summary = "Buscar un ingreso por su id")
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USUARIO')")
    public ResponseEntity<?> buscarPorId(@PathVariable int id, Authentication autenticacion) {
        Optional<Ingreso> ingreso = ingresoService.buscarPorId(id);
        if (ingreso.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).body(mensajes.get("ingreso.no.encontrado"));
        if (!usuarioActual.puedeGestionar(autenticacion, ingreso.get().getUsuario().getIdUsuario())) return ResponseEntity.status(HttpStatus.FORBIDDEN).body(mensajes.get("no.tiene.permiso.sobre.datos"));
        return ResponseEntity.ok(toDto(ingreso.get(), new ModelMapper()));
    }

    @Operation(summary = "Actualizar los datos de un ingreso (ajusta el saldo con la diferencia del monto)")
    @PutMapping("/actualiza")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USUARIO')")
    @Transactional
    public ResponseEntity<String> actualizar(@RequestBody IngresoDto dto, Authentication autenticacion) {
        String error = validar(dto);
        if (error != null) return ResponseEntity.badRequest().body(error);

        Optional<Ingreso> existente = ingresoService.buscarPorId(dto.getIdIngreso());
        if (existente.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).body(mensajes.get("ingreso.no.encontrado"));
        // Debe ser dueño del registro y no puede pasárselo a otro usuario
        if (!usuarioActual.puedeGestionar(autenticacion, existente.get().getUsuario().getIdUsuario())) return ResponseEntity.status(HttpStatus.FORBIDDEN).body(mensajes.get("no.tiene.permiso.sobre.datos"));
        if (!usuarioActual.puedeGestionar(autenticacion, dto.getIdUsuario())) return ResponseEntity.status(HttpStatus.FORBIDDEN).body(mensajes.get("no.tiene.permiso.sobre.datos"));
        Optional<Usuario> usuario = usuarioService.listId(dto.getIdUsuario());
        if (usuario.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).body(mensajes.get("usuario.no.encontrado"));
        Optional<Categoria> categoria = categoriaService.listId(dto.getIdCategoria());
        if (categoria.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).body(mensajes.get("categoria.no.encontrada"));
        if (!categoria.get().getTipoCategoria().equals("ingreso")) return ResponseEntity.badRequest().body(mensajes.get("categoria.debe.ser.tipo.ingreso"));
        if (!usuarioActual.categoriaDisponible(categoria.get(), dto.getIdUsuario())) return ResponseEntity.badRequest().body(mensajes.get("categoria.no.pertenece.usuario"));

        Ingreso ingreso = existente.get();
        // US12: se quita el monto anterior al dueño anterior y se suma el nuevo al dueño nuevo
        // (casi siempre es el mismo usuario; un ADMIN puede pasar el ingreso a otro)
        Usuario duenoAnterior = ingreso.getUsuario();
        BigDecimal montoAnterior = comoDecimal(ingreso.getMonto());
        BigDecimal montoNuevo = comoDecimal(dto.getMonto());
        boolean mismoDueno = duenoAnterior.getIdUsuario() == usuario.get().getIdUsuario();
        BigDecimal cambioAnterior = mismoDueno ? montoNuevo.subtract(montoAnterior) : montoAnterior.negate();
        if (!saldoService.alcanza(duenoAnterior, cambioAnterior)) {
            return ResponseEntity.badRequest().body(mensajes.get("saldo.insuficiente.ingreso"));
        }
        saldoService.aplicar(duenoAnterior, cambioAnterior);
        if (!mismoDueno) {
            saldoService.aplicar(usuario.get(), montoNuevo);
        }
        ingreso.setMonto(dto.getMonto());
        ingreso.setFecha(dto.getFecha());
        ingreso.setTipoIngreso(dto.getTipoIngreso());
        ingreso.setFrecuencia(dto.getFrecuencia());
        ingreso.setFuente(dto.getFuente());
        ingreso.setDescripcion(dto.getDescripcion());
        ingreso.setUsuario(usuario.get());
        ingreso.setCategoria(categoria.get());
        ingresoService.actualizar(ingreso);
        return ResponseEntity.ok(mensajes.get("ingreso.actualizado.correctamente"));
    }

    @Operation(summary = "Eliminar un ingreso (borrado lógico; el monto se descuenta del saldo)")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USUARIO')")
    @Transactional
    public ResponseEntity<String> eliminar(@PathVariable int id, Authentication autenticacion) {
        Optional<Ingreso> ingreso = ingresoService.buscarPorId(id);
        if (ingreso.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(mensajes.get("ingreso.no.encontrado"));
        }
        if (!usuarioActual.puedeGestionar(autenticacion, ingreso.get().getUsuario().getIdUsuario())) return ResponseEntity.status(HttpStatus.FORBIDDEN).body(mensajes.get("no.tiene.permiso.sobre.datos"));
        // Si ese dinero ya se gastó, eliminar el ingreso dejaría el saldo en negativo
        BigDecimal monto = comoDecimal(ingreso.get().getMonto()).negate();
        if (!saldoService.alcanza(ingreso.get().getUsuario(), monto)) {
            return ResponseEntity.badRequest().body(mensajes.get("saldo.insuficiente.ingreso"));
        }
        saldoService.aplicar(ingreso.get().getUsuario(), monto);
        ingresoService.eliminar(id);
        return ResponseEntity.ok(mensajes.get("ingreso.eliminado.correctamente"));
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
        if (dto == null) return mensajes.get("requiere.informacion.ingreso");
        if (dto.getIdIngreso() < 0) return mensajes.get("id.ingreso.no.puede.ser");
        if (dto.getMonto() <= 0) return mensajes.get("monto.debe.ser.mayor.cero");
        if (dto.getFecha() == null) return mensajes.get("requiere.fecha");
        if (dto.getTipoIngreso() == null || dto.getTipoIngreso().isBlank()) return mensajes.get("requiere.tipo.ingreso");
        if (dto.getFrecuencia() == null || dto.getFrecuencia().isBlank()) return mensajes.get("requiere.frecuencia");
        if (dto.getFuente() == null || dto.getFuente().isBlank()) return mensajes.get("requiere.fuente");
        if (dto.getIdUsuario() <= 0) return mensajes.get("requiere.id.usuario.valido");
        if (dto.getIdCategoria() <= 0) return mensajes.get("requiere.id.categoria.valido");
        return null;
    }
}
