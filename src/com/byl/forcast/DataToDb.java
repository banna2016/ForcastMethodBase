package com.byl.forcast;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.byl.forcast.danma.DanmaYuce;
import com.byl.forcast.qianSLFushi.FushiYuce;
import com.byl.forcast.renSLGroup.RenSanGroup;

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
	    String sql = "SELECT issue_number,no1,no2,no3,no4,no5,SMALLER_NUM,SMALLEST_NUM,"
	    		+ "MIDDLE_NUM,BIGGER_NUM,BIGGEST_NUM,NOARR FROM " + App.srcNumberTbName + " WHERE ISSUE_NUMBER = '" + issueCode + "'";
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
	        srcDataBean.setSMALLER_NUM(rs.getInt(7));
	        srcDataBean.setSMALLEST_NUM(rs.getInt(8));
	        srcDataBean.setMIDDLE_NUM(rs.getInt(9));
	        srcDataBean.setBIGGER_NUM(rs.getInt(10));
	        srcDataBean.setBIGGEST_NUM(rs.getInt(11));
	        srcDataBean.setNoArr(rs.getString(12));
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
	    		+ "WHERE ISSUE_NUMBER < '" + issueCode + "' order by ISSUE_NUMBER desc limit 1";
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
	    String sql = "SELECT count(0) FROM " + tbName + " where issue_number = '" + issueNumber + "' "
	    		+ "and PREDICTION_TYPE='"+App.ptypeid+"' and EXPERT_ID='"+App.beid+"'";
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
	
	public boolean hasFushiRecordByIssueNumber(String issueNumber, String tbName)
	  {
	    Connection srcConn = ConnectLTDb.getConnection();
	    boolean flag = false;
	    int count = 0;
	    ResultSet rs = null;
	    PreparedStatement pstmt = null;
	    String sql = "SELECT FUSHI,YUCE_ISSUE_START,YUCE_ISSUE_STOP,CYCLE,ID "
	    		+ " FROM " + tbName + " where " + issueNumber + ">=YUCE_ISSUE_START and  " + issueNumber + "<=YUCE_ISSUE_STOP"
	    		+ "  and  PREDICTION_TYPE='"+App.ptypeid+"' and EXPERT_ID='"+App.beid+"' order by YUCE_ISSUE_START desc limit 1";
	    try
	    {
	      pstmt = (PreparedStatement)srcConn.prepareStatement(sql);
	      rs = pstmt.executeQuery();
	      if (rs.next()) 
	      {
	        count = 1;
	        flag = true;
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
	    		+ "SHAMA_ONE,SHAMA_TWO FROM " + tbName + " where issue_number = '" + issueNumber + "'  and "
				+ " PREDICTION_TYPE='"+App.ptypeid+"' and EXPERT_ID='"+App.beid+"'  ";
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
	 * 获取任胆任杀
	* @Title: getRenDanYuceRecordByIssueNumber 
	* @Description: TODO(这里用一句话描述这个方法的作用) 
	* @param @param issueNumber
	* @param @param tbName
	* @param @return    设定文件 
	* @author banna
	* @date 2017年4月1日 下午3:55:12 
	* @return DanmaYuce    返回类型 
	* @throws
	 */
	public DanmaYuce getRenDanYuceRecordByIssueNumber(String issueNumber, String tbName)
	  {
	    Connection srcConn = ConnectLTDb.getConnection();
	    DanmaYuce danmaYuce = new DanmaYuce();
	    PreparedStatement pstmt = null;
	    ResultSet rs = null;
	    String sql = "SELECT PREDICTION_TYPE,EXPERT_ID,ISSUE_NUMBER,DANMA_ONE,"
	    		+ "SHAMA_ONE FROM " + tbName + " where issue_number = '" + issueNumber + "'   and "
				+ " PREDICTION_TYPE='"+App.ptypeid+"' and EXPERT_ID='"+App.beid+"' ";
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
	        	 danmaYuce.setSHAMA_ONE(rs.getString(5));
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
	
	public FushiYuce getQiansanLiuFushiYuceRecordByIssueNumber(String issueNumber, String tbName)
	  {
	    Connection srcConn = ConnectLTDb.getConnection();
	    FushiYuce fushiYuce = new FushiYuce();
	    PreparedStatement pstmt = null;
	    ResultSet rs = null;
	    String sql = "SELECT FUSHI,YUCE_ISSUE_START,YUCE_ISSUE_STOP,CYCLE,ID "
	    		+ " FROM " + tbName + " where " + issueNumber + ">=YUCE_ISSUE_START and  " + issueNumber + "<=YUCE_ISSUE_STOP"
	    		+ "  and  PREDICTION_TYPE='"+App.ptypeid+"' and EXPERT_ID='"+App.beid+"' order by YUCE_ISSUE_START desc limit 1 ";
	    try
	    {
	      pstmt = (PreparedStatement)srcConn.prepareStatement(sql);
	      rs = pstmt.executeQuery(); 
	      while (rs.next()) 
	      {
	         if(rs.isFirst())
	         {
	        	 fushiYuce.setFUSHI(rs.getString(1));
	        	 fushiYuce.setYUCE_ISSUE_START(rs.getString(2));
	        	 fushiYuce.setYUCE_ISSUE_STOP(rs.getString(3));
	        	 fushiYuce.setCYCLE(rs.getString(4));
	        	 fushiYuce.setID(rs.getInt(5));
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
	    return fushiYuce;
	  }
	//获取任三精选6组的预测结果
	public RenSanGroup getRensanSixGroupYuceRecordByIssueNumber(String issueNumber, String tbName)
	  {
	    Connection srcConn = ConnectLTDb.getConnection();
	    RenSanGroup renSanGroup = new RenSanGroup();
	    PreparedStatement pstmt = null;
	    ResultSet rs = null;
	    String sql = "SELECT GROUP1,GROUP2,GROUP3,GROUP4,GROUP5,GROUP6 FROM " + tbName + " "
	    		+ "WHERE ISSUE_NUMBER = '" + issueNumber + "' and  "
	    		+ "  PREDICTION_TYPE='"+App.ptypeid+"' and EXPERT_ID='"+App.beid+"' order by ISSUE_NUMBER desc limit 1 ";
	    try
	    {
	      pstmt = (PreparedStatement)srcConn.prepareStatement(sql);
	      rs = pstmt.executeQuery(); 
	      while (rs.next()) 
	      {
	         if(rs.isFirst())
	         {
	        	 renSanGroup.setGROUP1(rs.getString(1));
	        	 renSanGroup.setGROUP2(rs.getString(2));
	        	 renSanGroup.setGROUP3(rs.getString(3));
	        	 renSanGroup.setGROUP4(rs.getString(4));
	        	 renSanGroup.setGROUP5(rs.getString(5));
	        	 renSanGroup.setGROUP6(rs.getString(6));
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
	    return renSanGroup;
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
				+ " PREDICTION_TYPE='"+App.ptypeid+"' ");
	    
	    if(null != limitnumber && limitnumber >0)
	    {
	    	sql.append(" order by ISSUE_NUMBER desc  limit "+limitnumber +" ");
	    }
	    sql.append(" ) a");
	    
	    if(!isAll)
	    {
	    	sql.append(" where "+field+"=1");
	    	
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
