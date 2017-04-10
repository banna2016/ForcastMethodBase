package com.byl.forcast;

import java.util.HashMap;
import java.util.Map;

public class SrcFiveDataBean {
	private String issueId;
	  private int no1;
	  private int no2;
	  private int no3;
	  private int no4;
	  private int no5;
	  private int threeSum;
	  private int threeSpan;
	  private int fiveSum;
	  private int fiveSpan;
	  private int bigCount;
	  private int oddNum;
	  private int SMALLEST_NUM;//小码
	  private int SMALLER_NUM;//次小码
	  private int MIDDLE_NUM;//中码
	  private int BIGGER_NUM;//次大码
	  private int BIGGEST_NUM;//大码
	  private String noArr;
	  
	  public  Map<String, Object> numberMap = new HashMap<String, Object>();
	  
	  
	  
	  
	public int getSMALLEST_NUM() {
		return SMALLEST_NUM;
	}
	public void setSMALLEST_NUM(int sMALLEST_NUM) {
		numberMap.put("SMALLEST_NUM", sMALLEST_NUM);
		SMALLEST_NUM = sMALLEST_NUM;
	}
	public int getSMALLER_NUM() {
		return SMALLER_NUM;
	}
	public void setSMALLER_NUM(int sMALLER_NUM) {
		numberMap.put("SMALLER_NUM", sMALLER_NUM);
		SMALLER_NUM = sMALLER_NUM;
	}
	public int getMIDDLE_NUM() {
		return MIDDLE_NUM;
	}
	public void setMIDDLE_NUM(int mIDDLE_NUM) {
		numberMap.put("MIDDLE_NUM", mIDDLE_NUM);
		MIDDLE_NUM = mIDDLE_NUM;
	}
	public int getBIGGER_NUM() {
		return BIGGER_NUM;
	}
	public void setBIGGER_NUM(int bIGGER_NUM) {
		numberMap.put("BIGGER_NUM", bIGGER_NUM);
		BIGGER_NUM = bIGGER_NUM;
	}
	public int getBIGGEST_NUM() {
		return BIGGEST_NUM;
	}
	public void setBIGGEST_NUM(int bIGGEST_NUM) {
		numberMap.put("BIGGEST_NUM", bIGGEST_NUM);
		BIGGEST_NUM = bIGGEST_NUM;
	}
	public String getNoArr() {
		return noArr;
	}
	public void setNoArr(String noArr) {
		numberMap.put("noArr", noArr);
		this.noArr = noArr;
	}
	public Map<String, Object> getNumberMap() {
		return numberMap;
	}
	public void setNumberMap(Map<String, Object> numberMap) {
		this.numberMap = numberMap;
	}
	public String getIssueId() {
		return issueId;
	}
	public void setIssueId(String issueId) {
		this.issueId = issueId;
	}
	public int getNo1() {
		return no1;
	}
	public void setNo1(int no1) {
		numberMap.remove("no1");
		numberMap.put("no1", no1);
		this.no1 = no1;
	}
	public int getNo2() {
		return no2;
	}
	public void setNo2(int no2) {
		numberMap.remove("no2");
		numberMap.put("no2", no2);
		this.no2 = no2;
	}
	public int getNo3() {
		return no3;
	}
	public void setNo3(int no3) {
		numberMap.remove("no3");
		numberMap.put("no3", no3);
		this.no3 = no3;
	}
	public int getNo4() {
		return no4;
	}
	public void setNo4(int no4) {
		numberMap.remove("no4");
		numberMap.put("no4", no4);
		this.no4 = no4;
	}
	public int getNo5() {
		return no5;
	}
	public void setNo5(int no5) {
		numberMap.remove("no5");
		numberMap.put("no5", no5);
		this.no5 = no5;
	}
	public int getThreeSum() {
		return threeSum;
	}
	public void setThreeSum(int threeSum) {
		this.threeSum = threeSum;
	}
	public int getThreeSpan() {
		return threeSpan;
	}
	public void setThreeSpan(int threeSpan) {
		this.threeSpan = threeSpan;
	}
	public int getFiveSum() {
		return fiveSum;
	}
	public void setFiveSum(int fiveSum) {
		this.fiveSum = fiveSum;
	}
	public int getFiveSpan() {
		return fiveSpan;
	}
	public void setFiveSpan(int fiveSpan) {
		this.fiveSpan = fiveSpan;
	}
	public int getBigCount() {
		return bigCount;
	}
	public void setBigCount(int bigCount) {
		this.bigCount = bigCount;
	}
	public int getOddNum() {
		return oddNum;
	}
	public void setOddNum(int oddNum) {
		this.oddNum = oddNum;
	}
	
	  
	 
}
