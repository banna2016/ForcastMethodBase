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
	  private int smallNum;//小码
	  private int ciSmallNum;//次小码
	  private int middleNumber;//中码
	  private int ciBigNum;//次大码
	  private int bigNum;//大码
	  
	  public  Map<String, Object> numberMap = new HashMap<String, Object>();
	  
	  
	  
	public int getSmallNum() {
		return smallNum;
	}
	public void setSmallNum(int smallNum) {
		numberMap.remove("SMALL_NUM");
		numberMap.put("SMALL_NUM", smallNum);
		this.smallNum = smallNum;
	}
	public int getCiSmallNum() {
		return ciSmallNum;
	}
	public void setCiSmallNum(int ciSmallNum) {
		numberMap.remove("CI_SMALL_NUM");
		numberMap.put("CI_SMALL_NUM", ciSmallNum);
		this.ciSmallNum = ciSmallNum;
	}
	public int getMiddleNumber() {
		return middleNumber;
	}
	public void setMiddleNumber(int middleNumber) {
		numberMap.remove("MIDDLE_NUMBER");
		numberMap.put("MIDDLE_NUMBER", middleNumber);
		this.middleNumber = middleNumber;
	}
	public int getCiBigNum() {
		return ciBigNum;
	}
	public void setCiBigNum(int ciBigNum) {
		numberMap.remove("CI_BIG_NUM");
		numberMap.put("CI_BIG_NUM", ciBigNum);
		this.ciBigNum = ciBigNum;
	}
	public int getBigNum() {
		return bigNum;
	}
	public void setBigNum(int bigNum) {
		numberMap.remove("BIG_NUM");
		numberMap.put("BIG_NUM", bigNum);
		this.bigNum = bigNum;
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
