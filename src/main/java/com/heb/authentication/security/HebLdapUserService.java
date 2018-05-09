/*
 * $Id: HebLdapUserService.java,v 1.1.1.1 2012/06/18 14:19:03 r511755 Exp $
 *
 * Copyright (c) 2010 HEB
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of HEB.
 */
package com.heb.authentication.security;

import org.springframework.dao.DataAccessException;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.ldap.search.LdapUserSearch;
import org.springframework.security.ldap.userdetails.LdapAuthoritiesPopulator;
import org.springframework.security.ldap.userdetails.UserDetailsContextMapper;

import java.util.Collection;
import java.util.List;

/**
 * @author r511759
 *
 */
public class HebLdapUserService implements UserDetailsService {

	private List<LdapUserSearch> userFinders;
	private UserDetailsContextMapper userMapper;
	private LdapAuthoritiesPopulator authPopulator;
	/* (non-Javadoc)
	 * @see org.springframework.security.core.userdetails.UserDetailsService#loadUserByUsername(java.lang.String)
	 */
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException, DataAccessException {

		DirContextOperations ldapUser = null;

		//Try to search each ldap server for the user
		for(LdapUserSearch finder : this.getUserFinders()){
			try{
				ldapUser = finder.searchForUser(username);
				if(ldapUser!=null){
					break;
				}
			}catch(UsernameNotFoundException e){
			}
		}

		if(ldapUser==null){
			throw new UsernameNotFoundException("UserModel not found on any of the specified ldap contexts");
		}

		//Populate with permissions
		Collection<? extends GrantedAuthority> authorities = this.getAuthPopulator().getGrantedAuthorities(ldapUser, username);
		//Set the fields from ldap
		UserDetails retObj = this.getUserMapper().mapUserFromContext(ldapUser, username, authorities);


		return retObj;
	}
	/**
	 * @param userFinders the userFinders to set
	 */
	public void setUserFinders(List<LdapUserSearch> userFinders) {
		this.userFinders = userFinders;
	}

	/**
	 * @return the userFinders
	 */
	public List<LdapUserSearch> getUserFinders() {
		return this.userFinders;
	}

	/**
	 * @param userMapper the userMapper to set
	 */
	public void setUserMapper(UserDetailsContextMapper userMapper) {
		this.userMapper = userMapper;
	}

	/**
	 * @return the userMapper
	 */
	public UserDetailsContextMapper getUserMapper() {
		return this.userMapper;
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
