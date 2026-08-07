package com.macro.mall.model;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class OmsOrder implements Serializable {
    private Long id;
    private String orderSn;
    private Long memberId;
    private String memberUsername;
    private BigDecimal totalAmount;
    private BigDecimal payAmount;
    private Integer payStatus;
    private String payType;
    private Integer status;
    private String receiverName;
    private String receiverPhone;
    private String receiverProvince;
    private String receiverCity;
    private String receiverDistrict;
    private String receiverDetailAddress;
    private String deliveryCompany;
    private String deliverySn;
    private Date createTime;
    private Date payTime;
    private Date deliveryTime;
    private Date receiveTime;
    private Date cancelTime;
}