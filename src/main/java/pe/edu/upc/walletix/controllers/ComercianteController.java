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
import pe.edu.upc.walletix.dtos.ComercianteDTO;
import pe.edu.upc.walletix.entities.Categoria;
import pe.edu.upc.walletix.entities.Comerciante;
import pe.edu.upc.walletix.entities.Usuario;
import pe.edu.upc.walletix.i18n.Mensajes;
import pe.edu.upc.walletix.securities.UsuarioActual;
import pe.edu.upc.walletix.servicesinterfaces.ICategoriaService;
import pe.edu.upc.walletix.servicesinterfaces.IComercianteService;
import pe.edu.upc.walletix.servicesinterfaces.IUsuarioService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

// Comercios donde se hacen los gastos (US21). Mismo patrón que las categorías:
// - Predeterminados: los crea un ADMIN, no tienen dueño y los ven todos
// - Propios: los crea un cliente (por ejemplo, "Veterinaria Patitas") y solo los ve él
@Tag(name = "Comercios", description = "Comercios predeterminados (de todos) y propios de cada usuario")
@RestController
@RequestMapping("/comercios")
public class ComercianteController {
    // Ícono que se guarda cuando no se envía un logo (la app muestra el ícono de la categoría)
    private static final String LOGO_POR_DEFECTO = "storefront";

    @Autowired
    private Mensajes mensajes;
    @Autowired
    private IComercianteService comercianteService;
    @Autowired
    private ICategoriaService categoriaService;
    @Autowired
    private IUsuarioService usuarioService;
    @Autowired
    private UsuarioActual usuarioActual;

    private boolean estaVacio(String texto) {
        return texto == null || texto.isBlank();
    }

    private String validar(ComercianteDTO comercianteDTO) {
        if (estaVacio(comercianteDTO.getNombreComerciante())) {
            return mensajes.get("nombre.obligatorio");
        }
        return null;
    }

    private ComercianteDTO convertirADTO(Comerciante comerciante) {
        ComercianteDTO dto = new ModelMapper().map(comerciante, ComercianteDTO.class);
        dto.setIdCategoria(comerciante.getCategoria().getIdCategoria());
        dto.setIdUsuario(comerciante.esPredeterminado() ? null : comerciante.getUsuario().getIdUsuario());
        dto.setPredeterminadoComerciante(comerciante.esPredeterminado());
        return dto;
    }

    // Un USUARIO solo puede editar o eliminar sus comercios; un ADMIN, cualquiera
    private boolean puedeModificar(Authentication autenticacion, Comerciante comerciante) {
        return usuarioActual.esAdmin(autenticacion)
                || (!comerciante.esPredeterminado() && comerciante.getUsuario().getIdUsuario() == usuarioActual.id(autenticacion));
    }

    // La categoría del comercio: debe existir, ser de gasto y, para un cliente, ser predeterminada o suya.
    // Devuelve el error, o null si está bien.
    private ResponseEntity<String> revisarCategoria(Optional<Categoria> categoria, Authentication autenticacion) {
        if (categoria.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(mensajes.get("categoria.no.encontrada"));
        }
        if (!categoria.get().getTipoCategoria().equals("gasto")) {
            return ResponseEntity.badRequest().body(mensajes.get("categoria.debe.ser.tipo.gasto"));
        }
        if (!usuarioActual.esAdmin(autenticacion)
                && !usuarioActual.categoriaDisponible(categoria.get(), usuarioActual.id(autenticacion))) {
            return ResponseEntity.badRequest().body(mensajes.get("categoria.no.pertenece.usuario"));
        }
        return null;
    }

    @Operation(summary = "Listar los comercios activos (un cliente ve los predeterminados y los suyos; un ADMIN, todos)")
    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USUARIO')")
    public ResponseEntity<List<ComercianteDTO>> listar(Authentication autenticacion) {
        boolean esAdmin = usuarioActual.esAdmin(autenticacion);
        int idActual = usuarioActual.id(autenticacion);
        List<ComercianteDTO> listaComerciantes = comercianteService.list().stream()
                .filter(comerciante -> esAdmin || usuarioActual.comercioDisponible(comerciante, idActual))
                .map(this::convertirADTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(listaComerciantes);
    }

    @Operation(summary = "Registrar un comercio (de un ADMIN: predeterminado; de un cliente: propio)")
    @PostMapping("/web")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USUARIO')")
    public ResponseEntity<?> registrar(@RequestBody ComercianteDTO comercianteDTO, Authentication autenticacion) {
        String error = validar(comercianteDTO);
        if (error != null) {
            return ResponseEntity.badRequest().body(error);
        }
        Optional<Categoria> categoria = categoriaService.listId(comercianteDTO.getIdCategoria());
        ResponseEntity<String> errorCategoria = revisarCategoria(categoria, autenticacion);
        if (errorCategoria != null) {
            return errorCategoria;
        }
        // El dueño sale del token, no del JSON: así nadie crea comercios a nombre de otro
        int idActual = usuarioActual.id(autenticacion);
        boolean esAdmin = usuarioActual.esAdmin(autenticacion);
        if (comercianteService.existeNombre(comercianteDTO.getNombreComerciante(), idActual, 0)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(mensajes.get("comercio.duplicado"));
        }
        Comerciante nuevoComerciante = new Comerciante();
        nuevoComerciante.setCategoria(categoria.get());
        nuevoComerciante.setNombreComerciante(comercianteDTO.getNombreComerciante().trim());
        nuevoComerciante.setUrlLogoComerciante(estaVacio(comercianteDTO.getUrlLogoComerciante())
                ? LOGO_POR_DEFECTO : comercianteDTO.getUrlLogoComerciante());
        if (!esAdmin) {
            Optional<Usuario> dueno = usuarioService.listId(idActual);
            if (dueno.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(mensajes.get("usuario.no.encontrado"));
            }
            nuevoComerciante.setUsuario(dueno.get()); // comercio propio del cliente
        }
        Comerciante comercianteRegistrado = comercianteService.insert(nuevoComerciante);
        return ResponseEntity.status(HttpStatus.CREATED).body(convertirADTO(comercianteRegistrado));
    }

    @Operation(summary = "Buscar un comercio por su id")
    @GetMapping("/{idComerciante}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USUARIO')")
    public ResponseEntity<?> buscarPorId(@PathVariable int idComerciante, Authentication autenticacion) {
        Optional<Comerciante> comerciante = comercianteService.listId(idComerciante);
        if (comerciante.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(mensajes.get("comercio.no.encontrado"));
        }
        if (!usuarioActual.esAdmin(autenticacion)
                && !usuarioActual.comercioDisponible(comerciante.get(), usuarioActual.id(autenticacion))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(mensajes.get("no.tiene.permiso.sobre.datos"));
        }
        return ResponseEntity.ok(convertirADTO(comerciante.get()));
    }

    @Operation(summary = "Actualizar un comercio (un cliente solo los suyos; los predeterminados, solo un ADMIN)")
    @PutMapping("/actualiza")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USUARIO')")
    public ResponseEntity<String> actualizar(@RequestBody ComercianteDTO comercianteDTO, Authentication autenticacion) {
        String error = validar(comercianteDTO);
        if (error != null) {
            return ResponseEntity.badRequest().body(error);
        }
        Optional<Comerciante> comercianteExistente = comercianteService.listId(comercianteDTO.getIdComerciante());
        if (comercianteExistente.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(mensajes.get("comercio.no.encontrado"));
        }
        Comerciante comerciante = comercianteExistente.get();
        if (!puedeModificar(autenticacion, comerciante)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(mensajes.get("no.tiene.permiso.sobre.datos"));
        }
        Optional<Categoria> categoria = categoriaService.listId(comercianteDTO.getIdCategoria());
        ResponseEntity<String> errorCategoria = revisarCategoria(categoria, autenticacion);
        if (errorCategoria != null) {
            return errorCategoria;
        }
        // Los nombres se comparan con lo que ve el dueño del comercio (el ADMIN para los predeterminados)
        int idDueno = comerciante.esPredeterminado() ? usuarioActual.id(autenticacion) : comerciante.getUsuario().getIdUsuario();
        if (comercianteService.existeNombre(comercianteDTO.getNombreComerciante(), idDueno, comerciante.getIdComerciante())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(mensajes.get("comercio.duplicado"));
        }
        // El dueño no cambia al editar
        comerciante.setCategoria(categoria.get());
        comerciante.setNombreComerciante(comercianteDTO.getNombreComerciante().trim());
        if (!estaVacio(comercianteDTO.getUrlLogoComerciante())) {
            comerciante.setUrlLogoComerciante(comercianteDTO.getUrlLogoComerciante());
        }
        comercianteService.update(comerciante);
        return ResponseEntity.ok(mensajes.get("comercio.actualizado.correctamente"));
    }

    @Operation(summary = "Eliminar un comercio (borrado lógico; un cliente solo los suyos)")
    @DeleteMapping("/{idComerciante}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USUARIO')")
    public ResponseEntity<String> eliminar(@PathVariable int idComerciante, Authentication autenticacion) {
        Optional<Comerciante> comerciante = comercianteService.listId(idComerciante);
        if (comerciante.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(mensajes.get("comercio.no.encontrado"));
        }
        if (!puedeModificar(autenticacion, comerciante.get())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(mensajes.get("no.tiene.permiso.sobre.datos"));
        }
        // No se elimina un catálogo del que todavía dependen registros activos
        if (comercianteService.tieneRegistrosActivos(idComerciante)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(mensajes.get("no.puede.eliminar.comercio.tiene"));
        }
        comercianteService.delete(idComerciante);
        return ResponseEntity.ok(mensajes.get("comercio.eliminado.correctamente"));
    }
}
