package com.atguigu.gulimall.product.vo;

import com.atguigu.gulimall.product.entity.SkuImagesEntity;
import com.atguigu.gulimall.product.entity.SkuInfoEntity;
import com.atguigu.gulimall.product.entity.SpuInfoDescEntity;
import lombok.Data;

import java.util.List;

@Data
public class SkuItemVo {
    /**
     * 1、sku基本信息【标题、副标题、价格】pms_sku_info
     * 2、sku图片信息【每个sku_id对应了多个图片】pms_sku_images
     * 3、spu下所有sku销售属性组合【不只是当前sku_id所指定的商品】
     * 4、spu商品介绍【】
     * 5、spu规格与包装【参数信息】
     */

    //1、sku基本信息的获取  pms_sku_info
    private SkuInfoEntity info;

    private boolean hasStock = true;

    //2、sku的图片信息    pms_sku_images
    private List<SkuImagesEntity> images;

    //3、获取spu的销售属性组合【一个vo是一个销售属性】
    private List<SkuItemSaleAttrVo> saleAttr;

    //4、获取spu的介绍
    private SpuInfoDescEntity desc;

    //5、获取spu的规格参数信息【以组为单位】
    private List<SpuItemAttrGroupVo> groupAttrs;

//    //6、秒杀商品的优惠信息
//    private SeckillSkuVo seckillSkuVo;

    //7、秒杀信息
    private SeckillSkuVo seckillSkuVo;

}
