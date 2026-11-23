package pe.edu.upc.walletix.securities;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.HeaderParameter;
import io.swagger.v3.oas.models.tags.Tag;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

// Agrega el botón "Authorize" en Swagger para pegar el token JWT
@Configuration
@OpenAPIDefinition(info = @Info(title = "Walletix API", version = "1.0"),
        security = @SecurityRequirement(name = "bearerAuth"))
@SecurityScheme(name = "bearerAuth", type = SecuritySchemeType.HTTP, scheme = "bearer", bearerFormat = "JWT")
public class OpenApiConfig {

    // Orden de los grupos en Swagger: el mismo orden en que se crean los datos en la app
    private static final List<String> ORDEN_GRUPOS = List.of(
            "Inicio de sesión", "Usuarios", "Roles",
            "Categorías", "Comercios",
            "Gastos", "Ingresos", "Presupuestos", "Metas de ahorro",
            "Logros", "Logros de usuario", "Desafíos", "Desafíos de usuario", "Notificaciones",
            "Microlecciones", "Preguntas de quiz", "Intentos de quiz",
            "Auditoría");

    private int posicionGrupo(String grupo) {
        int posicion = ORDEN_GRUPOS.indexOf(grupo);
        return posicion == -1 ? ORDEN_GRUPOS.size() : posicion;
    }

    // Grupo al que pertenece una ruta (el @Tag de su controller)
    private String grupoDeRuta(PathItem ruta) {
        return ruta.readOperations().stream()
                .map(Operation::getTags)
                .filter(etiquetas -> etiquetas != null && !etiquetas.isEmpty())
                .map(etiquetas -> etiquetas.get(0))
                .findFirst().orElse("");
    }

    // Dentro de cada grupo, orden CRUD: listar, registrar, buscar/eliminar por id, actualizar y luego las consultas
    private int posicionRuta(String ruta) {
        if (ruta.endsWith("/web") || ruta.equals("/login")) return 1;
        if (ruta.endsWith("/actualiza")) return 3;
        if (ruta.matches("^/[^/]+$")) return 0;
        if (ruta.matches("^/[^/]+/\\{[^/]+}$")) return 2;
        return 4;
    }

    // i18n: en cada endpoint de Swagger aparece la cabecera Accept-Language para elegir el idioma de la respuesta
    @Bean
    public OperationCustomizer cabeceraIdioma() {
        return (operacion, metodo) -> operacion.addParametersItem(new HeaderParameter()
                .name("Accept-Language")
                .description("Idioma de los mensajes / Language of the messages: es-419 (default) o en-US")
                .required(false)
                .schema(new StringSchema()._enum(List.of("es-419", "en-US"))));
    }

    @Bean
    public OpenApiCustomizer ordenarSwagger() {
        return openApi -> {
            if (openApi.getTags() != null) {
                openApi.getTags().sort(Comparator.comparingInt((Tag grupo) -> posicionGrupo(grupo.getName())));
            }
            Paths rutasOrdenadas = new Paths();
            openApi.getPaths().entrySet().stream()
                    .sorted(Comparator.comparingInt((Map.Entry<String, PathItem> ruta) -> posicionGrupo(grupoDeRuta(ruta.getValue())))
                            .thenComparingInt(ruta -> posicionRuta(ruta.getKey()))
                            .thenComparing(Map.Entry::getKey))
                    .forEach(ruta -> rutasOrdenadas.addPathItem(ruta.getKey(), ruta.getValue()));
            openApi.setPaths(rutasOrdenadas);
        };
    }
}
