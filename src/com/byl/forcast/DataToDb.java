package com.byl.forcast;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.byl.forcast.danma.DanmaYuce;

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
	
	/**
	 * 获取当前期号对应的预测结果
	* @Title: getYuceRecordByIssueNumber 
	* @Description: TODO(这里用一句话描述这个方法的作用) 
	* @param @param issueNumber
	* @param @param tbName
	* @param @return    设定文件 
	* @author banna
	* @date 2017年3月31日 下午5:06:34 
	* @return DanmaYuce    返回类型 
	* @throws
	 */
	public DanmaYuce getYuceRecordByIssueNumber(String issueNumber, String tbName)
	  {
	    Connection srcConn = ConnectLTDb.getConnection();
	    DanmaYuce danmaYuce = new DanmaYuce();
	    PreparedStatement pstmt = null;
	    ResultSet rs = null;
	    String sql = "SELECT PREDICTION_TYPE,EXPERT_ID,ISSUE_NUMBER,DANMA_ONE,DANMA_TWO,"
	    		+ "SHAMA_ONE,SHAMA_TWO FROM " + tbName + " where issue_number = '" + issueNumber + "'";
	    try
	    {
	      pstmt = (PreparedStatement)srcConn.prepareStatement(sql);
	      rs = pstmt.executeQuery();
	      while (rs.next()) 
	      {
	         if(rs.isFirst())
	         {
	        	 danmaYuce.setPREDICTION_TYPE(rs.getString(1));
	        	 danmaYuce.setEXPERT_ID(rs.getString(2));
	        	 danmaYuce.setISSUE_NUMBER(rs.getString(3));
	        	 danmaYuce.setDANMA_ONE(rs.getString(4));
	        	 danmaYuce.setDANMA_TWO(rs.getString(5));
	        	 danmaYuce.setSHAMA_ONE(rs.getString(6));
	        	 danmaYuce.setSHAMA_TWO(rs.getString(7));
	         }
	      }
	    }
	    catch (Exception e)
	    {
	      e.printStackTrace();
	    }
	    finally{
	    	ConnectLTDb.dbClose(srcConn, pstmt, rs);
	    }
	    return danmaYuce;
	  }
	
	/**
	 * 获取当前专家预测该类型的指定期数内的准确数据量
	* @Title: getCountOfexpertprediction 
	* @Description: TODO(这里用一句话描述这个方法的作用) 
	* @param @param field
	* @param @param isAll
	* @param @return    设定文件 
	* @author banna
	* @date 2017年4月1日 上午11:26:47 
	* @return Integer    返回类型 
	* @throws
	 */
	public Integer getCountOfexpertprediction(String field,boolean isAll,Integer limitnumber)
	  {
	    Connection srcConn = ConnectLTDb.getConnection();
	    int count = 0;
	    PreparedStatement pstmt = null;
	    ResultSet rs = null;
	    StringBuffer sql = new StringBuffer("SELECT count(0) FROM (SELECT * FROM " + App.predictionTbName + " "
	    		+ "where  EXPERT_ID='"+App.beid+"' and "
				+ " PREDICTION_TYPE='"+App.ptypeid+"') a ");
	    if(!isAll)
	    {
	    	sql.append(" where "+field+"=1");
	    	
	    }
	    if(null != limitnumber && limitnumber >0)
	    {
	    	sql.append("  limit "+limitnumber);
	    }
	    
	    try
	    {
	      pstmt = (PreparedStatement)srcConn.prepareStatement(sql.toString());
	      rs = pstmt.executeQuery();
	      while (rs.next())
	      {
	        if(rs.isFirst())
	        {
	        	count=rs.getInt(1);
	        }
	      }
	    }
	    catch (Exception e)
	    {
	      e.printStackTrace();
	    }
	    finally
	    {
	    	ConnectLTDb.dbClose(srcConn, pstmt, rs);
	    }
	    return count;
	  }
}
