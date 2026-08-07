package com.macro.mall.model;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 专题
 */
@Data
public class CmsSubject implements Serializable {
    private Long id;
    private Long categoryId;
    private String title;
    private String pic;
    private Integer productCount;
    private Integer recommendStatus;
    private Integer showStatus;
    private Integer sort;
    private String description;
    private Integer deleteFlag;
    private Date createTime;
}
