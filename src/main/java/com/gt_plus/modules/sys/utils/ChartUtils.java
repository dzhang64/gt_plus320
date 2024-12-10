package com.gt_plus.modules.sys.utils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class ChartUtils {
	public static Map<String, Object> getPie(String title, String subTitle, String describe, Map<String, Object> data){
		return makePie(title, subTitle, describe, data);
	}
	
	private static Map<String, Object> makePie(String title, String subTitle, String describe, Map<String, Object> data) {
		Map<String, Object> pie = new HashMap<String, Object>();
		
		//封装标题
		if (title != null) pie.put("title", title);
		//封装小标题
		if (subTitle != null) pie.put("subTitle", subTitle);
		//封装数据描述
		if (describe != null) pie.put("describe", describe);
		
		//封装图例及数据
		if (data != null) {
			List<String> legend = new ArrayList<String>();
			List<String> series = new ArrayList<String>();
			for (Entry<String, Object> entry : data.entrySet()) {
				legend.add("'" + entry.getKey() + "'");
				series.add("{name:'" + entry.getKey() + "',value:" + entry.getValue() + "}");
			}
			pie.put("legend", legend);
			pie.put("series", series);
		}
		
		return pie;
	}
	
	public static Map<String, Object> getLine(String title, String subTitle, List<String> date, List<String> temperature, List<String> humidity) {
		return makeLine(title, subTitle, date, temperature , humidity);
	}
	
	private static Map<String, Object> makeLine(String title, String subTitle, List<String> date, List<String> temperature, List<String> humidity) {
		Map<String, Object> line = new HashMap<String, Object>();
		
		if (title != null) line.put("title", title);
		if (subTitle != null) line.put("subTitle", subTitle);
		if (date != null) line.put("date", date);
		if (temperature != null) line.put("temperature", temperature);
		if (humidity != null) line.put("humidity", humidity);
		
		return line;
	}

	public static Map<String, Object> getAnnular(String title, String subTitle,
			String describe, Map<String, Object> data) {
		return makeAnnular(title, subTitle, describe, data);
	}

	private static Map<String, Object> makeAnnular(String title,
			String subTitle, String describe, Map<String, Object> data) {
		Map<String, Object> annular = new HashMap<String, Object>();
		
		if (title != null) annular.put("title", title);
		if (subTitle != null) annular.put("subTitle", subTitle);
		if (describe != null) annular.put("describe", describe);
		if (data != null) {
			List<String> legend = new ArrayList<String>();
			List<String> series = new ArrayList<String>();
			for (Entry<String, Object> entry : data.entrySet()) {
				legend.add("'" + entry.getKey() + "'");
				series.add("{name:'" + entry.getKey() + "',value:" + entry.getValue() + "}");
			}
			annular.put("legend", legend);
			annular.put("series", series);
		}
		
		return annular;
	}

	public static Map<String, Object> getBar(String title, String subTitle, String barName,
			Map<String, Object> data1, Map<String, Object> data2) {
		return makeBar(title, subTitle, barName, data1, data2);
	}

	private static Map<String, Object> makeBar(String title, String subTitle, String barName,
			Map<String, Object> data1, Map<String, Object> data2) {
		Map<String, Object> bar = new HashMap<String, Object>();
		
		if (title != null) bar.put("title", title);
		if (subTitle != null) bar.put("subTitle", subTitle);
		if (barName != null) bar.put("barName", barName);
		if (data1 != null && data2 != null) {
			List<String> xAxisData = new ArrayList<String>();
			List<String> bar1 = new ArrayList<String>();
			List<String> bar2 = new ArrayList<String>();
			for (Entry<String, Object> entry : data1.entrySet()) {
				xAxisData.add("'" + entry.getKey() + "'");
				bar1.add(entry.getValue().toString());
				bar2.add(data2.get(entry.getKey()).toString());
			}
			bar.put("xAxisData", xAxisData);
			bar.put("bar1", bar1);
			bar.put("bar2", bar2);
		}
		
		return bar;
	}
}
