/*
 * $Id: HebAuthoritiesPopulator.java,v 1.1.1.1 2012/06/18 14:19:03 r511755 Exp $
 *
 * Copyright (c) 2010 HEB
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of HEB.
 */
package com.heb.authentication.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.ldap.userdetails.LdapAuthoritiesPopulator;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * @author r511759
 *
 */
public class HebAuthoritiesPopulator implements LdapAuthoritiesPopulator {

	private static final String HEB_JOB_CODE = "hebJobCode";

    JdbcTemplate arbafDao;
    @Value("${arbaf.appid}")
	private String applAbb;
    private String findApplicationQuery = "select appl_id from appl_nm where appl_nm=?";
    private String findRolesByOnepass = "select usr_sec_grp.usr_role_cd, USR_ROLE.usr_role_abb, USR_ROLE.usr_role_des " +
			"from usr_role, usr_sec_grp " +
			"where usr_sec_grp.usr_role_cd=usr_role.usr_role_cd " +
			"and appl_id=? and usr_sec_grp.usr_id=?";
    private String findRolesByOnepassAndJobCd = "select usr_role.usr_role_cd, usr_role_abb, usr_role_des "+
                                                      "from idm, usr_role, usr_sec_grp  " +
                                                        "where idm.usr_id=usr_sec_grp.usr_id  "  +
                                                        "and usr_sec_grp.usr_role_cd=usr_role.usr_role_cd " +
                                                        "and appl_id=? "  +
                                                        "and idm.usr_id=?  "  +
                                                        "union " +
                                                        "select usr_role.usr_role_cd, usr_role_abb, usr_role_des " +
                                                        "from idm, usr_role, usr_sec_grp, job_cd, usr_role_job_cd " +
                                                        "where job_cd.job_cd=usr_role_job_cd.job_cd " +
                                                        "and usr_role_job_cd.usr_role_cd=usr_role.usr_role_cd " +
                                                        "and appl_id=? "  +
                                                        "and job_cd.job_cd=? ";

    private String findResourcesByRolecd = "select resrc_nm, acs_abb" +
            " from sec_grp_resrc, access_type, resrc" +
            " where sec_grp_resrc.acs_cd=access_type.acs_cd" +
            " and sec_grp_resrc.resrc_id=resrc.resrc_id " +
            " and usr_role_cd=?";

    private RowMapper<Permission> rm = new RowMapper<Permission>() {
        @Override
        public Permission mapRow(ResultSet rs, int rowNum) throws SQLException {
            Permission retObj = new Permission();
            retObj.setRescName(rs.getString("resrc_nm"));
            retObj.setAccessTypeAbb(rs.getString("acs_abb"));
            return retObj;
        }
    };
	/* (non-Javadoc)
	 * @see org.springframework.security.ldap.userdetails.LdapAuthoritiesPopulator#getGrantedAuthorities(org.springframework.ldap.core.DirContextOperations, java.lang.String)
	 */
	@Override
	public Collection<GrantedAuthority> getGrantedAuthorities(DirContextOperations ldapCtx, String username) {

		//Grab the user so that we can see his roles.
		if(username==null){
			throw new IllegalArgumentException("A username is required to get GrantedAuthorities");
		}
		if(applAbb==null){
			throw new IllegalArgumentException("Application id is null");
		}
		List<Integer> appl = arbafDao.queryForList(findApplicationQuery, Integer.class, applAbb);
		if(appl==null || appl.size()!=1){
			throw new IllegalArgumentException("Application has not been configured to use the targeted ARBAF environment");
		}
		int applId= appl.get(0);
		
		String jobCode =  null;
		if(ldapCtx != null) {
			jobCode = ldapCtx.getStringAttribute(HEB_JOB_CODE);
		}
		Set<GrantedAuthority> roles = new HashSet<GrantedAuthority>();
        List<Map<String, Object>> results = null;

        if(jobCode!=null){
            results=arbafDao.queryForList(findRolesByOnepassAndJobCd, applId, username, applId, jobCode);
		}else{
            results=arbafDao.queryForList(findRolesByOnepass, applId, username);
        }

        for(Map<String, Object> role : results){
            roles.add(new Role(role.get("usr_role_abb").toString(), role.get("usr_role_des").toString()));
            roles.addAll(arbafDao.query(findResourcesByRolecd, rm, role.get("usr_role_cd")));
        }

		//Build Authorities
		return roles;
	}

	/**
	 * @param applAbb the applAbb to set
	 */
	public void setApplAbb(String applAbb) {
		this.applAbb = applAbb;
	}
	/**
	 * @return the applAbb
	 */
	public String getApplAbb() {
		return this.applAbb;
	}


    public JdbcTemplate getArbafDao() {
        return arbafDao;
    }

    public void setArbafDao(JdbcTemplate arbafDao) {
        this.arbafDao = arbafDao;
    }


}
