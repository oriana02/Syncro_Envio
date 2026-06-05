package com.Syncro.envios.repository;

import com.Syncro.envios.model.Transportista;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransportistaRepository extends JpaRepository<Transportista, Long> {
    Optional<Transportista> findByCodigo(String codigo);
    List<Transportista> findByActivoTrue();
}
