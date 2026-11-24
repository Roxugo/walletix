package pe.edu.upc.walletix.servicesinterfaces;

import pe.edu.upc.walletix.entities.Gasto;

import java.util.Optional;

// US15: alerta cuando los gastos de una categoría superan el presupuesto del mes
public interface IAlertaPresupuestoService {
    // Revisa el presupuesto de la categoría y del mes del gasto. Si se superó,
    // guarda una notificación para el usuario y devuelve el mensaje de la alerta
    public Optional<String> revisar(Gasto gasto);
}
