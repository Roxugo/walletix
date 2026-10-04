package pe.edu.upc.walletix.controllers;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.upc.walletix.dtos.CategoriaDTO;
import pe.edu.upc.walletix.entities.Categoria;
import pe.edu.upc.walletix.entities.Usuario;
import pe.edu.upc.walletix.servicesinterfaces.ICategoriaService;
import pe.edu.upc.walletix.servicesinterfaces.IUsuarioService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/categorias")
public class CategoriaController {
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


    @GetMapping
    public ResponseEntity<List<CategoriaDTO>> listar() {
        ModelMapper modelMapper = new ModelMapper();
        List<CategoriaDTO> listaCategorias = categoriaService.list().stream()
                .map(categoria -> modelMapper.map(categoria, CategoriaDTO.class))
                .collect(Collectors.toList());
        return ResponseEntity.ok(listaCategorias);
    }

    @PostMapping("/web")
    public ResponseEntity<?> registrar(@RequestBody CategoriaDTO categoriaDTO) {
        String error = validar(categoriaDTO);
        if (error != null) {
            return ResponseEntity.badRequest().body(error);
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
        Categoria categoriaRegistrada = categoriaService.insert(nuevaCategoria);
        return ResponseEntity.status(HttpStatus.CREATED).body(modelMapper.map(categoriaRegistrada, CategoriaDTO.class));
    }

    @GetMapping("/{idCategoria}")
    public ResponseEntity<?> buscarPorId(@PathVariable int idCategoria) {
        ModelMapper modelMapper = new ModelMapper();
        Optional<Categoria> categoria = categoriaService.listId(idCategoria);
        if (categoria.isPresent()) {
            return ResponseEntity.ok(modelMapper.map(categoria.get(), CategoriaDTO.class));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Categoría no encontrada");
        }
    }

    @PutMapping("/actualiza")
    public ResponseEntity<String> actualizar(@RequestBody CategoriaDTO categoriaDTO) {
        String error = validar(categoriaDTO);
        if (error != null) {
            return ResponseEntity.badRequest().body(error);
        }
        Optional<Categoria> categoriaExistente = categoriaService.listId(categoriaDTO.getIdCategoria());
        if (categoriaExistente.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Categoría no encontrada");
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
        categoria.setPredeterminadoCategoria(categoriaDTO.isPredeterminadoCategoria());
        categoriaService.update(categoria);
        return ResponseEntity.ok("Categoría actualizada correctamente");
    }

    @DeleteMapping("/{idCategoria}")
    public ResponseEntity<String> eliminar(@PathVariable int idCategoria) {
        Optional<Categoria> categoria = categoriaService.listId(idCategoria);
        if (categoria.isPresent()) {
            categoriaService.delete(idCategoria);
            return ResponseEntity.ok("Categoría eliminada correctamente");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Categoría no encontrada");
        }
    }

    // JPQL: categorías por tipo. Ej: /categorias/tipo/gasto
    @GetMapping("/tipo/{tipo}")
    public ResponseEntity<List<CategoriaDTO>> buscarPorTipo(@PathVariable String tipo) {
        ModelMapper modelMapper = new ModelMapper();
        List<CategoriaDTO> listaCategorias = categoriaService.buscarPorTipo(tipo).stream()
                .map(categoria -> modelMapper.map(categoria, CategoriaDTO.class))
                .collect(Collectors.toList());
        return ResponseEntity.ok(listaCategorias);
    }
}
