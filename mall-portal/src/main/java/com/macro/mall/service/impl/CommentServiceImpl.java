package com.macro.mall.portal.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.PageHelper;
import com.macro.mall.common.api.CommonPage;
import com.macro.mall.common.exception.Asserts;
import com.macro.mall.dto.SubmitCommentParam;
import com.macro.mall.mapper.OmsOrderCommentMapper;
import com.macro.mall.mapper.OmsOrderMapper;
import com.macro.mall.model.OmsOrder;
import com.macro.mall.model.OmsOrderComment;
import com.macro.mall.service.UmsMemberService;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class CommentServiceImpl {

    private final OmsOrderCommentMapper commentMapper;
    private final OmsOrderMapper orderMapper;
    private final UmsMemberService memberService;

    public CommentServiceImpl(OmsOrderCommentMapper commentMapper, OmsOrderMapper orderMapper,
                              UmsMemberService memberService) {
        this.commentMapper = commentMapper;
        this.orderMapper = orderMapper;
        this.memberService = memberService;
    }

    private Long currentMemberId() {
        return memberService.getCurrentMember().getId();
    }

    public void submit(SubmitCommentParam param) {
        Long memberId = currentMemberId();
        OmsOrder order = orderMapper.selectByPrimaryKey(param.getOrderId());
        if (order == null || !order.getMemberId().equals(memberId)) {
            Asserts.fail("订单不存在");
        }
        if (order.getStatus() != 4) {
            Asserts.fail("仅已完成订单可评价");
        }
        OmsOrderComment comment = new OmsOrderComment();
        comment.setOrderId(param.getOrderId());
        comment.setOrderItemId(param.getOrderItemId());
        comment.setMemberId(memberId);
        comment.setProductId(param.getProductId());
        comment.setStar(param.getStar());
        comment.setContent(param.getContent());
        comment.setPictures(param.getPictures());
        comment.setStatus(1);
        comment.setCreateTime(new Date());
        commentMapper.insert(comment);
    }

    public CommonPage<OmsOrderComment> myList(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        return CommonPage.restPage(commentMapper.selectList(
                new QueryWrapper<OmsOrderComment>().eq("member_id", currentMemberId()).orderByDesc("id")));
    }

    public CommonPage<OmsOrderComment> productList(Long productId, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        return CommonPage.restPage(commentMapper.selectList(
                new QueryWrapper<OmsOrderComment>().eq("product_id", productId).eq("status", 2).orderByDesc("id")));
    }
}
