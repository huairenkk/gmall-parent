package com.atguigu.gmall.product.api;

import com.atguigu.gmall.model.product.*;
import com.atguigu.gmall.product.service.ManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/product/inner")
public class ProductApiController {
    @Autowired
    private ManageService manageService;

    /**
     * 根据skuId获取SkuInfo
     * api/product/inner/getSkuInfo/{skuId}
     * @param skuId
     * @return
     */
    @GetMapping("/getSkuInfo/{skuId}")
    public SkuInfo getSkuInfo(@PathVariable Long skuId) {
        SkuInfo skuInfo = manageService.getSkuInfo(skuId);
        return skuInfo;
    }

    /**
     * 根据三级分类id获取分类信息
     * api/product/inner/getCategoryView/{category3Id}
     * @param category3Id
     * @return
     */
    @GetMapping("/getCategoryView/{category3Id}")
    public BaseCategoryView getCategoryView(@PathVariable Long category3Id) {
        BaseCategoryView baseCategoryView = manageService.getCategoryView(category3Id);
        return baseCategoryView;
    }

    /**
     * 根据skuId 获取最新的商品价格
     * api/product/inner/getSkuPrice/{skuId}
     * @param skuId
     * @return
     */
    @GetMapping("/getSkuPrice/{skuId}")
    public BigDecimal getSkuPrice(@PathVariable Long skuId) {
        return manageService.getSkuPrice(skuId);
    }

    /**
     * 根据spuId 获取海报数据
     * api/product/inner/findSpuPosterBySpuId/{spuId}
     * @param spuId
     * @return
     */
    @GetMapping("/findSpuPosterBySpuId/{spuId}")
    public List<SpuPoster> findSpuPosterBySpuId(@PathVariable Long spuId) {
        return manageService.findSpuPosterBySpuId(spuId);
    }

    /**
     * 根据spuId,skuId 获取销售属性数据
     * /api/product/inner/getSpuSaleAttrListCheckBySku/{skuId}/{spuId}
     * @param skuId
     * @param spuId
     * @return
     */
    @GetMapping("/getSpuSaleAttrListCheckBySku/{skuId}/{spuId}")
    public List<SpuSaleAttr> getSpuSaleAttrListCheckBySku(@PathVariable Long skuId,
                                                          @PathVariable Long spuId) {
        return manageService.getSpuSaleAttrListCheckBySku(skuId,spuId);
    }

    /**
     * /api/product/inner/getSkuValueIdsMap/{spuId}
     * 根据spuId 查询map 集合属性
     * @param spuId
     * @return
     */
    @GetMapping("/getSkuValueIdsMap/{spuId}")
    public Map getSkuValueIdsMap(@PathVariable Long spuId) {
        return manageService.getSkuValueIdsMap(spuId);
    }

    //api/product/inner/getAttrList/{skuId}
    //根据skuId 获取平台属性数据
    @GetMapping("/getAttrList/{skuId}")
    public List<BaseAttrInfo> getAttrList(@PathVariable Long skuId) {
        return manageService.getAttrList(skuId);
    }
}
