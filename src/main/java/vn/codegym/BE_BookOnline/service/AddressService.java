package vn.codegym.BE_BookOnline.service;

import vn.codegym.BE_BookOnline.dto.request.AddressRequest;
import vn.codegym.BE_BookOnline.dto.response.AddressResponse;
import vn.codegym.BE_BookOnline.model.Address;

import java.util.List;
import java.util.Optional;

public interface AddressService {
    List<AddressResponse> getAllAddressesByUser(String email);

    AddressResponse getAddressById(String email,Long addressId);

    AddressResponse createAddress(String email, AddressRequest request);

    AddressResponse updateAddress(String email, Long addressId, AddressRequest request );

    void deleteAddress(String email, Long addressId);

    AddressResponse setDefaultAddress(String email, Long addressId);

    AddressResponse getDefaultAddressByUserEmail(String email);

}
