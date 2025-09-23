package com.repository;

import com.entity.ClaimRequestData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClaimRequestDataRepo extends JpaRepository<ClaimRequestData,Long> {
    public void deleteByDonationId(Long id);
}
