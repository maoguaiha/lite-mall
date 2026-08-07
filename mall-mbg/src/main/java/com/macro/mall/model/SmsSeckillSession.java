package com.macro.mall.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 秒杀场次
 */
@Data
@TableName("sms_seckill_session")
public class SmsSeckillSession implements Serializable {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private Date startTime;
    private Date endTime;
    private Integer status;
    @TableLogic(value = "0", delval = "2")
    private Integer deleteFlag;
}
