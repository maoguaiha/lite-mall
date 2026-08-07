package com.macro.mall.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 秒杀商品（Redis 中库存为唯一真相源，DB 仅作落库与对账）
 */
@Data
@TableName("sms_seckill_product")
public class SmsSeckillProduct implements Serializable {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long sessionId;
    private Long productId;
    private BigDecimal seckillPrice;
    private Integer seckillStock;
    private Integer seckillSales;
    @TableLogic(value = "0", delval = "2")
    private Integer deleteFlag;
}
