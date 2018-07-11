package com.lanlanauto.mapper;

import com.lanlan.util.CommonUtil;

public class Parse implements TableNameToModelName {

	@Override
	public String toModelName(String tableName) {
		int i= tableName.toLowerCase().indexOf("t_");
		String tableNameWithoutT=null;
		if(i==0) {
			tableNameWithoutT =tableName.substring(2,tableName.length());
		}else
		{
			tableNameWithoutT=tableName;
		}
		String [] strs = tableNameWithoutT.split("_");
		StringBuffer sb = new StringBuffer();
		for(String str :strs) {
			sb.append(CommonUtil.upperCaseFirst(str));
		}
	
		return sb.toString();
	}

}
