package com.elpolloempoderado.backend.service;

import com.elpolloempoderado.backend.dto.LocalRequest;
import com.elpolloempoderado.backend.dto.LocalResponse;
import com.elpolloempoderado.backend.exception.ResourceNotFoundException;
import com.elpolloempoderado.backend.model.District;
import com.elpolloempoderado.backend.model.Local;
import com.elpolloempoderado.backend.repository.DistrictRepository;
import com.elpolloempoderado.backend.repository.LocalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio para gestionar locales/tiendas
 */
@Service
@RequiredArgsConstructor
public class LocalService {
    
    private final LocalRepository localRepository;
    private final DistrictRepository districtRepository;
    
    public List<LocalResponse> getAllLocals() {
        return localRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    public LocalResponse getLocalById(Long id) {
        Local local = localRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Local not found with id: " + id));
        return convertToResponse(local);
    }
    
    public List<LocalResponse> getLocalsByDistrict(Long districtId) {
        return localRepository.findByDistrictId(districtId).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    public List<LocalResponse> getLocalsByCity(Long cityId) {
        return localRepository.findByDistrictCityId(cityId).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    public LocalResponse createLocal(LocalRequest request) {
        District district = districtRepository.findById(request.getDistritoId())
                .orElseThrow(() -> new ResourceNotFoundException("District not found with id: " + request.getDistritoId()));
        
        Local local = new Local();
        local.setNombre(request.getNombre());
        local.setDistrict(district);
        local.setDireccion(request.getDireccion());
        local.setTelefono(request.getTelefono());
        local.setHorario(request.getHorario());
        local.setImagenUrl(request.getImagenUrl());
        local.setMapsUrl(request.getMapsUrl());
        
        Local savedLocal = localRepository.save(local);
        return convertToResponse(savedLocal);
    }
    
    public LocalResponse updateLocal(Long id, LocalRequest request) {
        Local local = localRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Local not found with id: " + id));
        
        if (request.getNombre() != null) {
            local.setNombre(request.getNombre());
        }
        if (request.getDistritoId() != null) {
            District district = districtRepository.findById(request.getDistritoId())
                    .orElseThrow(() -> new ResourceNotFoundException("District not found with id: " + request.getDistritoId()));
            local.setDistrict(district);
        }
        if (request.getDireccion() != null) {
            local.setDireccion(request.getDireccion());
        }
        if (request.getTelefono() != null) {
            local.setTelefono(request.getTelefono());
        }
        if (request.getHorario() != null) {
            local.setHorario(request.getHorario());
        }
        if (request.getImagenUrl() != null) {
            local.setImagenUrl(request.getImagenUrl());
        }
        if (request.getMapsUrl() != null) {
            local.setMapsUrl(request.getMapsUrl());
        }
        
        Local updatedLocal = localRepository.save(local);
        return convertToResponse(updatedLocal);
    }
    
    public void deleteLocal(Long id) {
        if (!localRepository.existsById(id)) {
            throw new ResourceNotFoundException("Local not found with id: " + id);
        }
        localRepository.deleteById(id);
    }
    
    private LocalResponse convertToResponse(Local local) {
        return new LocalResponse(
                local.getId(),
                local.getNombre(),
                local.getDistrict().getId(),
                local.getDistrict().getNombre(),
                local.getDistrict().getCity().getId(),
                local.getDistrict().getCity().getNombre(),
                local.getDireccion(),
                local.getTelefono(),
                local.getHorario(),
                local.getImagenUrl(),
                local.getMapsUrl(),
                local.getCreatedAt()
        );
    }
}
