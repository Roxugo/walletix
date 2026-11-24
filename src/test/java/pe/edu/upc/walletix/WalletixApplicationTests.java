package pe.edu.upc.walletix;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.TimeZone;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class WalletixApplicationTests {

    @Test
    void contextLoads() {
    }

    @Test
    void laApiUsaLaHoraDePeru() {
        // ZonaHorariaConfig: fechas y horas de la API en hora de Lima, no en la del servidor (UTC en Render)
        assertThat(TimeZone.getDefault().getID()).isEqualTo("America/Lima");
    }

}
