package com.ecommerce.backend.service;

import com.ecommerce.backend.model.Address;
import com.ecommerce.backend.model.User;
import com.ecommerce.backend.payload.AddressRequest;
import com.ecommerce.backend.repository.AddressRepository;
import com.ecommerce.backend.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AddressService {
    private final AddressRepository addressRepository;
    private final UserRepository userRepository;

    public List<Address> listUserAddresses() {
        User user = getCurrentUser();
        return addressRepository.findAll().stream()
                .filter(address -> address.getUser().getId().equals(user.getId()))
                .toList();
    }

    @Transactional
    public Address addAddress(AddressRequest req) {
        User user = getCurrentUser();
        Address address = Address.builder()
                .user(user)
                .street(req.getStreet())
                .city(req.getCity())
                .state(req.getState())
                .postalCode(req.getPostalCode())
                .country(req.getCountry())
                .build();
        return addressRepository.save(address);
    }

    @Transactional
    public Address updateAddress(Long id, AddressRequest req) {
        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Address not found"));
        User user = getCurrentUser();
        if (!address.getUser().getId().equals(user.getId())) {
            throw new SecurityException("Cannot update another user's address.");
        }
        address.setStreet(req.getStreet());
        address.setCity(req.getCity());
        address.setState(req.getState());
        address.setPostalCode(req.getPostalCode());
        address.setCountry(req.getCountry());
        return addressRepository.save(address);
    }

    @Transactional
    public void deleteAddress(Long id) {
        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Address not found"));
        User user = getCurrentUser();
        if (!address.getUser().getId().equals(user.getId())) {
            throw new SecurityException("Cannot delete another user's address.");
        }
        addressRepository.delete(address);
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found."));
    }
}
