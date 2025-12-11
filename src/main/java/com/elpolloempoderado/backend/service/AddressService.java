package com.elpolloempoderado.backend.service;

import com.elpolloempoderado.backend.dto.AddressRequest;
import com.elpolloempoderado.backend.dto.AddressResponse;
import com.elpolloempoderado.backend.dto.CityResponse;
import com.elpolloempoderado.backend.dto.DistrictResponse;
import com.elpolloempoderado.backend.exception.BadRequestException;
import com.elpolloempoderado.backend.exception.ForbiddenException;
import com.elpolloempoderado.backend.exception.ResourceNotFoundException;
import com.elpolloempoderado.backend.model.Address;
import com.elpolloempoderado.backend.model.City;
import com.elpolloempoderado.backend.model.District;
import com.elpolloempoderado.backend.model.User;
import com.elpolloempoderado.backend.repository.AddressRepository;
import com.elpolloempoderado.backend.repository.CityRepository;
import com.elpolloempoderado.backend.repository.DistrictRepository;
import com.elpolloempoderado.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio para gestión de direcciones de envío
 */
@Service
@RequiredArgsConstructor
public class AddressService {
    
    private final AddressRepository addressRepository;
    private final UserRepository userRepository;
    private final CityRepository cityRepository;
    private final DistrictRepository districtRepository;
    
    /**
     * Obtiene todas las direcciones de un usuario
     */
    public List<AddressResponse> getUserAddresses(Long userId) {
        // Validar que el usuario existe
        getUserOrThrow(userId);
        
        List<Address> addresses = addressRepository.findByUserIdOrderByIsDefaultDesc(userId);
        return addresses.stream()
            .map(this::convertToResponse)
            .collect(Collectors.toList());
    }
    
    /**
     * Obtiene una dirección específica del usuario
     */
    public AddressResponse getAddressById(Long addressId, Long userId) {
        Address address = getAddressOrThrow(addressId, userId);
        return convertToResponse(address);
    }
    
    /**
     * Crea una nueva dirección para el usuario
     */
    @Transactional
    public AddressResponse createAddress(AddressRequest request, Long userId) {
        User user = getUserOrThrow(userId);
        
        // Validar ciudad y distrito
        City city = cityRepository.findById(request.getCityId())
            .orElseThrow(() -> new ResourceNotFoundException("Ciudad no encontrada"));
        
        District district = districtRepository.findById(request.getDistrictId())
            .orElseThrow(() -> new ResourceNotFoundException("Distrito no encontrado"));
        
        // Validar que el distrito pertenece a la ciudad
        if (!district.getCity().getId().equals(city.getId())) {
            throw new BadRequestException("El distrito no pertenece a la ciudad seleccionada");
        }
        
        // Si es la primera dirección o se marca como default, hacerla default
        long addressCount = addressRepository.countByUserId(userId);
        boolean shouldBeDefault = addressCount == 0 || request.getIsDefault();
        
        // Si se marca como default, quitar el default de las demás
        if (shouldBeDefault) {
            removeDefaultFromAllAddresses(userId);
        }
        
        Address address = new Address();
        address.setUser(user);
        address.setCity(city);
        address.setDistrict(district);
        address.setStreet(request.getStreet());
        address.setNumber(request.getNumber());
        address.setReference(request.getReference());
        address.setPhone(request.getPhone());
        address.setLabel(request.getLabel());
        address.setIsDefault(shouldBeDefault);
        
        Address savedAddress = addressRepository.save(address);
        return convertToResponse(savedAddress);
    }
    
    /**
     * Actualiza una dirección existente
     */
    @Transactional
    public AddressResponse updateAddress(Long addressId, AddressRequest request, Long userId) {
        Address address = getAddressOrThrow(addressId, userId);
        
        // Validar ciudad y distrito
        City city = cityRepository.findById(request.getCityId())
            .orElseThrow(() -> new ResourceNotFoundException("Ciudad no encontrada"));
        
        District district = districtRepository.findById(request.getDistrictId())
            .orElseThrow(() -> new ResourceNotFoundException("Distrito no encontrado"));
        
        // Validar que el distrito pertenece a la ciudad
        if (!district.getCity().getId().equals(city.getId())) {
            throw new BadRequestException("El distrito no pertenece a la ciudad seleccionada");
        }
        
        // Si se marca como default, quitar el default de las demás
        if (request.getIsDefault() && !address.getIsDefault()) {
            removeDefaultFromAllAddresses(userId);
        }
        
        address.setCity(city);
        address.setDistrict(district);
        address.setStreet(request.getStreet());
        address.setNumber(request.getNumber());
        address.setReference(request.getReference());
        address.setPhone(request.getPhone());
        address.setLabel(request.getLabel());
        address.setIsDefault(request.getIsDefault());
        
        Address updatedAddress = addressRepository.save(address);
        return convertToResponse(updatedAddress);
    }
    
    /**
     * Establece una dirección como default
     */
    @Transactional
    public AddressResponse setDefaultAddress(Long addressId, Long userId) {
        Address address = getAddressOrThrow(addressId, userId);
        
        // Quitar default de todas las direcciones
        removeDefaultFromAllAddresses(userId);
        
        // Establecer esta como default
        address.setIsDefault(true);
        Address updatedAddress = addressRepository.save(address);
        
        return convertToResponse(updatedAddress);
    }
    
    /**
     * Elimina una dirección
     */
    @Transactional
    public void deleteAddress(Long addressId, Long userId) {
        Address address = getAddressOrThrow(addressId, userId);
        
        // Si es la dirección default, no permitir eliminarla si hay otras direcciones
        if (address.getIsDefault()) {
            long addressCount = addressRepository.countByUserId(userId);
            if (addressCount > 1) {
                throw new BadRequestException("No puedes eliminar la dirección por defecto. Primero establece otra dirección como predeterminada.");
            }
        }
        
        addressRepository.delete(address);
    }
    
    /**
     * Obtiene la dirección por defecto del usuario
     */
    public AddressResponse getDefaultAddress(Long userId) {
        Address address = addressRepository.findByUserIdAndIsDefaultTrue(userId)
            .orElseThrow(() -> new ResourceNotFoundException("No tienes una dirección por defecto configurada"));
        
        return convertToResponse(address);
    }
    
    // ============= MÉTODOS PRIVADOS =============
    
    private User getUserOrThrow(Long userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
    }
    
    private Address getAddressOrThrow(Long addressId, Long userId) {
        Address address = addressRepository.findById(addressId)
            .orElseThrow(() -> new ResourceNotFoundException("Dirección no encontrada"));
        
        // Verificar que la dirección pertenece al usuario
        if (!address.getUser().getId().equals(userId)) {
            throw new ForbiddenException("No tienes permiso para acceder a esta dirección");
        }
        
        return address;
    }
    
    private void removeDefaultFromAllAddresses(Long userId) {
        List<Address> addresses = addressRepository.findByUserId(userId);
        addresses.forEach(addr -> {
            if (addr.getIsDefault()) {
                addr.setIsDefault(false);
                addressRepository.save(addr);
            }
        });
    }
    
    private AddressResponse convertToResponse(Address address) {
        AddressResponse response = new AddressResponse();
        response.setId(address.getId());
        response.setUserId(address.getUser().getId());
        
        // City
        CityResponse cityResponse = new CityResponse();
        cityResponse.setId(address.getCity().getId());
        cityResponse.setNombre(address.getCity().getNombre());
        response.setCity(cityResponse);
        
        // District
        DistrictResponse districtResponse = new DistrictResponse();
        districtResponse.setId(address.getDistrict().getId());
        districtResponse.setNombre(address.getDistrict().getNombre());
        districtResponse.setCiudadId(address.getCity().getId());
        response.setDistrict(districtResponse);
        
        response.setStreet(address.getStreet());
        response.setNumber(address.getNumber());
        response.setReference(address.getReference());
        response.setPhone(address.getPhone());
        response.setLabel(address.getLabel());
        response.setIsDefault(address.getIsDefault());
        response.setCreatedAt(address.getCreatedAt());
        response.setUpdatedAt(address.getUpdatedAt());
        
        return response;
    }
}
