/*
 * $Id: Role.java,v 1.1.1.1 2012/06/18 14:19:03 r511755 Exp $
 *
 * Copyright (c) 2010 HEB
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of HEB.
 */
package com.heb.authentication.security;

import org.springframework.security.core.GrantedAuthority;


/**
 * @author r511759
 *
 */
public class Role implements GrantedAuthority {
	private static final long serialVersionUID = 1L;

	public static final String ROLE_PREFIX = "ROLE_";
	private String roleName;
	private String roleDesc;

	public Role(){}

	public Role(String roleNamep, String roleDescp){
		this.roleName = roleNamep;
		this.roleDesc = roleDescp;
	}

	/* (non-Javadoc)
	 * @see org.springframework.security.core.GrantedAuthority#getAuthority()
	 */
	@Override
	public String getAuthority() {
		return ROLE_PREFIX+this.roleName;
	}
	/**
	 * @param roleName the roleName to set
	 */
	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}
	/**
	 * @return the roleName
	 */
	public String getRoleName() {
		return this.roleName;
	}
	/**
	 * @param roleDesc the roleDesc to set
	 */
	public void setRoleDesc(String roleDesc) {
		this.roleDesc = roleDesc;
	}
	/**
	 * @return the roleDesc
	 */
	public String getRoleDesc() {
		return this.roleDesc;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.roleDesc == null) ? 0 : this.roleDesc.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {

		if (obj instanceof GrantedAuthority){
			return ((GrantedAuthority)obj).getAuthority().equals(this.getAuthority());
		}
		return false;
	}

	@Override
	public String toString() {
		return "Role " + this.roleName + ": " + this.roleDesc;
	}

}
