package cn.edu.bupt.springmvc.core.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TableUtil {

	public static String getBasedTableType(String type){
		if (type.toLowerCase().trim().equals("mysql")){
			return "DATABASE";
		}else if (type.toLowerCase().trim().equals("sqlserver")) {
			return "DATABASE";
		}else if (type.toLowerCase().trim().equals("excel")) {
			return "FILE";
		}else if (type.toLowerCase().trim().equals("csv")) {
			return "FILE";
		}
		return "FALSE_BASED_TABLE_TYPE";
	}
	
	public static String convertTablesToStr(String[] tables){
		StringBuilder sb = new StringBuilder();
		for (String t : tables) {
			sb.append(t).append(PropertiesValue.getProperty("separator"));
		}
		return sb.toString();
	}

	public static List<String> convertStrToTableList(String tableStr){
		List<String> tableList = null;
		String[] tables = tableStr.split(PropertiesValue.getProperty("separator"));
		if (tableStr != null && tables!= null){
			tableList = Arrays.asList(tables);
		}
		return tableList;
	}
	
}
