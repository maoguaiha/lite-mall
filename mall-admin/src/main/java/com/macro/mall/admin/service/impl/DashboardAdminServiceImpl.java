package com.macro.mall.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.macro.mall.mapper.OmsOrderAdminMapper;
import com.macro.mall.mapper.OmsOrderCommentMapper;
import com.macro.mall.mapper.PmsProductAdminMapper;
import com.macro.mall.mapper.PmsProductCategoryMapper;
import com.macro.mall.mapper.UmsMemberAdminMapper;
import com.macro.mall.model.OmsOrder;
import com.macro.mall.model.OmsOrderComment;
import com.macro.mall.model.PmsProduct;
import com.macro.mall.model.PmsProductCategory;
import com.macro.mall.model.UmsMember;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DashboardAdminServiceImpl {

    private final OmsOrderAdminMapper orderAdminMapper;
    private final UmsMemberAdminMapper memberAdminMapper;
    private final PmsProductAdminMapper productAdminMapper;
    private final PmsProductCategoryMapper categoryMapper;
    private final OmsOrderCommentMapper commentMapper;

    public DashboardAdminServiceImpl(OmsOrderAdminMapper orderAdminMapper,
                                     UmsMemberAdminMapper memberAdminMapper,
                                     PmsProductAdminMapper productAdminMapper,
                                     PmsProductCategoryMapper categoryMapper,
                                     OmsOrderCommentMapper commentMapper) {
        this.orderAdminMapper = orderAdminMapper;
        this.memberAdminMapper = memberAdminMapper;
        this.productAdminMapper = productAdminMapper;
        this.categoryMapper = categoryMapper;
        this.commentMapper = commentMapper;
    }

    public Map<String, Object> getStats() {
        Map<String, Object> stats = new HashMap<>();
        long orderCount = orderAdminMapper.selectCount(new QueryWrapper<OmsOrder>());
        long memberCount = memberAdminMapper.selectCount(new QueryWrapper<UmsMember>());
        long productCount = productAdminMapper.selectCount(new QueryWrapper<PmsProduct>().eq("publish_status", 1));
        BigDecimal totalSales = orderAdminMapper.selectTotalSales();
        long commentPending = commentMapper.selectCount(new QueryWrapper<OmsOrderComment>().eq("status", 1));
        stats.put("orderCount", orderCount);
        stats.put("memberCount", memberCount);
        stats.put("productCount", productCount);
        stats.put("totalSales", totalSales);
        stats.put("commentPending", commentPending);
        return stats;
    }

    /**
     * 最近订单：按创建时间倒序取 5 条
     */
    public List<OmsOrder> getRecentOrders() {
        QueryWrapper<OmsOrder> qw = new QueryWrapper<>();
        qw.orderByDesc("create_time").last("limit 5");
        return orderAdminMapper.selectList(qw);
    }

    /**
     * 热销商品：按销量倒序取 5 条
     */
    public List<PmsProduct> getHotProducts() {
        QueryWrapper<PmsProduct> qw = new QueryWrapper<>();
        qw.orderByDesc("sales").last("limit 5");
        return productAdminMapper.selectList(qw);
    }

    /**
     * 商品分类占比：按 category_id 分组计数，并映射分类名称
     */
    public List<Map<String, Object>> getCategoryStats() {
        List<PmsProduct> products = productAdminMapper.selectList(null);
        Map<Long, String> categoryNameMap = categoryMapper.selectAll().stream()
                .collect(Collectors.toMap(PmsProductCategory::getId, PmsProductCategory::getName, (a, b) -> a));
        Map<Long, Long> countByCategory = products.stream()
                .collect(Collectors.groupingBy(PmsProduct::getCategoryId, Collectors.counting()));
        List<Map<String, Object>> result = new ArrayList<>();
        for (Map.Entry<Long, Long> entry : countByCategory.entrySet()) {
            Map<String, Object> item = new HashMap<>();
            item.put("categoryId", entry.getKey());
            item.put("categoryName", categoryNameMap.getOrDefault(entry.getKey(), "未知分类"));
            item.put("count", entry.getValue());
            result.add(item);
        }
        return result;
    }

    /**
     * 近 7 天订单趋势：按创建日期在数据库层分组聚合（避免全表载入内存）
     */
    public List<Map<String, Object>> getOrderTrend() {
        LocalDate today = LocalDate.now();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("MM-dd");
        LocalDate start = today.minusDays(6);
        Date startDt = Date.from(start.atStartOfDay(ZoneId.systemDefault()).toInstant());
        List<Map<String, Object>> rows = orderAdminMapper.selectOrderTrendByDay(startDt);
        Map<String, Long> countByDate = new LinkedHashMap<>();
        for (int i = 6; i >= 0; i--) {
            countByDate.put(start.minusDays(i).format(fmt), 0L);
        }
        for (Map<String, Object> row : rows) {
            Object dayObj = row.get("day");
            String key;
            if (dayObj instanceof Date) {
                key = ((Date) dayObj).toInstant().atZone(ZoneId.systemDefault()).toLocalDate().format(fmt);
            } else {
                key = String.valueOf(dayObj);
            }
            if (countByDate.containsKey(key)) {
                countByDate.put(key, ((Number) row.get("orderCount")).longValue());
            }
        }
        List<Map<String, Object>> result = new ArrayList<>();
        for (Map.Entry<String, Long> entry : countByDate.entrySet()) {
            Map<String, Object> item = new HashMap<>();
            item.put("date", entry.getKey());
            item.put("count", entry.getValue());
            result.add(item);
        }
        return result;
    }
}
