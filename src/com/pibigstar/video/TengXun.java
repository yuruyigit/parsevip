package com.pibigstar.video;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.jsoup.Jsoup;
/**
 * 腾讯视频真实地址解析
 * @author pibigstar
 *
 */
public class TengXun {
	
	private static String url = "https://v.qq.com/x/cover/i57sqefkulqgkb5.html"; //视频的地址
	private static String definition = "sd";// 视频清晰度shd, hd, sd
	private static List<String> urls = null;//存放最后得到的视频真实地址
	private static SAXReader reader = null;
	
	public static void main(String[] args) throws UnsupportedEncodingException{
		
		String vid = getVid(url);
		
		urls = getRealUrls(vid,definition);
		for (String string : urls) {
			System.out.println(string);
		}
		
		
	}

	/**
	 * 拿到真实视频URL
	 * @param vid 视频vid
	 * @param definition 清晰度
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	private static List<String> getRealUrls(String vid, String definition) throws UnsupportedEncodingException {
		urls = new ArrayList<>();//存放最后得到的视频真实地址
		
		Map<String, String> data = new HashMap<>();
		data.put("isHLS", "False");
		data.put("charge", "0");
		data.put("vid", vid);
		data.put("defn", definition);
		data.put("defnpayver", "1");
		data.put("otype", "application/json");
		data.put("platform", "10901");
		data.put("sdtfrom", "v1010");
		data.put("host", "v.qq.com");
		data.put("fhdswitch", "0");
		data.put("show1080p", "1");
		String content = null;
		try {
			content = Jsoup.connect("http://h5vv.video.qq.com/getinfo").data(data).post().toString();
		} catch (IOException e) {
			e.printStackTrace();
		}
		reader = new SAXReader();
		try {
			Document document = reader.read(new ByteArrayInputStream(content.getBytes("UTF-8")));
			Element root = document.getRootElement();
			List<Element> uis = root.element("vl").element("vi").element("ul").elements("ui");
			String url_prefix = uis.get(0).element("url").getText().trim();
			String streamId = null;
			String filename = null;
			
			List<Element> fis = root.element("fl").elements("fi");
			for (Element fi : fis) {
				if (!fi.element("name").getText().trim().equals(definition)) {
					continue;
				}
				streamId = fi.element("id").getText().trim();				
				List<Element> cis = root.element("vl").element("vi").element("cl").elements("ci");
				for (Element ci : cis) {
					String keyId = ci.element("keyid").getText().trim();
					filename = keyId.replace(".10", ".p")+".mp4";
					
					data.put("format", streamId);
					data.put("filename", filename);
					data.put("vt", "217");
				
					String keyUrl = "http://h5vv.video.qq.com/getkey";
					try {
						String keyContent = Jsoup.connect(keyUrl).data(data).post().toString();
						Document keyDocument = reader.read(new ByteArrayInputStream(keyContent.getBytes("UTF-8")));
						Element rootElement = keyDocument.getRootElement();
						String key = rootElement.element("key").getText().trim();
						String url = url_prefix+"/"+filename+"?sdtfrom=v1010&vkey="+key;
						urls.add(url);
						
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		return urls;
	}
	
	/**
	 * 通过正则拿到视频的vid
	 * @param url
	 * @return
	 */
	public static String getVid(String url) {
		String vid = "";
		try {
			String vidContent = Jsoup.connect(url).get().toString();
			Pattern pattern = Pattern.compile("\"V\":\".*\"");
			Matcher matcher = pattern.matcher(vidContent);
			if (matcher.find()) {
				String first = matcher.group().replaceAll("\"V\":\"", "");
				vid = first.substring(0, first.indexOf("\""));
				//System.out.println(vid);
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		return vid;
	}

}
