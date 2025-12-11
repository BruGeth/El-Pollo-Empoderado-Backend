package com.elpolloempoderado.backend.service;

import com.elpolloempoderado.backend.dto.CityResponse;
import com.elpolloempoderado.backend.dto.DistrictResponse;
import com.elpolloempoderado.backend.model.City;
import com.elpolloempoderado.backend.model.District;
import com.elpolloempoderado.backend.repository.CityRepository;
import com.elpolloempoderado.backend.repository.DistrictRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio para gestionar ciudades y distritos
 */
@Service
@RequiredArgsConstructor
public class LocationService {
    
    private final CityRepository cityRepository;
    private final DistrictRepository districtRepository;
    
    public List<CityResponse> getAllCities() {
        return cityRepository.findAll().stream()
                .map(this::convertToCityResponse)
                .collect(Collectors.toList());
    }
    
    public List<DistrictResponse> getAllDistricts() {
        return districtRepository.findAll().stream()
                .map(this::convertToDistrictResponse)
                .collect(Collectors.toList());
    }
    
    public List<DistrictResponse> getDistrictsByCity(Long cityId) {
        return districtRepository.findByCityId(cityId).stream()
                .map(this::convertToDistrictResponse)
                .collect(Collectors.toList());
    }
    
    private CityResponse convertToCityResponse(City city) {
        return new CityResponse(city.getId(), city.getNombre());
    }
    
    private DistrictResponse convertToDistrictResponse(District district) {
        return new DistrictResponse(
                district.getId(),
                district.getNombre(),
                district.getCity().getId()
        );
    }
}
