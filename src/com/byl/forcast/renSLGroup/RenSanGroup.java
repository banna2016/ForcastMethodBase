package com.byl.forcast.renSLGroup;

import java.util.HashMap;
import java.util.Map;

public class RenSanGroup 
{
	private String PREDICTION_TYPE;
	private String EXPERT_ID;
	private String ISSUE_NUMBER;
	private String GROUP1;
	private String GROUP2;
	private String GROUP3;
	private String GROUP4;
	private String GROUP5;
	private String GROUP6;
	
	public Map<String,String> map = new HashMap<String,String>();
	
	public String getPREDICTION_TYPE() {
		return PREDICTION_TYPE;
	}
	public void setPREDICTION_TYPE(String pREDICTION_TYPE) {
		PREDICTION_TYPE = pREDICTION_TYPE;
	}
	public String getEXPERT_ID() {
		return EXPERT_ID;
	}
	public void setEXPERT_ID(String eXPERT_ID) {
		EXPERT_ID = eXPERT_ID;
	}
	public String getISSUE_NUMBER() {
		return ISSUE_NUMBER;
	}
	public void setISSUE_NUMBER(String iSSUE_NUMBER) {
		ISSUE_NUMBER = iSSUE_NUMBER;
	}
	public String getGROUP1() {
		return GROUP1;
	}
	public void setGROUP1(String gROUP1) {
		map.put("group1", gROUP1);
		GROUP1 = gROUP1;
	}
	public String getGROUP2() {
		return GROUP2;
	}
	public void setGROUP2(String gROUP2) {
		map.put("group2", gROUP2);
		GROUP2 = gROUP2;
	}
	public String getGROUP3() {
		return GROUP3;
	}
	public void setGROUP3(String gROUP3) {
		map.put("group3", gROUP3);
		GROUP3 = gROUP3;
	}
	public String getGROUP4() {
		return GROUP4;
	}
	public void setGROUP4(String gROUP4) {
		map.put("group4", gROUP4);
		GROUP4 = gROUP4;
	}
	public String getGROUP5() {
		return GROUP5;
	}
	public void setGROUP5(String gROUP5) {
		map.put("group5", gROUP5);
		GROUP5 = gROUP5;
	}
	public String getGROUP6() {
		return GROUP6;
	}
	public void setGROUP6(String gROUP6) {
		map.put("group6", gROUP6);
		GROUP6 = gROUP6;
	}
	
	
}
