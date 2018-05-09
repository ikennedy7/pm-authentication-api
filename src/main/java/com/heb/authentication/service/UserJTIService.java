package com.heb.authentication.service;

import com.heb.authentication.entity.UserJTIEntity;
import com.heb.authentication.repository.UserJTIRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by vn01571 on 5/1/2017.
 */

@Service
public class UserJTIService {

    @Autowired
    UserJTIRepository userJTIRepository;

    public UserJTIEntity findbyJTI(String jti){
        return this.userJTIRepository.findByJti(jti);
    }

    public UserJTIEntity findbyusername(String username){
        return this.userJTIRepository.findByUsername(username);
    }

//    public void deleteUserJTI(String jti){
//        this.userJTIRepository.delete(jti);
//    }

    public void deleteUserEntity(UserJTIEntity userJTIEntity){this.userJTIRepository.delete(userJTIEntity); }

    public UserJTIEntity saveUserJTI(UserJTIEntity userJTIEntity){
        return this.userJTIRepository.save(userJTIEntity);
    }
    public void listUserJTI(){
        for (UserJTIEntity userJTIEntity : this.userJTIRepository.findAll()) {
            userJTIEntity.toString();}
    }
}
