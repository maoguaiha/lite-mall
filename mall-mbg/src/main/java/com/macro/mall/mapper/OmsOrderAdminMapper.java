package com.macro.mall.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.macro.mall.model.OmsOrder;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Mapper
public interface OmsOrderAdminMapper extends BaseMapper<OmsOrder> {

    /**
     * 看板：已支付订单累计销售额
     */
    BigDecimal selectTotalSales();

    /**
     * 按天聚合近 N 天订单量与成交额（避免全表载入内存）
     */
    @Select("SELECT CAST(create_time AS DATE) AS \"day\", COUNT(*) AS orderCount, COALESCE(SUM(pay_amount), 0) AS amount " +
            "FROM oms_order WHERE create_time >= #{start} " +
            "GROUP BY CAST(create_time AS DATE) ORDER BY \"day\"")
    List<Map<String, Object>> selectOrderTrendByDay(@Param("start") Date start);
}
