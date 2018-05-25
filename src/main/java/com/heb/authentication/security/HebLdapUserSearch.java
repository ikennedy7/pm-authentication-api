/*
 * $Id: HebLdapUserSearch.java,v 1.1.1.1 2012/06/18 14:19:03 r511755 Exp $
 *
 * Copyright (c) 2010 HEB
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of HEB.
 */
package com.heb.authentication.security;


import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.ldap.core.ContextSource;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.ldap.SpringSecurityLdapTemplate;
import org.springframework.security.ldap.search.LdapUserSearch;

/**
 * @author r511755
 *
 */
public class HebLdapUserSearch implements LdapUserSearch {

	private ContextSource ctx;
	private String searchBasePattern;
	private String searchFilter;

	/**
	 * 
	 */
	public HebLdapUserSearch(){}

	/**
	 * @param ctxi
	 */
	public HebLdapUserSearch(ContextSource ctxi){
		this.setCtx(ctxi);
	}

	@Override
	public DirContextOperations searchForUser(String username) throws UsernameNotFoundException {

		SpringSecurityLdapTemplate ldap = new SpringSecurityLdapTemplate(this.getCtx());
		DirContextOperations retObj = null;
		try{
			retObj = ldap.searchForSingleEntry(this.searchBasePattern, this.searchFilter, new Object[]{username});
		}catch(IncorrectResultSizeDataAccessException e){
			throw new UsernameNotFoundException("Username not found");
		}
		return retObj;
	}

	/**
	 * @param ctx the ctx to set
	 */
	public void setCtx(ContextSource ctx) {
		this.ctx = ctx;
	}

	/**
	 * @return the ctx
	 */
	public ContextSource getCtx() {
		return this.ctx;
	}

	/**
	 * @param searchDnPattern the searchDnPattern to set
	 */
	public void setSearchBasePattern(String searchDnPattern) {
		this.searchBasePattern = searchDnPattern;
	}

	/**
	 * @return the searchDnPattern
	 */
	public String getSearchBasePattern() {
		return this.searchBasePattern;
	}

	/**
	 * @param searchFilter the searchFilter to set
	 */
	public void setSearchFilter(String searchFilter) {
		this.searchFilter = searchFilter;
	}

	/**
	 * @return the searchFilter
	 */
	public String getSearchFilter() {
		return this.searchFilter;
	}

	public static class HebUserRoleFormat {
	}
}
