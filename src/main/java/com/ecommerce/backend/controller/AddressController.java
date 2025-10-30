package com.ecommerce.backend.controller;

import com.ecommerce.backend.model.Address;
import com.ecommerce.backend.payload.AddressRequest;
import com.ecommerce.backend.service.AddressService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/addresses")
@RequiredArgsConstructor
public class AddressController {
    private final AddressService addressService;

    @GetMapping
    public ResponseEntity<List<Address>> getAddresses() {
        return ResponseEntity.ok(addressService.listUserAddresses());
    }

    @PostMapping
    public ResponseEntity<Address> addAddress(@RequestBody AddressRequest request) {
        return ResponseEntity.ok(addressService.addAddress(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateAddress(@PathVariable Long id, @RequestBody AddressRequest request) {
        try {
            return ResponseEntity.ok(addressService.updateAddress(id, request));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (SecurityException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAddress(@PathVariable Long id) {
        try {
            addressService.deleteAddress(id);
            return ResponseEntity.ok().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (SecurityException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        }
    }
}
