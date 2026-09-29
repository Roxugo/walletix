package pe.edu.upc.walletix.controllers;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.upc.walletix.dtos.MerchantDTO;
import pe.edu.upc.walletix.entities.Merchant;
import pe.edu.upc.walletix.servicesinterfaces.IMerchantService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/comercios")
public class MerchantController {
    @Autowired
    private IMerchantService mS;

    @GetMapping
    public ResponseEntity<List<MerchantDTO>> listar() {
        ModelMapper m = new ModelMapper();
        List<MerchantDTO> listaComercios = mS.list().stream()
                .map(y -> m.map(y, MerchantDTO.class))
                .collect(Collectors.toList());
        return ResponseEntity.ok(listaComercios);
    }

    @PostMapping("/web")
    public ResponseEntity<?> registrar(@RequestBody MerchantDTO dto) {
        ModelMapper m = new ModelMapper();
        Merchant c = m.map(dto, Merchant.class);
        Merchant cur = mS.insert(c);
        MerchantDTO responseDTO = m.map(cur, MerchantDTO.class);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable int id) {
        ModelMapper m = new ModelMapper();
        Optional<Merchant> mach = mS.listId(id);
        if (mach.isPresent()) {
            MerchantDTO dto = m.map(mach.get(), MerchantDTO.class);
            return ResponseEntity.ok(dto);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Comercio no encontrado");
        }
    }

    @PutMapping("/actualiza")
    public ResponseEntity<String> actualizar(@RequestBody MerchantDTO dto) {
        Optional<Merchant> existente = mS.listId(dto.getIdMerchant());
        if (existente.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Comercio no encontrado");
        }
        Merchant c = existente.get();
        c.setNameMerchant(dto.getNameMerchant());
        c.setLogoUrlMerchant(dto.getLogoUrlMerchant());
        c.setDefaultCategoryIdMerchant(dto.getDefaultCategoryIdMerchant());
        mS.update(c);
        return ResponseEntity.ok("Comercio actualizado correctamente");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(@PathVariable int id) {
        Optional<Merchant> machine = mS.listId(id);
        if (machine.isPresent()) {
            mS.delete(id);
            return ResponseEntity.ok("Comercio eliminado correctamente");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Comercio no encontrado");
        }
    }
}
