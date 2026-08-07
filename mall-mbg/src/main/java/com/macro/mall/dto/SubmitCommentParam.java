package com.macro.mall.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SubmitCommentParam {
    @NotNull(message = "订单ID不能为空")
    private Long orderId;
    private Long orderItemId;
    @NotNull(message = "商品ID不能为空")
    private Long productId;
    @NotNull(message = "评分不能为空")
    private Integer star;
    private String content;
    private String pictures;
}
