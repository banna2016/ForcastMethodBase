package com.byl.forcast;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DataToDb 
{
	  
	  public Map<String, String> findMaxIssueIdFromDescDb()
	  {
		  Map<String, String> rtnMap = new HashMap();
		    String sql = null;
		    sql = "SELECT ISSUE_NUMBER,NO1,NO2,NO3 FROM "+App.descNumberTbName+" ORDER BY ISSUE_NUMBER DESC LIMIT 2";
		    Connection conn = ConnectDesDb.getConnection();
		    PreparedStatement pstmt = null;
		    ResultSet rs = null;
		    String no1 = null;
		    try
		    {
		      pstmt = (PreparedStatement)conn.prepareStatement(sql);
		      rs = pstmt.executeQuery();
		      while (rs.next())
		      {
		        if (rs.isFirst()) {
		          rtnMap.put("maxIssuenum", rs.getString(1));
		        }
		       /* no1 = rs.getString(2);
		        if (!StringUtils.isNullOrEmpty(no1))
		        {
		          rtnMap.put("lotteryIssueNumber", rs.getString(1));
		          break;
		        }*/
		      }
		    }
		    catch (SQLException e)
		    {
		      e.printStackTrace();
		    }
		    finally {}
		    try
		    {
		    	ConnectDesDb.dbClose(conn, pstmt, rs);
		    }
		    catch (SQLException localSQLException1) {}finally
		    {
		      return rtnMap;
		    }
	  }
	  
	  public boolean judgeIssueNumber(String issueNumber)
	  {
	    Pattern pattern = Pattern.compile("[0-9]*");
	    Matcher isNum = pattern.matcher(issueNumber);
	    if (!isNum.matches()) {
	      return false;
	    }
	    return true;
	  }
	  
	  private boolean match(String regex, String str)
	  {
	    Pattern pattern = Pattern.compile(regex);
	    Matcher matcher = pattern.matcher(str);
	    return matcher.matches();
	  }
	  
	  
	 
	  
	  public  SrcFiveDataBean caluExtentInfo(SrcFiveDataBean srcDataBean)
	  {
		  int oneInt = srcDataBean.getNo1();
		    int twoInt = srcDataBean.getNo2();
		    int threeInt = srcDataBean.getNo3();
		    int fourInt = srcDataBean.getNo4();
		    int fiveInt = srcDataBean.getNo5();
		    int threeSpan = 0;int threeSum = 0;int oddNumber = 0;int bigCount = 0;
		    threeSum = oneInt + twoInt + threeInt;
		    int fiveSum = oneInt + twoInt + threeInt + fourInt + fiveInt;
		    int[] three = { oneInt, twoInt, threeInt };
		    int[] five = { oneInt, twoInt, threeInt, fourInt, fiveInt };
		    for (int i = 0; i < five.length; i++)
		    {
		      if (five[i] % 2 != 0) {
		        oddNumber++;
		      }
		      if (five[i] > 6) {
		        bigCount++;
		      }
		    }
		    Arrays.sort(three);
		    threeSpan = three[2] - three[0];
		    Arrays.sort(five);
		    int fiveSpan = five[4] - five[0];
		    srcDataBean.setOddNum(oddNumber);
		    srcDataBean.setBigCount(bigCount);
		    srcDataBean.setThreeSpan(threeSpan);
		    srcDataBean.setFiveSpan(fiveSpan);
		    srcDataBean.setThreeSum(threeSum);
		    srcDataBean.setFiveSum(fiveSum);
		    return srcDataBean;
	  }
	  
	 
	  
	  public  void insertData(SrcFiveDataBean srcDataBean, Connection conn)
	    throws SQLException
	  {
	    String sql = "INSERT INTO "+App.descNumberTbName+" (issue_number,no1,no2,no3,no4,no5,three_sum,three_span,five_sum,five_span,big_count,odd_count,create_time,origin) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	    PreparedStatement pstmt = null;
	    try
	    {
	    	pstmt = (PreparedStatement)conn.prepareStatement(sql);
	        pstmt.setString(1, srcDataBean.getIssueId());
	        pstmt.setInt(2, srcDataBean.getNo1());
	        pstmt.setInt(3, srcDataBean.getNo2());
	        pstmt.setInt(4, srcDataBean.getNo3());
	        pstmt.setInt(5, srcDataBean.getNo4());
	        pstmt.setInt(6, srcDataBean.getNo5());
	        pstmt.setInt(7, srcDataBean.getThreeSum());
	        pstmt.setInt(8, srcDataBean.getThreeSpan());
	        pstmt.setInt(9, srcDataBean.getFiveSum());
	        pstmt.setInt(10, srcDataBean.getFiveSpan());
	        pstmt.setInt(11, srcDataBean.getBigCount());
	        pstmt.setInt(12, srcDataBean.getOddNum());
	        pstmt.setTimestamp(13, new Timestamp(new Date().getTime()));
	        pstmt.setInt(14, 1);
	        pstmt.executeUpdate();
	    }
	    catch (SQLException e)
	    {
	    	e.printStackTrace();
	    }
	    finally
	    {
	      if ((!pstmt.isClosed()) && (pstmt != null)) {
	        pstmt.close();
	      }
	    }
	  }
	  
	  private boolean haveDataInIssueId(String issueId, Connection conn)
	    throws SQLException
	  {
	    boolean flag = false;
	    int count = 0;
	    String sql = "SELECT COUNT(*) FROM " + App.descNumberTbName + " WHERE ISSUE_NUMBER = '" + issueId + "'";
	    PreparedStatement pstmt = null;
	    ResultSet rs = null;
	    try
	    {
	      pstmt = (PreparedStatement)conn.prepareStatement(sql);
	      rs = pstmt.executeQuery();
	      while (rs.next()) {
	        count = rs.getInt(1);
	      }
	      if (count > 0) {
	        flag = true;
	      }
	    }
	    catch (SQLException e)
	    {
	    	System.out.println("haveDataInIssueId方法异常" + e.getCause());
	    }
	    finally
	    {
	      if ((rs != null) && (!rs.isClosed())) {
	        rs.close();
	      }
	      if ((pstmt != null) && (!pstmt.isClosed())) {
	        pstmt.close();
	      }
	    }
	    return flag;
	  }
	  
	  private boolean haveMissDataInIssueId(String issueId, Connection conn)
	    throws SQLException
	  {
	    boolean flag = false;
	    int count = 0;
	    String sql = "SELECT COUNT(*) FROM "+App.descNumberTbName+" WHERE ISSUE_NUMBER = '" + issueId + "'";
	    PreparedStatement pstmt = null;
	    ResultSet rs = null;
	    try
	    {
	      pstmt = (PreparedStatement)conn.prepareStatement(sql);
	      rs = pstmt.executeQuery();
	      while (rs.next()) {
	        count = rs.getInt(1);
	      }
	      if (count > 0) {
	        flag = true;
	      }
	    }
	    catch (SQLException e)
	    {
	    	System.out.println("查询分析表是否存在数据异?" + e.getCause());
	    }
	    finally
	    {
	      if ((rs != null) && (!rs.isClosed())) {
	        rs.close();
	      }
	      if ((pstmt != null) && (!pstmt.isClosed())) {
	        pstmt.close();
	      }
	    }
	    return flag;
	  }
	  
	  private void batchUpdateMiss(SrcFiveDataBean srcDataBean, Connection conn)
	    throws SQLException
	  {
	    PreparedStatement stmt = null;
	    try
	    {
	      DatabaseMetaData dbmd = conn.getMetaData();
	      boolean a = dbmd.supportsBatchUpdates();
	      if (a)
	      {
	        boolean booleanautoCommit = conn.getAutoCommit();
	        
	        conn.setAutoCommit(false);
	        stmt = (PreparedStatement)conn.prepareStatement("");
	        
	       
	        
	        stmt.addBatch("UPDATE "+App.descNumberTbName+" SET MAX_MISS = CURRENT_MISS WHERE CURRENT_MISS > MAX_MISS AND CURRENT_MISS <> 0;");
	        stmt.executeBatch();
	        
	        conn.commit();
	        conn.setAutoCommit(booleanautoCommit);
	      }
	    }
	    catch (SQLException sqlEx)
	    {
	    	sqlEx.printStackTrace();
	    }
	    finally
	    {
	      if ((stmt != null) && (!stmt.isClosed())) {
	        stmt.close();
	      }
	    }
	  }
}
