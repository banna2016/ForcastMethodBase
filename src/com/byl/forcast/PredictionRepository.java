package com.byl.forcast;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.byl.forcast.danma.ExecDanma;
import com.mysql.jdbc.PreparedStatement;

/**
 * Ԥ�ⷽ����
* @Description: TODO(������һ�仰�����������������) 
* @author banna
* @date 2017��3��29�� ����9:58:34
 */
public class PredictionRepository 
{
	/**
	 * ǰ�����롢ɱ��
	* @Title: execFirstThreeDanma 
	* @Description: TODO(������һ�仰�����������������) 
	* @param     �趨�ļ� 
	* @author banna
	* @date 2017��3��29�� ����9:59:29 
	* @return void    �������� 
	* @throws
	 */
	public void execFirstThreeDanma()
	{
		//��ȡԴ��
		List<SrcFiveDataBean> yuanBeans = this.getOriginData(null);
		
		//ʹ��Դ�����Ԥ��
		
		
	}
	/**
	 * ��ѡ���롢ɱ��
	* @Title: execDanma 
	* @Description: TODO(������һ�仰�����������������) 
	* @param     �趨�ļ� 
	* @author banna
	* @date 2017��3��29�� ����9:59:55 
	* @return void    �������� 
	* @throws
	 */
	public void execDanma()
	{
		ExecDanma execDanma = new ExecDanma();
		DataToDb dataToDb = new DataToDb();
		//�жϵ����ں��Ƿ��Ѿ�Ԥ�⣬Ԥ����Ҫ�ж��н���
		if(dataToDb.hasRecordByIssueNumber(App.maxIssueId,App.predictionTbName))
		{//�ж��н���
			execDanma.updateDanAndShaStatus();
		}
		else
		{
			List<SrcFiveDataBean> yuanBeans = this.getOriginData(null);
			execDanma.execDanma(yuanBeans);
		}
		
		
	}
	
	/**
	 * ǰ�����븴ʽ
	* @Title: firstThreeDuplex 
	* @Description: TODO(������һ�仰�����������������) 
	* @param     �趨�ļ� 
	* @author banna
	* @date 2017��3��29�� ����10:01:11 
	* @return void    �������� 
	* @throws
	 */
	public void firstThreeDuplex()
	{
		
	}
	/**
	 * ������ѡ6��
	* @Title: groupSix 
	* @Description: TODO(������һ�仰�����������������) 
	* @param     �趨�ļ� 
	* @author banna
	* @date 2017��3��29�� ����10:03:33 
	* @return void    �������� 
	* @throws
	 */
	public void groupSixOfRensan()
	{
		
	}
	
	/**
	 * ��ѡ4���ڼƻ�
	* @Title: LeFourOfRensi 
	* @Description: TODO(������һ�仰�����������������) 
	* @param     �趨�ļ� 
	* @author banna
	* @date 2017��3��29�� ����10:04:17 
	* @return void    �������� 
	* @throws
	 */
	public void LeFourOfRensi()
	{
		
	}
	
	/**
	 * �������ڼƻ�
	* @Title: LiangThreeOfRener 
	* @Description: TODO(������һ�仰�����������������) 
	* @param     �趨�ļ� 
	* @author banna
	* @date 2017��3��29�� ����10:04:58 
	* @return void    �������� 
	* @throws
	 */
	public void LiangThreeOfRener()
	{
		
	}
	
	/**
	 * ��ȡԴ������
	* @Title: getOriginData 
	* @Description: TODO(������һ�仰�����������������) 
	* @param @return    �趨�ļ� 
	* @author banna
	* @date 2017��3��29�� ����10:42:34 
	* @return List<SrcFiveDataBean>    �������� 
	* @throws
	 */
	public List<SrcFiveDataBean> getOriginData(String lastTimeIssue)//lastTimeIssue���ϴλ�ȡԴ������һ�ڵ��ں�
	{
		List<SrcFiveDataBean> list = new ArrayList<SrcFiveDataBean>();
		
		//��ȡ��������
		DataToDb dataToDb = new DataToDb();
		SrcFiveDataBean srcFiveDataBean = dataToDb.getRecordByIssueCode(App.maxIssueId);
		if("0".equals(App.type))
		{//���ڻ�ȡ����
			String lArr[] = App.CI_LOCATION_NUMBER.split(",");//��ȡҪʹ�õ�����λ��
			if("0".equals(App.locationOrContain))
			{//��λ
				list = this.getLocationList(lArr,srcFiveDataBean,lastTimeIssue);
			}
			else
				if("1".equals(App.locationOrContain))
				{//����(���������밴��С��������ת��AJQ)���Ϳ��������е�noArr�ֶαȽϰ�����ϵ,��NOARR��ʱ��û��ά�����ֶ�
					list = this.getContainList(lArr,srcFiveDataBean,lastTimeIssue);
				}
			
		}
		else
			if("1".equals(App.type))
			{//�����ڻ�ȡ����
				list = this.getContactIssueList( srcFiveDataBean,lastTimeIssue);
			}
			else
				if("2".equals(App.type))
				{//���ڻ�ȡ����
					list = this.getCycleIssueList(srcFiveDataBean,lastTimeIssue);
				}
		
		
		return list;
	}
	
	/**
	 * �������ڻ�ȡ�����ȡԴ��
	* @Title: getCycleIssueList 
	* @Description: TODO(������һ�仰�����������������) 
	* @param @param srcFiveDataBean
	* @param @return    �趨�ļ� 
	* @author banna
	* @date 2017��3��30�� ����10:46:41 
	* @return List<SrcFiveDataBean>    �������� 
	* @throws
	 */
	public List<SrcFiveDataBean> getCycleIssueList(SrcFiveDataBean srcFiveDataBean,String lastTimeIssue)
	{
		List<SrcFiveDataBean> beans = new ArrayList<SrcFiveDataBean>();
		String issueId = srcFiveDataBean.getIssueId();
		String lastissuenum = issueId.substring(issueId.length()-2);//��ȡ����λ
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Connection conn = ConnectSrcDb.getSrcConnection();
		
		StringBuffer sql = new StringBuffer();
		
		if(App.cycle == 1)
		{//��ʱֻд����1��sql������ȡÿ��ͬ�ںŵ�����
			sql.append("SELECT issue_number,no1,no2,no3,no4,no5 FROM "+App.srcNumberTbName+" "
					+ "WHERE ISSUE_NUMBER LIKE '%"+lastissuenum+"'");
			
			if(null != lastTimeIssue && !"".equals(lastTimeIssue))
			{//��Ϊ�ٴλ�ȡԴ�룬��Ҫ�����ϴλ�ȡ��С�ںŵ�����
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
	 * ��ȡ������Դ��
	* @Title: getContactIssueList 
	* @Description: TODO(������һ�仰�����������������) 
	* @param @param lArr
	* @param @param srcFiveDataBean
	* @param @return    �趨�ļ� 
	* @author banna
	* @date 2017��3��29�� ����4:34:46 
	* @return List<SrcFiveDataBean>    �������� 
	* @throws
	 */
	public List<SrcFiveDataBean> getContactIssueList(SrcFiveDataBean srcFiveDataBean,String lastTimeIssue)
	{
		List<SrcFiveDataBean> beans = new ArrayList<SrcFiveDataBean>();
		List<SrcFiveDataBean> yuanBeans = new ArrayList<SrcFiveDataBean>();
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Connection conn = ConnectSrcDb.getSrcConnection();
		
		//���ӵ�������
		StringBuffer crConditions = new StringBuffer();
		
		//������������
		StringBuffer lcConditions = new StringBuffer();
		DataToDb dataToDb = new DataToDb();
		SrcFiveDataBean lastBean = dataToDb.getLastRecordByIssueCode(srcFiveDataBean.getIssueId());
		
		if(null != App.CI_LOCATION_NUMBER && !"".equals(App.CI_LOCATION_NUMBER))
		{//��λ������Ϊ��
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
		{//���������ֶβ�Ϊ��
			String lcArr[] = App.CI_RULE_FILED.split(",");
			for(int i=0;i<lcArr.length;i++)
			{
				if(i == 0 && crConditions.length() == 0)//����λ����Ϊ�գ����������ֶ��ǵ�һ�����ӵĵ���Դ��ɸѡ����
				{
					crConditions.append(" "+lcArr[i]+"="+srcFiveDataBean.numberMap.get(lcArr[i]));
				}
				else
				{
					crConditions.append(" and "+lcArr[i]+"="+srcFiveDataBean.numberMap.get(lcArr[i]));
				}
			}
		}
		
		//������������
		if(null != App.LI_LOCATION_NUMBER && !"".equals(App.LI_LOCATION_NUMBER))
		{//��λ������Ϊ��
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
		{//���������ֶβ�Ϊ��
			String llnArr[] = App.LI_RULE_FILED.split(",");
			for(int i=0;i<llnArr.length;i++)
			{
				if(i == 0 && lcConditions.length() == 0)//����λ����Ϊ�գ������������ֶ��ǵ�һ�����ӵ�����Դ��ɸѡ����
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
		      //ɸѡԴ��
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
	
	//��ȡ����������Դ��
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
					 lastIssue = beans.get(m).getIssueId();//�������һ�ڵ��ں�
				 }
					
			  }
			  
			 if(yuanBeans.size()<App.originDataCount)
			 {//��Դ������û�дﵽԴ��������Ҫ����Ҫ�ٻ�ȡ���������ٴ�ɸѡԴ��
				 this.getLastIssue(beans, lcConditions, crConditions, lastIssue, yuanBeans, pstmt, rs, conn,srcFiveDataBean);
			 }
		  }
		  
			
		return yuanBeans;
	}
	
	
	//������Դ���ȡ--��ȡ���ڷ�����������
	public List<SrcFiveDataBean> getCurrentIssue(StringBuffer crConditions,String lastIssue,SrcFiveDataBean srcFiveDataBean)
	{
		List<SrcFiveDataBean> beans = new ArrayList<SrcFiveDataBean>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Connection conn = ConnectSrcDb.getSrcConnection();
		
		if(null != lastIssue && !"".equals(lastIssue))
		{//�����ڻ�ȡ�ںŲ�Ϊ��
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
	 * ��ȡ���ڰ�����ϵԴ��
	* @Title: getContainList 
	* @Description: TODO(������һ�仰�����������������) 
	* @param @param lArr
	* @param @param srcFiveDataBean
	* @param @return    �趨�ļ� 
	* @author banna
	* @date 2017��3��29�� ����3:04:56 
	* @return List<SrcFiveDataBean>    �������� 
	* @throws
	 */
	public List<SrcFiveDataBean> getContainList(String lArr[] ,SrcFiveDataBean srcFiveDataBean,
			String lastTimeIssue)
	{
		List<SrcFiveDataBean> beans = new ArrayList<SrcFiveDataBean>();
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Connection conn = ConnectSrcDb.getSrcConnection();
		
		StringBuffer condistions = new StringBuffer();//where����
		
		String location = "";
		List<Integer> numList = new ArrayList<Integer>();
		for (int i=0;i<lArr.length;i++) 
		{
			location = lArr[i];
			numList.add((Integer) srcFiveDataBean.numberMap.get("no"+location));
		}
		
		Collections.sort(numList);//����
		
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
		
		//NOARR:��Ҫ���ӵ��ɼ�����ʱ������ֶΣ����������밴��С��������Ȼ��ת��λAJQ�洢
		StringBuffer sql = new StringBuffer("select issue_number,no1,no2,no3,no4,no5 from "+App.srcNumberTbName+" "
				+ "where NOARR like '"+condistions +"'") ;
		if(null != lastTimeIssue && !"".equals(lastTimeIssue))
		{//��Ϊ�ٴλ�ȡԴ�룬��Ҫ�����ϴλ�ȡ��С�ںŵ�����
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
	 * ��ȡ��λ����
	* @Title: getLocationList 
	* @Description: TODO(������һ�仰�����������������) 
	* @param @return    �趨�ļ� 
	* @author banna
	* @date 2017��3��29�� ����2:50:35 
	* @return List<SrcFiveDataBean>    �������� 
	* @throws
	 */
	public List<SrcFiveDataBean> getLocationList(String lArr[] ,SrcFiveDataBean srcFiveDataBean,
			String lastTimeIssue)
	{
		List<SrcFiveDataBean> beans = new ArrayList<SrcFiveDataBean>();
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Connection conn = ConnectSrcDb.getSrcConnection();
		
		StringBuffer condistions = new StringBuffer();//where����
		
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
		{//��Ϊ�ٴλ�ȡԴ�룬��Ҫ�����ϴλ�ȡ��С�ںŵ�����
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
	 * ��ȡ�ں�ǰ�Ķ�����Դ��
	* @Title: getOriginDataExpect 
	* @Description: TODO(������һ�仰�����������������) 
	* @param @param number
	* @param @param issueNumber
	* @param @return    �趨�ļ� 
	* @author banna
	* @date 2017��3��29�� ����10:43:43 
	* @return List<SrcFiveDataBean>    �������� 
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