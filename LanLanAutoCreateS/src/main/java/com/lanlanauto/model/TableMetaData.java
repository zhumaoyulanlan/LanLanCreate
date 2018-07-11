package com.lanlanauto.model;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.lanlan.util.CommonUtil;
import com.lanlanauto.mapper.TableNameToModelName;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;


public class TableMetaData {
	private String tableName;
	private String modelNameFistUp;
	private String modelNameFistlow;
	private List<Field> fieldList=new ArrayList<Field>();
	private List<String> idNameList =new ArrayList<String>();
	
	private String fieldWithIDStr;
	private String fieldWithoutIDStr;
	private String getterAndSetterStr;
	private String importStr;
	
	/**
	 * 通过传入modelName ,tableName fieldList 和 idNameList参数初始化类
	 * 一般用于通过sql语句创建viewmodel,此时无需传入tableName和idNameList 
	 */
	public TableMetaData(String tableName,String modelName,List<Field> fieldList) {
		this.tableName=tableName;
		this.modelNameFistUp= CommonUtil.upperCaseFirst(modelName);
		this.modelNameFistlow= CommonUtil.lowerCaseFirst(modelName);
		this.fieldList=fieldList;
	}
	
	
	public TableMetaData(Connection connection ,String catalog, String schemaPattern, String tableNamePattern ,TableNameToModelName tomodelName ) throws SQLException {
		DatabaseMetaData metaData = connection.getMetaData();
		try(ResultSet rs= metaData.getTables(catalog, schemaPattern, tableNamePattern,  new String[]{"TABLE"});){
			while(rs.next()) {
				tableName=rs.getString("TABLE_NAME");
				break;
			}
		}
		if(tableName!=null) {
			modelNameFistUp = CommonUtil.upperCaseFirst(tomodelName.toModelName(tableName));
			modelNameFistlow = CommonUtil.lowerCaseFirst(tomodelName.toModelName(tableName));
		}
		//找到主健
		try(ResultSet rs = metaData.getPrimaryKeys(catalog, schemaPattern, tableNamePattern)){
			while(rs.next()) {
				idNameList.add(rs.getString("COLUMN_NAME"));
			}
		}
		//找到所有字段并给主健设上标记
		try(ResultSet rs = metaData.getColumns(catalog, schemaPattern, tableNamePattern,"%")){
			while(rs.next()) {
				Field field= new Field(rs.getString("COLUMN_NAME"), rs.getInt("DATA_TYPE"));
				for(String idName: idNameList) {
					if(field.getNameFirstLower().equals(CommonUtil.lowerCaseFirst(idName)))
					{
						field.setId(true);						
					}
				}
				fieldList.add(field);
			}
		}
	}
	
	public String getTableName() {
		return tableName;
	}
	public String getModelNameFistUp() {
		return modelNameFistUp;
	}
	public String getModelNameFistlow() {
		return modelNameFistlow;
	}

	/**
	 * 获取字段str
	 * @return
	 */
	public String getFieldStrWithID() {
		if(fieldWithIDStr==null) {
			fieldWithIDStr=getFieldStr(true);
		}
		return fieldWithIDStr;
	}
	/**
	 * 获取字段str
	 * @return
	 */
	public String getFieldStrWithodID() {
		if(fieldWithoutIDStr==null) {
			fieldWithoutIDStr=getFieldStr(true);
		}
		return fieldWithoutIDStr;
	}
	/**
	 * 获取字段str 可选是否添加@id
	 * @return
	 */
	private String getFieldStr(boolean addId) {
		StringBuffer sb = new StringBuffer();
		for(Field field : fieldList) {
			if(addId&&field.isId()) {
				sb.append("\t").append("@ID\n");
			}
			sb.append("\tprivate ").append(field.getTypeName()).append(" ").append(field.getNameFirstLower()).append(";\n");
		}
		return sb.toString();
	}
	

	public String getGetterAndSetterStr() {
		if(getterAndSetterStr==null) {
			StringBuffer sb = new StringBuffer();
			for(Field field : fieldList) {
				//set
				sb.append("\n\tpublic void set").append(field.getNameFirstUpper())
					.append("(").append(field.getTypeName()).append(" ").append(field.getNameFirstLower()).append("){").append("\n");
				sb.append("\t\tthis.").append(field.getNameFirstLower()).append("=").append(field.getNameFirstLower()).append(";\n");
				sb.append("\t}\n");
				
				//get
				sb.append("\n\tpublic ").append(field.getTypeName()).append(" get").append(field.getNameFirstUpper()).append("(){").append("\n");
				sb.append("\t\treturn ").append(field.getNameFirstLower()).append(";\n");
				sb.append("\t}\n");
			
			}
			getterAndSetterStr=sb.toString();
		}
		return getterAndSetterStr;
	}
	
	
	public String getImportStr() {
		if(importStr==null) {
			StringBuffer sb = new StringBuffer();
			for(Field field : fieldList) {
				if(field.getFullTypName().indexOf("java.lang")<0) {
					sb.append("import ").append(field.getFullTypName()).append(";\n");
				}
			}
			importStr=sb.toString();
		}
		return importStr;
	}
}
