package com.atguigu.gulimall.cart.service;

import com.atguigu.gulimall.cart.vo.CartItemVo;
import com.atguigu.gulimall.cart.vo.CartVo;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * 购物车服务
 */
public interface CartService {

    /**
     * 将商品添加至购物车
     */
    CartItemVo addToCart(Long skuId, Integer num) throws ExecutionException, InterruptedException;

    /**
     * 获取购物车某个购物项
     */
    CartItemVo getCartItem(Long skuId);

    /**
     * 获取购物车里面的信息
     * @return
     */
    CartVo getCart() throws ExecutionException, InterruptedException;

    /**
     * 清空购物车的数据
     * @param cartKey
     */
    public void clearCartInfo(String cartKey);

    /**
     * 勾选购物项
     * @param skuId
     * @param check
     */
    void checkItem(Long skuId, Integer check);

    /**
     * 改变商品数量
     * @param skuId
     * @param num
     */
    void changeItemCount(Long skuId, Integer num);


    /**
     * 删除购物项
     * @param skuId
     */
    void deleteIdCartInfo(Integer skuId);

    /**
     * 获取当前用户的购物车所有商品项
     * @return
     */
    List<CartItemVo> getUserCartItems();

}
