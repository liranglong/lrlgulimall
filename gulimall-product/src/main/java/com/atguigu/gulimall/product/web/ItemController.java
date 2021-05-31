package com.atguigu.gulimall.product.web;

import com.atguigu.gulimall.product.service.SkuInfoService;
import com.atguigu.gulimall.product.vo.SkuItemVo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.annotation.Resource;
import java.util.concurrent.ExecutionException;

@Controller
public class ItemController {

    @Resource
    private SkuInfoService skuInfoService;

    /**
     * 展示当前sku的详情
     * @param skuId
     * @return
     */
    @GetMapping("/{skuId}.html")
    public String skuItem(@PathVariable("skuId") Long skuId, Model model) throws ExecutionException, InterruptedException {
        System.out.println("准备查询" + skuId + "详情");

        /**
         * 1、sku基本信息【标题、副标题、价格】pms_sku_info
         * 2、sku图片信息【每个sku_id对应了多个图片】pms_sku_images
         * 3、spu下所有sku销售属性组合【不只是当前sku_id所指定的商品】
         * 4、spu商品介绍【】
         * 5、spu规格与包装【参数信息】
         */
        SkuItemVo vos = skuInfoService.item(skuId);
        model.addAttribute("item",vos);
        return "item";
    }
}
