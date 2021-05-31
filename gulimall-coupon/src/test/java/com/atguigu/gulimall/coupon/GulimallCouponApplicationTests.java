package com.atguigu.gulimall.coupon;

import com.atguigu.gulimall.coupon.entity.CouponEntity;
import com.atguigu.gulimall.coupon.service.CouponService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class GulimallCouponApplicationTests {

//    @Autowired
//    CouponService couponService;
//
//    @Test
//    void contextLoads() {
//        CouponEntity entity = new CouponEntity();
//        entity.setId(1L);
//        entity.setCouponName("test");
//        boolean save = couponService.save(entity);
//        System.out.println("保存成功：" + save);
//        List<CouponEntity> list = couponService.list(new QueryWrapper<CouponEntity>().eq("id", 1L));
//        list.forEach((item) -> {
//            System.out.println(item);
//        });
//    }

}
