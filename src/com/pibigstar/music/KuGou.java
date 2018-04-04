package com.pibigstar.music;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.apache.commons.codec.digest.DigestUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import com.pibigstar.bean.Song;
/**
 * 酷狗音乐下载
 * @author pibigstar
 *
 */
public class KuGou {
	private static List<Song> songs = new ArrayList<>();
	public static void main(String[] args) {
		try {
			Scanner scanner = new Scanner(System.in);
			String name = new String(scanner.nextLine().getBytes("utf-8"),"gbk");
			String urlName = URLEncoder.encode(name, "gbk");
			scanner.close();
			parse(urlName);
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
	}
	
	/**
	 * 解析歌曲数据
	 * @param name
	 */
	private static void parse(String name) {
		Document document = null;
		try {
			document = Jsoup.connect("http://mobilecdn.kugou.com/api/v3/search/song?format=json&keyword="+ name +"&page=1&pagesize=10").get();
		} catch (IOException e) {
			e.printStackTrace();
		}
		String allData = document.select("body").text();
		JSONObject json = new JSONObject(allData);
		JSONObject data = (JSONObject) json.get("data");
		JSONArray infos =  (JSONArray) data.get("info");
		for (Object object : infos) {
			JSONObject info = (JSONObject)object;
			String songname = (String) info.get("songname");
			String hash = (String) info.get("hash");
			String singername = (String) info.get("singername");
			Song song = new Song();
			song.setSongname(songname);
			song.setSingername(singername);
			song.setHash(hash);
			String download = getKey(hash);;
			song.setDownload(download);
			songs.add(song);
		}
		
		for (Song song : songs) {
			System.out.println(song);
		}
	}
	
	
	/**
	 * 返回下载地址
	 * @param hash
	 * @return
	 */
	public static String getKey(String hash) {
		String md5 = DigestUtils.md5Hex(hash+"kgcloud");
		String url = "http://trackercdn.kugou.com/i/?cmd=4&hash="+ hash +"&key="+ md5 +"&pid=1&forceDown=0&vip=1";
		Document document = null;
		try {
			document = Jsoup.connect(url).get();
			String data = document.select("body").text();
			JSONObject json = new JSONObject(data);
			return json.getString("url");
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

}
