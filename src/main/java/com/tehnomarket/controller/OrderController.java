package com.tehnomarket.controller;

import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.Set;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.tehnomarket.model.Product;

@Controller
public class OrderController {

	@RequestMapping(value="/cart",method=RequestMethod.GET)
	public String goToCart(Model m,HttpSession session) {
		
		HashMap<Product,Integer> cart = (HashMap<Product, Integer>) session.getAttribute("cart");
		if(cart.isEmpty()) {
			return "emptyCart";
		}
		
		// we get an array with the products in the cart
		Product[] products = (Product[]) cart.keySet().toArray();
		
			
		return "cart";
	}
	
	
}
