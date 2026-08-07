package com.macro.mall.model;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class PmsProductCategory implements Serializable {
    private Long id;
    private String name;
    private Long parentId;
    private Integer level;
    private Integer sort;
    private String icon;
    private Integer showStatus;
    private Integer productCount;
    private Date createTime;
    private Date updateTime;
}