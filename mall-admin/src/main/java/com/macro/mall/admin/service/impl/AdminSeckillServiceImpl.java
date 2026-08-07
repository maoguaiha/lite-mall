package com.macro.mall.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.github.pagehelper.PageHelper;
import com.macro.mall.admin.dto.SeckillProductVO;
import com.macro.mall.admin.dto.SeckillSessionVO;
import com.macro.mall.common.api.CommonPage;
import com.macro.mall.common.exception.Asserts;
import com.macro.mall.mapper.PmsProductMapper;
import com.macro.mall.mapper.SmsSeckillProductMapper;
import com.macro.mall.mapper.SmsSeckillSessionMapper;
import com.macro.mall.model.PmsProduct;
import com.macro.mall.model.SmsSeckillProduct;
import com.macro.mall.model.SmsSeckillSession;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AdminSeckillServiceImpl {

    private final SmsSeckillSessionMapper sessionMapper;
    private final SmsSeckillProductMapper productMapper;
    private final PmsProductMapper pmsProductMapper;

    public AdminSeckillServiceImpl(SmsSeckillSessionMapper sessionMapper,
                                   SmsSeckillProductMapper productMapper,
                                   PmsProductMapper pmsProductMapper) {
        this.sessionMapper = sessionMapper;
        this.productMapper = productMapper;
        this.pmsProductMapper = pmsProductMapper;
    }

    public CommonPage<SeckillSessionVO> list(Integer pageNum, Integer pageSize, String keyword) {
        PageHelper.startPage(pageNum, pageSize);
        QueryWrapper<SmsSeckillSession> qw = new QueryWrapper<>();
        qw.eq("delete_flag", 0);
        if (StringUtils.hasText(keyword)) {
            qw.like("name", keyword);
        }
        qw.orderByDesc("id");
        List<SmsSeckillSession> sessions = sessionMapper.selectList(qw);
        Map<Long, Long> productCountMap = productCountBySession();
        Map<Long, BigDecimal> amountMap = productAmountBySession();
        List<SeckillSessionVO> vos = sessions.stream().map(s -> {
            SeckillSessionVO vo = new SeckillSessionVO();
            BeanUtils.copyProperties(s, vo);
            vo.setFlashCount(productCountMap.getOrDefault(s.getId(), 0L));
            vo.setTotalAmount(amountMap.getOrDefault(s.getId(), BigDecimal.ZERO));
            return vo;
        }).collect(Collectors.toList());
        return CommonPage.restPage(vos);
    }

    public void create(SmsSeckillSession session) {
        if (session.getStatus() == null) {
            session.setStatus(0);
        }
        session.setDeleteFlag(0);
        sessionMapper.insert(session);
    }

    public void update(SmsSeckillSession session) {
        if (session.getId() == null) {
            Asserts.fail("秒杀活动ID不能为空");
        }
        sessionMapper.updateById(session);
    }

    public void delete(Long id) {
        sessionMapper.update(new SmsSeckillSession(),
                new UpdateWrapper<SmsSeckillSession>().eq("id", id).set("delete_flag", 2));
    }

    public List<SeckillProductVO> products(Long sessionId) {
        List<SmsSeckillProduct> list = productMapper.selectList(
                new QueryWrapper<SmsSeckillProduct>().eq("session_id", sessionId).eq("delete_flag", 0));
        Map<Long, String> nameMap = list.stream()
                .map(p -> pmsProductMapper.selectByPrimaryKey(p.getProductId()))
                .filter(java.util.Objects::nonNull)
                .collect(Collectors.toMap(PmsProduct::getId, PmsProduct::getName, (a, b) -> a));
        return list.stream().map(p -> {
            SeckillProductVO vo = new SeckillProductVO();
            BeanUtils.copyProperties(p, vo);
            vo.setProductName(nameMap.getOrDefault(p.getProductId(), "未知商品"));
            return vo;
        }).collect(Collectors.toList());
    }

    private Map<Long, Long> productCountBySession() {
        List<SmsSeckillProduct> all = productMapper.selectList(
                new QueryWrapper<SmsSeckillProduct>().eq("delete_flag", 0));
        return all.stream().collect(Collectors.groupingBy(SmsSeckillProduct::getSessionId, Collectors.counting()));
    }

    private Map<Long, BigDecimal> productAmountBySession() {
        List<SmsSeckillProduct> all = productMapper.selectList(
                new QueryWrapper<SmsSeckillProduct>().eq("delete_flag", 0));
        return all.stream().collect(Collectors.groupingBy(SmsSeckillProduct::getSessionId,
                Collectors.reducing(BigDecimal.ZERO,
                        p -> p.getSeckillPrice().multiply(BigDecimal.valueOf(p.getSeckillStock() == null ? 0 : p.getSeckillStock())),
                        BigDecimal::add)));
    }
}
