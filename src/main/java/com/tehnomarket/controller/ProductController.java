package com.tehnomarket.controller;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.HttpSessionRequiredException;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.tehnomarket.model.Characteristics;
import com.tehnomarket.model.Product;
import com.tehnomarket.model.Review;
import com.tehnomarket.model.User;
import com.tehnomarket.model.dao.CharacteristicsDao;
import com.tehnomarket.model.dao.ProductDao;
import com.tehnomarket.model.dao.ReviewDao;

@Controller
public class ProductController {
	
	@Autowired
	ServletContext context;

	@Autowired
	private ProductDao productDao;
	
	@Autowired
	private CharacteristicsDao characteristicsDao;
	
	@Autowired 
	private ReviewDao reviewDao;

	@RequestMapping(value= {"/products/{catId}"},method=RequestMethod.GET)
	public String goToProducts(@PathVariable("catId") Integer catId,Model m,HttpSession session) {
		
		System.out.println(catId);
		ArrayList<Product> products = new ArrayList<Product>();
		
		try {
			 products = (ArrayList<Product>) productDao.getProductByCat(catId);
			 System.out.println("no problem from db");
		} catch (SQLException e) {
			e.printStackTrace();
			m.addAttribute("error", "Could not get products");
			return "error";
		}
		
		m.addAttribute("products", products);
		
		// "position in session saves the last category you have been 
		m.addAttribute("categoryId", catId);
		
		// "position in session saves the last category you have been 
		session.setAttribute("position", catId);
		return "products";
	}
	
	
	// SORTING OF PRODUCTS 
	@RequestMapping(value="/sort/{sort}",method=RequestMethod.GET)
	public String goToProductsSorted(@PathVariable("sort") String sortType,Model m,HttpSession session) {
		
		ArrayList<Product> products = new ArrayList<Product>();
		
		//sort by category
		Integer position = (Integer) session.getAttribute("position");
		if(position != null) {
			try {
				products = (ArrayList<Product>) productDao.getProductByCat(position);
				System.out.println("no problem from db");
			} catch (SQLException e) {
				m.addAttribute("error", "Could not sort");
				return "error";
			}
		}else {
			String search = (String) session.getAttribute("search");
			try {
				products = (ArrayList<Product>) productDao.search(search);
				System.out.println("no problem from db");
			} catch (SQLException e) {
				m.addAttribute("error", "Could not sort");
				return "error";
			}
		}
		
		if(sortType.equals("min")) {
			Collections.sort(products, (p1,p2)-> Float.compare(p1.getPrice(), p2.getPrice()));
		}
		else {
			Collections.sort(products, (p1,p2)-> Float.compare(p2.getPrice(), p1.getPrice()));
		}
		m.addAttribute("products", products);
		
		return "products";
	}
	
	@RequestMapping(value="/searchProduct",method=RequestMethod.POST)
	public String searchProducts(@ModelAttribute("search") String search,Model m,HttpSession session) {
		session.setAttribute("position", null);
		System.out.println(search);
		ArrayList<Product> products = new ArrayList<>();
		
		try {
			products = (ArrayList<Product>) productDao.search(search);
			m.addAttribute("products",products);
			
		} catch (SQLException e) {
			m.addAttribute("error", "Could not find products");
			return "error";
		}
		
		session.setAttribute("search",search);
		
		return "products";
	}
	
	
	// The cart is saved in a session with a HashMap
	@RequestMapping(value="/add_to_cart/{id}",method=RequestMethod.GET)
	public String addToCart(HttpSession session,HttpServletRequest request,@PathVariable("id") int id) throws SQLException {
		final Integer initialQuantitiy = 1; 
		// add to session 
		// check if basket exists in session
		// basket should be a collection of products and their quantity
		// quantity by default will be 1 when added
		System.out.println("Adding "+id+"to the cart");
		
		//get the cart from session
		HashMap<Product,Integer> theCart = (HashMap<Product,Integer>) session.getAttribute("cart");
		
		// do this if cart does not exist already
		if(theCart==null) {
			theCart = new HashMap<Product,Integer>();
		}
		
		
		//add product id to set , quantity is set to 1 as default
		Product product = productDao.getProductById(id);
		
		theCart.put(product, initialQuantitiy);
		
		// save it in the session
		session.setAttribute("cart", theCart);
		
		return "redirect:/";
	}
	
	@RequestMapping(value="/product/{id}",method=RequestMethod.GET)
	public String goToProduct(HttpServletRequest request,Model m,@PathVariable("id") Integer id,HttpSession session) {
		
		ArrayList<Characteristics> characts = new ArrayList<Characteristics>();
		ArrayList<Review> reviews = new ArrayList<Review>();
		
		
		session.setAttribute("position", id);
		System.out.println("SESSION ID IS"+id);
		int productId;
		// D change 
		productId = id;
		
		
		try {
			Product product = productDao.getProductById(productId);
			characts = characteristicsDao.getAllProductChar(productId);
			reviews= reviewDao.getAllProductReview(productId);
			Collections.reverse(reviews);
			m.addAttribute("product",product);
			m.addAttribute("characeristics",characts);
			m.addAttribute("reviews",reviews);
			System.out.println("tuka "+ productId + " sum "+product );
			if(product!=null){
				return "product";
			}
			else {
				m.addAttribute("error", "Could not load product ");
				return "error";
			}
		} catch (SQLException e) {
			m.addAttribute("error", "Could not find products " + e.getMessage());
			return "error";
		}
	}
	
	// Controller for adding to favourites
	@RequestMapping(value="/add_to_fav/{id}",method=RequestMethod.GET)
	public String addToFav(HttpServletRequest request,HttpSession session,@PathVariable("id") Integer id,Model m ) {
		
		//int idItem =Integer.parseInt(request.getParameter("id"));
		System.out.println("Adding "+id+"to the fav");
		User user = (User) session.getAttribute("user");
		
		long userId = user.getId();
		
		try {
			productDao.addToFavourites(userId,id);
		} catch (SQLException e) {
			m.addAttribute("error", "Could not add to favourites");
			return "error";
		}
		
		return "redirect:/";
	}
}
