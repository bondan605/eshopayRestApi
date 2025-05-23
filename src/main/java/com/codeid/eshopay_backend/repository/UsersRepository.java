package com.codeid.eshopay_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.codeid.eshopay_backend.model.entity.Users;

@Repository
public interface UsersRepository extends JpaRepository<Users, Long> {

}
