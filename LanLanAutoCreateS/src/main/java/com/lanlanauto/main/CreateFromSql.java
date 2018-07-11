package com.lanlanauto.main;


import java.io.IOException;
import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import com.lanlan.util.CommonUtil;
import com.lanlan.util.DBUtil;
import com.lanlanauto.model.Field;

public class CreateFromSql {
	public static void main(String[] args)  {
		//sqlCreate.properties
		try(InputStream ins = CommonUtil.getResourceAsStream("create/sqlCreate.properties")){
			Properties properties = new Properties();
			properties.load(ins);
			List<Field> fieldList = new ArrayList<Field>();
			Set<Object> keys= properties.keySet();
			for(Object key :keys) {
				String modelName=CommonUtil.upperCaseFirst((String)key);
				String sql= (String) properties.get(key);
				try(ResultSet resultset=DBUtil.executeQuery(sql)){
					ResultSetMetaData rsmd = resultset.getMetaData(); 
					int len  =rsmd.getColumnCount();
					for(int i = 1 ;i<=len ;i++ ) {
						Field field= new Field(rsmd.getColumnName(i),rsmd.getColumnType(i));
						fieldList.add(field);
					}
				}				
			}
			//
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 通过字段和Model名创建一个显示model文件
	 */
//	public void modelNameAndFieldToViewModel(String modelName ,List<Field> fieldList) {
//		//@@basepackage@
//		for(Field field: fieldList) {
//			
//		}
//		
//		if(fieldStr==null) {
//			StringBuffer sb = new StringBuffer();
//			for(Field field : fieldList) {
//				if(field.isId()) {
//					sb.append("\t").append("@ID\n");
//				}
//				sb.append("\t").append(field.getTypeName()).append(" ").append(field.getNameFirstLower()).append(";\n");
//			}
//			fieldStr=sb.toString();
//		}
//		return fieldStr;
//	}
	
}
