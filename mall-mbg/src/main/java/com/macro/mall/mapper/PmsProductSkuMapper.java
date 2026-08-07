package com.macro.mall.mapper;

import com.macro.mall.model.PmsProductSku;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PmsProductSkuMapper {
    List<PmsProductSku> selectByProductId(@Param("productId") Long productId);
    
    PmsProductSku selectByPrimaryKey(@Param("id") Long id);
    
    int updateStock(@Param("id") Long id, @Param("stock") Integer stock);
}