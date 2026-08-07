package com.macro.mall.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 订单评价（用户端提交，商家端审核）
 * status: 1 待审核 2 通过 3 驳回
 */
@Data
@TableName("oms_order_comment")
public class OmsOrderComment implements Serializable {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long orderId;
    private Long orderItemId;
    private Long memberId;
    private Long productId;
    private Integer star;
    private String content;
    private String pictures;
    private Integer status;
    private String reply;
    @TableLogic(value = "0", delval = "2")
    private Integer deleteFlag;
    private Date createTime;
}
