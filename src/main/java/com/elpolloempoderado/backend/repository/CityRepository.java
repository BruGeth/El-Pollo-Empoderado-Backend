package com.elpolloempoderado.backend.repository;

import com.elpolloempoderado.backend.model.City;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CityRepository extends JpaRepository<City, Long> {
}
