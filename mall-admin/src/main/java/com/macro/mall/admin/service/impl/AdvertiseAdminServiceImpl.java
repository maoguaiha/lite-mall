package com.macro.mall.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.PageHelper;
import com.macro.mall.common.api.CommonPage;
import com.macro.mall.common.exception.Asserts;
import com.macro.mall.mapper.SmsHomeAdvertiseAdminMapper;
import com.macro.mall.model.SmsHomeAdvertise;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;

@Service
public class AdvertiseAdminServiceImpl {

    private final SmsHomeAdvertiseAdminMapper advertiseMapper;

    public AdvertiseAdminServiceImpl(SmsHomeAdvertiseAdminMapper advertiseMapper) {
        this.advertiseMapper = advertiseMapper;
    }

    public CommonPage<SmsHomeAdvertise> list(Integer pageNum, Integer pageSize, String keyword) {
        PageHelper.startPage(pageNum, pageSize);
        QueryWrapper<SmsHomeAdvertise> qw = new QueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            qw.like("name", keyword);
        }
        qw.eq("delete_flag", 0).orderByDesc("create_time");
        return CommonPage.restPage(advertiseMapper.selectList(qw));
    }

    public void create(SmsHomeAdvertise advertise) {
        if (advertise.getCreateTime() == null) {
            advertise.setCreateTime(new Date());
        }
        if (advertise.getDeleteFlag() == null) {
            advertise.setDeleteFlag(0);
        }
        advertiseMapper.insert(advertise);
    }

    public void update(SmsHomeAdvertise advertise) {
        if (advertise.getId() == null) {
            Asserts.fail("广告ID不能为空");
        }
        advertiseMapper.updateById(advertise);
    }

    public void delete(Long id) {
        advertiseMapper.deleteById(id);
    }
}
