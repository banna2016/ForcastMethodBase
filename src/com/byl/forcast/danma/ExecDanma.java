package com.byl.forcast.danma;

import java.sql.Connection;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.byl.forcast.App;
import com.byl.forcast.ConnectLTDb;
import com.byl.forcast.FiveInCount;
import com.byl.forcast.PredictionRepository;
import com.byl.forcast.SrcFiveDataBean;
import com.mysql.jdbc.PreparedStatement;

public class ExecDanma 
{
	//Ԥ��ǰ������ɱ��
	public void execDanma(List<SrcFiveDataBean> yuanBeans)
	{
		//����Դ���ȡ����
		PredictionRepository pre = new PredictionRepository();
		List<SrcFiveDataBean> flowbeans = new ArrayList<SrcFiveDataBean>();
		flowbeans = pre.getFlowData(yuanBeans, App.nPlan);
		
		//�����������Ԥ��
		int[] count = new int[App.number];
		
		for (SrcFiveDataBean bean : flowbeans) 
		{
			int[] numIntArr = {bean.getNo1(),bean.getNo2(),bean.getNo3()};
				
			for (int i : numIntArr)
			{
				count[i - 1] += 1;
			}
		}
		
		//ͳ��ǰ�����뿪������
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
//			int[] dudanArr = new int[countEqual];
			List<Integer> dudanArr = new ArrayList<Integer>();
			for(int s = 0;s<countEqual;s++)
			{
//				dudanArr[s] = countlist.get(s).getNumber();//��ȡ�Ͷ������ִ�����ͬ�ĺ���
				dudanArr.add(countlist.get(s).getNumber());
			}
			//��ȡԴ��
			List<SrcFiveDataBean> newYuan = pre.getOriginData(yuanBeans.get(yuanBeans.size()-1).getIssueId());
			//�ҳ��µ�����
			flowbeans = pre.getFlowData(newYuan, App.nPlan);
			//���÷�����ȡ����
			danList = this.findDanma(dudanArr, flowbeans,2);
//			inserToDb(danList);
		}
		else
		{//
			danList.add(countlist.get(0));//�����������
			
			int ciCountEqual = (int) this.judgeEqualCount(countlist,1).get("countEqual");//�ε���ͬ������ִ���
			
			if(ciCountEqual != 0)
			{
//				int[] cidanArr = new int[ciCountEqual];
				List<Integer> cidanArr = new ArrayList<Integer>();
				for(int s = 1;s <ciCountEqual+1;s++)
				{
//					cidanArr[s-1] = countlist.get(s).getNumber();//��ȡ�Ͷ������ִ�����ͬ�ĺ���
					cidanArr.add(countlist.get(s).getNumber());
				}
				//��ȡԴ��
				List<SrcFiveDataBean> newYuan = pre.getOriginData(yuanBeans.get(yuanBeans.size()-1).getIssueId());
				//�ҳ��µ�����
				flowbeans = pre.getFlowData(newYuan, App.nPlan);
				//���÷�����ȡ����
				List<FiveInCount> cidanlist = this.findDanma(cidanArr, flowbeans,1);//�ε�����ͬ����ֻ��Ҫ��һ���ε����뼴��
				
				danList.add(cidanlist.get(0));//����list����Ӵε�����
			}
			else
			{
				danList.add(countlist.get(1));//����ε�����
			}
		}
		
		//ɱ��ͳ��
		List<FiveInCount> shalist = new ArrayList<FiveInCount>();
		int countShaEqual = (int) this.judgeShaEqualCount(countlist,countlist.size()).get("countEqual");//��ͬ������ִ���
		if(countShaEqual != 0 && countShaEqual > 2)
		{//����ͬ���ִ����ĺ���
//			int[] shamaArr = new int[countShaEqual];
			List<Integer> shamaArr = new ArrayList<Integer>();
			for(int s = countlist.size()-1;s>=countlist.size()-countShaEqual;s--)
			{
				shamaArr.add(countlist.get(s).getNumber());
//				shamaArr[s] = countlist.get(s).getNumber();//��ȡ�Ͷ������ִ�����ͬ�ĺ���
			}
			//��ȡԴ��
			List<SrcFiveDataBean> newYuan = pre.getOriginData(yuanBeans.get(yuanBeans.size()-1).getIssueId());
			//�ҳ��µ�����
			flowbeans = pre.getFlowData(newYuan, App.nPlan);
			//���÷�����ȡ����
			shalist = this.findShama(shamaArr, flowbeans,2);
//			inserToDb(danList);
		}
		else
		{
			shalist.add(countlist.get(countlist.size()-1));//����ɱһ������
			shalist.add(countlist.get(countlist.size()-2));//����ɱ��������
			if(countShaEqual<=2)
			{//
				countShaEqual = (int) this.judgeShaEqualCount(countlist,countlist.size()-2).get("countEqual");//ɱ������ͬ������ִ���
				
				if(countShaEqual != 0)
				{
//					int[] shasanArr = new int[countShaEqual];
					List<Integer> shasanArr = new ArrayList<Integer>();
					int size = countlist.size();
					int s1 = 0;
					for(int s = size-1-2;s>size-1-2-countShaEqual;s--)//��ʼλ����ɱ���뿪ʼ��λ��
					{
//						shasanArr[s1] = countlist.get(s).getNumber();//��ȡ�Ͷ������ִ�����ͬ�ĺ���
//						s1++;
						shasanArr.add(countlist.get(s).getNumber());
					}
					//��ȡԴ��
					List<SrcFiveDataBean> newYuan = pre.getOriginData(yuanBeans.get(yuanBeans.size()-1).getIssueId());
					//�ҳ��µ�����
					flowbeans = pre.getFlowData(newYuan, App.nPlan);
					//���÷�����ȡ����
					List<FiveInCount> shasanlist = this.findShama(shasanArr, flowbeans,1);//ɱ����
					
					shalist.add(shasanlist.get(0));//����list����Ӵε�����
				}
				else
				{
					shalist.add(countlist.get(countlist.size()-1-2));//����ɱ����
				}
			}
		
		}
		
		//�������ݿ�
		inserToDb(danList,shalist);
		
	}
	
	//���ҵ���
	private List<FiveInCount> findDanma(List<Integer> duArr,List<SrcFiveDataBean> flowData,int dancount)//dancount:��ȡ�������
	{
		List<FiveInCount> list = new ArrayList<FiveInCount>();
		
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
						if(flag)
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
								fcount.setNumber(duint);
								list.add(fcount);
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
		
		return list;
	}
	
	//����ɱ��
	private List<FiveInCount> findShama(List<Integer> shaArr,List<SrcFiveDataBean> flowData,int shacount)//shacount:��ȡɱ�����
	{
		List<FiveInCount> list = new ArrayList<FiveInCount>();
		
		
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
	    		+ "(issue_number,DANMA_ONE,DANMA_TWO,CREATE_TIME,PREDICTION_TYPE,EXPERT_ID,SHAMA_ONE,SHAMA_TWO) "
	    		+ "values(?,?,?,?,?,?,?,?)";
	    try
	    {
	    	pstmt = (PreparedStatement)conn.prepareStatement(sql);
	 	    pstmt.setString(1, nextIssue);
	 	    pstmt.setString(2, App.translate(danlist.get(0).getNumber()));
	 	    pstmt.setString(3, App.translate(danlist.get(0).getNumber())+App.translate(danlist.get(1).getNumber()));
	 	    pstmt.setTimestamp(4, new Timestamp(new Date().getTime()));
	 	    pstmt.setString(5, App.ptypeid);
	 	    pstmt.setString(6, App.beid);
	 	    pstmt.setString(7, App.translate(shalist.get(0).getNumber())+App.translate(shalist.get(1).getNumber()));
	 	    pstmt.setString(8, App.translate(shalist.get(0).getNumber())+App.translate(shalist.get(1).getNumber())+App.translate(shalist.get(2).getNumber()));
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
		System.out.println("ͳ���н���");
	}
}
