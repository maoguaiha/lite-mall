package com.macro.mall.model;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class OmsOrderItem implements Serializable {
    private Long id;
    private Long orderId;
    private String orderSn;
    private Long productId;
    private Long productSkuId;
    private String productName;
    private String productImage;
    private BigDecimal productPrice;
    private Integer quantity;
    private BigDecimal totalPrice;
    private Date createTime;
}