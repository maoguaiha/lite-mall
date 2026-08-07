package com.macro.mall.mapper;

import com.macro.mall.model.UmsMember;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 会员Mapper接口
 */
@Mapper
public interface UmsMemberMapper {
    /**
     * 根据用户名获取会员
     */
    UmsMember selectByUsername(@Param("username") String username);

    /**
     * 根据手机号获取会员
     */
    UmsMember selectByPhone(@Param("phone") String phone);

    /**
     * 插入会员
     */
    int insert(UmsMember record);

    /**
     * 更新会员信息
     */
    int updateByPrimaryKeySelective(UmsMember record);

    /**
     * 根据ID获取会员
     */
    UmsMember selectByPrimaryKey(@Param("id") Long id);
}
