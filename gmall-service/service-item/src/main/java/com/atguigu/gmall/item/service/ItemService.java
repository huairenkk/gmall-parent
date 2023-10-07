package com.atguigu.gmall.item.service;

import java.util.Map;

public interface ItemService {
    /**
     * 商品详情页
     * @return
     */
    Map<String, Object> getItemBySkuId(Long skuId);
}
