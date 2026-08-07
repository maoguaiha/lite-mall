package com.macro.mall.model;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class PmsProductSku implements Serializable {
    private Long id;
    private Long productId;
    private String skuCode;
    private String skuName;
    private String attributes;
    private BigDecimal price;
    private Integer stock;
    private String image;
    private Integer sort;
    private Date createTime;
    private Date updateTime;
}