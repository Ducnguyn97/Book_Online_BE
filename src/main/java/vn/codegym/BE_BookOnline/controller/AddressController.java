package vn.codegym.BE_BookOnline.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import vn.codegym.BE_BookOnline.dto.request.AddressRequest;
import vn.codegym.BE_BookOnline.dto.response.AddressResponse;
import vn.codegym.BE_BookOnline.service.AddressService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/address")
@RequiredArgsConstructor
public class AddressController {
    private final AddressService addressService;
    @GetMapping
    public ResponseEntity<List<AddressResponse>> getAllAddressesByUser(Authentication authentication){
        String email = authentication.getName();
        List<AddressResponse> response = addressService.getAllAddressesByUser(email);
        return ResponseEntity.ok(response);
    }
    @GetMapping("/{addressId}")
    public ResponseEntity<AddressResponse> getAddressById(Authentication authentication, @PathVariable("addressId") Long addressId){
        String email = authentication.getName();
        AddressResponse response = addressService.getAddressById(email, addressId);
        return ResponseEntity.ok(response);
    }
    @PostMapping
    public ResponseEntity<AddressResponse> createAddress(Authentication authentication, @Valid @RequestBody AddressRequest request){
        String email = authentication.getName();
        AddressResponse response = addressService.createAddress(email, request);
        return ResponseEntity.ok(response);
    }
    @PatchMapping ("/{addressId}")
    public ResponseEntity<AddressResponse> updateAddress(Authentication authentication, @PathVariable("addressId") Long addressId, @Valid @RequestBody AddressRequest request){
        String email = authentication.getName();
        AddressResponse response = addressService.updateAddress(email, addressId, request);
        return ResponseEntity.ok(response);
    }
    @DeleteMapping("/{addressId}")
    public ResponseEntity<?> deleteAddress(Authentication authentication, @PathVariable("addressId") Long addressId){
        String email = authentication.getName();
        addressService.deleteAddress(email, addressId);
        return ResponseEntity.ok("Xóa địa chỉ thành công");
    }
    @PatchMapping("/{addressId}/default")
    public ResponseEntity<AddressResponse> setDefaultAddress(Authentication authentication, @PathVariable("addressId") Long addressId){
        String email = authentication.getName();
        AddressResponse response = addressService.setDefaultAddress(email, addressId);
        return ResponseEntity.ok(response);
    }
    @GetMapping("/default")
    public ResponseEntity<Optional<AddressResponse>> getDefaultAddress(Authentication authentication) {
        String email = authentication.getName();
        AddressResponse response = addressService.getDefaultAddressByUserEmail(email);
        return ResponseEntity.ok(Optional.ofNullable(response));
    }

}

