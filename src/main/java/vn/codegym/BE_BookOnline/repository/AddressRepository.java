package vn.codegym.BE_BookOnline.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.codegym.BE_BookOnline.model.Address;

import java.util.List;
import java.util.Optional;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {

    @Query("SELECT a FROM Address a WHERE a.user.id = :userId AND a.isDeleted = false")
    List<Address> findByUserIdAndActiveAddress(@Param("userId") Long userId);

    @Query("select count(a) > 0 from Address a where lower(a.fullAddress) = lower(:fullAddress) and a.user.id = :userId and a.isDeleted = false")
    Boolean existsByFullAddressAndUserId(@Param("fullAddress") String fullAddress, @Param("userId") Long userId);

    @Query("SELECT COUNT(a) FROM Address a WHERE a.user.id = :userId")
    Long countByUserId(Long userId);

    @Query("SELECT a from Address a WHERE a.user.id = :userId AND a.isDefault = true AND a.isDeleted = false")
    Address findDefaultAddressByUserId(Long userId);

    @Modifying
    @Query("update Address a set a.isDefault = false where a.user.id = :userId and a.isDefault= true and a.isDeleted = false")
    void unsetDefaultAddressByUserId(@Param("userId") Long userId);

    @Query("SELECT a FROM Address a WHERE a.idAddress = :idAddress AND a.user.id = :userId")
    Optional<Address> findByIdAddressAndUserId(@Param("idAddress") Long idAddress, @Param("userId") Long userId);
}
