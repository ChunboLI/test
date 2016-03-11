package com.lightchat.ims.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.lightchat.ims.core.UserService;
import com.lightchat.ims.dal.domain.UserDO;

@Controller
public class LoginController {
	
	@Autowired
	UserService userService;
	
	@RequestMapping(value="/login.htm")
	public String login(){
		return "xingeims/aaa";
	}
	@RequestMapping(value = "/love.htm")
	public String love(ModelMap map){
		UserDO userDO = new UserDO();
		map.put("userList", userService.queryForList(userDO));
		return "xingeims/aaa";
	}
	
	@RequestMapping(value = "/message.htm")
	public String message(){
		return "xingeims/message";
	}
}
