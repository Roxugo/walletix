package pe.edu.upc.walletix.unitarias;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pe.edu.upc.walletix.entities.Categoria;
import pe.edu.upc.walletix.entities.Comerciante;
import pe.edu.upc.walletix.entities.Gasto;
import pe.edu.upc.walletix.servicesimplements.ReglasReto;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

// Pruebas UNITARIAS de las reglas que verifican los retos con los gastos registrados
class ReglasRetoTest {

    private static Gasto gasto(String comercio, String descripcion, String categoria, String monto, boolean fijo) {
        Categoria cat = new Categoria();
        cat.setNombreCategoria(categoria);
        Comerciante com = new Comerciante();
        com.setNombreComerciante(comercio);
        Gasto gasto = new Gasto();
        gasto.setCategoria(cat);
        gasto.setComerciante(com);
        gasto.setDescripcionGasto(descripcion);
        gasto.setMontoGasto(new BigDecimal(monto));
        gasto.setFechaGasto(LocalDate.now());
        gasto.setFijoGasto(fijo);
        gasto.setEstadoGasto(1);
        return gasto;
    }

    @Test
    @DisplayName("Sin delivery: lo rompe un gasto en Rappi, PedidosYa o con la palabra delivery")
    void delivery() {
        assertThat(ReglasReto.rompe("sin_delivery", gasto("Rappi", "Pollo a la brasa", "Comida", "25", false))).isTrue();
        assertThat(ReglasReto.rompe("sin_delivery", gasto("Otro", "Pedí por PedidosYa", "Comida", "30", false))).isTrue();
        assertThat(ReglasReto.rompe("sin_delivery", gasto("Otro", "Delivery de chifa", "Comida", "30", false))).isTrue();
        assertThat(ReglasReto.rompe("sin_delivery", gasto("Plaza Vea", "Compras del mercado", "Comida", "80", false))).isFalse();
    }

    @Test
    @DisplayName("Sin gastos hormiga: lo rompe un gasto menor a S/ 10 que no sea fijo ni de transporte")
    void gastosHormiga() {
        assertThat(ReglasReto.rompe("sin_gastos_hormiga", gasto("Tambo", "Galletas", "Comida", "3.50", false))).isTrue();
        assertThat(ReglasReto.rompe("sin_gastos_hormiga", gasto("Tambo", "Almuerzo", "Comida", "10", false))).isFalse();
        assertThat(ReglasReto.rompe("sin_gastos_hormiga", gasto("Spotify", "Suscripción", "Entretenimiento", "9.90", true))).isFalse();
        assertThat(ReglasReto.rompe("sin_gastos_hormiga", gasto("Metropolitano", "Pasaje", "Transporte", "3.20", false))).isFalse();
    }

    @Test
    @DisplayName("Transporte inteligente: lo rompe un taxi o una app de taxi, pero no un delivery")
    void transporte() {
        assertThat(ReglasReto.rompe("transporte_inteligente", gasto("Uber", "Viaje al trabajo", "Transporte", "18", false))).isTrue();
        assertThat(ReglasReto.rompe("transporte_inteligente", gasto("Otro", "Taxi a casa", "Transporte", "12", false))).isTrue();
        assertThat(ReglasReto.rompe("transporte_inteligente", gasto("Uber Eats", "Hamburguesa", "Comida", "25", false))).isFalse();
        assertThat(ReglasReto.rompe("transporte_inteligente", gasto("Metropolitano", "Pasaje", "Transporte", "3.20", false))).isFalse();
    }

    @Test
    @DisplayName("Los retos que no se pueden detectar con datos siguen siendo por honestidad")
    void porHonestidad() {
        Gasto rappi = gasto("Rappi", "Delivery", "Comida", "5", false);
        assertThat(ReglasReto.seVerificaConGastos("ahorro_libre")).isFalse();
        assertThat(ReglasReto.seVerificaConGastos("sin_compras_impulsivas")).isFalse();
        assertThat(ReglasReto.rompe("ahorro_libre", rappi)).isFalse();
        assertThat(ReglasReto.rompe(null, rappi)).isFalse();
        rappi.setEstadoGasto(0); // un gasto eliminado ya no cuenta
        assertThat(ReglasReto.rompe("sin_delivery", rappi)).isFalse();
    }
}
