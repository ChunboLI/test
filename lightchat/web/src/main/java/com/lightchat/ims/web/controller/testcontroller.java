package com.lightchat.ims.web.controller;

import java.io.IOException;

import com.lightchat.ims.common.Valicode;


public class testcontroller {
	public static void main(String args[]){
		try {
			Valicode v = new Valicode();
			System.out.println(v.getCode());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
