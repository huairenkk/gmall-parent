package com.atguigu.gmall.item.service.impl;

import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.item.service.ItemService;
import com.atguigu.gmall.model.product.*;
import com.atguigu.gmall.product.client.ProductFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
//@SuppressWarnings("all")
public class ItemServiceImpl implements ItemService {
//    @Autowired
    @Resource
    private ProductFeignClient productFeignClient;

    /**
     * 商品详情页
     *
     * @return
     */
    @Override
    public Map<String, Object> getItemBySkuId(Long skuId) {
        //声明返回数据对象
        HashMap<String, Object> result = new HashMap<>();
        SkuInfo skuInfo = productFeignClient.getSkuInfo(skuId);

        if (skuInfo != null) {
            //封装categoryView三级分类
            BaseCategoryView categoryView = productFeignClient.getCategoryView(skuInfo.getCategory3Id());
            result.put("categoryView", categoryView);

            //spuSaleAttrList spu销售属性
            List<SpuSaleAttr> spuSaleAttrList = productFeignClient.getSpuSaleAttrListCheckBySku(skuId, skuInfo.getSpuId());
            result.put("spuSaleAttrList", spuSaleAttrList);

            //查询销售属性值Id 与skuId 组合的map
            Map skuValueIdsMap = productFeignClient.getSkuValueIdsMap(skuInfo.getSpuId());
            //将map转换为页面需要的json对象
            String valuesSkuJson = JSON.toJSONString(skuValueIdsMap);
            result.put("valuesSkuJson",valuesSkuJson);

            //spuPosterList spu海报数据
            List<SpuPoster> spuPosterList = productFeignClient.findSpuPosterBySpuId(skuInfo.getSpuId());
            result.put("spuPosterList", spuPosterList);
        }

        //封装SkuInfo *
        result.put("skuInfo",skuInfo);

        //price 最新价格
        BigDecimal price = productFeignClient.getSkuPrice(skuId);
        result.put("price",price);

        //封装skuAttrList sku平台属性
        List<BaseAttrInfo> AttrList = productFeignClient.getAttrList(skuId);
        List<Map<String,String>> skuAttrList = AttrList.stream().map(baseAttrInfo -> {
            HashMap<String, String> attrValueMap = new HashMap<>();
            attrValueMap.put("attrName", baseAttrInfo.getAttrName());
            attrValueMap.put("attrValue",baseAttrInfo.getAttrValueList().get(0).getValueName());
            return attrValueMap;
        }).collect(Collectors.toList());
        result.put("skuAttrList",skuAttrList);


        return result;
    }

}
