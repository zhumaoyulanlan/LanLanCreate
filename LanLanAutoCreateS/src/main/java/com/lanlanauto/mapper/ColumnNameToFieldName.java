package com.lanlanauto.mapper;

import com.lanlan.util.CommonUtil;

public class ColumnNameToFieldName {
	public static String ToFieldName(String fieldName) {
		String[] strs = fieldName.split("_");
		StringBuffer sb = new StringBuffer();
		for (String string : strs) {
			//append(CommonUtil.lowerCaseFirst(string))
			sb.append(CommonUtil.upperCaseFirst(string));
		}
		return CommonUtil.lowerCaseFirst(sb.toString());
	}     
}
