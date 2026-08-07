package com.macro.mall.model;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class CmsCartItem implements Serializable {
    private Long id;
    private Long memberId;
    private Long productId;
    private Long productSkuId;
    private String productName;
    private String productImage;
    private BigDecimal productPrice;
    private Integer quantity;
    private String skuAttributes;
    private Date createTime;
    private Date updateTime;
    private Integer deleteStatus;
}