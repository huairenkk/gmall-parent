package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.model.product.*;
import com.atguigu.gmall.product.mapper.*;
import com.atguigu.gmall.product.service.ManageService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mysql.cj.QueryResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
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
    @Autowired
    private SpuImageMapper spuImageMapper;
    @Autowired
    private SpuPosterMapper spuPosterMapper;
    @Autowired
    private SpuSaleAttrMapper spuSaleAttrMapper;
    @Autowired
    private SpuSaleAttrValueMapper spuSaleAttrValueMapper;
    @Autowired
    private SkuInfoMapper skuInfoMapper;
    @Autowired
    private SkuSaleAttrValueMapper skuSaleAttrValueMapper;
    @Autowired
    private SkuAttrValueMapper skuAttrValueMapper;
    @Autowired
    private SkuImageMapper skuImageMapper;

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
     * @param
     * @param
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

    /**
     *
     * 保存spu
     * spu_info *
     * spu_poster *
     * spu_image *
     * spu_sale_attr_value
     * spu_sale_attr
     *
     * @param spuInfo
     */
    @Override
    public void saveSpuInfo(SpuInfo spuInfo) {
        spuInfoMapper.insert(spuInfo);
        Long spuId = spuInfo.getId();
        //spuImage
        List<SpuImage> imageList = spuInfo.getSpuImageList();
        if (!CollectionUtils.isEmpty(imageList)) {
            for (SpuImage spuImage : imageList) {
                spuImage.setSpuId(spuId);
                spuImageMapper.insert(spuImage);
            }
        }
        //SpuPoster
        List<SpuPoster> posterList = spuInfo.getSpuPosterList();
        if (!CollectionUtils.isEmpty(posterList)) {
            for (SpuPoster spuPoster : posterList) {
                spuPoster.setSpuId(spuId);
                spuPosterMapper.insert(spuPoster);
            }
        }
        //SpuSaleAttr
        List<SpuSaleAttr> spuSaleAttrList = spuInfo.getSpuSaleAttrList();
        if (!CollectionUtils.isEmpty(spuSaleAttrList)) {
            for (SpuSaleAttr spuSaleAttr : spuSaleAttrList) {
                spuSaleAttr.setSpuId(spuId);
                String saleAttrName = spuSaleAttr.getSaleAttrName();
                spuSaleAttrMapper.insert(spuSaleAttr);
                List<SpuSaleAttrValue> spuSaleAttrValueList = spuSaleAttr.getSpuSaleAttrValueList();
                for (SpuSaleAttrValue spuSaleAttrValue : spuSaleAttrValueList) {
                    spuSaleAttrValue.setSaleAttrName(saleAttrName);
                    spuSaleAttrValue.setSpuId(spuId);
                    spuSaleAttrValueMapper.insert(spuSaleAttrValue);
                }
            }
        }

    }

    @Override
    public List<SpuSaleAttr> getSpuSaleAttrList(Long spuId) {
        List<SpuSaleAttr> spuSaleAttrList = spuSaleAttrMapper.getSpuSaleAttrList(spuId);
        return spuSaleAttrList;
    }

    @Override
    public List<SpuImage> getSpuImageList(Long spuId) {
        QueryWrapper<SpuImage> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("spu_id",spuId);
        List<SpuImage> spuImageList = spuImageMapper.selectList(queryWrapper);
        return spuImageList;
    }

    /**
     *
     * sku_ingo
     * sku_image
     * sku_sale_attr_value
     * sku_attr_value
     *
     * @param skuInfo
     */

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveSkuInfo(SkuInfo skuInfo) {
        skuInfoMapper.insert(skuInfo);
        Long skuId = skuInfo.getId();
        //sku图片
        List<SkuImage> skuImageList = skuInfo.getSkuImageList();
        if (!CollectionUtils.isEmpty(skuImageList)) {
            for (SkuImage skuImage : skuImageList) {
                skuImage.setSkuId(skuId);
                skuImageMapper.insert(skuImage);
            }
        }
        //销售属性
        List<SkuSaleAttrValue> skuSaleAttrValueList = skuInfo.getSkuSaleAttrValueList();
        if (!CollectionUtils.isEmpty(skuSaleAttrValueList)) {
            for (SkuSaleAttrValue skuSaleAttrValue : skuSaleAttrValueList) {
                skuSaleAttrValue.setSkuId(skuId);
                skuSaleAttrValue.setSpuId(skuInfo.getSpuId());
                skuSaleAttrValueMapper.insert(skuSaleAttrValue);
            }
        }
        //平台属性
        List<SkuAttrValue> skuAttrValueList = skuInfo.getSkuAttrValueList();
        if (!CollectionUtils.isEmpty(skuAttrValueList)) {
            for (SkuAttrValue skuAttrValue : skuAttrValueList) {
                skuAttrValue.setSkuId(skuId);
                skuAttrValueMapper.insert(skuAttrValue);
            }
        }
    }

    @Override
    public IPage<SkuInfo> getSkuList(Page<SkuInfo> skuInfoPage) {
        QueryWrapper<SkuInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("id");
        return skuInfoMapper.selectPage(skuInfoPage,queryWrapper);
    }

    @Override
    public void onSale(Long skuId) {
        SkuInfo skuInfo = new SkuInfo();
        skuInfo.setId(skuId);
        skuInfo.setIsSale(1);
        skuInfoMapper.updateById(skuInfo);
    }

    @Override
    public void cancelSale(Long skuId) {
        SkuInfo skuInfo = new SkuInfo();
        skuInfo.setId(skuId);
        skuInfo.setIsSale(0);
        skuInfoMapper.updateById(skuInfo);
    }
}
