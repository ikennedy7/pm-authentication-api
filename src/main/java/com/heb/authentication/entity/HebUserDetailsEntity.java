package com.heb.authentication.entity;

import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name ="HEB_USER_DETAILS")
public class HebUserDetailsEntity implements Serializable{
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="USR_ID",columnDefinition = "char", length = 7)
	private String username;
	@Column(name="PWD",columnDefinition = "char", length = 8)
	private String password;
	@Column(name="DISPLAY_NAME",columnDefinition = "char", length = 7)
	private String displayName;
	@Column(name="FIRSTNAME",columnDefinition = "char", length = 7)
	private String firstName;
	@Column(name="LASTNAME",columnDefinition = "char", length = 7)
	private String lastName;
	@Column(name="HEBJOBCODE",columnDefinition = "char", length = 7)
	private String hebJobCode;
	@Column(name="HEBJOBDESC",columnDefinition = "char", length = 7)
	private String hebJobDesc;
	@Column(name="DEPARTMENT_NUMBER",columnDefinition = "char", length = 7)
	private String departmentNumber;
	@Column(name="MAIL",columnDefinition = "char", length = 7)
	private String mail;
	@Column(name="ROLE",columnDefinition = "char", length = 7)
	private String role;
	@Column(name="MOBILE",columnDefinition = "char", length = 7)
	private String mobile;

	@Column(name="LOG_TS")
	private Date logintime;

	//Valid only for vendors
	@Column(name="VENDORORGID",columnDefinition = "char", length = 7)
	private String vendorOrgId;
	@Column(name="VENDORORGNAME",columnDefinition = "char", length = 7)
	private String vendorOrgName;
	@Column(name="HEBGLLOCATION",columnDefinition = "char", length = 7)
	private String hebGLlocation;

	@Type(type = "yes_no")
	@Column(name = "BAT_SW", length = 1, columnDefinition = "char", nullable = false)
	private boolean batch;

	@Column(name="IP_TXT",length = 30)
	private String ip_txt;



	protected HebUserDetailsEntity() { }

	public HebUserDetailsEntity(String username, Date logintime, Boolean batch) {
		this.username = "user";
		this.displayName = username;
		this.logintime = logintime;
		this.batch=batch;
	}


	public Date getLogintime() {
		return logintime;
	}


	public String getIp_txt() {
		return ip_txt;
	}

	public void setIp_txt(String ip_txt) {
		this.ip_txt = ip_txt;
	}

	public void setLogintime(Date logintime){this.logintime=logintime; }


	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public String toString() {
		return "HebUserDetailsEntity{" +
				"username='" + username + '\'' +
				", password='" + password + '\'' +
				'}';
	}
}
