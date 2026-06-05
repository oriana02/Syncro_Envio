package com.Syncro.envios.repository;

import com.Syncro.envios.model.HistorialEstadoEnvio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface HistorialEstadoEnvioRepository extends JpaRepository<HistorialEstadoEnvio, Long> {
    List<HistorialEstadoEnvio> findByDespachoIdOrderByFechaCambioAsc(Long despachoId);
}
