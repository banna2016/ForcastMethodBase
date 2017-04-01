package com.byl.forcast;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.byl.forcast.danma.ExecDanma;
import com.mysql.jdbc.PreparedStatement;

/**
 * 预测方法库
* @Description: TODO(这里用一句话描述这个方法的作用) 
* @author banna
* @date 2017年3月29日 上午9:58:34
 */
public class PredictionRepository 
{
	/**
	 * 前三胆码、杀码
	* @Title: execFirstThreeDanma 
	* @Description: TODO(这里用一句话描述这个方法的作用) 
	* @param     设定文件 
	* @author banna
	* @date 2017年3月29日 上午9:59:29 
	* @return void    返回类型 
	* @throws
	 */
	public void execRenDanma()
	{
		//获取源码
		List<SrcFiveDataBean> yuanBeans = this.getOriginData(null);
		
		//使用源码进行预测
		
		
	}
	/**
	 * 任选胆码、杀码
	* @Title: execDanma 
	* @Description: TODO(这里用一句话描述这个方法的作用) 
	* @param     设定文件 
	* @author banna
	* @date 2017年3月29日 上午9:59:55 
	* @return void    返回类型 
	* @throws
	 */
	public void execDanma()
	{
		ExecDanma execDanma = new ExecDanma();
		DataToDb dataToDb = new DataToDb();
		//判断当期期号是否已经预测，预测则要判断中奖率
		if(dataToDb.hasRecordByIssueNumber(App.maxIssueId,App.predictionTbName))
		{//判断中奖率
			execDanma.updateDanAndShaStatus();
			System.out.println("判断中奖率");
			//判断完这期中奖率再预测下一期
			List<SrcFiveDataBean> yuanBeans = this.getOriginData(null);
			execDanma.execDanma(yuanBeans);
		}
		else
		{
			List<SrcFiveDataBean> yuanBeans = this.getOriginData(null);
			execDanma.execDanma(yuanBeans);
			System.out.println("开始预测");
		}
		
		
	}
	
	/**
	 * 前三六码复式
	* @Title: firstThreeDuplex 
	* @Description: TODO(这里用一句话描述这个方法的作用) 
	* @param     设定文件 
	* @author banna
	* @date 2017年3月29日 上午10:01:11 
	* @return void    返回类型 
	* @throws
	 */
	public void firstThreeDuplex()
	{
		
	}
	/**
	 * 任三精选6组
	* @Title: groupSix 
	* @Description: TODO(这里用一句话描述这个方法的作用) 
	* @param     设定文件 
	* @author banna
	* @date 2017年3月29日 上午10:03:33 
	* @return void    返回类型 
	* @throws
	 */
	public void groupSixOfRensan()
	{
		
	}
	
	/**
	 * 乐选4四期计划
	* @Title: LeFourOfRensi 
	* @Description: TODO(这里用一句话描述这个方法的作用) 
	* @param     设定文件 
	* @author banna
	* @date 2017年3月29日 上午10:04:17 
	* @return void    返回类型 
	* @throws
	 */
	public void LeFourOfRensi()
	{
		
	}
	
	/**
	 * 两码三期计划
	* @Title: LiangThreeOfRener 
	* @Description: TODO(这里用一句话描述这个方法的作用) 
	* @param     设定文件 
	* @author banna
	* @date 2017年3月29日 上午10:04:58 
	* @return void    返回类型 
	* @throws
	 */
	public void LiangThreeOfRener()
	{
		
	}
	
	/**
	 * 获取源码数据
	* @Title: getOriginData 
	* @Description: TODO(这里用一句话描述这个方法的作用) 
	* @param @return    设定文件 
	* @author banna
	* @date 2017年3月29日 上午10:42:34 
	* @return List<SrcFiveDataBean>    返回类型 
	* @throws
	 */
	public List<SrcFiveDataBean> getOriginData(String lastTimeIssue)//lastTimeIssue：上次获取源码的最后一期的期号
	{
		List<SrcFiveDataBean> list = new ArrayList<SrcFiveDataBean>();
		
		//获取开奖号码
		DataToDb dataToDb = new DataToDb();
		SrcFiveDataBean srcFiveDataBean = dataToDb.getRecordByIssueCode(App.maxIssueId);
		if("0".equals(App.type))
		{//当期获取规则
			String lArr[] = App.CI_LOCATION_NUMBER.split(",");//获取要使用的数字位置
			if("0".equals(App.locationOrContain))
			{//定位
				list = this.getLocationList(lArr,srcFiveDataBean,lastTimeIssue);
			}
			else
				if("1".equals(App.locationOrContain))
				{//包含(将开奖号码按从小到大排序，转换AJQ)，和开奖号码中的noArr字段比较包含关系,※NOARR暂时还没用维护的字段
					list = this.getContainList(lArr,srcFiveDataBean,lastTimeIssue);
				}
			
		}
		else
			if("1".equals(App.type))
			{//关联期获取规则
				list = this.getContactIssueList( srcFiveDataBean,lastTimeIssue);
			}
			else
				if("2".equals(App.type))
				{//周期获取规则
					list = this.getCycleIssueList(srcFiveDataBean,lastTimeIssue);
				}
		
		
		return list;
	}
	
	/**
	 * 按照周期获取规则获取源码
	* @Title: getCycleIssueList 
	* @Description: TODO(这里用一句话描述这个方法的作用) 
	* @param @param srcFiveDataBean
	* @param @return    设定文件 
	* @author banna
	* @date 2017年3月30日 上午10:46:41 
	* @return List<SrcFiveDataBean>    返回类型 
	* @throws
	 */
	public List<SrcFiveDataBean> getCycleIssueList(SrcFiveDataBean srcFiveDataBean,String lastTimeIssue)
	{
		List<SrcFiveDataBean> beans = new ArrayList<SrcFiveDataBean>();
		String issueId = srcFiveDataBean.getIssueId();
		String lastissuenum = issueId.substring(issueId.length()-2);//获取后两位
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Connection conn = ConnectSrcDb.getSrcConnection();
		
		StringBuffer sql = new StringBuffer();
		
		if(App.cycle == 1)
		{//暂时只写周期1的sql，即获取每天同期号的数据
			sql.append("SELECT issue_number,no1,no2,no3,no4,no5 FROM "+App.srcNumberTbName+" "
					+ "WHERE ISSUE_NUMBER LIKE '%"+lastissuenum+"'");
			
			if(null != lastTimeIssue && !"".equals(lastTimeIssue))
			{//若为再次获取源码，则要连接上次获取最小期号的条件
				sql.append(" and issue_number < "+lastTimeIssue);
			}
			
			sql.append(" and issue_number!="+srcFiveDataBean.getIssueId()+"  ORDER BY ISSUE_NUMBER DESC LIMIT "+App.originDataCount);
		}
		
		try
		{
			pstmt = (PreparedStatement)conn.prepareStatement(sql.toString());
			rs = pstmt.executeQuery();
		      while (rs.next())
		      {
		    	SrcFiveDataBean srcDataBean = new SrcFiveDataBean();
		        srcDataBean.setIssueId(rs.getString(1));
		        srcDataBean.setNo1(rs.getInt(2));
		        srcDataBean.setNo2(rs.getInt(3));
		        srcDataBean.setNo3(rs.getInt(4));
		        srcDataBean.setNo4(rs.getInt(5));
		        srcDataBean.setNo5(rs.getInt(6));
		        
		        beans.add(srcDataBean);
		      }
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			ConnectSrcDb.dbClose(conn, pstmt, rs);
		}
		
		return beans;
	}
	
	/**
	 * 获取关联期源码
	* @Title: getContactIssueList 
	* @Description: TODO(这里用一句话描述这个方法的作用) 
	* @param @param lArr
	* @param @param srcFiveDataBean
	* @param @return    设定文件 
	* @author banna
	* @date 2017年3月29日 下午4:34:46 
	* @return List<SrcFiveDataBean>    返回类型 
	* @throws
	 */
	public List<SrcFiveDataBean> getContactIssueList(SrcFiveDataBean srcFiveDataBean,String lastTimeIssue)
	{
		List<SrcFiveDataBean> beans = new ArrayList<SrcFiveDataBean>();
		List<SrcFiveDataBean> yuanBeans = new ArrayList<SrcFiveDataBean>();
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Connection conn = ConnectSrcDb.getSrcConnection();
		
		//连接当期条件
		StringBuffer crConditions = new StringBuffer();
		
		//连接上期条件
		StringBuffer lcConditions = new StringBuffer();
		DataToDb dataToDb = new DataToDb();
		SrcFiveDataBean lastBean = dataToDb.getLastRecordByIssueCode(srcFiveDataBean.getIssueId());
		
		if(null != App.CI_LOCATION_NUMBER && !"".equals(App.CI_LOCATION_NUMBER))
		{//定位条件不为空
			String lcArr[] = App.CI_LOCATION_NUMBER.split(",");
			for(int i=0;i<lcArr.length;i++)
			{
				if(i == 0)
				{
					crConditions.append(" NO"+lcArr[i]+"="+srcFiveDataBean.numberMap.get("no"+lcArr[i]));
				}
				else
				{
					crConditions.append(" and NO"+lcArr[i]+"="+srcFiveDataBean.numberMap.get("no"+lcArr[i]));
				}
			}
		}
		
		if(null != App.CI_RULE_FILED && !"".equals(App.CI_RULE_FILED))
		{//当期条件字段不为空
			String lcArr[] = App.CI_RULE_FILED.split(",");
			for(int i=0;i<lcArr.length;i++)
			{
				if(i == 0 && crConditions.length() == 0)//若定位条件为空，则当期条件字段是第一次连接的当期源码筛选条件
				{
					crConditions.append(" "+lcArr[i]+"="+srcFiveDataBean.numberMap.get(lcArr[i]));
				}
				else
				{
					crConditions.append(" and "+lcArr[i]+"="+srcFiveDataBean.numberMap.get(lcArr[i]));
				}
			}
		}
		
		//处理上期条件
		if(null != App.LI_LOCATION_NUMBER && !"".equals(App.LI_LOCATION_NUMBER))
		{//定位条件不为空
			String llnArr[] = App.LI_LOCATION_NUMBER.split(",");
			for(int i=0;i<llnArr.length;i++)
			{
				if(i == 0)
				{
					lcConditions.append(" NO"+llnArr[i]+"="+lastBean.numberMap.get("no"+llnArr[i]));
				}
				else
				{
					lcConditions.append(" and NO"+llnArr[i]+"="+lastBean.numberMap.get("no"+llnArr[i]));
				}
			}
		}
		
		if(null != App.LI_RULE_FILED && !"".equals(App.LI_RULE_FILED))
		{//上期条件字段不为空
			String llnArr[] = App.LI_RULE_FILED.split(",");
			for(int i=0;i<llnArr.length;i++)
			{
				if(i == 0 && lcConditions.length() == 0)//若定位条件为空，则上期条件字段是第一次连接的上期源码筛选条件
				{
					lcConditions.append(" "+llnArr[i]+"="+lastBean.numberMap.get(llnArr[i]));
				}
				else
				{
					lcConditions.append(" and "+llnArr[i]+"="+lastBean.numberMap.get(llnArr[i]));
				}
			}
		}
		
		
		try
		{
		      //筛选源码
		      yuanBeans = this.getLastIssue(beans, lcConditions, crConditions, lastTimeIssue,yuanBeans,
		    		   pstmt, rs, conn,srcFiveDataBean);
					
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			ConnectSrcDb.dbClose(conn, pstmt, rs);
		}
		
		return yuanBeans;
	}
	
	//获取符合条件的源码
	public List<SrcFiveDataBean> getLastIssue(List<SrcFiveDataBean> beans,StringBuffer lcConditions,
			StringBuffer crConditions,String lastIssue,List<SrcFiveDataBean> yuanBeans,
			 PreparedStatement pstmt,ResultSet rs,Connection conn,SrcFiveDataBean srcFiveDataBean) throws Exception
	{
		  beans = this.getCurrentIssue(crConditions, lastIssue,srcFiveDataBean);
		
		  if(null != beans && beans.size()>0)
		  {
			  StringBuffer sql2 = new StringBuffer();
			  for (int m=0;m<beans.size();m++) 
			  {
				  SrcFiveDataBean crbean = beans.get(m);
				  sql2.setLength(0);
				  sql2.append("SELECT issue_number,no1,no2,no3,no4,no5 FROM " + App.srcNumberTbName + " "
				    		+ "WHERE "+lcConditions+" and  ISSUE_NUMBER < " + crbean.getIssueId() + " order by desc limit 1");
				  pstmt = (PreparedStatement)conn.prepareStatement(sql2.toString());
				 rs = pstmt.executeQuery();
				 if(rs.next())
				 {
					 yuanBeans.add(crbean);
				 }
				 if(m == beans.size()-1)
				 {
					 lastIssue = beans.get(m).getIssueId();//更新最近一期的期号
				 }
					
			  }
			  
			 if(yuanBeans.size()<App.originDataCount)
			 {//若源码数量没有达到源码数据量要求，则要再获取当期数据再次筛选源码
				 this.getLastIssue(beans, lcConditions, crConditions, lastIssue, yuanBeans, pstmt, rs, conn,srcFiveDataBean);
			 }
		  }
		  
			
		return yuanBeans;
	}
	
	
	//关联期源码获取--获取当期符合条件数据
	public List<SrcFiveDataBean> getCurrentIssue(StringBuffer crConditions,String lastIssue,SrcFiveDataBean srcFiveDataBean)
	{
		List<SrcFiveDataBean> beans = new ArrayList<SrcFiveDataBean>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Connection conn = ConnectSrcDb.getSrcConnection();
		
		if(null != lastIssue && !"".equals(lastIssue))
		{//若上期获取期号不为空
			crConditions.append(" and issue_number<"+lastIssue);
		}
		
		StringBuffer sql = new StringBuffer("select issue_number,no1,no2,no3,no4,no5 from "+App.srcNumberTbName+" "
				+ "where "+crConditions);
		
		sql.append(" and issue_number!="+srcFiveDataBean.getIssueId()+" ORDER BY ISSUE_NUMBER DESC LIMIT 500");
		try
		{
			pstmt = (PreparedStatement)conn.prepareStatement(sql.toString());
			rs = pstmt.executeQuery();
		      while (rs.next())
		      {
		    	SrcFiveDataBean srcDataBean = new SrcFiveDataBean();
		        srcDataBean.setIssueId(rs.getString(1));
		        srcDataBean.setNo1(rs.getInt(2));
		        srcDataBean.setNo2(rs.getInt(3));
		        srcDataBean.setNo3(rs.getInt(4));
		        srcDataBean.setNo4(rs.getInt(5));
		        srcDataBean.setNo5(rs.getInt(6));
		        
		        beans.add(srcDataBean);
		      }
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			ConnectSrcDb.dbClose(conn, pstmt, rs);
		}
		
		
		return beans;
	}
	
	/**
	 * 获取当期包含关系源码
	* @Title: getContainList 
	* @Description: TODO(这里用一句话描述这个方法的作用) 
	* @param @param lArr
	* @param @param srcFiveDataBean
	* @param @return    设定文件 
	* @author banna
	* @date 2017年3月29日 下午3:04:56 
	* @return List<SrcFiveDataBean>    返回类型 
	* @throws
	 */
	public List<SrcFiveDataBean> getContainList(String lArr[] ,SrcFiveDataBean srcFiveDataBean,
			String lastTimeIssue)
	{
		List<SrcFiveDataBean> beans = new ArrayList<SrcFiveDataBean>();
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Connection conn = ConnectSrcDb.getSrcConnection();
		
		StringBuffer condistions = new StringBuffer();//where条件
		
		String location = "";
		List<Integer> numList = new ArrayList<Integer>();
		for (int i=0;i<lArr.length;i++) 
		{
			location = lArr[i];
			numList.add((Integer) srcFiveDataBean.numberMap.get("no"+location));
		}
		
		Collections.sort(numList);//排序
		
		for(int j=0;j<numList.size();j++)
		{
			if(j == 0)
			{
				condistions.append("%"+numList.get(0)+"%");
			}
			else
			{
				condistions.append(numList.get(0)+"%");
			}
		}
		
		//NOARR:需要添加到采集数据时计算的字段，将开奖号码按照小到大排序，然后转换位AJQ存储
		StringBuffer sql = new StringBuffer("select issue_number,no1,no2,no3,no4,no5 from "+App.srcNumberTbName+" "
				+ "where NOARR like '"+condistions +"'") ;
		if(null != lastTimeIssue && !"".equals(lastTimeIssue))
		{//若为再次获取源码，则要连接上次获取最小期号的条件
			sql.append(" and issue_number < "+lastTimeIssue);
		}
		sql.append(" and issue_number!="+srcFiveDataBean.getIssueId()+" order by issue_number desc limit "+App.originDataCount);
		try
		{
			pstmt = (PreparedStatement)conn.prepareStatement(sql.toString());
			rs = pstmt.executeQuery();
		      while (rs.next())
		      {
		    	SrcFiveDataBean srcDataBean = new SrcFiveDataBean();
		        srcDataBean.setIssueId(rs.getString(1));
		        srcDataBean.setNo1(rs.getInt(2));
		        srcDataBean.setNo2(rs.getInt(3));
		        srcDataBean.setNo3(rs.getInt(4));
		        srcDataBean.setNo4(rs.getInt(5));
		        srcDataBean.setNo5(rs.getInt(6));
		        
		        beans.add(srcDataBean);
		      }
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			ConnectSrcDb.dbClose(conn, pstmt, rs);
		}
		
		
		
		return beans;
	}
	
	/**
	 * 获取定位数据
	* @Title: getLocationList 
	* @Description: TODO(这里用一句话描述这个方法的作用) 
	* @param @return    设定文件 
	* @author banna
	* @date 2017年3月29日 下午2:50:35 
	* @return List<SrcFiveDataBean>    返回类型 
	* @throws
	 */
	public List<SrcFiveDataBean> getLocationList(String lArr[] ,SrcFiveDataBean srcFiveDataBean,
			String lastTimeIssue)
	{
		List<SrcFiveDataBean> beans = new ArrayList<SrcFiveDataBean>();
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Connection conn = ConnectSrcDb.getSrcConnection();
		
		StringBuffer condistions = new StringBuffer();//where条件
		
		String location = "";
		for (int i=0;i<lArr.length;i++) 
		{
			location = lArr[i];
			if(i > 1 )
			{
				condistions.append(" and  no"+location+" = "+srcFiveDataBean.numberMap.get("no"+location) );
			}
			else
			{
				condistions.append(" no"+location+" = "+srcFiveDataBean.numberMap.get("no"+location) );
			}
		}
		
		StringBuffer sql = new StringBuffer("select issue_number,no1,no2,no3,no4,no5 from "+App.srcNumberTbName+" "
				+ "where "+condistions +" ");
		if(null != lastTimeIssue && !"".equals(lastTimeIssue))
		{//若为再次获取源码，则要连接上次获取最小期号的条件
			sql.append(" and issue_number < "+lastTimeIssue);
		}
		
		sql.append(" and issue_number!="+srcFiveDataBean.getIssueId()+" ORDER BY ISSUE_NUMBER DESC LIMIT "+App.originDataCount);
		
		try
		{
			pstmt = (PreparedStatement)conn.prepareStatement(sql.toString());
			rs = pstmt.executeQuery();
		      while (rs.next())
		      {
		    	SrcFiveDataBean srcDataBean = new SrcFiveDataBean();
		        srcDataBean.setIssueId(rs.getString(1));
		        srcDataBean.setNo1(rs.getInt(2));
		        srcDataBean.setNo2(rs.getInt(3));
		        srcDataBean.setNo3(rs.getInt(4));
		        srcDataBean.setNo4(rs.getInt(5));
		        srcDataBean.setNo5(rs.getInt(6));
		        
		        beans.add(srcDataBean);
		      }
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			ConnectSrcDb.dbClose(conn, pstmt, rs);
		}
		
		
		
		
		
		return beans;
	}
	
	
	/**
	 * 获取期号前的多少期源码
	* @Title: getOriginDataExpect 
	* @Description: TODO(这里用一句话描述这个方法的作用) 
	* @param @param number
	* @param @param issueNumber
	* @param @return    设定文件 
	* @author banna
	* @date 2017年3月29日 上午10:43:43 
	* @return List<SrcFiveDataBean>    返回类型 
	* @throws
	 */
	public List<SrcFiveDataBean> getFlowData(List<SrcFiveDataBean> yuanBeans,String nplan)
	{
		List<SrcFiveDataBean> list = new ArrayList<SrcFiveDataBean>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Connection conn = ConnectSrcDb.getSrcConnection();
		
		StringBuffer sql = new StringBuffer();
		try
		{
			for(int i=0;i<yuanBeans.size();i++)
			{
				sql.setLength(0);
				sql.append("select issue_number,no1,no2,no3,no4,no5 from "+App.srcNumberTbName+" "
						+ "where issue_number >"+yuanBeans.get(i).getIssueId()+" limit "+nplan);
				pstmt = (PreparedStatement)conn.prepareStatement(sql.toString());
				rs = pstmt.executeQuery();
				 while (rs.next())
			      {
			    	SrcFiveDataBean srcDataBean = new SrcFiveDataBean();
			        srcDataBean.setIssueId(rs.getString(1));
			        srcDataBean.setNo1(rs.getInt(2));
			        srcDataBean.setNo2(rs.getInt(3));
			        srcDataBean.setNo3(rs.getInt(4));
			        srcDataBean.setNo4(rs.getInt(5));
			        srcDataBean.setNo5(rs.getInt(6));
			        
			        list.add(srcDataBean);
			      }
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			ConnectSrcDb.dbClose(conn, pstmt, rs);
		}
		
		
		return list;
	}
	
	
}
