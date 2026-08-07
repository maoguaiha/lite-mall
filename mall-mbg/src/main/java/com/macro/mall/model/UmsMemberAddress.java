package com.macro.mall.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 会员收货地址
 */
@Data
@TableName("ums_member_address")
public class UmsMemberAddress implements Serializable {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long memberId;
    private String name;
    private String phone;
    private String province;
    private String city;
    private String district;
    private String detailAddress;
    private Integer isDefault;
    @TableLogic(value = "0", delval = "2")
    private Integer deleteFlag;
    private Date createTime;
}
