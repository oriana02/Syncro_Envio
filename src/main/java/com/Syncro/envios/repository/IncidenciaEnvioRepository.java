package com.Syncro.envios.repository;

import com.Syncro.envios.model.IncidenciaEnvio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface IncidenciaEnvioRepository extends JpaRepository<IncidenciaEnvio, Long> {
    List<IncidenciaEnvio> findByDespachoId(Long despachoId);
    List<IncidenciaEnvio> findByDespachoIdAndEstado(Long despachoId, String estado);
}
