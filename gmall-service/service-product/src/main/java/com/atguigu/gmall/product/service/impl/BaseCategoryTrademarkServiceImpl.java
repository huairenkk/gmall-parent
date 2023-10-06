package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.model.product.BaseCategoryTrademark;
import com.atguigu.gmall.model.product.BaseTrademark;
import com.atguigu.gmall.model.product.CategoryTrademarkVo;
import com.atguigu.gmall.product.mapper.BaseCategoryTrademarkMapper;
import com.atguigu.gmall.product.mapper.BaseTrademarkMapper;
import com.atguigu.gmall.product.service.BaseCategoryTrademarkService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BaseCategoryTrademarkServiceImpl extends ServiceImpl<BaseCategoryTrademarkMapper, BaseCategoryTrademark> implements BaseCategoryTrademarkService {
    @Autowired
    private BaseCategoryTrademarkMapper baseCategoryTrademarkMapper;
    @Autowired
    private BaseTrademarkMapper baseTrademarkMapper;

    @Override
    public List<BaseCategoryTrademark> findCategoryTrademarkList(Long category3Id) {
        LambdaQueryWrapper<BaseCategoryTrademark> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(BaseCategoryTrademark::getCategory3Id, category3Id);
        List<BaseCategoryTrademark> categoryTrademarkList = baseCategoryTrademarkMapper.selectList(queryWrapper);
        return categoryTrademarkList;
    }

    @Override
    public void removeCategoryTrademark(Long category3Id, Long trademarkId) {
        QueryWrapper<BaseCategoryTrademark> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("category3_id", category3Id);
        queryWrapper.eq("trademark_id", trademarkId);
        baseCategoryTrademarkMapper.delete(queryWrapper);
    }

    @Override
    public List<BaseTrademark> findCurrentTrademarkList(Long category3Id) {
        List<BaseCategoryTrademark> categoryTrademarkList = findCategoryTrademarkList(category3Id);
        if (!CollectionUtils.isEmpty(categoryTrademarkList)) {

            List<Long> trademarkIdList = categoryTrademarkList.stream().
                    map(baseCategoryTrademark -> baseCategoryTrademark.getTrademarkId()).
                    collect(Collectors.toList());
            List<BaseTrademark> trademarkList = baseTrademarkMapper.selectList(null).
                    stream().filter(baseTrademark ->
                            !trademarkIdList.contains(baseTrademark.getId())
                    ).collect(Collectors.toList());
            return trademarkList;
        }
        return baseTrademarkMapper.selectList(null);
    }

    @Override
    public void saveBaseCategoryTrademark(CategoryTrademarkVo categoryTrademarkVo) {
        Long category3Id = categoryTrademarkVo.getCategory3Id();
        List<Long> trademarkIdList = categoryTrademarkVo.getTrademarkIdList();
        if (!CollectionUtils.isEmpty(trademarkIdList)) {
            List<BaseCategoryTrademark> baseCategoryTrademarkList = trademarkIdList.
                    stream().map(trademarkId -> {
                        BaseCategoryTrademark baseCategoryTrademark = new BaseCategoryTrademark();
                        baseCategoryTrademark.setCategory3Id(category3Id);
                        baseCategoryTrademark.setTrademarkId(trademarkId);
                        return baseCategoryTrademark;
                    }).collect(Collectors.toList());
            saveBatch(baseCategoryTrademarkList);
        }
    }
}
