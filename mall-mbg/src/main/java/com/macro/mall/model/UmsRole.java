package com.macro.mall.model;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 后台角色
 */
@Data
public class UmsRole implements Serializable {
    private Long id;
    private String name;
    private String code;
    private String description;
    private Integer status;
    private Integer sort;
    private Integer deleteFlag;
    private Date createTime;
    private Date updateTime;
}
