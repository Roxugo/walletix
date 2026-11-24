package pe.edu.upc.walletix.servicesimplements;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.edu.upc.walletix.entities.Usuario;
import pe.edu.upc.walletix.servicesinterfaces.ISaldoService;
import pe.edu.upc.walletix.servicesinterfaces.IUsuarioService;

import java.math.BigDecimal;

@Service
public class SaldoServiceImplement implements ISaldoService {
    @Autowired
    private IUsuarioService usuarioService;

    private BigDecimal saldoDe(Usuario usuario) {
        return usuario.getSaldoActualUsuario() == null ? BigDecimal.ZERO : usuario.getSaldoActualUsuario();
    }

    @Override
    public boolean alcanza(Usuario usuario, BigDecimal cambio) {
        return saldoDe(usuario).add(cambio).compareTo(BigDecimal.ZERO) >= 0;
    }

    @Override
    public void aplicar(Usuario usuario, BigDecimal cambio) {
        usuario.setSaldoActualUsuario(saldoDe(usuario).add(cambio));
        usuarioService.update(usuario);
    }
}
