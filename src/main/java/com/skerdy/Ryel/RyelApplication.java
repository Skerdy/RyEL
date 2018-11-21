package com.skerdy.Ryel;

import com.skerdy.Ryel.ryel.RyelParser;
import com.skerdy.Ryel.ryel.RyelRecord;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;

@SpringBootApplication
public class RyelApplication {

	@Autowired
	private RyelParser ryelParser;

	public static void main(String[] args) {
		SpringApplication.run(RyelApplication.class, args);
	}

	@PostConstruct
	private void test(){
		String test =RyelStrings.oneLevelOrOperation();
		System.out.println("");
		System.out.println(test);
		System.out.println("");
		for(RyelRecord ryelRecord : ryelParser.getRecords(test)){
			System.out.println(ryelRecord.toString());
		}
		System.out.println("");
		//System.out.println("ROOT RECORD : " + ryelParser.getRootRecord(test));
		//ryelParser.getRootRecord(test).printNice();
		JSONObject object = new JSONObject();
		object.put("key", "skerdi");

		System.out.println("ROOT RYEL" +  ryelParser.getRootMongoRyel(object));

		System.out.println("QUERY : " +ryelParser.getRootMongoRyel(object).getQuery().toString() );
	}
}
