package com.byl.forcast;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DataToDb 
{
	public String findMaxIssueIdFromSrcDb()
	  {
	    Connection srcConn = ConnectSrcDb.getSrcConnection();
	    String issueId = null;
	    PreparedStatement pstmt = null;
	    String sql = "SELECT max(ISSUE_NUMBER) FROM " + App.srcNumberTbName;
	    try
	    {
	      pstmt = (PreparedStatement)srcConn.prepareStatement(sql);
	      ResultSet rs = pstmt.executeQuery();
	      while (rs.next()) {
	        issueId = rs.getString(1);
	      }
	      if ((rs != null) && (!rs.isClosed())) {
	        rs.close();
	      }
	    }
	    catch (SQLException e)
	    {
	    	e.printStackTrace();
	    }
	    return issueId;
	  }
	
	public SrcFiveDataBean getRecordByIssueCode(String issueCode)
	  {
	    Connection srcConn = ConnectSrcDb.getSrcConnection();
	    PreparedStatement pstmt = null;
	    SrcFiveDataBean srcDataBean = null;
	    String sql = "SELECT issue_number,no1,no2,no3,no4,no5 FROM " + App.srcNumberTbName + " WHERE ISSUE_NUMBER = '" + issueCode + "'";
	    try
	    {
	      pstmt = (PreparedStatement)srcConn.prepareStatement(sql);
	      ResultSet rs = pstmt.executeQuery();
	      while (rs.next())
	      {
	        srcDataBean = new SrcFiveDataBean();
	        srcDataBean.setIssueId(rs.getString(1));
	        srcDataBean.setNo1(rs.getInt(2));
	        srcDataBean.setNo2(rs.getInt(3));
	        srcDataBean.setNo3(rs.getInt(4));
	        srcDataBean.setNo4(rs.getInt(5));
	        srcDataBean.setNo5(rs.getInt(6));
	      }
	      if ((rs != null) && (!rs.isClosed())) {
	        rs.close();
	      }
	    }
	    catch (SQLException e)
	    {
	    	e.printStackTrace();
	    }
	    return srcDataBean;
	  }
	
	//获取当前期号的上一期开奖号码
	public SrcFiveDataBean getLastRecordByIssueCode(String issueCode)
	  {
	    Connection srcConn = ConnectSrcDb.getSrcConnection();
	    PreparedStatement pstmt = null;
	    SrcFiveDataBean srcDataBean = null;
	    String sql = "SELECT issue_number,no1,no2,no3,no4,no5 FROM " + App.srcNumberTbName + " "
	    		+ "WHERE ISSUE_NUMBER < '" + issueCode + "' order by desc limit 1";
	    try
	    {
	      pstmt = (PreparedStatement)srcConn.prepareStatement(sql);
	      ResultSet rs = pstmt.executeQuery();
	      while (rs.next())
	      {
	        srcDataBean = new SrcFiveDataBean();
	        srcDataBean.setIssueId(rs.getString(1));
	        srcDataBean.setNo1(rs.getInt(2));
	        srcDataBean.setNo2(rs.getInt(3));
	        srcDataBean.setNo3(rs.getInt(4));
	        srcDataBean.setNo4(rs.getInt(5));
	        srcDataBean.setNo5(rs.getInt(6));
	      }
	      if ((rs != null) && (!rs.isClosed())) {
	        rs.close();
	      }
	    }
	    catch (SQLException e)
	    {
	    	e.printStackTrace();
	    }
	    return srcDataBean;
	  }
	
	public boolean hasRecordByIssueNumber(String issueNumber, String tbName)
	  {
	    Connection srcConn = ConnectLTDb.getConnection();
	    boolean flag = false;
	    int count = 0;
	    PreparedStatement pstmt = null;
	    String sql = "SELECT count(0) FROM " + tbName + " where issue_number = '" + issueNumber + "'";
	    try
	    {
	      pstmt = (PreparedStatement)srcConn.prepareStatement(sql);
	      ResultSet rs = pstmt.executeQuery();
	      while (rs.next()) {
	        count = rs.getInt(1);
	      }
	      if (count > 0) {
	        flag = true;
	      }
	      if ((rs != null) && (!rs.isClosed())) {
	        rs.close();
	      }
	    }
	    catch (Exception e)
	    {
	      e.printStackTrace();
	    }
	    return flag;
	  }
}
