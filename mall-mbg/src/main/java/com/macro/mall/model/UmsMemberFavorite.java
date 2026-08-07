package com.macro.mall.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 会员商品收藏
 */
@Data
@TableName("ums_member_favorite_product")
public class UmsMemberFavorite implements Serializable {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long memberId;
    private Long productId;
    private String productName;
    private String productPic;
    private BigDecimal productPrice;
    private Date createTime;
}
