package com.heb.authentication.entity;

import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;


/**
 * Created by vn01571 on 5/1/2017.
 */
@Entity
@Table(name = "USR_JTI", schema = "PATAUTH")
public class UserJTIEntity implements Serializable {
    private static final long serialVersionUID = 1L;


    @Id
    @Column(name="JWT_ID",columnDefinition = "varchar2", nullable = false)
    private String jti;

    @Column(name="USR_ID",columnDefinition = "char", length = 7)
    private String username;


    @Column(name="USR_NM",columnDefinition = "varchar2", nullable = false )
    private String fullusername;



    @Column(name="LOG_TS")
    private Date logintime;

    @Column(name="RFRSH_TS")
    private Date refreshtime;

    @Type(type = "yes_no")
    @Column(name = "BAT_SW", length = 1, columnDefinition = "char", nullable = false)
    private boolean batch;


    @Column(name="IP_TXT",length = 30)
    private String ip_txt;




    protected UserJTIEntity() { }
    
    public UserJTIEntity(String username, String jti, Date logintime, Boolean batch) {
        this.username = username;
        this.fullusername=username;
        this.jti = jti;
        this.logintime = logintime;
        this.batch=batch;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username){this.username=username;}

    public String getJti() {
        return jti;
    }

    public void setJti(String jti){ this.jti=jti;}

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

    @Override
    public String toString() {
        return String.format(
                "UserJTI[id=%d, username='%s', jti='%s' ,logintime='%s']",
                 username,fullusername, jti,logintime );
    }

    public String getFullusername() {
        return fullusername;
    }

    public void setFullusername(String fullusername) {
        this.fullusername = fullusername;
    }

    public Date getRefreshtime() {
        return refreshtime;
    }

    public void setRefreshtime(Date refreshtime) {
        this.refreshtime = refreshtime;
    }

    public boolean isBatch() {
        return batch;
    }

    public void setBatch(boolean batch) {
        this.batch = batch;
    }


}
