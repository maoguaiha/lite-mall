package com.macro.mall.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.github.pagehelper.PageHelper;
import com.macro.mall.common.api.CommonPage;
import com.macro.mall.common.exception.Asserts;
import com.macro.mall.mapper.OmsOrderCommentMapper;
import com.macro.mall.model.OmsOrderComment;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentAdminServiceImpl {

    private final OmsOrderCommentMapper commentMapper;

    public CommentAdminServiceImpl(OmsOrderCommentMapper commentMapper) {
        this.commentMapper = commentMapper;
    }

    public CommonPage<OmsOrderComment> list(Integer pageNum, Integer pageSize, Integer status) {
        PageHelper.startPage(pageNum, pageSize);
        QueryWrapper<OmsOrderComment> qw = new QueryWrapper<>();
        if (status != null) {
            qw.eq("status", status);
        }
        qw.orderByDesc("id");
        return CommonPage.restPage(commentMapper.selectList(qw));
    }

    public OmsOrderComment get(Long id) {
        OmsOrderComment comment = commentMapper.selectById(id);
        if (comment == null) {
            Asserts.fail("评价不存在");
        }
        return comment;
    }

    public void audit(Long id, Integer status, String reply) {
        if (status != 2 && status != 3) {
            Asserts.fail("审核状态只能是 2(通过) 或 3(驳回)");
        }
        commentMapper.update(new OmsOrderComment(),
                new UpdateWrapper<OmsOrderComment>().eq("id", id).set("status", status).set("reply", reply));
    }

    public void delete(Long id) {
        commentMapper.update(new OmsOrderComment(),
                new UpdateWrapper<OmsOrderComment>().eq("id", id).set("delete_flag", 2));
    }
}
