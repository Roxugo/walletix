package pe.edu.upc.walletix.servicesinterfaces;

import pe.edu.upc.walletix.entities.Usuario;

import java.math.BigDecimal;

// Saldo del usuario (US11, US12, US14): los gastos lo restan y los ingresos lo suman
public interface ISaldoService {
    // true si el saldo no queda negativo al aplicar el cambio (cambio negativo = sale dinero)
    public boolean alcanza(Usuario usuario, BigDecimal cambio);

    // Suma el cambio al saldo y lo guarda (cambio negativo = resta)
    public void aplicar(Usuario usuario, BigDecimal cambio);
}
