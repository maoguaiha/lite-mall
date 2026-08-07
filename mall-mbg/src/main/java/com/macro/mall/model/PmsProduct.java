package com.macro.mall.model;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class PmsProduct implements Serializable {
    private Long id;
    private Long categoryId;
    private Long brandId;
    private String name;
    private String subtitle;
    private String mainImage;
    private String subImages;
    private String detail;
    private BigDecimal price;
    private Integer stock;
    private Integer lowStock;
    private Integer sales;
    private Integer sort;
    private Integer publishStatus;
    private Integer newStatus;
    private Integer recommendStatus;
    private String keywords;
    private String note;
    private Date createTime;
    private Date updateTime;
}