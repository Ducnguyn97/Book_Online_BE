package vn.codegym.BE_BookOnline.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import vn.codegym.BE_BookOnline.dto.request.AddressRequest;
import vn.codegym.BE_BookOnline.dto.response.AddressResponse;
import vn.codegym.BE_BookOnline.model.Address;
import vn.codegym.BE_BookOnline.model.User;
import vn.codegym.BE_BookOnline.repository.AddressRepository;
import vn.codegym.BE_BookOnline.repository.UserRepository;
import vn.codegym.BE_BookOnline.service.AddressService;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j

public class AddressServiceImpl implements AddressService {
    private final AddressRepository addressRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public List<AddressResponse> getAllAddressesByUser(String email) {
        User user = userRepository.findByEmail(email)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Không tìm thấy người dùng với email: " + email));
List<Address> addresses = addressRepository.findByUserIdAndActiveAddress(user.getId());
        return addresses.stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public AddressResponse getAddressById(String email, Long addressId) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy người dùng với email: " + email));
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy địa chỉ với ID: " + addressId));
        if(!address.getUser().getId().equals(user.getId())){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Bạn không có quyền truy cập địa chỉ này");
        }
         return mapToResponse(address);
    }


    @Override
    @Transactional
    public AddressResponse createAddress(String email, AddressRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy người dùng với email: " + email));
        if(addressRepository.existsByFullAddressAndUserId(request.getFullAddress(), user.getId())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Địa chỉ đã tồn tại");
        }
        Long addressCount = addressRepository.countByUserId(user.getId());

        if(addressCount >= 10){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bạn chỉ được phép lưu tối đa 10 địa chỉ");
        }

        boolean isDefault = (addressCount == 0) || Boolean.TRUE.equals(request.getIsDefault());
        if(isDefault){
            unsetOtherDefaultAddress(user.getId());
        }
        Address address = Address.builder()
                .contactName(request.getContactName())
                .contactPhone(request.getContactPhone())
                .provinceId(request.getProvinceId())
                .provinceName(request.getProvince())
                .districtId(request.getDistrictId())
                .districtName(request.getDistrict())
                .wardCode(request.getWardCode())
                .wardName(request.getWard())
                .street(request.getStreet())
                .building(request.getBuilding())
                .isDefault(isDefault)
                .user(user)
                .fullAddress(request.getFullAddress())
                .build();
        Address saveAddress=addressRepository.save(address);
        return mapToResponse(saveAddress);
    }

    private void unsetOtherDefaultAddress(Long userId) {
        addressRepository.unsetDefaultAddressByUserId(userId);
    }

    @Override
    @Transactional
    public AddressResponse updateAddress(String email, Long addressId, AddressRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy người dùng với email: " + email));
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy địa chỉ với ID: " + addressId));
        if(!address.getUser().getId().equals(user.getId())){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Bạn không có quyền truy cập địa chỉ này");
        }
        address.setIdAddress(addressId);
        address.setFullAddress(request.getFullAddress());
        address.setContactName(request.getContactName());
        address.setContactPhone(request.getContactPhone());
        address.setProvinceId(request.getProvinceId());
        address.setProvinceName(request.getProvince());
        address.setDistrictId(request.getDistrictId());
        address.setDistrictName(request.getDistrict());
        address.setWardCode(request.getWardCode());
        address.setWardName(request.getWard());
        address.setStreet(request.getStreet());
        address.setBuilding(request.getBuilding());

        if(request.getIsDefault() != null && request.getIsDefault()){
            unsetOtherDefaultAddress(user.getId());
            address.setIsDefault(true);
        }
        Address addressUpdated = addressRepository.save(address);
        return mapToResponse(addressUpdated);
    }

    @Override
    @Transactional
    public void deleteAddress(String email, Long addressId) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy người dùng với email: " + email));
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy địa chỉ với ID: " + addressId));
        if(!address.getUser().getId().equals(user.getId())){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Bạn không có quyền truy cập địa chỉ này");
        }
        //soft delete
        address.setDeleted(true);
        boolean wasDefault = address.getIsDefault();
        if(wasDefault){
            address.setIsDefault(false);
        }
        addressRepository.save(address);
        //tu dong set default address
        if(wasDefault){
            List<Address> remainingAddresses = addressRepository.findByUserIdAndActiveAddress(user.getId());
            if(!remainingAddresses.isEmpty()){
                Address firstAddress = remainingAddresses.get(0);
                firstAddress.setIsDefault(true);
                addressRepository.save(firstAddress);
            }
        }
    }

    @Override
    @Transactional
    public AddressResponse setDefaultAddress(String email, Long addressId) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy người dùng với email: " + email));
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy địa chỉ với ID: " + addressId));
        if(!address.getUser().getId().equals(user.getId())){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Bạn không có quyền truy cập địa chỉ này");
        }
        unsetOtherDefaultAddress(user.getId());
        address.setIsDefault(true);
        Address updateAddress = addressRepository.save(address);
        return mapToResponse(updateAddress);
    }

    @Override
    @Transactional(readOnly = true)
    public AddressResponse getDefaultAddressByUserEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy người dùng với email: " + email));
        Address defaultAddress = addressRepository.findDefaultAddressByUserId(user.getId());
        if(defaultAddress == null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy địa chỉ mặc định cho người dùng này");
        }
        return mapToResponse(defaultAddress);
    }

    private AddressResponse mapToResponse(Address address) {
        AddressResponse response = AddressResponse.builder()
                .id(address.getIdAddress())
                .contactName(address.getContactName())
                .contactPhone(address.getContactPhone())
                .province(address.getProvinceName())
                .district(address.getDistrictName())
                .ward(address.getWardName())
                .street(address.getStreet())
                .building(address.getBuilding())
                .isDefault(address.getIsDefault())
                .provinceId(address.getProvinceId())
                .districtId(address.getDistrictId())
                .wardCode(address.getWardCode())
                .build();

        response.setFullAddress(response.buildFullAddress());
        if(address.getIsDefault()){
            response.setAddressType("Mặc định");
        } else if(address.getBuilding() != null && !address.getBuilding().isEmpty()){
            response.setAddressType("Nhà riêng");
        } else {
            response.setAddressType("Văn phòng");
        }
        return response;
    }
}
