package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.model.product.SpuImage;
import com.atguigu.gmall.model.product.SpuSaleAttr;
import com.atguigu.gmall.product.service.ManageService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "商品SKU接口")
@RestController
@RequestMapping("/admin/product")
public class SkuManageController {
    @Autowired
    private ManageService manageService;
    //admin/product/spuSaleAttrList/{spuId}
    @GetMapping("/spuSaleAttrList/{spuId}")
    public Result getSpuSaleAttrList(@PathVariable Long spuId) {
        List<SpuSaleAttr> spuSaleAttrList = manageService.getSpuSaleAttrList(spuId);
        return Result.ok(spuSaleAttrList);
    }
    //admin/product/spuImageList/{spuId}
    @GetMapping("/spuImageList/{spuId}")
    public Result getSpuImageList (@PathVariable Long spuId) {
        List<SpuImage> spuImageList = manageService.getSpuImageList(spuId);
        return Result.ok(spuImageList);
    }
    @PostMapping("/saveSkuInfo")
    //admin/product/saveSkuInfo
    public Result saveSkuInfo(@RequestBody SkuInfo skuInfo) {
        manageService.saveSkuInfo(skuInfo);
        return Result.ok();
    }

    //admin/product/list/{page}/{limit}

    /**
     *
     * @param
     * @param
     * @return
     */
    @GetMapping("/list/{page}/{limit}")
    public Result getSkuList(@PathVariable Long page,
                             @PathVariable Long limit) {
        Page<SkuInfo> skuInfoPage = new Page<>(page,limit);
        IPage<SkuInfo> skuPage = manageService.getSkuList(skuInfoPage);
        return Result.ok(skuPage);
    }
    //admin/product/onSale/{skuId}
    @GetMapping("/onSale/{skuId}")
    public Result onSale(@PathVariable Long skuId) {
        manageService.onSale(skuId);
        return Result.ok();
    }
    //admin/product/cancelSale/{skuId}
    @GetMapping("/cancelSale/{skuId}")
    public Result cancelSale(@PathVariable Long skuId) {
        manageService.cancelSale(skuId);
        return Result.ok();
    }
}
