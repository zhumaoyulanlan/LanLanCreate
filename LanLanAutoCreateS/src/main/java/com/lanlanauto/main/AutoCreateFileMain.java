package com.lanlanauto.main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.lanlan.util.CommonUtil;
import com.lanlan.util.DBUtil;

/**
 * 此类废弃
 * @author 朱矛宇
 * @date 2018年6月20日
 */
@Deprecated
public class AutoCreateFileMain {

	Connection connection;
	String catalog;
	String schema;
	String tableName;
	boolean forceOutPut =false;
	
	DatabaseMetaData metaData ;
	
	String baseSrc;
	String encoding = "UTF-8";   
	
//	//此路径以/结尾
//	String modelFloderSrc="/home/soft01/eclipse-workspace/LanLanAutoCreate/src/main/java/com/lanlanauto/tt/";
//	//此文件名不包含任何/
//	String daoTTName="Dao.TT";
//	String TargetDaoSrc="/home/soft01/eclipse-workspace/MyNewServletDemo/src/main/java/cn/tedu/dao/";
//	
//	String daoImplTTName ="DaoImpl.TT";
//	String TargetDaoImplSrc="/home/soft01/eclipse-workspace/MyNewServletDemo/src/main/java/cn/tedu/dao/impl/";
//
//	String ModeTTName="Model.TT";
//	String TargetModelSrc="/home/soft01/eclipse-workspace/MyNewServletDemo/src/main/java/cn/tedu/model/";
//	
//	String ColumnAndGetSetTTName="ColumnAndGetSet.TT";
	
	//此路径以/结尾
	String modelFloderSrc="/home/soft01/eclipse-workspace/LanLanAutoCreate/src/main/java/com/lanlanauto/tt/";
	//此文件名不包含任何/
	String daoTTName="Dao.TT";
	String TargetDaoSrc="/home/soft01/eclipse-workspace/LingFengOA/src/main/java/com/lingfeng/dao/";
	
	String daoImplTTName ="DaoImpl.TT";
	String TargetDaoImplSrc="/home/soft01/eclipse-workspace/LingFengOA/src/main/java/com/lingfeng/dao/impl/";
	
	String ModeTTName="Model.TT";
	String TargetModelSrc="/home/soft01/eclipse-workspace/LingFengOA/src/main/java/com/lingfeng/model/";
	
	String ColumnAndGetSetTTName="ColumnAndGetSet.TT";

	
	public AutoCreateFileMain(Connection connection ,String catalog, String schemas, String tableName,String baseSrc) {
		this.connection=connection;
		this.catalog =catalog;
		this.schema = schemas;
		this.tableName=tableName;
		this.baseSrc = baseSrc;
		try {
			metaData = connection.getMetaData();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	

	//表名改类名规则
	public String TableNameToModelName(String TableName) {
		int i= TableName.toLowerCase().indexOf("t_");
		String tableNameWithoutT=null;
		if(i==0) {
			tableNameWithoutT =TableName.substring(2,TableName.length());
		}else
		{
			tableNameWithoutT=TableName;
		}
		String [] strs = tableNameWithoutT.split("_");
		StringBuffer sb = new StringBuffer();
		for(String str :strs) {
			sb.append(upperCaseFirst(str));
		}
	
		return sb.toString();
	}

	

	//主方法,供main方法调用
	public void AutoCreate() {
		try {
			//获取所有表 Typical types are "TABLE","VIEW", "SYSTEM TABLE", "GLOBAL TEMPORARY","LOCAL TEMPORARY", "ALIAS", "SYNONYM".
			List<String>  tableNameList = new ArrayList<String>();
			try(ResultSet rs= metaData.getTables(catalog, schema, tableName, new String[]{"TABLE"} )) {
				while(rs.next()) {
					tableNameList.add(rs.getString("TABLE_NAME"));
				}
			}

			//遍历所有表.同时创建Dao文件,创建service
			for(String tableName:tableNameList) {
					String modelNameFistUp=TableNameToModelName(tableName);
					String modelNameFistlow = lowerCaseFirst(modelNameFistUp);
					
					String ttPath= null;
					String targetPath =null;
					String[] reg=null;
					String[] pattem=null;
					//dao文件
					ttPath=modelFloderSrc+daoTTName;
					targetPath=TargetDaoSrc+modelNameFistUp+"Dao.java";
					reg= new String[]{"@@ModelName@","@@modelName@"};
					pattem= new String[]{modelNameFistUp,modelNameFistlow};
					replaceTTtoJavaFile(ttPath, targetPath, reg, pattem);
					
					//daoImpl文件
					ttPath=modelFloderSrc+daoImplTTName;
					targetPath=TargetDaoImplSrc+modelNameFistUp+"DaoImpl.java";
					reg= new String[]{"@@ModelName@","@@modelName@"};
					pattem= new String[]{modelNameFistUp,modelNameFistlow};
					replaceTTtoJavaFile(ttPath, targetPath, reg, pattem);
					
					//service文件
					
					//serviceImpl暂未实现
					
					//model文件
					ttPath=modelFloderSrc+ModeTTName;
					targetPath=TargetModelSrc+modelNameFistUp+".java";
					String fieldAndSetGet=createFieldAndGetSet(tableName);
					reg= new String[]{"@@ModelName@","@@tableName@","@@fieldAndSetGet@"};
					pattem= new String[]{modelNameFistUp,tableName,fieldAndSetGet};
					replaceTTtoJavaFile(ttPath, targetPath, reg, pattem);
					
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

	public void replaceTTtoJavaFile(String ttPath,String targetPath,String[] reg,String[] pattem) throws IOException {

		File fileout=new File(targetPath);
		if(fileout.exists() && !forceOutPut) {
			System.out.println("目标文件:\""+targetPath+"\"已经存在,如果依旧需要覆盖原文件,请使用强制生成参数:\n在创建AutoCreateFile类后,执行对象的setForceOutPut(true)方法");
			return;
		}

		try (FileWriter fw = new FileWriter(fileout)){
			String all =replaceTTtoString( ttPath, reg, pattem);
			for(int i=0;i<reg.length;i++) {
				all=all.replaceAll(reg[i], pattem[i]);
			}
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
	public String replaceTTtoString(String ttPath,String[] reg,String[] pattem) throws IOException {
		File filein= new File(ttPath);
		try (
			FileInputStream fin=new FileInputStream(filein);
			){
			String encoding = "UTF-8";   
			Long filelength = filein.length();  
			byte[] filecontent = new byte[filelength.intValue()];   
			fin.read(filecontent);  
			
			String all =new String(filecontent,encoding);	
			for(int i=0;i<reg.length;i++) {
				all=all.replaceAll(reg[i], pattem[i]);
			}
			return all;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return ";;";
	}
	
	
	int id;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	private String createFieldAndGetSet(String tableName) {
		StringBuffer sb= new StringBuffer();
		try(ResultSet rs = metaData.getColumns(catalog, schema, tableName,"%")) {
			
			List<String> primaryKeyColumnNameList =new ArrayList<String>();
			try(ResultSet pkrs = metaData.getPrimaryKeys(catalog, schema, tableName)){
				while(pkrs.next()) {
					primaryKeyColumnNameList.add(pkrs.getString("COLUMN_NAME"));
				}
			}
		
			while(rs.next()) {
				int dateType = rs.getInt("DATA_TYPE");// java.sql.Types类型名称
				String annotation="";
				String columnName = rs.getString("COLUMN_NAME");// 列名
				String typeName =CommonUtil.sqlTypetoJavaType(dateType).getSimpleName();
				String lColumnName =CommonUtil.lowerCaseFirst(columnName);
				String uColumnName =CommonUtil.upperCaseFirst(columnName);
				
				for(String str: primaryKeyColumnNameList) {
					if(columnName.equals(str)) {
						annotation="\t@ID";
						break;
					}
				}
				String[] reg= new String[]{"@@annotation@","@@typeaName@","@@columnName@","@@ColumnName@"};
				String[] pattem= new String[]{annotation,typeName,lColumnName,uColumnName};
				
				String columnAndGetSet=replaceTTtoString(modelFloderSrc+ColumnAndGetSetTTName,reg,pattem);
				sb.append(columnAndGetSet);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return sb.toString();
	}
	
	public static String upperCaseFirst(String str) {  
	    char[] ch = str.toCharArray();  
	    if (ch[0] >= 'a' && ch[0] <= 'z') {  
	        ch[0] = (char) (ch[0] - 32);  
	    }  
	    return new String(ch);  
	}
	
	
	public static String lowerCaseFirst(String str) {  
	    char[] ch = str.toCharArray();  
	    if (ch[0] >= 'A' && ch[0] <= 'Z') {  
	        ch[0] = (char) (ch[0] + 32);  
	    }  
	    return new String(ch);  
	}
	

	public static void main(String[] args) {
		AutoCreateFileMain autoCreateFileMain=new AutoCreateFileMain(DBUtil.getConnection(),"LingFeng","LingFeng","%",null);
		//autoCreateFileMain.setForceOutPut(true);
		autoCreateFileMain.AutoCreate();

	}


	public boolean isForceOutPut() {
		return forceOutPut;
	}


	public void setForceOutPut(boolean forceOutPut) {
		this.forceOutPut = forceOutPut;
	}
}
