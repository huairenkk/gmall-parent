package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.*;
import com.atguigu.gmall.product.service.ManageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@Api(tags = "商品基础属性接口")
@RestController
@RequestMapping("/admin/product")
public class BaseManageController {
    @Autowired
    private ManageService manageService;
    /**
     * 获取一级分类列表
     * @return
     */
    @GetMapping("/getCategory1")
    @ApiOperation("获取一级分类列表")
    public Result<List<BaseCategory1>> getCategory1(){
        List<BaseCategory1> baseCategory1List = manageService.getCategory1();
        return Result.ok(baseCategory1List);
    }
    /**
     * 获取二级分类列表
     * @return
     */
    @GetMapping("/getCategory2/{category1Id}")
    @ApiOperation("获取二级分类列表")
    public Result<List<BaseCategory2>> getCategory2(@PathVariable Long category1Id){
        List<BaseCategory2> baseCategory2List = manageService.getCategory2(category1Id);
        return Result.ok(baseCategory2List);
    }
    /**
     * 获取三级分类列表
     * @return
     */
    @GetMapping("/getCategory3/{category2Id}")
    @ApiOperation("获取三级分类列表")
    public Result<List<BaseCategory3>> getCategory3(@PathVariable Long category2Id){
        List<BaseCategory3> baseCategory3List = manageService.getCategory3(category2Id);
        return Result.ok(baseCategory3List);
    }
//admin/product/attrInfoList/{category1Id}/{category2Id}/{category3Id}

    /**
     * 根据分类Id 获取平台属性数据
     * @param category1Id
     * @param category2Id
     * @param category3Id
     * @return 个分类级别下的排序好的平台属性列表
     */
    @ApiOperation("根据分类Id 获取平台属性数据")
    @GetMapping("/attrInfoList/{category1Id}/{category2Id}/{category3Id}")
    public Result attrInfoList(@PathVariable Long category1Id,
                               @PathVariable Long category2Id,
                               @PathVariable Long category3Id){
        List<BaseAttrInfo> baseAttrInfoList = manageService.attrInfoList(category1Id,category2Id,category3Id);
        return Result.ok(baseAttrInfoList);
    }

    /**
     * admin/product/saveAttrInfo
     * 保存-修改平台属性
     * @param baseAttrInfo
     * @return
     */
    @PostMapping("/saveAttrInfo")
    @ApiOperation("保存-修改平台属性")
    public Result saveAttrInfo(@RequestBody BaseAttrInfo baseAttrInfo){
        manageService.saveAttrInfo(baseAttrInfo);
        return Result.ok();
    }

    /**
     * 根据平台属性Id 获取到平台属性值集合
     * @param attrId
     * @return
     */
    //根据平台属性Id 获取到平台属性值集合
    //admin/product/getAttrValueList/{attrId}
    @GetMapping("/getAttrValueList/{attrId}")
    @ApiOperation("根据平台属性Id 获取到平台属性值集合")
    public Result getAttrValueList(@PathVariable Long attrId) {
        List<BaseAttrValue> valueList = manageService.getAttrValueList(attrId);

        return Result.ok(valueList);
    }
}
