package com.macro.mall.admin.dto;

import java.math.BigDecimal;

public class SeckillProductVO {
    private Long id;
    private Long sessionId;
    private Long productId;
    private String productName;
    private BigDecimal seckillPrice;
    private Integer seckillStock;
    private Integer seckillSales;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getSessionId() { return sessionId; }
    public void setSessionId(Long sessionId) { this.sessionId = sessionId; }
    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    public BigDecimal getSeckillPrice() { return seckillPrice; }
    public void setSeckillPrice(BigDecimal seckillPrice) { this.seckillPrice = seckillPrice; }
    public Integer getSeckillStock() { return seckillStock; }
    public void setSeckillStock(Integer seckillStock) { this.seckillStock = seckillStock; }
    public Integer getSeckillSales() { return seckillSales; }
    public void setSeckillSales(Integer seckillSales) { this.seckillSales = seckillSales; }
}
