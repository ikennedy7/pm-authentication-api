/*
 * $Id: Permission.java,v 1.1.1.1 2012/06/18 14:19:03 r511755 Exp $
 *
 * Copyright (c) 2010 HEB
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of HEB.
 */
package com.heb.authentication.security;

import org.springframework.security.core.GrantedAuthority;

import java.util.Date;


/**
 * @author r511759
 *
 */
public class Permission implements GrantedAuthority {
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	public static final String RESC_PREFIX = "RESC_";
	/**
	 * 
	 */
	public static final String ACTION_PREFIX = "_ACTION_";
	private String rescName;
	private String rescDef;
	private String rescScrnNm;
	private String rescTypeAbb;
	private String rescTypeDesc;
	private String accessTypeAbb;
	private String accessTypeDesc;
	private Date dtEff;
	private Date dtExp;


	/* (non-Javadoc)
	 * @see org.springframework.security.core.GrantedAuthority#getAuthority()
	 */
	@Override
	public String getAuthority() {
		return Permission.RESC_PREFIX+this.rescName+ Permission.ACTION_PREFIX+this.accessTypeAbb;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.accessTypeAbb == null) ? 0 : this.accessTypeAbb.hashCode());
		return result;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof GrantedAuthority){
			return ((GrantedAuthority)obj).getAuthority().equals(this.getAuthority());
		}
		return false;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Permission: "+this.rescName+" "+this.accessTypeAbb;
	}


	/**
	 * @param rescName the rescName to set
	 */
	public void setRescName(String rescName) {
		this.rescName = rescName;
	}


	/**
	 * @return the rescName
	 */
	public String getRescName() {
		return this.rescName;
	}


	/**
	 * @param rescDef the rescDef to set
	 */
	public void setRescDef(String rescDef) {
		this.rescDef = rescDef;
	}


	/**
	 * @return the rescDef
	 */
	public String getRescDef() {
		return this.rescDef;
	}


	/**
	 * @param rescScrnNm the rescScrnNm to set
	 */
	public void setRescScrnNm(String rescScrnNm) {
		this.rescScrnNm = rescScrnNm;
	}


	/**
	 * @return the rescScrnNm
	 */
	public String getRescScrnNm() {
		return this.rescScrnNm;
	}


	/**
	 * @param rescTypeAbb the rescTypeAbb to set
	 */
	public void setRescTypeAbb(String rescTypeAbb) {
		this.rescTypeAbb = rescTypeAbb;
	}


	/**
	 * @return the rescTypeAbb
	 */
	public String getRescTypeAbb() {
		return this.rescTypeAbb;
	}


	/**
	 * @param rescTypeDesc the rescTypeDesc to set
	 */
	public void setRescTypeDesc(String rescTypeDesc) {
		this.rescTypeDesc = rescTypeDesc;
	}


	/**
	 * @return the rescTypeDesc
	 */
	public String getRescTypeDesc() {
		return this.rescTypeDesc;
	}


	/**
	 * @param accessTypeAbb the accessTypeAbb to set
	 */
	public void setAccessTypeAbb(String accessTypeAbb) {
		this.accessTypeAbb = accessTypeAbb;
	}


	/**
	 * @return the accessTypeAbb
	 */
	public String getAccessTypeAbb() {
		return this.accessTypeAbb;
	}


	/**
	 * @param accessTypeDesc the accessTypeDesc to set
	 */
	public void setAccessTypeDesc(String accessTypeDesc) {
		this.accessTypeDesc = accessTypeDesc;
	}


	/**
	 * @return the accessTypeDesc
	 */
	public String getAccessTypeDesc() {
		return this.accessTypeDesc;
	}


	/**
	 * @param dtEff the dtEff to set
	 */
	public void setDtEff(Date dtEff) {
		this.dtEff = dtEff;
	}


	/**
	 * @return the dtEff
	 */
	public Date getDtEff() {
		return this.dtEff;
	}


	/**
	 * @param dtExp the dtExp to set
	 */
	public void setDtExp(Date dtExp) {
		this.dtExp = dtExp;
	}


	/**
	 * @return the dtExp
	 */
	public Date getDtExp() {
		return this.dtExp;
	}

}
