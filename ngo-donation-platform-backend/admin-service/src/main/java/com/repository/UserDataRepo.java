package com.repository;

import com.entity.UserData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserDataRepo extends JpaRepository<UserData,Long> {
}
