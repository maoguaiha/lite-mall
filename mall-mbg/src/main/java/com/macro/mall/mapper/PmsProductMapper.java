package com.macro.mall.mapper;

import com.macro.mall.model.PmsProduct;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PmsProductMapper {
    PmsProduct selectByPrimaryKey(@Param("id") Long id);
    
    List<PmsProduct> selectByCategoryId(@Param("categoryId") Long categoryId);
    
    List<PmsProduct> selectByKeyword(@Param("keyword") String keyword);
    
    List<PmsProduct> selectByCategoryIdAndKeyword(
            @Param("categoryId") Long categoryId,
            @Param("keyword") String keyword);

    List<PmsProduct> selectByCategoryIds(@Param("ids") List<Long> ids);

    List<PmsProduct> selectByCategoryIdsAndKeyword(
            @Param("ids") List<Long> ids,
            @Param("keyword") String keyword);

    List<PmsProduct> selectRecommendProducts();
    
    List<PmsProduct> selectNewProducts();
    
    int updateStock(@Param("id") Long id, @Param("stock") Integer stock);

    int decrementStock(@Param("id") Long id, @Param("quantity") Integer quantity);
}