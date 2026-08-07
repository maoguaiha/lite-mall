package com.macro.mall.model;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 会员表
 */
@Data
public class UmsMember implements Serializable {
    private Long id;
    private Long memberLevelId;
    private String username;
    private String password;
    private String nickname;
    private String phone;
    private Integer status;
    private Date createTime;
    private Date updateTime;
    private String icon;
    private Integer gender;
    private Date birthday;
    private String city;
    private String job;
    private String personalizedSignature;
    private Integer sourceType;
    private Integer integration;
    private Integer growth;
    private Integer luckeyCount;
    private Integer historyIntegration;
}
