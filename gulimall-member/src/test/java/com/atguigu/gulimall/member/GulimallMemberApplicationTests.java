package com.atguigu.gulimall.member;

import com.atguigu.gulimall.member.entity.MemberEntity;
import com.atguigu.gulimall.member.service.MemberService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class GulimallMemberApplicationTests {

//    @Autowired
//    MemberService memberService;
//
//    @Test
//    void contextLoads() {
//        MemberEntity memberEntity = new MemberEntity();
//        memberEntity.setId(1L);
//        memberEntity.setNickname("test nickname");
//        boolean save = memberService.save(memberEntity);
//        System.out.println("保存成功：" + save);
//        List<MemberEntity> list = memberService.list(new QueryWrapper<MemberEntity>().eq("id", 1L));
//        list.forEach((item)->{
//            System.out.println(item);
//        });
//    }

}
