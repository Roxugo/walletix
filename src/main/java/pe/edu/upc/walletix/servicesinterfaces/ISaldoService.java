package pe.edu.upc.walletix.servicesinterfaces;

import pe.edu.upc.walletix.entities.Usuario;

import java.math.BigDecimal;

// Saldo del usuario (US11, US12, US14): los gastos lo restan y los ingresos lo suman
public interface ISaldoService {
    // Aplica el cambio (negativo = sale dinero) solo si el saldo no queda negativo.
    // Es atómico: dos peticiones simultáneas no pueden gastar el mismo dinero.
    // Devuelve false (y no cambia nada) si no alcanza.
    public boolean aplicar(Usuario usuario, BigDecimal cambio);

    // Saldo actual guardado en la base
    public BigDecimal saldoActual(Usuario usuario);
}
