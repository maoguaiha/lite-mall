package com.macro.mall.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.github.pagehelper.PageHelper;
import com.macro.mall.common.api.CommonPage;
import com.macro.mall.common.exception.Asserts;
import com.macro.mall.mapper.OmsOrderAdminMapper;
import com.macro.mall.model.OmsOrder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class OrderAdminServiceImpl {

    private final OmsOrderAdminMapper orderAdminMapper;

    public OrderAdminServiceImpl(OmsOrderAdminMapper orderAdminMapper) {
        this.orderAdminMapper = orderAdminMapper;
    }

    public CommonPage<OmsOrder> list(Integer pageNum, Integer pageSize, Integer status, String memberUsername) {
        PageHelper.startPage(pageNum, pageSize);
        com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<OmsOrder> qw =
                new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>();
        if (status != null) {
            qw.eq("status", status);
        }
        if (StringUtils.hasText(memberUsername)) {
            qw.eq("member_username", memberUsername);
        }
        qw.orderByDesc("id");
        return CommonPage.restPage(orderAdminMapper.selectList(qw));
    }

    public OmsOrder get(Long id) {
        OmsOrder order = orderAdminMapper.selectById(id);
        if (order == null) {
            Asserts.fail("订单不存在");
        }
        return order;
    }

    public void ship(Long id, String deliveryCompany, String deliverySn) {
        OmsOrder order = new OmsOrder();
        order.setStatus(2);
        order.setDeliveryCompany(deliveryCompany);
        order.setDeliverySn(deliverySn);
        int affected = orderAdminMapper.update(order, new UpdateWrapper<OmsOrder>().eq("id", id));
        if (affected == 0) {
            Asserts.fail("订单不存在或已发货");
        }
    }

    public void refund(Long id) {
        int affected = orderAdminMapper.update(new OmsOrder(),
                new UpdateWrapper<OmsOrder>().eq("id", id).set("status", 5));
        if (affected == 0) {
            Asserts.fail("订单不存在");
        }
    }
}
