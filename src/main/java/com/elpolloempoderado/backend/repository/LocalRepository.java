package com.elpolloempoderado.backend.repository;

import com.elpolloempoderado.backend.model.Local;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LocalRepository extends JpaRepository<Local, Long> {
    // Método para obtener locales por distrito
    List<Local> findByDistrictId(Long districtId);
    
    // Método para obtener locales por ciudad (a través del distrito)
    List<Local> findByDistrictCityId(Long cityId);
}
