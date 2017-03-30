package com.byl.forcast;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class InitParams 
{
	public static void getMethodName()
	{
		Connection conn = ConnectLTDb.getConnection();//获取LotterytTools数据库的连接
		
		String basePtypeId = "";
		
		//根据预测类型id获取基本预测类型id
		String sql = "select BASE_PREDICTION_TYPE_ID,LOTTERYPLAY_ID,PREDICTION_TABLE,SANMA_TABLE_NAME,SIMA_TABLE_NAME,LIUMA_TABLE_NAME,LIANGMA_TABLE_NAME"
				+ " from "+App.ltptypetbName+" where id='"+App.ptypeid+"'";
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try
		{
			pstmt = (PreparedStatement)conn.prepareStatement(sql);
			
			 rs = pstmt.executeQuery();
		      while (rs.next())
		      {
		        if (rs.isFirst()) 
		        {
		        	basePtypeId= rs.getString(1);
		        	App.lpId = rs.getString(2);
		        	App.predictionTbName = rs.getString(3);
		        	App.sanmatbName = rs.getString(4);
		        	App.simatbName = rs.getString(5);
		        	App.liumatbName = rs.getString(6);
		        	App.liangmatbName = rs.getString(7);
		        	
		        }
		      }
		      
		    //根据基本预测类型id获取方法名
		    if(null != basePtypeId && !"".equals(basePtypeId))
		    {
		    	  String sql2 = "select METHOD_NAME,N_PLAN,ORIGIN_DATA_SIZE,ORGINDATA_RULE_ID from "+App.baseptypetbName+" where id = '"+basePtypeId+"'";
				    
		    	  pstmt = (PreparedStatement)conn.prepareStatement(sql2);
				
		    	  rs = pstmt.executeQuery();
			      while (rs.next())
			      {
			        if (rs.isFirst()) 
			        {
			        	App.methodName= rs.getString(1);
			        	App.nPlan = rs.getString(2);
			        	App.originDataCount = rs.getInt(3);
			        	App.originId = rs.getString(4);
			        }
			      }
		    }
		    
		    if(null != App.originId && !"".equals(App.originId))
		    {//获取源码规则的相关属性
		    	
		    	String sql3 = "select TYPE,LOCATION_OR_CONTAIN,CI_LOCATION_NUMBER,CI_RULE_FILED,LI_LOCATION_NUMBER,LI_RULE_FILED from "+App.originruleTbName+" where id = '"+basePtypeId+"'";
			    
		    	  pstmt = (PreparedStatement)conn.prepareStatement(sql3);
				
		    	  rs = pstmt.executeQuery();
			      while (rs.next())
			      {
			        if (rs.isFirst()) 
			        {
			        	App.type= rs.getString(1);
			        	App.locationOrContain = rs.getString(2);
			        	App.CI_LOCATION_NUMBER = rs.getString(3);
			        	App.CI_RULE_FILED = rs.getString(4);
			        	App.LI_LOCATION_NUMBER = rs.getString(5);
			        	App.LI_RULE_FILED = rs.getString(6);
			        }
			      }
		    	
		    }
		  
		} 
		catch (SQLException e) 
		{
			e.printStackTrace();
		}
		finally
		{
			ConnectLTDb.dbClose(conn, pstmt, rs);
		}
		
		
		
		
	     
	      
	}
}
