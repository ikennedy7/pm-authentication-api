/*
 * $Id: HebUserService.java,v 1.1.1.1 2012/06/18 14:19:03 r511755 Exp $
 *
 * Copyright (c) 2010 HEB
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of HEB.
 */
package com.heb.authentication.security;

import com.heb.authentication.entity.HebUserDetailsEntity;
import com.heb.authentication.repository.HebUserDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.ldap.userdetails.LdapAuthoritiesPopulator;
import org.springframework.security.ldap.userdetails.UserDetailsContextMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author r511759
 *
 */
@Service
public class HebUserService implements UserDetailsService {


	@Autowired
	private HebUserDetailsRepository hebUserDetailsRepository;
	private LdapAuthoritiesPopulator authPopulator;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException, DataAccessException {
//
//		DirContextOperations  ctx=  null;
		HebUserDetailsEntity hebUserDetailsEntity = this.hebUserDetailsRepository.findByUsername("user");
		List<HebUserDetailsEntity> hebUserDetailsEntities = this.hebUserDetailsRepository.findAll();
//
//
//		//Populate with permissions
//		HebAuthoritiesPopulator hebAuthoritiesPopulator = new HebAuthoritiesPopulator();
		Collection<? extends GrantedAuthority> grantedAuthorities =	this.authPopulator.getGrantedAuthorities(null, "sadc001");
//
//		UserDetails retObj2 = hebUserDetailsMapper.createHEBUserOnFly(ctx, username, grantedAuthorities);
//		return retObj2;
//		List<GrantedAuthority> authorities = new ArrayList<>();
		return new HebUserDetails("sadc001", "{noop}happy123", true, true, true, true, grantedAuthorities);
	}

	/**
	 * @param authPopulator the authPopulator to set
	 */
	public void setAuthPopulator(LdapAuthoritiesPopulator authPopulator) {
		this.authPopulator = authPopulator;
	}

	/**
	 * @return the authPopulator
	 */
	public LdapAuthoritiesPopulator getAuthPopulator() {
		return this.authPopulator;
	}
}
