package com.macro.mall.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.PageHelper;
import com.macro.mall.common.api.CommonPage;
import com.macro.mall.common.exception.Asserts;
import com.macro.mall.mapper.CmsSubjectAdminMapper;
import com.macro.mall.model.CmsSubject;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;

@Service
public class SubjectAdminServiceImpl {

    private final CmsSubjectAdminMapper subjectMapper;

    public SubjectAdminServiceImpl(CmsSubjectAdminMapper subjectMapper) {
        this.subjectMapper = subjectMapper;
    }

    public CommonPage<CmsSubject> list(Integer pageNum, Integer pageSize, String keyword) {
        PageHelper.startPage(pageNum, pageSize);
        QueryWrapper<CmsSubject> qw = new QueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            qw.like("title", keyword);
        }
        qw.eq("delete_flag", 0).orderByDesc("create_time");
        return CommonPage.restPage(subjectMapper.selectList(qw));
    }

    public void create(CmsSubject subject) {
        if (subject.getCreateTime() == null) {
            subject.setCreateTime(new Date());
        }
        if (subject.getDeleteFlag() == null) {
            subject.setDeleteFlag(0);
        }
        subjectMapper.insert(subject);
    }

    public void update(CmsSubject subject) {
        if (subject.getId() == null) {
            Asserts.fail("专题ID不能为空");
        }
        subjectMapper.updateById(subject);
    }

    public void delete(Long id) {
        subjectMapper.deleteById(id);
    }
}
