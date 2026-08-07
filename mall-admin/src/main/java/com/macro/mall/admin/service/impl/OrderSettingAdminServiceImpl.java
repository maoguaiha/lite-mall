package com.macro.mall.admin.service.impl;

import com.macro.mall.mapper.OmsOrderSettingAdminMapper;
import com.macro.mall.model.OmsOrderSetting;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * 订单设置：单例配置，固定 id=1
 */
@Service
public class OrderSettingAdminServiceImpl {

    private final OmsOrderSettingAdminMapper settingMapper;

    public OrderSettingAdminServiceImpl(OmsOrderSettingAdminMapper settingMapper) {
        this.settingMapper = settingMapper;
    }

    public OmsOrderSetting get() {
        OmsOrderSetting setting = settingMapper.selectById(1L);
        if (setting == null) {
            setting = new OmsOrderSetting();
            setting.setId(1L);
            setting.setCreateTime(new Date());
            settingMapper.insert(setting);
        }
        return setting;
    }

    public void update(OmsOrderSetting setting) {
        setting.setId(1L);
        setting.setUpdateTime(new Date());
        if (settingMapper.selectById(1L) == null) {
            setting.setCreateTime(new Date());
            settingMapper.insert(setting);
        } else {
            settingMapper.updateById(setting);
        }
    }
}
