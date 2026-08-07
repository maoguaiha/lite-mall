package com.macro.mall.model;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 首页轮播广告
 */
@Data
public class SmsHomeAdvertise implements Serializable {
    private Long id;
    private String name;
    private String pic;
    private Date startTime;
    private Date endTime;
    private Integer status;
    private Integer clickCount;
    private Integer orderNum;
    private Integer type;
    private String url;
    private String note;
    private Integer deleteFlag;
    private Date createTime;
}
