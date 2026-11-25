package pe.edu.upc.walletix.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pe.edu.upc.walletix.dtos.MicroleccionDTO;
import pe.edu.upc.walletix.entities.Microleccion;
import pe.edu.upc.walletix.i18n.Mensajes;
import pe.edu.upc.walletix.servicesinterfaces.IMicroleccionService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Tag(name = "Microlecciones", description = "Lecciones cortas de educación financiera")
@RestController
@RequestMapping("/microlecciones")
public class MicroleccionController {
    @Autowired
    private Mensajes mensajes;
    @Autowired
    private IMicroleccionService microleccionService;

    private boolean estaVacio(String texto) {
        return texto == null || texto.isBlank();
    }

    private String validar(MicroleccionDTO microleccionDTO) {
        if (estaVacio(microleccionDTO.getTituloMicroleccion())) {
            return mensajes.get("titulo.obligatorio");
        }
        if (estaVacio(microleccionDTO.getDescripcionMicroleccion())) {
            return mensajes.get("descripcion.obligatoria");
        }
        if (estaVacio(microleccionDTO.getContenidoMicroleccion())) {
            return mensajes.get("contenido.obligatorio");
        }
        if (estaVacio(microleccionDTO.getUrlMediaMicroleccion())) {
            return mensajes.get("url.imagen.video.obligatoria");
        }
        if (estaVacio(microleccionDTO.getCategoriaEducativaMicroleccion())) {
            return mensajes.get("categoria.educativa.obligatoria");
        }
        if (microleccionDTO.getTipoMicroleccion() == null
                || !(microleccionDTO.getTipoMicroleccion().equalsIgnoreCase("tip") || microleccionDTO.getTipoMicroleccion().equalsIgnoreCase("leccion"))) {
            return mensajes.get("tipo.debe.ser.tip.leccion");
        }
        if (estaVacio(microleccionDTO.getTituloQuizMicroleccion())) {
            return mensajes.get("titulo.quiz.obligatorio");
        }
        if (microleccionDTO.getPuntajeAprobatorioMicroleccion() < 0 || microleccionDTO.getPuntajeAprobatorioMicroleccion() > 100) {
            return mensajes.get("puntaje.aprobatorio.debe.estar.entre");
        }
        if (microleccionDTO.getPuntosRecompensaMicroleccion() < 0) {
            return mensajes.get("puntos.recompensa.no.pueden.ser");
        }
        return null;
    }

    @Operation(summary = "Listar las microlecciones activas")
    @GetMapping
    // Catálogo para el ADMIN; el usuario usa /aprende (así no lee el contenido bloqueado)
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<MicroleccionDTO>> listar() {
        ModelMapper modelMapper = new ModelMapper();
        List<MicroleccionDTO> listaMicrolecciones = microleccionService.list().stream()
                .map(microleccion -> modelMapper.map(microleccion, MicroleccionDTO.class))
                .collect(Collectors.toList());
        return ResponseEntity.ok(listaMicrolecciones);
    }

    @Operation(summary = "Registrar una microlección (solo ADMIN)")
    @PostMapping("/web")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> registrar(@RequestBody MicroleccionDTO microleccionDTO) {
        String error = validar(microleccionDTO);
        if (error != null) {
            return ResponseEntity.badRequest().body(error);
        }
        ModelMapper modelMapper = new ModelMapper();
        microleccionDTO.setTipoMicroleccion(microleccionDTO.getTipoMicroleccion().toLowerCase());
        Microleccion nuevaMicroleccion = modelMapper.map(microleccionDTO, Microleccion.class);
        Microleccion microleccionRegistrada = microleccionService.insert(nuevaMicroleccion);
        return ResponseEntity.status(HttpStatus.CREATED).body(modelMapper.map(microleccionRegistrada, MicroleccionDTO.class));
    }

    @Operation(summary = "Buscar una microlección por su id")
    @GetMapping("/{idMicroleccion}")
    // Catálogo para el ADMIN; el usuario usa /aprende (así no lee el contenido bloqueado)
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> buscarPorId(@PathVariable int idMicroleccion) {
        ModelMapper modelMapper = new ModelMapper();
        Optional<Microleccion> microleccion = microleccionService.listId(idMicroleccion);
        if (microleccion.isPresent()) {
            return ResponseEntity.ok(modelMapper.map(microleccion.get(), MicroleccionDTO.class));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(mensajes.get("microleccion.no.encontrada"));
        }
    }

    @Operation(summary = "Actualizar los datos de una microlección (solo ADMIN)")
    @PutMapping("/actualiza")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<String> actualizar(@RequestBody MicroleccionDTO microleccionDTO) {
        String error = validar(microleccionDTO);
        if (error != null) {
            return ResponseEntity.badRequest().body(error);
        }
        Optional<Microleccion> microleccionExistente = microleccionService.listId(microleccionDTO.getIdMicroleccion());
        if (microleccionExistente.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(mensajes.get("microleccion.no.encontrada"));
        }
        Microleccion microleccion = microleccionExistente.get();
        microleccion.setTituloMicroleccion(microleccionDTO.getTituloMicroleccion());
        microleccion.setDescripcionMicroleccion(microleccionDTO.getDescripcionMicroleccion());
        microleccion.setContenidoMicroleccion(microleccionDTO.getContenidoMicroleccion());
        microleccion.setUrlMediaMicroleccion(microleccionDTO.getUrlMediaMicroleccion());
        microleccion.setCategoriaEducativaMicroleccion(microleccionDTO.getCategoriaEducativaMicroleccion());
        microleccion.setTipoMicroleccion(microleccionDTO.getTipoMicroleccion().toLowerCase());
        microleccion.setBloqueadoMicroleccion(microleccionDTO.isBloqueadoMicroleccion());
        microleccion.setTituloQuizMicroleccion(microleccionDTO.getTituloQuizMicroleccion());
        microleccion.setPuntajeAprobatorioMicroleccion(microleccionDTO.getPuntajeAprobatorioMicroleccion());
        microleccion.setPuntosRecompensaMicroleccion(microleccionDTO.getPuntosRecompensaMicroleccion());
        microleccionService.update(microleccion);
        return ResponseEntity.ok(mensajes.get("microleccion.actualizada.correctamente"));
    }

    @Operation(summary = "Eliminar una microlección (borrado lógico, solo ADMIN)")
    @DeleteMapping("/{idMicroleccion}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<String> eliminar(@PathVariable int idMicroleccion) {
        Optional<Microleccion> microleccion = microleccionService.listId(idMicroleccion);
        if (microleccion.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(mensajes.get("microleccion.no.encontrada"));
        }
        // No se elimina un catálogo del que todavía dependen registros activos
        if (microleccionService.tieneRegistrosActivos(idMicroleccion)) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(mensajes.get("no.puede.eliminar.microleccion.tiene"));
        }
        microleccionService.delete(idMicroleccion);
        return ResponseEntity.ok(mensajes.get("microleccion.eliminada.correctamente"));
    }

    // JPQL: consejos y microlecciones por categoría (US31). Ej: /microlecciones/categoria?categoria=Ahorro
    @Operation(summary = "Listar las microlecciones de una categoría educativa")
    @GetMapping("/categoria")
    // Catálogo para el ADMIN; el usuario usa /aprende (así no lee el contenido bloqueado)
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> buscarPorCategoria(@RequestParam("categoria") String categoria) {
        ModelMapper modelMapper = new ModelMapper();
        List<MicroleccionDTO> listaMicrolecciones = microleccionService.buscarPorCategoria(categoria).stream()
                .map(microleccion -> modelMapper.map(microleccion, MicroleccionDTO.class))
                .collect(Collectors.toList());
        if (listaMicrolecciones.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(mensajes.get("microleccion.categoria.sin.resultados", String.valueOf(categoria)));
        }
        return ResponseEntity.ok(listaMicrolecciones);
    }
}
