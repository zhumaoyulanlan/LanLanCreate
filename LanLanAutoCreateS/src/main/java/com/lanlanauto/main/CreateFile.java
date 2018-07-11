package com.lanlanauto.main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.swing.plaf.metal.MetalIconFactory.FolderIcon16;

import com.lanlan.util.CommonUtil;
import com.lanlan.util.DBUtil;
import com.lanlanauto.mapper.Parse;
import com.lanlanauto.mapper.TableNameToModelName;
import com.lanlanauto.model.Field;
import com.lanlanauto.model.TableMetaData;

public class CreateFile {
	

	Connection connection;
	String catalog;
	String schema;
	String tableName;
	TableNameToModelName toModelName;
	Properties replacePro;
	
//	DatabaseMetaData metaData ;
	private Boolean forceOutPut =false;
	//private String encoding ;

	private String modelFlodSrc;
	private String daoTTName;
	private String serviceTTName;
	private String modelTTName;
	private String daoImplTTName;
	private String serviceImplTTName;
	private String viewModelTTName;

	private String targetBaseSrc;
	private String targetDaoSrc;
	private String targetDaoImplSrc;
	private String targetServiceSrc;
	private String targetServiceImplSrc;
	private String targetModelSrc;
	private String targetViewModelSrc;

	private String basePackage;

	
	public CreateFile(Connection connection ,String catalog, String schemas, String tableName,TableNameToModelName toModelName) {
	
	
		try {
			
			this.connection=connection;
			this.catalog =catalog;
			this.schema = schemas;
			this.tableName=tableName;
			this.toModelName=toModelName;
			
			Properties properties= new Properties() ;
			properties.load(CreateFile.class.getClassLoader().getResourceAsStream("config/tt.properties"));
			
			
			modelFlodSrc=properties.getProperty("modelFlodSrc");
			daoTTName= properties.getProperty("daoTTName");
			serviceTTName= properties.getProperty("serviceTTName");
			modelTTName=properties.getProperty("modelTTName");
			daoImplTTName=properties.getProperty("daoImplTTName");
			serviceImplTTName=properties.getProperty("serviceImplTTName");
			viewModelTTName=properties.getProperty("viewModelTTName");
			                                                          
			targetBaseSrc=properties.getProperty("targetBaseSrc");
			targetDaoSrc=properties.getProperty("targetDaoSrc");
			targetDaoImplSrc=properties.getProperty("targetDaoImplSrc");
			targetServiceSrc=properties.getProperty("targetServiceSrc");
			targetServiceImplSrc=properties.getProperty("targetServiceImplSrc");
			targetModelSrc=properties.getProperty("targetModelSrc");
			targetViewModelSrc=properties.getProperty("targetViewModelSrc");
			
			basePackage=properties.getProperty("basePackage");
		
			//获取
			replacePro= new Properties() ;
			replacePro.load(new FileInputStream(new File(modelFlodSrc+"replace.properties")));
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
		
		

	
	
	/**
	 * 主方法创建主要dao
	 */
	public void createMain() {
		
		try {
			//获取所有表 
			List<String>  tableNameList = new ArrayList<String>();
			try(ResultSet rs= connection.getMetaData().getTables(catalog, schema, tableName, new String[]{"TABLE"} )) {
				while(rs.next()) {
					tableNameList.add(rs.getString("TABLE_NAME"));
				}
			}
	
			//遍历所有表.同时创建Dao文件,创建service
			for(String tableName:tableNameList) {
					String modelNameFistUp=CommonUtil.upperCaseFirst(toModelName.toModelName(tableName));
					String modelNameFistlow = CommonUtil.lowerCaseFirst(toModelName.toModelName(tableName));
					String ttPath= null;
					String targetPath =null;
					String packageStr = null;

					Map< String, String> replaceMap=new HashMap<String, String>();
					replaceMap.put(replacePro.getProperty("modelNameFistUp"),CommonUtil.upperCaseFirst(toModelName.toModelName(tableName)));
					replaceMap.put(replacePro.getProperty("modelNameFistlow"),CommonUtil.lowerCaseFirst(toModelName.toModelName(tableName)));
					replaceMap.put(replacePro.getProperty("tableName"),tableName);
					
					
					//dao文件
//					ttPath=modelFlodSrc+daoTTName;
//					targetPath=targetBaseSrc+targetDaoSrc+modelNameFistUp+"Dao.java";
//					replaceTTtoJavaFile(ttPath, targetPath, replaceMap);
					
					//daoImpl文件
//					ttPath=modelFlodSrc+daoImplTTName;
//					targetPath=targetBaseSrc+targetDaoImplSrc+modelNameFistUp+"DaoImpl.java";
//					replaceTTtoJavaFile(ttPath, targetPath, replaceMap);
	
					//service文件
//					ttPath=modelFlodSrc+serviceTTName;
//					targetPath=targetBaseSrc+targetServiceSrc+modelNameFistUp+"Service.java";
//					replaceTTtoJavaFile(ttPath, targetPath, replaceMap);
//					
					//serviceImpl
//					ttPath=modelFlodSrc+serviceImplTTName;
//					targetPath=targetBaseSrc+targetServiceImplSrc+modelNameFistUp+"ServiceImpl.java";
//					replaceTTtoJavaFile(ttPath, targetPath, replaceMap);
					
					//model文件
					ttPath=modelFlodSrc+modelTTName;
					targetPath=targetBaseSrc+targetModelSrc+modelNameFistUp+".java";
					packageStr=getPackage(basePackage, targetModelSrc);
					TableMetaData data=new TableMetaData(connection, catalog, schema, tableName,toModelName);
					replaceMap.put(replacePro.getProperty("importStr"), data.getImportStr());
					replaceMap.put(replacePro.getProperty("getterAndSetterStr"), data.getGetterAndSetterStr());
					replaceMap.put(replacePro.getProperty("fieldStr"), data.getFieldStrWithID());
					replaceMap.put(replacePro.getProperty("package"), packageStr);
					replaceTTtoJavaFile(ttPath, targetPath,replaceMap);
					
			}
			//读取模板文件,替换内容,写入文件
			//创建model
			//遍历表中数据,生成数据写入model

		} catch (SQLException e) {

			e.printStackTrace();
		} catch (FileNotFoundException e) {  
			e.printStackTrace();  
		} catch (IOException e) {  
			e.printStackTrace();  
		}  
	}
	
	
	public void createViewModeFromSql()
	{
		try(InputStream ins = CommonUtil.getResourceAsStream("create/sqlCreate.properties")){
			Properties properties = new Properties();
			properties.load(ins);
			List<Field> fieldList = new ArrayList<Field>();
			Set<Object> keys= properties.keySet();
			for(Object key :keys) {
				String modelName=(String)key;
				String modelNameFistUp=CommonUtil.upperCaseFirst(modelName);
				String modelNameFistlow = CommonUtil.lowerCaseFirst(modelName);
				String packageStr = getPackage(basePackage, targetViewModelSrc);

				//添加基本替换
				Map< String, String> replaceMap=new HashMap<String, String>();
				replaceMap.put(replacePro.getProperty("modelNameFistUp"),modelNameFistUp);
				replaceMap.put(replacePro.getProperty("modelNameFistlow"),modelNameFistlow);
				replaceMap.put(replacePro.getProperty("package"),packageStr);
				
				String sql= (String) properties.get(key);
				try(ResultSet resultset=DBUtil.executeQuery(sql)){
					ResultSetMetaData rsmd = resultset.getMetaData(); 
					int len  =rsmd.getColumnCount();
					for(int i = 1 ;i<=len ;i++ ) {
						Field field= new Field(rsmd.getColumnName(i),rsmd.getColumnType(i));
						fieldList.add(field);
					}
				}
				//model文件
				String ttPath=modelFlodSrc+viewModelTTName;
				String targetPath=targetBaseSrc+targetViewModelSrc+modelNameFistUp+".java";
				TableMetaData data=new TableMetaData(null,modelName,fieldList);
				replaceMap.put(replacePro.getProperty("importStr"), data.getImportStr());
				replaceMap.put(replacePro.getProperty("getterAndSetterStr"), data.getGetterAndSetterStr());
				replaceMap.put(replacePro.getProperty("fieldStr"), data.getFieldStrWithID());
				replaceTTtoJavaFile(ttPath, targetPath,replaceMap);
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	
		
		
	}
	
	public void replaceTTtoJavaFile(String ttPath,String targetPath,Map<String, String> map) throws IOException {

		File fileout=new File(targetPath);
		if(fileout.exists() && !forceOutPut) {
			System.out.println("目标文件:\""+targetPath+"\"已经存在,如果依旧需要覆盖原文件,请使用强制生成参数:\n在创建AutoCreateFile类后,执行对象的setForceOutPut(true)方法");
			return;
		}
		
		try (FileWriter fw = new FileWriter(fileout)){
			String all =replaceTTtoString(ttPath, map);
			fw.write(all);
			fw.flush();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 将模板文件按规则替换内容,返回字符串
	 * @param ttPath
	 * @param reg
	 * @param pattem
	 * @return
	 * @throws IOException
	 */
	public String replaceTTtoString(String ttPath,Map<String, String> map) throws IOException {
		File filein= new File(ttPath);
		try (
			FileInputStream fin=new FileInputStream(filein);
			){
			String encoding = "UTF-8";   
			Long filelength = filein.length();  
			byte[] filecontent = new byte[filelength.intValue()];   
			fin.read(filecontent);  
			String all =new String(filecontent,encoding);	
			
			Set<String> keys= map.keySet();
			for(String key :keys) {
				all=all.replaceAll(key, map.get(key));
			}

			return all;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return ";;";
	}

	private static String getPackage (String basePackage ,String targetSrc) {
		String rs =basePackage+targetSrc;
		rs= rs.replaceAll(File.separator, ".");
		if(rs.length()==rs.lastIndexOf(".")+1) {
			rs=rs.substring(0, rs.length()-1);
		}
		return rs;
	}


	public Boolean getForceOutPut() {
		return forceOutPut;
	}



	public void setForceOutPut(Boolean forceOutPut) {
		this.forceOutPut = forceOutPut;
	}
	
	public static void main(String[] args) {
		CreateFile createFile= new CreateFile(DBUtil.getConnection(), "LingFeng", "LingFeng", "%", new Parse());
//		createFile.setForceOutPut(true);
//		createFile.createMain();
		createFile.setForceOutPut(true);
		createFile.createViewModeFromSql();
	}
}
