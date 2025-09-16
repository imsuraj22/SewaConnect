package com.repository;

import com.entity.DonationImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DonationImageRepo extends JpaRepository<DonationImage,Long> {
}
