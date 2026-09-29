package pe.edu.upc.walletix.controllers;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.upc.walletix.dtos.CategoryDTO;
import pe.edu.upc.walletix.entities.Category;
import pe.edu.upc.walletix.servicesinterfaces.ICategoryService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/categorias")
public class CategoryController {
    @Autowired
    private ICategoryService cS;

    @GetMapping
    public ResponseEntity<List<CategoryDTO>> listar() {
        ModelMapper m = new ModelMapper();
        List<CategoryDTO> listaCategorias = cS.list().stream()
                .map(y -> m.map(y, CategoryDTO.class))
                .collect(Collectors.toList());
        return ResponseEntity.ok(listaCategorias);
    }

    @PostMapping("/web")
    public ResponseEntity<?> registrar(@RequestBody CategoryDTO dto) {
        ModelMapper m = new ModelMapper();
        Category c = m.map(dto, Category.class);
        Category cur = cS.insert(c);
        CategoryDTO responseDTO = m.map(cur, CategoryDTO.class);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable int id) {
        ModelMapper m = new ModelMapper();
        Optional<Category> mach = cS.listId(id);
        if (mach.isPresent()) {
            CategoryDTO dto = m.map(mach.get(), CategoryDTO.class);
            return ResponseEntity.ok(dto);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Categoría no encontrada");
        }
    }

    @PutMapping("/actualiza")
    public ResponseEntity<String> actualizar(@RequestBody CategoryDTO dto) {
        Optional<Category> existente = cS.listId(dto.getIdCategory());
        if (existente.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Categoría no encontrada");
        }
        Category c = existente.get();
        c.setUserIdCategory(dto.getUserIdCategory());
        c.setNameCategory(dto.getNameCategory());
        c.setTypeCategory(dto.getTypeCategory());
        c.setIconUrlCategory(dto.getIconUrlCategory());
        c.setColorHexCategory(dto.getColorHexCategory());
        c.setDefaultCategory(dto.isDefaultCategory());
        cS.update(c);
        return ResponseEntity.ok("Categoría actualizada correctamente");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(@PathVariable int id) {
        Optional<Category> machine = cS.listId(id);
        if (machine.isPresent()) {
            cS.delete(id);
            return ResponseEntity.ok("Categoría eliminada correctamente");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Categoría no encontrada");
        }
    }
}
