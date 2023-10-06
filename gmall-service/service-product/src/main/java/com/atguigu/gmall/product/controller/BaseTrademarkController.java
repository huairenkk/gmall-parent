package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.BaseTrademark;
import com.atguigu.gmall.product.service.BaseTrademarkService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/product/baseTrademark")
public class BaseTrademarkController {
    @Autowired
    private BaseTrademarkService baseTrademarkService;

    //admin/product/baseTrademark/{page}/{limit}
    //商品管理，获取分页列表
    @GetMapping("/{page}/{limit}")
    public Result getBaseTrademarkByPage(@PathVariable Long page,
                                         @PathVariable Long limit) {
        Page<BaseTrademark> trademarkPage = new Page<>(page, limit);
        Page<BaseTrademark> baseTrademarkPage = baseTrademarkService.getBaseTrademarkByPage(trademarkPage);
        return Result.ok(baseTrademarkPage);
    }

    /***
     * 根据品牌Id 获取品牌对象
     * @param id
     * @return
     */
    //admin/product/baseTrademark/get/{id}
    @GetMapping("/get/{id}")
    public Result getBaseTrademark(@PathVariable Long id) {
        BaseTrademark trademark = baseTrademarkService.getById(id);
        return Result.ok(trademark);
    }

    /**
     * 保存
     *
     * @param baseTrademark
     * @return
     */
    //admin/product/baseTrademark/save
    @PostMapping("/save")
    public Result saveBaseTrademark(@RequestBody BaseTrademark baseTrademark) {
        baseTrademarkService.save(baseTrademark);
        return Result.ok();
    }

    /**
     * 修改
     *
     * @param baseTrademark
     * @return
     */
    //admin/product/baseTrademark/update
    @PutMapping("/update")
    public Result updateBaseTrademark(@RequestBody BaseTrademark baseTrademark) {
        baseTrademarkService.updateById(baseTrademark);
        return Result.ok();
    }

    //admin/product/baseTrademark/remove/{id}
    @DeleteMapping("/remove/{id}")
    public Result removeBaseTrademark(@PathVariable Long id) {
        baseTrademarkService.removeById(id);
        return Result.ok();
    }
}
