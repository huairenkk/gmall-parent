package com.atguigu.gmall.item.service.impl;

import com.atguigu.gmall.item.service.ItemService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class ItemServiceImpl implements ItemService {
    /**
     * 商品详情页
     * @return
     */
    @Override
    public Map<String, Object> getItemBySkuId(Long skuId) {
        HashMap<String, Object> result = new HashMap<>();

        //封装SkuInfo *
        //封装categoryView三级分类
        //封装skuAttrList sku平台属性
        //price 最新价格
        //spuPosterList spu海报数据
        //spuSaleAttrList spu销售属性

        return result;
    }
}
