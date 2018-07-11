package com.lanlanauto.model;

import com.lanlan.util.CommonUtil;
import com.lanlan.util.Count;
import com.lanlanauto.mapper.ColumnNameToFieldName;

public class Field {
	
	private String nameFirstLower;
	private String nameFirstUpper;
	private String typeName;
	private String fullTypName;
	private boolean id=false;
	
	public Field(String name ,int sqltype) {
		this.nameFirstUpper =CommonUtil.upperCaseFirst(ColumnNameToFieldName.ToFieldName(name));
		this.nameFirstLower=CommonUtil.lowerCaseFirst(ColumnNameToFieldName.ToFieldName(name));
		this.typeName=CommonUtil.sqlTypetoJavaType(sqltype).getSimpleName();
		this.fullTypName=CommonUtil.sqlTypetoJavaType(sqltype).getName();
	}

	public String getNameFirstLower() {
		return nameFirstLower;
	}

	public String getNameFirstUpper() {
		return nameFirstUpper;
	}

	public String getTypeName() {
		return typeName;
	}

	public boolean isId() {
		return id;
	}

	public void setId(boolean id) {
		this.id = id;
	}

	public String getFullTypName() {
		return fullTypName;
	}

	
}
