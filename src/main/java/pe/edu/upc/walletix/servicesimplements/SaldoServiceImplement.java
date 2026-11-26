package pe.edu.upc.walletix.servicesimplements;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upc.walletix.entities.Usuario;
import pe.edu.upc.walletix.repositories.IUsuarioRepository;
import pe.edu.upc.walletix.servicesinterfaces.ISaldoService;

import java.math.BigDecimal;

@Service
public class SaldoServiceImplement implements ISaldoService {
    @Autowired
    private IUsuarioRepository usuarioRepository;

    @Override
    @Transactional
    public boolean aplicar(Usuario usuario, BigDecimal cambio) {
        // Antes se leía el saldo, se sumaba en Java y se guardaba: con dos gastos a la vez,
        // los dos leían el mismo saldo y uno de los descuentos se perdía.
        if (usuarioRepository.sumarSaldoSiAlcanza(usuario.getIdUsuario(), cambio) == 0) {
            return false;
        }
        // El objeto en memoria queda con el saldo nuevo (lo usan las respuestas y los logros)
        usuario.setSaldoActualUsuario(usuarioRepository.saldoGuardado(usuario.getIdUsuario()));
        return true;
    }

    @Override
    public BigDecimal saldoActual(Usuario usuario) {
        return usuarioRepository.saldoGuardado(usuario.getIdUsuario());
    }
}
