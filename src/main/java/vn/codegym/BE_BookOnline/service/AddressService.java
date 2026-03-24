package vn.codegym.BE_BookOnline.service;

import vn.codegym.BE_BookOnline.dto.request.AddressRequest;
import vn.codegym.BE_BookOnline.dto.response.AddressResponse;

import java.util.List;

public interface AddressService {
    List<AddressResponse> getAllAddressesByUser(String email);

    AddressResponse getAddressById(String email,Long addressId);

    AddressResponse createAddress(String email, AddressRequest request);

    AddressResponse updateAddress(String email, Long addressId, AddressRequest request );

    void deleteAddress(String email, Long addressId);

    AddressResponse setDefaultAddress(String email, Long addressId);

    AddressResponse getDefaultAddressByUserEmail(String email);

}
