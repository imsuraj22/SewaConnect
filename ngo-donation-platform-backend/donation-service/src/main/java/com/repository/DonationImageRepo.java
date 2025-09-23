package com.repository;

import com.entity.DonationImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DonationImageRepo extends JpaRepository<DonationImage,Long> {
}
