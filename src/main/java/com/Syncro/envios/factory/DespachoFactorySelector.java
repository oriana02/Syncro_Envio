package com.Syncro.envios.factory;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Selector del patron Factory Method.
 * Recibe todas las fabricas disponibles via inyeccion de dependencias
 * y selecciona la correcta segun el tipo de envio solicitado.
 * Si no hay fabrica especifica, usa ESTANDAR por defecto.
 */
@Component
@Slf4j
public class DespachoFactorySelector {

    private final Map<String, DespachoFactory> factories;
    private static final String DEFAULT_TIPO = "ESTANDAR";

    public DespachoFactorySelector(List<DespachoFactory> factories) {
        this.factories = factories.stream()
                .collect(Collectors.toMap(
                        DespachoFactory::getTipoEnvio,
                        f -> f
                ));
        log.info("Fabricas de despacho registradas: {}", this.factories.keySet());
    }

    /**
     * Selecciona la fabrica adecuada segun el tipo de envio.
     *
     * @param tipoEnvio tipo de envio solicitado
     * @return fabrica correspondiente, o ESTANDAR si no existe
     */
    public DespachoFactory seleccionar(String tipoEnvio) {
        String tipo = tipoEnvio != null ? tipoEnvio.toUpperCase() : DEFAULT_TIPO;
        DespachoFactory factory = factories.getOrDefault(tipo, factories.get(DEFAULT_TIPO));
        log.debug("Factory seleccionada para tipo {}: {}", tipo, factory.getClass().getSimpleName());
        return factory;
    }
}
