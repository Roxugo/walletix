package pe.edu.upc.walletix.controllers;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pe.edu.upc.walletix.dtos.GastoDTO;
import pe.edu.upc.walletix.entities.Categoria;
import pe.edu.upc.walletix.entities.Comerciante;
import pe.edu.upc.walletix.entities.Gasto;
import pe.edu.upc.walletix.entities.Usuario;
import pe.edu.upc.walletix.servicesinterfaces.ICategoriaService;
import pe.edu.upc.walletix.servicesinterfaces.IComercianteService;
import pe.edu.upc.walletix.servicesinterfaces.IGastoService;
import pe.edu.upc.walletix.servicesinterfaces.IUsuarioService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/gastos")
public class GastoController {
    @Autowired
    private IGastoService gastoService;
    @Autowired
    private IUsuarioService usuarioService;
    @Autowired
    private ICategoriaService categoriaService;
    @Autowired
    private IComercianteService comercianteService;

    private boolean estaVacio(String texto) {
        return texto == null || texto.isBlank();
    }

    private String validar(GastoDTO gastoDTO) {
        if (gastoDTO.getMontoGasto() == null || gastoDTO.getMontoGasto().compareTo(BigDecimal.ZERO) <= 0) {
            return "El monto debe ser mayor que cero";
        }
        if (gastoDTO.getFechaGasto() == null) {
            return "La fecha es obligatoria";
        }
        if (gastoDTO.getFechaGasto().isAfter(LocalDate.now())) {
            return "La fecha del gasto no puede ser futura";
        }
        if (estaVacio(gastoDTO.getDescripcionGasto())) {
            return "La descripción es obligatoria";
        }
        if (estaVacio(gastoDTO.getMetodoPagoGasto())) {
            return "El método de pago es obligatorio";
        }
        return null;
    }

    // ModelMapper no puede armar los ids de las relaciones solo, así que se completan a mano
    private GastoDTO convertirADTO(Gasto gasto) {
        GastoDTO gastoDTO = new GastoDTO();
        gastoDTO.setIdGasto(gasto.getIdGasto());
        gastoDTO.setIdUsuario(gasto.getUsuario().getIdUsuario());
        gastoDTO.setIdCategoria(gasto.getCategoria().getIdCategoria());
        gastoDTO.setIdComerciante(gasto.getComerciante().getIdComerciante());
        gastoDTO.setDescripcionGasto(gasto.getDescripcionGasto());
        gastoDTO.setMontoGasto(gasto.getMontoGasto());
        gastoDTO.setFechaGasto(gasto.getFechaGasto());
        gastoDTO.setMetodoPagoGasto(gasto.getMetodoPagoGasto());
        gastoDTO.setFijoGasto(gasto.isFijoGasto());
        return gastoDTO;
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USUARIO')")
    public ResponseEntity<List<GastoDTO>> listar() {
        List<GastoDTO> listaGastos = gastoService.list().stream()
                .map(gasto -> convertirADTO(gasto))
                .collect(Collectors.toList());
        return ResponseEntity.ok(listaGastos);
    }

    @PostMapping("/web")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USUARIO')")
    public ResponseEntity<?> registrar(@RequestBody GastoDTO gastoDTO) {
        String error = validar(gastoDTO);
        if (error != null) {
            return ResponseEntity.badRequest().body(error);
        }
        Optional<Usuario> usuario = usuarioService.listId(gastoDTO.getIdUsuario());
        if (usuario.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Usuario no encontrado");
        }
        Optional<Categoria> categoria = categoriaService.listId(gastoDTO.getIdCategoria());
        if (categoria.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Categoría no encontrada");
        }
        if (!categoria.get().getTipoCategoria().equals("gasto")) {
            return ResponseEntity.badRequest()
                    .body("La categoría debe ser de tipo gasto");
        }
        Optional<Comerciante> comerciante = comercianteService.listId(gastoDTO.getIdComerciante());
        if (comerciante.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Comercio no encontrado");
        }

        ModelMapper modelMapper = new ModelMapper();
        Gasto nuevoGasto = modelMapper.map(gastoDTO, Gasto.class);
        nuevoGasto.setUsuario(usuario.get());
        nuevoGasto.setCategoria(categoria.get());
        nuevoGasto.setComerciante(comerciante.get());
        Gasto gastoRegistrado = gastoService.insert(nuevoGasto);
        return ResponseEntity.status(HttpStatus.CREATED).body(convertirADTO(gastoRegistrado));
    }

    @GetMapping("/{idGasto}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USUARIO')")
    public ResponseEntity<?> buscarPorId(@PathVariable int idGasto) {
        Optional<Gasto> gasto = gastoService.listId(idGasto);
        if (gasto.isPresent()) {
            return ResponseEntity.ok(convertirADTO(gasto.get()));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Gasto no encontrado");
        }
    }

    @PutMapping("/actualiza")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USUARIO')")
    public ResponseEntity<String> actualizar(@RequestBody GastoDTO gastoDTO) {
        String error = validar(gastoDTO);
        if (error != null) {
            return ResponseEntity.badRequest().body(error);
        }
        Optional<Gasto> gastoExistente = gastoService.listId(gastoDTO.getIdGasto());
        if (gastoExistente.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Gasto no encontrado");
        }
        Optional<Categoria> categoria = categoriaService.listId(gastoDTO.getIdCategoria());
        if (categoria.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Categoría no encontrada");
        }
        if (!categoria.get().getTipoCategoria().equals("gasto")) {
            return ResponseEntity.badRequest()
                    .body("La categoría debe ser de tipo gasto");
        }
        Optional<Comerciante> comerciante = comercianteService.listId(gastoDTO.getIdComerciante());
        if (comerciante.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Comercio no encontrado");
        }
        // El dueño del gasto no cambia al editarlo
        Gasto gasto = gastoExistente.get();
        gasto.setCategoria(categoria.get());
        gasto.setComerciante(comerciante.get());
        gasto.setDescripcionGasto(gastoDTO.getDescripcionGasto());
        gasto.setMontoGasto(gastoDTO.getMontoGasto());
        gasto.setFechaGasto(gastoDTO.getFechaGasto());
        gasto.setMetodoPagoGasto(gastoDTO.getMetodoPagoGasto());
        gasto.setFijoGasto(gastoDTO.isFijoGasto());
        gastoService.update(gasto);
        return ResponseEntity.ok("Gasto actualizado correctamente");
    }

    @DeleteMapping("/{idGasto}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USUARIO')")
    public ResponseEntity<String> eliminar(@PathVariable int idGasto) {
        Optional<Gasto> gasto = gastoService.listId(idGasto);
        if (gasto.isPresent()) {
            gastoService.delete(idGasto);
            return ResponseEntity.ok("Gasto eliminado correctamente");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Gasto no encontrado");
        }
    }

    // JPQL: gastos de un usuario en un rango de fechas (historial, US18 y US19)
    // Ej: /gastos/usuario/1?fechaInicio=2026-10-01&fechaFin=2026-10-31
    @GetMapping("/usuario/{idUsuario}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USUARIO')")
    public ResponseEntity<?> buscarPorUsuarioYRango(@PathVariable int idUsuario,
                                                    @RequestParam LocalDate fechaInicio,
                                                    @RequestParam LocalDate fechaFin) {
        if (fechaInicio.isAfter(fechaFin)) {
            return ResponseEntity.badRequest()
                    .body("La fecha de inicio no puede ser posterior a la fecha de fin");
        }
        if (usuarioService.listId(idUsuario).isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Usuario no encontrado");
        }
        List<GastoDTO> listaGastos = gastoService.buscarPorUsuarioYRango(idUsuario, fechaInicio, fechaFin).stream()
                .map(gasto -> convertirADTO(gasto))
                .collect(Collectors.toList());
        return ResponseEntity.ok(listaGastos);
    }
}
