//package com.heb.db;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Profile;
//import org.springframework.jdbc.core.JdbcTemplate;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestMethod;
//import org.springframework.web.bind.annotation.RestController;
//
//
//@Profile({"local"})
//@RestController
//public class H2_dbController {
//	@Autowired
//	private JdbcTemplate jdbcTemplate;
//
//	@RequestMapping(value="/createTable", method = RequestMethod.GET)
//	public void createTable(){
//		try {
//			jdbcTemplate.execute("CREATE TABLE CUSTOMERDATA(" + "id SERIAL, name VARCHAR(255), age VARCHAR(255))");
//		} catch (Exception e){
//			e.printStackTrace();
//		}
//	}
//	@RequestMapping(value="/putData", method = RequestMethod.GET)
//	public void putData(){
//		try{
//			jdbcTemplate.execute("INSERT INTO CUSTOMERDATA(id, name, age) VALUES (1,'Ikennedy', '30')");
//		} catch (Exception e){
//			e.printStackTrace();
//		}
//	}
//
//}
