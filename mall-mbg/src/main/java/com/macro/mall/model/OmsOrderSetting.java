package com.macro.mall.model;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 订单设置（单例配置，id=1）
 */
@Data
public class OmsOrderSetting implements Serializable {
    private Long id;
    private Integer flashOrderOvertime;
    private Integer normalOrderOvertime;
    private Integer confirmOvertime;
    private Integer finishOvertime;
    private Integer commentOvertime;
    private Integer memberLevel;
    private Integer autoComment;
    private Integer deleteFlag;
    private Date createTime;
    private Date updateTime;
}
