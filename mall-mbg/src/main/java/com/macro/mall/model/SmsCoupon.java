package com.macro.mall.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 优惠券模板
 * usableRange: ALL 全品类 / CATEGORY:xx 指定分类
 */
@Data
@TableName("sms_coupon")
public class SmsCoupon implements Serializable {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private BigDecimal amount;
    private BigDecimal minPoint;
    private Integer perLimit;
    private Integer publishCount;
    private Integer receivedCount;
    private String usableRange;
    private Date startTime;
    private Date endTime;
    @TableLogic(value = "0", delval = "2")
    private Integer deleteFlag;
    private Date createTime;
}
