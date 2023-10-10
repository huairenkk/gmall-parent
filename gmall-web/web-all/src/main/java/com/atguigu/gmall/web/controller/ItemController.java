package com.atguigu.gmall.web.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.item.client.ItemFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Map;

@Controller
public class ItemController {
    @Resource
    private ItemFeignClient itemFeignClient;
    /**
     * 商品详情页
     * @return
     */
    @GetMapping("/{skuId}.html")
    public String getItem(@PathVariable Long skuId, Model model) {
        Result<Map<String,Object>> item = itemFeignClient.getItem(skuId);
        model.addAllAttributes(item.getData());
        return "item/item";
    }
}
