package pe.edu.upc.walletix.controllers;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.upc.walletix.dtos.LogroPopularidadDTO;
import pe.edu.upc.walletix.dtos.UsuarioLogroDTO;
import pe.edu.upc.walletix.dtos.UsuarioLogrosCountDTO;
import pe.edu.upc.walletix.entities.Logro;
import pe.edu.upc.walletix.entities.Usuario;
import pe.edu.upc.walletix.entities.UsuarioLogro;
import pe.edu.upc.walletix.servicesinterfaces.ILogroService;
import pe.edu.upc.walletix.servicesinterfaces.IUsuarioLogroService;
import pe.edu.upc.walletix.servicesinterfaces.IUsuarioService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/usuarioslogros")
public class UsuarioLogroController {
    @Autowired
    private IUsuarioLogroService usuariologroService;

    @Autowired
    private IUsuarioService usuarioService;

    @Autowired
    private ILogroService logroService;

    @GetMapping
    public ResponseEntity<List<UsuarioLogroDTO>> listar(){
        ModelMapper m= new ModelMapper();
        List<UsuarioLogroDTO> listalogros =usuariologroService.list().stream()
                .map(y->m.map(y, UsuarioLogroDTO.class))
                .collect(Collectors.toList());
        return ResponseEntity.ok(listalogros);
    }
    @PostMapping("/web")
    public ResponseEntity<?> registrar(@RequestBody UsuarioLogroDTO dto){
        Optional<Usuario> usuarioOpt = usuarioService.listId(dto.getIdUsuario());
        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("El usuario con ID " + dto.getIdUsuario() + " no existe.");
        }

        // 2. Validar si el logro existe
        Optional<Logro> logroOpt = logroService.listId(dto.getIdLogro());
        if (logroOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("El logro con ID " + dto.getIdLogro() + " no existe.");
        }
        ModelMapper m = new ModelMapper();
        UsuarioLogro c = m.map(dto, UsuarioLogro.class);

        // Le asignamos los objetos validados directamente (evita que queden en null)
        c.setUsuario(usuarioOpt.get());
        c.setLogro(logroOpt.get());

        UsuarioLogro cur = usuariologroService.insert(c);

        UsuarioLogroDTO responseDTO = m.map(cur, UsuarioLogroDTO.class);
        // Aseguramos que la respuesta devuelva los IDs correctos y no 0
        responseDTO.setIdUsuario(cur.getUsuario().getIdUsuario());
        responseDTO.setIdLogro(cur.getLogro().getIdLogro());

        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }
    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable int id) {
        ModelMapper m = new ModelMapper();
        Optional<UsuarioLogro> mach = usuariologroService.listId(id);
        if (mach.isPresent()) {
            UsuarioLogroDTO dto = m.map(mach.get(), UsuarioLogroDTO.class);
            return ResponseEntity.ok(dto);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Logro no encontrado");
        }
    }
    @PutMapping("/actualiza")
    public ResponseEntity<String> actualizar(@RequestBody UsuarioLogroDTO dto) {
        Optional<UsuarioLogro> existente = usuariologroService.listId(dto.getIdUsuarioLogro());
        if (existente.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Registro no encontrado");
        }

        UsuarioLogro ah = existente.get();

        Usuario u = new Usuario();
        u.setIdUsuario(dto.getIdUsuario());
        ah.setUsuario(u);

        Logro a = new Logro();
        a.setIdLogro(dto.getIdLogro());
        ah.setLogro(a);

        usuariologroService.update(ah);
        return ResponseEntity.ok("Registro actualizado correctamente");
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(@PathVariable int id) {
        Optional<UsuarioLogro> usersAchiev = usuariologroService.listId(id);
        if (usersAchiev.isPresent()) {
            usuariologroService.delete(id);
            return ResponseEntity.ok("Registro eliminado correctamente");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Registro no encontrado");
        }
    }
    @GetMapping("/ranking-usuario")
    public ResponseEntity<List<UsuarioLogrosCountDTO>> cantidadLogrosPorUsuario() {
        List<String[]> lista = usuariologroService.cantidadLogrosPorUsuario();
        List<UsuarioLogrosCountDTO> listaDTO = new ArrayList<>();
        for (String[] columna : lista) {
            UsuarioLogrosCountDTO dto = new UsuarioLogrosCountDTO();
            dto.setNombreUsuario(columna[0]);
            dto.setTotalLogros(Long.parseLong(columna[1]));
            listaDTO.add(dto);
        }
        return ResponseEntity.ok(listaDTO);
    }

    @GetMapping("/logros-populares")
    public ResponseEntity<List<LogroPopularidadDTO>> logrosMasObtenidos() {
        List<String[]> lista = usuariologroService.logrosMasObtenidos();
        List<LogroPopularidadDTO> listaDTO = new ArrayList<>();
        for (String[] columna : lista) {
            LogroPopularidadDTO dto = new LogroPopularidadDTO();
            dto.setNombreLogro(columna[0]);
            dto.setCantidadUsuarios(Long.parseLong(columna[1]));
            listaDTO.add(dto);
        }
        return ResponseEntity.ok(listaDTO);
    }
}
