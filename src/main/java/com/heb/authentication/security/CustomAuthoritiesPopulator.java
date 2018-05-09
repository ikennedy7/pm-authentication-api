/*
 * m228250\:m228250 PatAuthoritiesPopulator.java, v1.0 5/19/2015 3:16 PM m228250 $
 *
 * Copyright (c) 2015 HEB
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of HEB.
 */
package com.heb.authentication.security;

import org.springframework.ldap.core.DirContextOperations;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.HashSet;

/**
 * PatAuthoritiesPopulator.java.
 *
 * @author m228250
 */
public class CustomAuthoritiesPopulator extends HebAuthoritiesPopulator {

    @Override
    public Collection<GrantedAuthority> getGrantedAuthorities(DirContextOperations ldapCtx, String username) {
        Collection<GrantedAuthority> authorities = new HashSet<>();
        authorities.addAll(super.getGrantedAuthorities(ldapCtx, username));
        //Do any custom authorities
        return authorities;
    }
}