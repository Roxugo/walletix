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
import pe.edu.upc.walletix.dtos.CategoriaDTO;
import pe.edu.upc.walletix.entities.Categoria;
import pe.edu.upc.walletix.entities.Usuario;
import pe.edu.upc.walletix.servicesinterfaces.ICategoriaService;
import pe.edu.upc.walletix.servicesinterfaces.IUsuarioService;
import pe.edu.upc.walletix.securities.UsuarioActual;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Tag(name = "Categorías", description = "Categorías de gastos e ingresos del usuario")
@RestController
@RequestMapping("/categorias")
public class CategoriaController {
    // Usuario que inició sesión: un USUARIO solo trabaja con sus datos, un ADMIN con todos
    @Autowired
    private UsuarioActual usuarioActual;
    @Autowired
    private ICategoriaService categoriaService;
    @Autowired
    private IUsuarioService usuarioService;

    private boolean estaVacio(String texto) {
        return texto == null || texto.isBlank();
    }

    private String validar(CategoriaDTO categoriaDTO) {
        if (estaVacio(categoriaDTO.getNombreCategoria())) {
            return "El nombre es obligatorio";
        }
        if (categoriaDTO.getTipoCategoria() == null
                || !(categoriaDTO.getTipoCategoria().equalsIgnoreCase("gasto") || categoriaDTO.getTipoCategoria().equalsIgnoreCase("ingreso"))) {
            return "El tipo debe ser gasto o ingreso";
        }
        if (estaVacio(categoriaDTO.getUrlIconoCategoria())) {
            return "La URL del ícono es obligatoria";
        }
        if (categoriaDTO.getColorHexCategoria() == null || !categoriaDTO.getColorHexCategoria().matches("#[0-9A-Fa-f]{6}")) {
            return "El color debe tener el formato #RRGGBB, por ejemplo #FF5733";
        }
        return null;
    }


    @Operation(summary = "Listar las categorías activas")
    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USUARIO')")
    public ResponseEntity<List<CategoriaDTO>> listar(Authentication autenticacion) {
        // Un USUARIO ve sus categorías y las predeterminadas; un ADMIN ve todas
        boolean esAdmin = usuarioActual.esAdmin(autenticacion);
        int idActual = usuarioActual.id(autenticacion);
        ModelMapper modelMapper = new ModelMapper();
        List<CategoriaDTO> listaCategorias = categoriaService.list().stream()
                .filter(categoria -> esAdmin || categoria.isPredeterminadoCategoria() || categoria.getUsuario().getIdUsuario() == idActual)
                .map(categoria -> modelMapper.map(categoria, CategoriaDTO.class))
                .collect(Collectors.toList());
        return ResponseEntity.ok(listaCategorias);
    }

    @Operation(summary = "Registrar una categoría (tipo gasto o ingreso)")
    @PostMapping("/web")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USUARIO')")
    public ResponseEntity<?> registrar(@RequestBody CategoriaDTO categoriaDTO, Authentication autenticacion) {
        String error = validar(categoriaDTO);
        if (error != null) {
            return ResponseEntity.badRequest().body(error);
        }
        if (!usuarioActual.puedeGestionar(autenticacion, categoriaDTO.getIdUsuario())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No tiene permiso sobre los datos de otro usuario");
        }
        Optional<Usuario> usuario = usuarioService.listId(categoriaDTO.getIdUsuario());
        if (usuario.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Usuario no encontrado");
        }
        ModelMapper modelMapper = new ModelMapper();
        categoriaDTO.setTipoCategoria(categoriaDTO.getTipoCategoria().toLowerCase());
        Categoria nuevaCategoria = modelMapper.map(categoriaDTO, Categoria.class);
        nuevaCategoria.setUsuario(usuario.get());
        nuevaCategoria.setEstadoCategoria(1); // Siempre nace en 1 al registrar
        // Solo un ADMIN crea categorías predeterminadas (las que usan todos los usuarios)
        if (!usuarioActual.esAdmin(autenticacion)) {
            nuevaCategoria.setPredeterminadoCategoria(false);
        }
        Categoria categoriaRegistrada = categoriaService.insert(nuevaCategoria);
        return ResponseEntity.status(HttpStatus.CREATED).body(modelMapper.map(categoriaRegistrada, CategoriaDTO.class));
    }

    @Operation(summary = "Buscar una categoría por su id")
    @GetMapping("/{idCategoria}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USUARIO')")
    public ResponseEntity<?> buscarPorId(@PathVariable int idCategoria, Authentication autenticacion) {
        ModelMapper modelMapper = new ModelMapper();
        Optional<Categoria> categoria = categoriaService.listId(idCategoria);
        if (categoria.isPresent()) {
            if (!categoria.get().isPredeterminadoCategoria() && !usuarioActual.puedeGestionar(autenticacion, categoria.get().getUsuario().getIdUsuario())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No tiene permiso sobre los datos de otro usuario");
            }
            return ResponseEntity.ok(modelMapper.map(categoria.get(), CategoriaDTO.class));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Categoría no encontrada");
        }
    }

    @Operation(summary = "Actualizar los datos de una categoría")
    @PutMapping("/actualiza")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USUARIO')")
    public ResponseEntity<String> actualizar(@RequestBody CategoriaDTO categoriaDTO, Authentication autenticacion) {
        String error = validar(categoriaDTO);
        if (error != null) {
            return ResponseEntity.badRequest().body(error);
        }
        Optional<Categoria> categoriaExistente = categoriaService.listId(categoriaDTO.getIdCategoria());
        if (categoriaExistente.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Categoría no encontrada");
        }
        // Las predeterminadas son del ADMIN, así que un USUARIO no puede editarlas
        if (!usuarioActual.puedeGestionar(autenticacion, categoriaExistente.get().getUsuario().getIdUsuario())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No tiene permiso sobre los datos de otro usuario");
        }
        if (!usuarioActual.puedeGestionar(autenticacion, categoriaDTO.getIdUsuario())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No tiene permiso sobre los datos de otro usuario");
        }
        Optional<Usuario> usuario = usuarioService.listId(categoriaDTO.getIdUsuario());
        if (usuario.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Usuario no encontrado");
        }
        Categoria categoria = categoriaExistente.get();
        categoria.setUsuario(usuario.get());
        categoria.setNombreCategoria(categoriaDTO.getNombreCategoria());
        categoria.setTipoCategoria(categoriaDTO.getTipoCategoria().toLowerCase());
        categoria.setUrlIconoCategoria(categoriaDTO.getUrlIconoCategoria());
        categoria.setColorHexCategoria(categoriaDTO.getColorHexCategoria());
        if (usuarioActual.esAdmin(autenticacion)) {
            categoria.setPredeterminadoCategoria(categoriaDTO.isPredeterminadoCategoria());
        }
        categoriaService.update(categoria);
        return ResponseEntity.ok("Categoría actualizada correctamente");
    }

    @Operation(summary = "Eliminar una categoría (borrado lógico)")
    @DeleteMapping("/{idCategoria}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USUARIO')")
    public ResponseEntity<String> eliminar(@PathVariable int idCategoria, Authentication autenticacion) {
        Optional<Categoria> categoria = categoriaService.listId(idCategoria);
        if (categoria.isPresent()) {
            if (!usuarioActual.puedeGestionar(autenticacion, categoria.get().getUsuario().getIdUsuario())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No tiene permiso sobre los datos de otro usuario");
            }
            // No se elimina un catálogo del que todavía dependen registros activos
            if (categoriaService.tieneRegistrosActivos(idCategoria)) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("No se puede eliminar: la categoría tiene comercios, gastos, ingresos o presupuestos activos");
            }
            categoriaService.delete(idCategoria);
            return ResponseEntity.ok("Categoría eliminada correctamente");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Categoría no encontrada");
        }
    }

    // JPQL: categorías por tipo. Ej: /categorias/tipo/gasto
    @Operation(summary = "Listar las categorías por tipo (gasto o ingreso)")
    @GetMapping("/tipo/{tipo}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USUARIO')")
    public ResponseEntity<List<CategoriaDTO>> buscarPorTipo(@PathVariable String tipo, Authentication autenticacion) {
        boolean esAdmin = usuarioActual.esAdmin(autenticacion);
        int idActual = usuarioActual.id(autenticacion);
        ModelMapper modelMapper = new ModelMapper();
        List<CategoriaDTO> listaCategorias = categoriaService.buscarPorTipo(tipo).stream()
                .filter(categoria -> esAdmin || categoria.isPredeterminadoCategoria() || categoria.getUsuario().getIdUsuario() == idActual)
                .map(categoria -> modelMapper.map(categoria, CategoriaDTO.class))
                .collect(Collectors.toList());
        return ResponseEntity.ok(listaCategorias);
    }
}
