package com.skerdy.Ryel;

import com.skerdy.Ryel.ryel.MongoRyelQueryParser;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;

@SpringBootApplication
public class RyelApplication {

	@Autowired
	private MongoRyelQueryParser parser;

	public static void main(String[] args) {
		SpringApplication.run(RyelApplication.class, args);
	}

	@PostConstruct
	private void test(){
		String test =RyelStrings.twoLevelsTwoOperators();

		System.out.println("");
		System.out.println(test);
		System.out.println("");

		JSONObject object = new JSONObject();
		object.put("key1", "skerdi");

		parser.calculate(test, object);
		System.out.println("QUERY : " + parser.getRoot().getOperation());
	}
}
