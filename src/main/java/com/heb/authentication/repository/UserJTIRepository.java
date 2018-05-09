package com.heb.authentication.repository;


import com.heb.authentication.entity.UserJTIEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by vn01571 on 5/1/2017.
 */
public interface UserJTIRepository extends JpaRepository<UserJTIEntity, String> {



    UserJTIEntity findByJti(String jti);


    UserJTIEntity findByUsername(String username);
}