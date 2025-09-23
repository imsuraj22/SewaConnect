package com.repository;

import com.entity.UserData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserDataRepo extends JpaRepository<UserData,Long> {
}
