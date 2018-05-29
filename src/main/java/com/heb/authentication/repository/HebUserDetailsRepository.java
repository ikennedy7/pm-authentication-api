package com.heb.authentication.repository;

import com.heb.authentication.entity.HebUserDetailsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by k792389 on 5/21/2018.
 */
@Repository
public interface HebUserDetailsRepository extends JpaRepository<HebUserDetailsEntity, String> {
	HebUserDetailsEntity findByUsername(String username);
}
