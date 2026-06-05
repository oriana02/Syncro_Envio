package com.Syncro.envios.repository;

import com.Syncro.envios.model.TarifaEnvio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface TarifaEnvioRepository extends JpaRepository<TarifaEnvio, Long> {
    Optional<TarifaEnvio> findFirstByTipoEnvioAndRegionDestinoAndActivoTrueAndPesoMinKgLessThanEqualAndPesoMaxKgGreaterThan(
        String tipoEnvio, String regionDestino, BigDecimal peso, BigDecimal peso2);
    List<TarifaEnvio> findByActivoTrue();
}
