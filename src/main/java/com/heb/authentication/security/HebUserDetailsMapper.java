/*
 * $Id: HebUserDetailsMapper.java,v 1.1.1.1 2012/06/18 14:19:03 r511755 Exp $
 *
 * Copyright (c) 2010 HEB
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of HEB.
 */
package com.heb.authentication.security;

import org.springframework.ldap.core.DirContextAdapter;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.ldap.userdetails.UserDetailsContextMapper;

import java.util.Collection;

/**
 * @author r511759
 *
 */
public class HebUserDetailsMapper implements UserDetailsContextMapper {

	private static final String MAIL = "mail";
	private static final String DEPARTMENT_NUMBER = "departmentNumber";
	private static final String HEB_JOB_DESC = "hebJobDesc";
	private static final String HEB_JOB_CODE = "hebJobCode";
	private static final String DISPLAY_NAME = "displayName";
	private static final String HEB_KEY =  "!@#$%^";
	private static final String MOBILE = "mobile";



    @Override
    public UserDetails mapUserFromContext(DirContextOperations ctx, String username, Collection<? extends GrantedAuthority> authorities) {
        HebUserDetails retObj = new HebUserDetails(username,HEB_KEY, false, false, false, false, authorities);
        retObj.setDisplayName(ctx.getStringAttribute(HebUserDetailsMapper.DISPLAY_NAME));
        retObj.setHebJobCode(ctx.getStringAttribute(HebUserDetailsMapper.HEB_JOB_CODE));
        retObj.setHebJobDesc(ctx.getStringAttribute(HebUserDetailsMapper.HEB_JOB_DESC));
        retObj.setDepartmentNumber(ctx.getStringAttribute(HebUserDetailsMapper.DEPARTMENT_NUMBER));
        retObj.setMail(ctx.getStringAttribute(HebUserDetailsMapper.MAIL));
        retObj.setMobile(ctx.getStringAttribute(HebUserDetailsMapper.MOBILE));
        retObj.setHebGLlocation(ctx.getStringAttribute("hebGLlocation"));
        retObj.setFirstName(ctx.getStringAttribute("givenname"));
        retObj.setLastName(ctx.getStringAttribute("sn"));
        //retObj.setDisplayName()

        return retObj;
    }

    @Override
	public void mapUserToContext(UserDetails arg0, DirContextAdapter arg1) {
		// TODO Auto-generated method stub

	}

}
