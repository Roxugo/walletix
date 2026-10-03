package pe.edu.upc.walletix.controllers;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.upc.walletix.dtos.AchievementDTO;
import pe.edu.upc.walletix.entities.Achievement;
import pe.edu.upc.walletix.servicesinterfaces.IAchievementService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/logros")
public class AchievementController {
    @Autowired
    private IAchievementService aS;

    @GetMapping
    public ResponseEntity<List<AchievementDTO>> listar(){
        ModelMapper m= new ModelMapper();
        List<AchievementDTO> listalogros =aS.list().stream()
                .map(y->m.map(y,AchievementDTO.class))
                .collect(Collectors.toList());
        return ResponseEntity.ok(listalogros);
    }
    @PostMapping("/web")
    public ResponseEntity<?> registrar(@RequestBody AchievementDTO dto){
        ModelMapper m=new ModelMapper();
        Achievement c=m.map(dto, Achievement.class);
        Achievement cur= aS.insert(c);
        AchievementDTO responseDTO=m.map(cur,AchievementDTO.class);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }
    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable int id) {
        ModelMapper m = new ModelMapper();
        Optional<Achievement> mach = aS.listId(id);
        if (mach.isPresent()) {
            AchievementDTO dto = m.map(mach.get(), AchievementDTO.class);
            return ResponseEntity.ok(dto);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Logro no encontrado");
        }
    }
    @PutMapping("/actualiza")
    public ResponseEntity<String> actualizar(@RequestBody AchievementDTO dto) {
        Optional<Achievement> existente = aS.listId(dto.getIdAchievement());
        if (existente.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Logro no encontrado");
        }
        Achievement ac = existente.get();
        ac.setNameAchievement(dto.getNameAchievement());
        ac.setDescriptionAchievement(dto.getDescriptionAchievement());
        ac.setIconurlAchievement(dto.getIconurlAchievement());
        ac.setPointAchievement(dto.getPointAchievement());
        aS.update(ac);
        return ResponseEntity.ok("Logro actualizado correctamente");
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(@PathVariable int id) {
        Optional<Achievement> achievement = aS.listId(id);
        if (achievement.isPresent()) {
            aS.delete(id);
            return ResponseEntity.ok("Logro eliminado correctamente");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Logro no encontrado");
        }
    }
}
