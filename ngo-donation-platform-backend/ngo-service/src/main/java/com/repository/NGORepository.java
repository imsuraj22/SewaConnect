package com.repository;

import com.entity.NGO;
import com.entity.NGOStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface NGORepository extends JpaRepository<NGO,Long> {
//    boolean existsByEmail(String email);
//    boolean existsByPhoneNumber(String phoneNumber);
//    boolean existsByName(String name);
//
//    // 🔹 Fetch by unique identifiers
//    Optional<NGO> findByEmail(String email);

    // 🔹 Search / discover NGOs
    List<NGO> findByNameContainingIgnoreCase(String name);
    List<NGO> findByAddressContainingIgnoreCase(String address);
    List<NGO>  findByStatus(NGOStatus status);
}
