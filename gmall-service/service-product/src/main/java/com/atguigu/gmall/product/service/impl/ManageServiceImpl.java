package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.model.product.*;
import com.atguigu.gmall.product.mapper.*;
import com.atguigu.gmall.product.service.ManageService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

@Service
public class ManageServiceImpl implements ManageService {
    @Autowired
    private BaseCategory1Mapper baseCategory1Mapper;
    @Autowired
    private BaseCategory2Mapper baseCategory2Mapper;
    @Autowired
    private BaseCategory3Mapper baseCategory3Mapper;
    @Autowired
    private BaseAttrInfoMapper baseAttrInfoMapper;
    @Autowired
    private BaseAttrValueMapper baseAttrValueMapper;
    @Autowired
    private SpuInfoMapper spuInfoMapper;
    @Autowired
    private BaseSaleAttrMapper baseSaleAttrMapper;

    /**
     * 获取一级分类列表
     *
     * @return
     */
    @Override
    public List<BaseCategory1> getCategory1() {
        QueryWrapper<BaseCategory1> wrapper = new QueryWrapper<>();
        //添加排序条件
        wrapper.orderByAsc("id");
        List<BaseCategory1> baseCategory1List = baseCategory1Mapper.selectList(wrapper);
        return baseCategory1List;
    }

    /**
     * 获取二级分类列表
     *
     * @return
     */
    @Override
    public List<BaseCategory2> getCategory2(Long category1Id) {
        LambdaQueryWrapper<BaseCategory2> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BaseCategory2::getCategory1Id, category1Id);
        wrapper.orderByAsc(BaseCategory2::getId);
        List<BaseCategory2> baseCategory2List = baseCategory2Mapper.selectList(wrapper);
        return baseCategory2List;
    }

    /**
     * 获取三级分类列表
     *
     * @return
     */
    @Override
    public List<BaseCategory3> getCategory3(Long category2Id) {
        LambdaQueryWrapper<BaseCategory3> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BaseCategory3::getCategory2Id, category2Id);
        wrapper.orderByAsc(BaseCategory3::getId);
        List<BaseCategory3> baseCategory2List = baseCategory3Mapper.selectList(wrapper);
        return baseCategory2List;
    }

    @Override
    public List<BaseAttrInfo> attrInfoList(Long category1Id, Long category2Id, Long category3Id) {
        return baseAttrInfoMapper.selectBaseAttrInfoList(category1Id, category2Id, category3Id);
    }

    /**
     * 1、涉及两张表
     * attr_info
     * attr_value
     * <p>
     * 2、两张表的关系一对多
     * <p>
     * 3、声明式事务
     * 异常的处理RuntimeException
     * IOException
     * SQLException
     *
     * @param baseAttrInfo
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveAttrInfo(BaseAttrInfo baseAttrInfo) {
        //判断id是否存在
        Long id = baseAttrInfo.getId();
        if (id!=null) {
            //修改平台属性值
            baseAttrInfoMapper.updateById(baseAttrInfo);
        } else {

            //新增baseAttrInfo
            baseAttrInfoMapper.insert(baseAttrInfo);

        }
        // 修改：通过先删除{baseAttrValue}，再新增的方式！
        List<BaseAttrValue> valueList = baseAttrInfo.getAttrValueList();
        QueryWrapper queryWrapper = new QueryWrapper<BaseAttrValue>();
        queryWrapper.eq("attr_id",baseAttrInfo.getId());
        baseAttrValueMapper.delete(queryWrapper);
        //保存BaseAttrValue
        if (!CollectionUtils.isEmpty(valueList)) {
            for (BaseAttrValue baseAttrValue : valueList) {
                baseAttrValue.setAttrId(baseAttrInfo.getId());
                baseAttrValueMapper.insert(baseAttrValue);

            }
        }
    }

    @Override
    public List<BaseAttrValue> getAttrValueList(Long attrId) {
        LambdaQueryWrapper<BaseAttrValue> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(BaseAttrValue::getAttrId,attrId);
        List<BaseAttrValue> valueList = baseAttrValueMapper.selectList(queryWrapper);
        return valueList;
    }
    /**
     * //admin/product/{page}/{limit}
     * //spu分页列表
     * @param page
     * @param limit
     * @return
     */
    @Override
    public IPage<SpuInfo> getSpuInfoPage(Page<SpuInfo> spuInfoPage, Long category3Id) {
        LambdaQueryWrapper<SpuInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SpuInfo::getCategory3Id,category3Id);
        queryWrapper.orderByDesc(SpuInfo::getId);
        Page<SpuInfo> spuInfoPageList = spuInfoMapper.selectPage(spuInfoPage, queryWrapper);
        return spuInfoPageList;
    }

    @Override
    public List<BaseSaleAttr> getBaseSaleAttrList() {
        return baseSaleAttrMapper.selectList(null);

    }
}
