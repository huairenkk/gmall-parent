package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.BaseCategoryTrademark;
import com.atguigu.gmall.model.product.BaseTrademark;
import com.atguigu.gmall.model.product.CategoryTrademarkVo;
import com.atguigu.gmall.product.service.BaseCategoryTrademarkService;
import com.atguigu.gmall.product.service.BaseTrademarkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/admin/product/baseCategoryTrademark")
public class BaseCategoryTrademarkController {
    @Autowired
    private BaseCategoryTrademarkService baseCategoryTrademarkService;
    @Autowired
    private BaseTrademarkService baseTrademarkService;

    /**
     * 根据category3Id获取品牌列表
     * @param category3Id
     * @return
     */
    //admin/product/baseCategoryTrademark/findTrademarkList/{category3Id}
    @GetMapping("/findTrademarkList/{category3Id}")
    public Result findTrademarkList(@PathVariable Long category3Id) {
        List<BaseCategoryTrademark> categoryTrademarkList = baseCategoryTrademarkService.findCategoryTrademarkList(category3Id);
        List<BaseTrademark> baseTrademarkList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(categoryTrademarkList)) {
            for (BaseCategoryTrademark baseCategoryTrademark : categoryTrademarkList) {
                Long trademarkId = baseCategoryTrademark.getTrademarkId();
                BaseTrademark baseTrademark = baseTrademarkService.getById(trademarkId);
                baseTrademarkList.add(baseTrademark);
            }
        }
        return Result.ok(baseTrademarkList);
    }

    /**
     * 删除分类品牌关联
     * @param category3Id
     * @param trademarkId
     * @return
     */
    //admin/product/baseCategoryTrademark/remove/{category3Id}/{trademarkId}
    @DeleteMapping("/remove/{category3Id}/{trademarkId}")
    public Result removeCategoryTrademark (@PathVariable Long category3Id,
                                           @PathVariable Long trademarkId) {
        baseCategoryTrademarkService.removeCategoryTrademark(category3Id,trademarkId);
        return Result.ok();
    }

    /**
     * 根据category3Id获取可选品牌列表
     * @param category3Id
     * @return
     */
    //admin/product/baseCategoryTrademark/findCurrentTrademarkList/{category3Id}
    @GetMapping("/findCurrentTrademarkList/{category3Id}")
    public Result findCurrentTrademarkList (@PathVariable Long category3Id) {
        List<BaseTrademark> trademarkList = baseCategoryTrademarkService.findCurrentTrademarkList(category3Id);
        return Result.ok(trademarkList);
    }

    /**
     * 保存分类品牌关联
     * @param categoryTrademarkVo
     * @return
     */
    //admin/product/baseCategoryTrademark/save
    @PostMapping("/save")
    public Result saveBaseCategoryTrademark(@RequestBody CategoryTrademarkVo categoryTrademarkVo) {
        baseCategoryTrademarkService.saveBaseCategoryTrademark(categoryTrademarkVo);
        return Result.ok();
    }



}
