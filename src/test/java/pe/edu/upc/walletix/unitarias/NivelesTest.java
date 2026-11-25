package pe.edu.upc.walletix.unitarias;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pe.edu.upc.walletix.servicesimplements.Niveles;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

// Pruebas UNITARIAS de las reglas puras de la gamificación: nivel según XP y racha de días
class NivelesTest {

    private static final LocalDate HOY = LocalDate.of(2026, 11, 24);

    @Test
    @DisplayName("Con 0 XP se empieza en el nivel 1 (principiante) y el siguiente está en 100 XP")
    void nivelInicial() {
        Niveles.Nivel nivel = Niveles.nivelDe(0);
        assertThat(nivel.numero()).isEqualTo(1);
        assertThat(nivel.nombre()).isEqualTo("principiante");
        assertThat(nivel.xpSiguiente()).isEqualTo(100);
    }

    @Test
    @DisplayName("Se sube de nivel exactamente al llegar al XP mínimo del siguiente")
    void subirDeNivel() {
        assertThat(Niveles.nivelDe(99).numero()).isEqualTo(1);
        assertThat(Niveles.nivelDe(100).numero()).isEqualTo(2);
        assertThat(Niveles.nivelDe(1250).nombre()).isEqualTo("ahorrador_pro");
    }

    @Test
    @DisplayName("En el último nivel no hay siguiente")
    void ultimoNivel() {
        Niveles.Nivel nivel = Niveles.nivelDe(999_999);
        assertThat(nivel.nombre()).isEqualTo("maestro");
        assertThat(nivel.xpSiguiente()).isNull();
    }

    @Test
    @DisplayName("Racha: cuenta los días seguidos que terminan hoy")
    void rachaHastaHoy() {
        List<LocalDate> fechas = List.of(HOY, HOY.minusDays(1), HOY.minusDays(2), HOY.minusDays(5));
        assertThat(Niveles.racha(fechas, HOY)).isEqualTo(3);
    }

    @Test
    @DisplayName("Racha: si hoy todavía no registró, la racha de ayer no se pierde")
    void rachaHastaAyer() {
        assertThat(Niveles.racha(List.of(HOY.minusDays(1), HOY.minusDays(2)), HOY)).isEqualTo(2);
    }

    @Test
    @DisplayName("Racha: si el último registro fue antes de ayer, la racha es 0")
    void rachaPerdida() {
        assertThat(Niveles.racha(List.of(HOY.minusDays(2), HOY.minusDays(3)), HOY)).isZero();
        assertThat(Niveles.racha(List.of(), HOY)).isZero();
    }
}
