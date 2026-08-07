package com.macro.mall.mapper;

import com.macro.mall.model.PmsProductCategory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface PmsProductCategoryMapper {
    List<PmsProductCategory> selectByParentId(@Param("parentId") Long parentId);

    @Select("SELECT id, name, parent_id AS parentId, level, sort, icon, show_status AS showStatus, product_count AS productCount, create_time AS createTime, update_time AS updateTime FROM pms_product_category")
    List<PmsProductCategory> selectAll();

    PmsProductCategory selectByPrimaryKey(@Param("id") Long id);
}