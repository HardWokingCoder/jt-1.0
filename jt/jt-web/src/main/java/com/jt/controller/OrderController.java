package com.jt.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.dubbo.config.annotation.Reference;
import com.jt.pojo.Cart;
import com.jt.pojo.Order;
import com.jt.service.CartService;
import com.jt.service.OrderService;
import com.jt.util.UserThreadLocal;
import com.jt.vo.SysResult;

@Controller
@RequestMapping("/order")
public class OrderController {
	
	@Reference(timeout=3000,check=false)
	private CartService cartService;
	
	@Reference(timeout=3000,check=false)
	private OrderService orderService;
	
	//实现根据orderId查询数据信息
	@RequestMapping("/success")
	public String findOrderByOrderId(String id,Model model) {
		Order order = orderService.findOrderById(id);
		model.addAttribute("order",order);
		return "success";
	}
	
	//跳转订单确认页面
	@RequestMapping("/create")
	public String create(Model model) {
		//根据用户信息查询购物车记录
		Long userId = UserThreadLocal.get().getId();
		List<Cart> carts = cartService.findCartList(userId);
		model.addAttribute("carts",carts);
		return "order-cart";
	}
	
	@RequestMapping("/submit")
	@ResponseBody
	public SysResult saveResult(Order order) {
		try {
			Long userId = UserThreadLocal.get().getId();
			order.setUserId(userId);
			String orderId = orderService.saveOrder(order);
			if(!StringUtils.isEmpty(orderId)) {
				return SysResult.ok((Object)orderId);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return SysResult.fail();
	}
	
}
