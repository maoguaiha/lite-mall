package com.macro.mall.admin.dto;

import java.math.BigDecimal;
import java.util.Date;

public class SeckillSessionVO {
    private Long id;
    private String name;
    private Date startTime;
    private Date endTime;
    private Integer status;
    private Long flashCount;
    private BigDecimal totalAmount;

    public SeckillSessionVO() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Date getStartTime() { return startTime; }
    public void setStartTime(Date startTime) { this.startTime = startTime; }
    public Date getEndTime() { return endTime; }
    public void setEndTime(Date endTime) { this.endTime = endTime; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public Long getFlashCount() { return flashCount; }
    public void setFlashCount(Long flashCount) { this.flashCount = flashCount; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
}
