package com.macro.mall.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 商家端管理员账号
 */
@Data
@TableName("ums_admin")
public class UmsAdmin implements Serializable {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String username;
    private String password;
    private String nickname;
    private String role;
    @TableLogic(value = "0", delval = "2")
    private Integer deleteFlag;
    private Date createTime;
}
