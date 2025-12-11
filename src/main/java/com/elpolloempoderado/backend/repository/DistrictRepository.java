package com.elpolloempoderado.backend.repository;

import com.elpolloempoderado.backend.model.District;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DistrictRepository extends JpaRepository<District, Long> {
    // Método para obtener distritos por ciudad
    List<District> findByCityId(Long cityId);
}
