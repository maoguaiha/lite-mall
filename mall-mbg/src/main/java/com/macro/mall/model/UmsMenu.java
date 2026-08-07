package com.macro.mall.model;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 后台菜单
 */
@Data
public class UmsMenu implements Serializable {
    private Long id;
    private Long parentId;
    private String title;
    private String name;
    private String url;
    private String icon;
    private Integer type;
    private Integer sort;
    private Date createTime;
    private Date updateTime;
}
