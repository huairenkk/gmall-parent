package com.atguigu.gmall.product.service;

import com.atguigu.gmall.model.product.BaseCategoryTrademark;
import com.atguigu.gmall.model.product.BaseTrademark;
import com.atguigu.gmall.model.product.CategoryTrademarkVo;

import java.util.List;

public interface BaseCategoryTrademarkService {
    List<BaseCategoryTrademark> findCategoryTrademarkList(Long category3Id);

    void removeCategoryTrademark(Long category3Id, Long trademarkId);

    List<BaseTrademark> findCurrentTrademarkList(Long category3Id);

    void saveBaseCategoryTrademark(CategoryTrademarkVo categoryTrademarkVo);
}
