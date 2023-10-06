package com.atguigu.gmall.product.service;

import com.atguigu.gmall.model.product.*;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;

public interface ManageService {
    /**
     * 获取一级分类列表
     * @return
     */
    List<BaseCategory1> getCategory1();

    /**
     * 获取二级分类列表
     * @return
     */
    List<BaseCategory2> getCategory2(Long category1Id);


    /**
     * 获取三级分类列表
     * @return
     */
    List<BaseCategory3> getCategory3(Long category2Id);
    /**
     * 根据分类Id 获取平台属性数据
     * @param category1Id
     * @param category2Id
     * @param category3Id
     * @return 个分类级别下的排序好的平台属性列表
     */
    List<BaseAttrInfo> attrInfoList(Long category1Id, Long category2Id, Long category3Id);
    /**
     * admin/product/saveAttrInfo
     * 保存-修改平台属性
     * @param baseAttrInfo
     * @return
     */
    void saveAttrInfo(BaseAttrInfo baseAttrInfo);
    /**
     * 根据平台属性Id 获取到平台属性值集合
     * @param attrId
     * @return
     */
    List<BaseAttrValue> getAttrValueList(Long attrId);

    IPage<SpuInfo> getSpuInfoPage(Page<SpuInfo> spuInfoPage, Long category3Id);

    List<BaseSaleAttr> getBaseSaleAttrList();

    void saveSpuInfo(SpuInfo spuInfo);

    List<SpuSaleAttr> getSpuSaleAttrList(Long spuId);

    List<SpuImage> getSpuImageList(Long spuId);

    void saveSkuInfo(SkuInfo skuInfo);

    IPage<SkuInfo> getSkuList(Page<SkuInfo> skuInfoPage);

    void onSale(Long skuId);

    void cancelSale(Long skuId);
}
