package com.macro.mall.portal.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.macro.mall.common.exception.Asserts;
import com.macro.mall.mapper.UmsMemberAddressMapper;
import com.macro.mall.model.UmsMemberAddress;
import com.macro.mall.service.UmsMemberService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;

@Service
public class AddressServiceImpl {

    private final UmsMemberAddressMapper addressMapper;
    private final UmsMemberService memberService;

    public AddressServiceImpl(UmsMemberAddressMapper addressMapper, UmsMemberService memberService) {
        this.addressMapper = addressMapper;
        this.memberService = memberService;
    }

    private Long currentMemberId() {
        return memberService.getCurrentMember().getId();
    }

    public List<UmsMemberAddress> list() {
        return addressMapper.selectList(new QueryWrapper<UmsMemberAddress>()
                .eq("member_id", currentMemberId())
                .orderByDesc("is_default")
                .orderByDesc("id"));
    }

    public UmsMemberAddress get(Long id) {
        UmsMemberAddress address = addressMapper.selectById(id);
        if (address == null || !address.getMemberId().equals(currentMemberId())) {
            Asserts.fail("地址不存在");
        }
        return address;
    }

    public void create(UmsMemberAddress address) {
        address.setMemberId(currentMemberId());
        address.setCreateTime(new Date());
        if (address.getIsDefault() == null) {
            address.setIsDefault(0);
        }
        addressMapper.insert(address);
        if (address.getIsDefault() == 1) {
            setDefault(address.getId());
        }
    }

    public void update(UmsMemberAddress address) {
        if (address.getId() == null) {
            Asserts.fail("地址ID不能为空");
        }
        get(address.getId());
        addressMapper.updateById(address);
        if (address.getIsDefault() != null && address.getIsDefault() == 1) {
            setDefault(address.getId());
        }
    }

    public void delete(Long id) {
        get(id);
        addressMapper.update(new UmsMemberAddress(),
                new UpdateWrapper<UmsMemberAddress>().eq("id", id).set("delete_flag", 2));
    }

    public void setDefault(Long id) {
        get(id);
        addressMapper.update(new UmsMemberAddress(),
                new UpdateWrapper<UmsMemberAddress>().eq("member_id", currentMemberId()).set("is_default", 0));
        addressMapper.update(new UmsMemberAddress(),
                new UpdateWrapper<UmsMemberAddress>().eq("id", id).set("is_default", 1));
    }
}
