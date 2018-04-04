package com.pibigstar.test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class TengXun {
	
	public static void main(String[] args){

		Map<String, String> data = new HashMap<>();
		data.put("isHLS", "False");
		data.put("charge", "0");
		data.put("vid", "y00221a60w7");
		data.put("defn", "hd");
		data.put("defnpayver", "1");
		data.put("otype", "json");
		data.put("platform", "10901");
		data.put("sdtfrom", "v1010");
		data.put("host", "v.qq.com");
		data.put("fhdswitch", "0");
		data.put("show1080p", "1");
		Document document = null;
		try {
			document = Jsoup.connect("http://h5vv.video.qq.com/getinfo").data(data).post();
		} catch (IOException e) {
			e.printStackTrace();
		}
		JSONObject json = new JSONObject(document);
		System.out.println(json);
		
		
	}

}
