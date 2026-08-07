package com.macro.mall.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 优惠券领取/核销记录（幂等真相源：同一 member+coupon 仅一条未使用记录）
 * useStatus: 0 未领取 1 已领取未用 2 已使用 3 已过期
 */
@Data
@TableName("sms_coupon_history")
public class SmsCouponHistory implements Serializable {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long couponId;
    private Long memberId;
    private Long orderId;
    private Integer useStatus;
    private Date receiveTime;
    private Date useTime;
    @TableLogic(value = "0", delval = "2")
    private Integer deleteFlag;
}
