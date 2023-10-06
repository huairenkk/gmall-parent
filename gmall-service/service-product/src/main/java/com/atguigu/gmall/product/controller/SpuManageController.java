package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.BaseSaleAttr;
import com.atguigu.gmall.model.product.SpuInfo;
import com.atguigu.gmall.product.service.ManageService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/product")
public class SpuManageController {
    @Autowired
    private ManageService manageService;


    /**
     * //admin/product/{page}/{limit}
     * //spu分页列表
     *
     * @param page
     * @param limit
     * @return
     */
    @GetMapping("/{page}/{limit}")
    public Result getSpuInfoPage(@PathVariable Long page,
                                 @PathVariable Long limit,
                                 Long category3Id) {
        //创建分页对象
        Page<SpuInfo> spuInfoPage = new Page<>(page, limit);
        //获取数据
        IPage<SpuInfo> spuInfoPageList = manageService.getSpuInfoPage(spuInfoPage, category3Id);
        return Result.ok(spuInfoPageList);
    }

    @GetMapping("/baseSaleAttrList")
    //admin/product/baseSaleAttrList
    public Result getBaseSaleAttrList() {
        List<BaseSaleAttr> baseSaleAttrList = manageService.getBaseSaleAttrList();
        return Result.ok(baseSaleAttrList);
    }

    /**
     * 保存spu
     * @return
     */
    //admin/product/saveSpuInfo
    @PostMapping("/saveSpuInfo")
    public Result saveSpuInfo() {

        return Result.ok();
    }
}
