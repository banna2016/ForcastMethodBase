package com.byl.forcast.renDanAndShama;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.byl.forcast.App;
import com.byl.forcast.ConnectLTDb;
import com.byl.forcast.DataToDb;
import com.byl.forcast.FiveInCount;
import com.byl.forcast.PredictionRepository;
import com.byl.forcast.SrcFiveDataBean;
import com.byl.forcast.danma.DanmaYuce;
import com.mysql.jdbc.PreparedStatement;

public class ExecRenDanma 
{
	//Ԥ��ǰ������ɱ��
	public void execRenDanma(List<SrcFiveDataBean> yuanBeans)
	{
		//����Դ���ȡ����
		PredictionRepository pre = new PredictionRepository();
		List<SrcFiveDataBean> flowbeans = new ArrayList<SrcFiveDataBean>();
		flowbeans = pre.getFlowData(yuanBeans, App.nPlan);
		
		//�����������Ԥ��
		int[] count = new int[App.number];
		
		for (SrcFiveDataBean bean : flowbeans) 
		{
			//��ȡ5�����
			int[] numIntArr = {bean.getNo1(),bean.getNo2(),bean.getNo3(),bean.getNo4(),bean.getNo5()};
				
			for (int i : numIntArr)
			{
				count[i - 1] += 1;
			}
		}
		
		//ͳ���κ��뿪������
		List<FiveInCount> countlist = new ArrayList();
		for(int j=0;j<App.number;j++)
		{
			FiveInCount fcount = new FiveInCount();
			
			fcount.setNumber(j+1);
			fcount.setCount1(count[j]);
			
			countlist.add(fcount);
		}
		
		Collections.sort(countlist);
		List<FiveInCount> danList = new ArrayList<FiveInCount>();
		//1.�ж��Ƿ��кͶ������ִ�����ͬ������
		int countEqual = (int) this.judgeEqualCount(countlist,0).get("countEqual");//��ͬ������ִ���
		if(countEqual != 0)
		{//����ͬ���ִ����ĺ���
			List<Integer> dudanArr = new ArrayList<Integer>();
			for(int s = 0;s<=countEqual;s++)
			{
				//��ȡ�Ͷ������ִ�����ͬ�ĺ���
				dudanArr.add(countlist.get(s).getNumber());
			}
			//��ȡԴ��
			List<SrcFiveDataBean> newYuan = pre.getOriginData(yuanBeans.get(yuanBeans.size()-1).getIssueId());
			//�ҳ��µ�����
			flowbeans = pre.getFlowData(newYuan, App.nPlan);
			//���÷�����ȡ����
			danList = this.findDanma(dudanArr, flowbeans,1,pre,yuanBeans.get(yuanBeans.size()-1).getIssueId(),danList);
//			inserToDb(danList);
		}
		else
		{//
			danList.add(countlist.get(0));//�����������
		}
		
		//ɱ��ͳ��
		List<FiveInCount> shalist = new ArrayList<FiveInCount>();
		int countShaEqual = (int) this.judgeShaEqualCount(countlist,countlist.size()).get("countEqual");//��ͬ������ִ���
		if(countShaEqual != 0 )
		{//����ͬ���ִ����ĺ���
			List<Integer> shamaArr = new ArrayList<Integer>();
			for(int s = countlist.size()-1;s>=countlist.size()-countShaEqual;s--)
			{
				//��ȡ��ɱһ����ִ�����ͬ�ĺ���
				shamaArr.add(countlist.get(s).getNumber());
			}
			//��ȡԴ��
			List<SrcFiveDataBean> newYuan = pre.getOriginData(yuanBeans.get(yuanBeans.size()-1).getIssueId());
			//�ҳ��µ�����
			flowbeans = pre.getFlowData(newYuan, App.nPlan);
			//���÷�����ȡ����
			shalist = this.findShama(shamaArr, flowbeans,1,pre,yuanBeans.get(yuanBeans.size()-1).getIssueId(),shalist);
		}
		else
		{
			shalist.add(countlist.get(countlist.size()-1));//����ɱһ������
		
		}
		
		//�������ݿ�
		inserToDb(danList,shalist);
		
	}
	
	//���ҵ���
	private List<FiveInCount> findDanma(List<Integer> duArr,List<SrcFiveDataBean> flowData,int dancount,
			PredictionRepository pre,String smallIssueId,List<FiveInCount> list)//dancount:��ȡ�������
	{
//		List<FiveInCount> list = new ArrayList<FiveInCount>();
		
		for (SrcFiveDataBean bean : flowData) 
		{
			//ȡ������ת��ΪAJQ��ʽ�ַ���
			StringBuffer flowstr = new StringBuffer(App.translate(bean.getNo1()));
			flowstr.append(App.translate(bean.getNo2()));
			flowstr.append(App.translate(bean.getNo3()));
			flowstr.append(App.translate(bean.getNo4()));
			flowstr.append(App.translate(bean.getNo5()));
			
			boolean flag = false;//�жϵ��������Ƿ����һ�������ѡ������һ����true
			List<Integer> newarr = new ArrayList<Integer>();//��ɸѡ��������
			
			for (Integer number : duArr) 
			{
				if(flowstr.toString().contains(App.translate(number)))
				{
					if(newarr.size()==0)
					{
						flag = true;
						newarr.add(number);
					}
					else
						if(flag && newarr.size()>0)
						{//���֮ǰ�Ѿ��м������ɸѡ���֣����������д��ڶ�������ѡ����Ҫ����ʹ������ɸѡ
							newarr.add(number);
							flag = false;
						}
				}
				
			}
			
			if(flag)
			{
				if(list.size()<dancount)
				{
					FiveInCount fcount = new FiveInCount();
					fcount.setNumber(newarr.get(0));
					list.add(fcount);
					duArr.remove(newarr.get(0));//�Ƴ��Ѿ�ɸѡ���ĵ���
					
					if(list.size() == dancount)
					{
						break;
					}
					else
						if(duArr.size()+1 == dancount)
						{
							for (Integer duint : duArr) 
							{
								FiveInCount count = new FiveInCount();
								count.setNumber(duint);
								list.add(count);
							}
							break;
						}
				}
				else
				{
					break;//�ѻ�ȡ�������ʹε����룬��������ѭ��
				}
			}
			else
			{//���õ���ɸѡ
				if(newarr.size() != 0)
				{
					if(newarr.size()>=dancount)
					{
						duArr.removeAll(duArr);
						for (int ns = 0;ns<newarr.size();ns++)
						{
							duArr.add(newarr.get(ns));
						}
					}
					
				}
			}
			
		}
		if(list.size()<dancount)
		{
			//��ȡԴ��
			List<SrcFiveDataBean> newYuan = pre.getOriginData(smallIssueId);
			//�ҳ��µ�����
			List<SrcFiveDataBean> flowbeans = pre.getFlowData(newYuan, App.nPlan);
			
			if(flowbeans.size()>0)
			{
				list = this.findDanma(duArr, flowbeans, dancount, pre, newYuan.get(newYuan.size()-1).getIssueId(),list);
			}
			else
			{
				for (Integer duint : duArr) 
				{
					FiveInCount count = new FiveInCount();
					count.setNumber(duint);
					list.add(count);
				}
			}
			
		}
		
		return list;
	}
	
	//����ɱ��
	private List<FiveInCount> findShama(List<Integer> shaArr,List<SrcFiveDataBean> flowData,int shacount,
			PredictionRepository pre,String smallIssueId,List<FiveInCount> list)//shacount:��ȡɱ�����
	{
//		List<FiveInCount> list = new ArrayList<FiveInCount>();
		
		
		List<Integer> linshi = null;
		for (SrcFiveDataBean bean : flowData) 
		{
			//ȡ������ת��ΪAJQ��ʽ�ַ���
			StringBuffer flowstr = new StringBuffer(App.translate(bean.getNo1()));
			flowstr.append(App.translate(bean.getNo2()));
			flowstr.append(App.translate(bean.getNo3()));
			flowstr.append(App.translate(bean.getNo4()));
			flowstr.append(App.translate(bean.getNo5()));
			
			boolean flag = true;//�жϵ��������Ƿ񲻰���һ��ɱ���ѡ������һ����true
			List<Integer> newarr = new ArrayList<Integer>();//��ɸѡ��������
			
			for (Integer number : shaArr) 
			{
				if(flowstr.toString().contains(App.translate(number)))
				{
					flag = false;
					newarr.add(number);
				}
				
			}
			
			if(flag)
			{
				continue;
			}
			else
			{//��ɱ���ѡ������list��,���Ƴ�
				if(shaArr.size() == shacount)
				{//��ɱ���ѡ����ĳ�����Ҫ��ȡ��ɱ�������ͬ��������ѭ��
					for (Integer integer : shaArr) 
					{
						FiveInCount fcount = new FiveInCount();
						fcount.setNumber(integer);
						list.add(fcount);
					}
					
					break;
				}
				else
				{
					if(shaArr.size()>shacount)
					{//�Ƴ����ֵ�����
						linshi = new ArrayList<Integer>();
						//����ɸѡ���룬���ȫ���Ƴ���Ҫ���¸�ֵ
						for (Integer shalin : shaArr) {
							linshi.add(shalin);
						}
						for (Integer integer : newarr) 
						{
							shaArr.remove(integer);
						}
						if(shaArr.size()<shacount && shaArr.size()!=0)
						{
							for (Integer integer : shaArr) 
							{
								FiveInCount fcount = new FiveInCount();
								fcount.setNumber(integer);
								list.add(fcount);
							}
							
							//��ʣ��ĺ������������ȥ�ж�
							for (Integer shaint : shaArr) 
							{
								linshi.remove(shaint);//����ʱ���Ƴ��Ѿ�ȷ��Ϊɱ�������
							}
							shaArr = linshi;
						}
						else
							if(shaArr.size() == shacount)
							{
								for (Integer integer : shaArr) 
								{
									FiveInCount fcount = new FiveInCount();
									fcount.setNumber(integer);
									list.add(fcount);
								}
								
								break;
							}
							else
								if(shaArr.size() == 0)
								{
									//��Ϊ���д�ѡ��ɱ�붼��������һ�������У������±Ƚ�
									shaArr = linshi;
								}
					}
					else
					{
						if(list.size()<shacount)//����Ҫɸѡ����
						{
							linshi = new ArrayList<Integer>();
							//����ɸѡ���룬���ȫ���Ƴ���Ҫ���¸�ֵ
							for (Integer shalin : shaArr) {
								linshi.add(shalin);
							}
							for (Integer integer : newarr) 
							{
								shaArr.remove(integer);
							}
							
							if((shaArr.size()+list.size())<=shacount)//������������ɱ��������Ѿ�ɸѡ����ɱ������ĺ���Ҫ���ȡɱ�����������ֱ�ӽ�ʣ��ɱ��洢����ֵ
							{
								for (Integer integer : shaArr) 
								{
									FiveInCount fcount = new FiveInCount();
									fcount.setNumber(integer);
									list.add(fcount);
								}
							}
							else
							{
								//��ʣ��ĺ������������ȥ�ж�
								for (Integer shaint : shaArr) 
								{
									linshi.remove(shaint);//����ʱ���Ƴ��Ѿ�ȷ��Ϊɱ�������
								}
								shaArr = linshi;
							}
							
							
						}
					}
				}
			}
			
		}
		if(list.size()<shacount)
		{//û��ȡ���㹻��ɱ��
			//��ȡԴ��
			List<SrcFiveDataBean> newYuan = pre.getOriginData(smallIssueId);
			//�ҳ��µ�����
			List<SrcFiveDataBean> flowbeans = pre.getFlowData(newYuan, App.nPlan);
			if(flowbeans.size()>0)
			{
				this.findShama(shaArr, flowbeans, shacount,pre,newYuan.get(newYuan.size()-1).getIssueId(),list);
			}
			else
			{
				for (Integer integer : shaArr) 
				{
					FiveInCount fcount = new FiveInCount();
					fcount.setNumber(integer);
					list.add(fcount);
				}
			}
		}
		return list;
	}
	
	/**
	 * �ж�ͳ�ƽ������ͬ�����ĺ���
	* @Title: judgeEqualCount 
	* @Description: TODO(������һ�仰�����������������) 
	* @param @param countlist
	* @param @return    �趨�ļ� 
	* @author banna
	* @date 2017��3��31�� ����9:29:12 
	* @return Map<String,Object>    �������� 
	* @throws
	 */
	private Map<String,Object> judgeEqualCount(List<FiveInCount> countlist,int start)
	{
		Map<String,Object> result = new HashMap<String,Object>();
		
		int count1  = countlist.get(start).getCount1();//��ǰ���ִ�����һλ�ĺ���ĳ��ִ���
		int countEqual = 0;//������ͬ���ִ����ĺ������
		for(int i=start+1;i<countlist.size();i++)
		{
			if(countlist.get(i).getCount1() == count1)
			{//���һλ���ֳ��ִ�����ͬ
				countEqual++;
			}
			else
			{//���ڶ�λ���͵�һλ�ĳ��ִ�����ͬ������Ҫ�ٽ��бȽ�
				break;
			}
		}
		result.put("countEqual", countEqual);
		return result;
	}
	//�ж�ɱ����ͬ���ִ����ĺ���
	private Map<String,Object> judgeShaEqualCount(List<FiveInCount> countlist,int end)
	{
		Map<String,Object> result = new HashMap<String,Object>();
		
		int count1  = countlist.get(end-1).getCount1();//��ǰ���ִ�����һλ�ĺ���ĳ��ִ���
		int countEqual = 0;//������ͬ���ִ����ĺ������
		for(int i=end-1 ;i >=0; i--)
		{
			if(countlist.get(i).getCount1() == count1)
			{//���һλ���ֳ��ִ�����ͬ
				countEqual++;
			}
			else
			{//���ڶ�λ���͵�һλ�ĳ��ִ�����ͬ������Ҫ�ٽ��бȽ�
				break;
			}
		}
		result.put("countEqual", countEqual);
		return result;
	}
	
	//���뵨��Ԥ���������ݿ�
	private void inserToDb(List<FiveInCount> danlist,List<FiveInCount> shalist)
	{
		//�ں��Ǵ���������ںŵ���һ��
		String nextIssue = App.getNextIssueByCurrentIssue(App.maxIssueId);
		 PreparedStatement pstmt = null;
		Connection conn = ConnectLTDb.getConnection();
	    String sql = "insert into " + App.predictionTbName + " "
	    		+ "(issue_number,DANMA_ONE,CREATE_TIME,PREDICTION_TYPE,EXPERT_ID,SHAMA_ONE) "
	    		+ "values(?,?,?,?,?,?)";
	    try
	    {
	    	pstmt = (PreparedStatement)conn.prepareStatement(sql);
	 	    pstmt.setString(1, nextIssue);
	 	    pstmt.setString(2, App.translate(danlist.get(0).getNumber()));
	 	    pstmt.setTimestamp(3, new Timestamp(new Date().getTime()));
	 	    pstmt.setString(4, App.ptypeid);
	 	    pstmt.setString(5, App.beid);
	 	    pstmt.setString(6, App.translate(shalist.get(0).getNumber()));
	 	    pstmt.executeUpdate();
	    }
	   catch(Exception e)
	    {
		   e.printStackTrace();
	    }
	    finally
	    {
	    	ConnectLTDb.dbClose(conn, pstmt, null);
	    }
		
	}
	
	//���µ�ǰԤ���׼ȷ��
	public void updateDanAndShaStatus()
	{
		//��ȡ���ڿ�������
		DataToDb dataToDb = new DataToDb();
		SrcFiveDataBean curIssue = dataToDb.getRecordByIssueCode(App.maxIssueId);
		List<String> numList = new ArrayList<String>();
		numList.add(App.translate(curIssue.getNo1()));
		numList.add(App.translate(curIssue.getNo2()));
		numList.add(App.translate(curIssue.getNo3()));
		numList.add(App.translate(curIssue.getNo4()));
		numList.add(App.translate(curIssue.getNo5()));
		
		StringBuffer drownNumber = new StringBuffer();
		for (String string : numList) 
		{
			drownNumber.append(string);
		}
		
		//1.�жϵ����н���
		//1.��ȡ����
		DanmaYuce danmaYuce = dataToDb.getRenDanYuceRecordByIssueNumber(curIssue.getIssueId(), App.predictionTbName);
		String dudanstatus = "0";//0:δ�У�1����ȷ
		if(numList.contains(danmaYuce.getDANMA_ONE()))
		{//�ж϶����Ƿ��г������г�����˫��Ҳ�г�
			dudanstatus = "1";
		}
		
		
		//2.�ж�ɱ���н���
		
		String flagshayi = "1";//ɱһ���Ƿ��г�
		if(numList.contains(danmaYuce.getSHAMA_ONE()))
		{
			flagshayi = "0";
		}
		
		//����׼ȷ�ʵ����ݿ�
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Connection conn = ConnectLTDb.getConnection();
		StringBuffer sql = new StringBuffer();
		
		try 
		{
			pstmt = (PreparedStatement)conn.prepareStatement(sql.toString());
			
			sql.append("update "+App.predictionTbName+" set "
					+ " DUDAN_STATUS="+dudanstatus+" ,"
					+ " SHAMAYI_STATUS="+flagshayi+" ,"
					+ " DROWN_NUMBER='"+drownNumber+"' "
					+ " where"
					+ " ISSUE_NUMBER="+App.maxIssueId+" and "
					+ " EXPERT_ID='"+App.beid+"' and "
					+ " PREDICTION_TYPE='"+App.ptypeid+"' ");
			
			pstmt.executeUpdate(sql.toString());
			
			//3.�ж�ר�Ҹ���ָ��Ԥ��׼ȷ��
			//ȡ����ǰ�ڵ�Ԥ������Ȼ��ȡ��������ĸ�ר������Ԥ������Ȼ��ȡ��Ԥ��׼ȷ����������Ȼ����б������㣬�ٰѽ���������ݿ�
			//1)���㵨��׼ȷ��
			int limitnumber = App.orderRule;
			double countAll = limitnumber;//all
			double countZJ = dataToDb.getCountOfexpertprediction("DUDAN_STATUS", false,limitnumber);//��ȡ�н��Ķ���
			double dudanZJL = countZJ/countAll;
			
			//4)����ɱһ��ȫ��׼ȷ��
			countAll = limitnumber;//all
			countZJ = dataToDb.getCountOfexpertprediction("SHAMAYI_STATUS", false,limitnumber);//��ȡ�н���
			double shayiZJL = countZJ/countAll;
			
			//����Ԥ�⵽��ǰ��Ϊֹ��ר�ҵ��н�����
			StringBuffer sqlzjl =new StringBuffer();
			sqlzjl.append("update "+App.predictionTbName+" set "
					+ " WIN_RATE_DUDAN="+dudanZJL+" ,"
					+ " WIN_RATE_SHAYI="+shayiZJL+" "
					+ " where"
					+ " ISSUE_NUMBER="+App.maxIssueId+" and "
					+ " EXPERT_ID='"+App.beid+"' and "
					+ " PREDICTION_TYPE='"+App.ptypeid+"' ");
			
			pstmt.executeUpdate(sqlzjl.toString());
			
			//TODO:����ɣ������н����жϵ�ǰר���Ƿ��շ�
			
			
			
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
