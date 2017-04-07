package com.byl.forcast.renSLGroup;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.byl.forcast.App;
import com.byl.forcast.ConnectLTDb;
import com.byl.forcast.DataToDb;
import com.byl.forcast.GroupNumber;
import com.byl.forcast.Maputil;
import com.byl.forcast.PredictionRepository;
import com.byl.forcast.SrcFiveDataBean;
import com.byl.forcast.qianSLFushi.ExecQSLiumaFushi;
import com.byl.forcast.qianSLFushi.FushiYuce;

/**
 * ������ѡ�����㷨
* @Description: TODO(������һ�仰�����������������) 
* @author banna
* @date 2017��4��6�� ����4:26:04
 */
public class ExecRensanTopSixgroup 
{

	//Ԥ��ǰ�����븴ʽ
	public void execRensanTopsixGroup(List<SrcFiveDataBean> yuanBeans)
	{
		//����Դ���ȡ����
		PredictionRepository pre = new PredictionRepository();
		List<SrcFiveDataBean> flowbeans = new ArrayList<SrcFiveDataBean>();
		flowbeans = pre.getFlowData(yuanBeans, App.nPlan);
		
		
		List<GroupNumber> list = this.fushiFromBeans(flowbeans,3);
		
		ExecQSLiumaFushi execQSLiumaFushi = new ExecQSLiumaFushi();
		//�Ƚ�ͳ�Ʊ����
		execQSLiumaFushi.clearTongji();
		
		//���������ɵ�����������µ�����ͳ�Ʊ���
		this.updateTimes(list);
		
		//�ҳ����ִ�������һ��
		List<GroupNumber> maxgroup = this.findMaxTimesGroup(20);//10:ȡ20�����ݣ���limit�Ĳ���
		
		List<GroupNumber> gSixMaxGroup = new ArrayList<GroupNumber>();//ȡ�����ִ�������6��
		for (int i=0;i<maxgroup.size();i++) 
		{
			if(i<6)
			{
				gSixMaxGroup.add(maxgroup.get(i));
			}
		}
		
		//��Ԥ�������뵽���ݿ���
		insertToDB(gSixMaxGroup);
		
	}
	
	/**
	 * ��ȡ�����Ӧ�������������
	* @Title: fushiFromBeans 
	* @Description: TODO(������һ�仰�����������������) 
	* @param @param flowbeans
	* @param @param fushi
	* @param @return    �趨�ļ� 
	* @author banna
	* @date 2017��4��6�� ����5:09:11 
	* @return List<GroupNumber>    �������� 
	* @throws
	 */
	public List<GroupNumber> fushiFromBeans(List<SrcFiveDataBean> flowbeans,int fushi)
	{
		List<GroupNumber> groupNumbers = new ArrayList<GroupNumber>();
		
		for (SrcFiveDataBean bean : flowbeans)
		{
			List<GroupNumber> beanFushi = this.getFushiFromBeans(bean, fushi);
			groupNumbers.addAll(beanFushi);
		}
		
		return groupNumbers;
	}
	
	
	/**
	 * 
	* @Title: findMaxCountGroupnumber 
	* @Description: TODO(ɸѡ���ִ����������) 
	* @param @param pre
	* @param @param equallist
	* @param @param smallYuanIssueId
	* @param @return    �趨�ļ� 
	* @author banna
	* @date 2017��4��5�� ����3:15:16 
	* @return GroupNumber    �������� 
	* @throws
	 */
	/*public GroupNumber findMaxCountGroupnumber(PredictionRepository pre,List<GroupNumber> equallist,String smallYuanIssueId,
			ExecQSLiumaFushi execQSLiumaFushi)
	{
		GroupNumber gmax = new GroupNumber();
		
		List<SrcFiveDataBean> newYuan = pre.getOriginData(smallYuanIssueId);
		//�ҳ��µ�����
		List<SrcFiveDataBean> flowbeans = pre.getFlowData(newYuan, App.nPlan);
		//�ҳ������Ӧ��ǰ��6�븴ʽ
		List<GroupNumber> list = this.fushiFromBeans(flowbeans, 3);
		
		for (GroupNumber groupNumber : equallist) 
		{
			if(ExecQSLiumaFushi.listContainValue(list, groupNumber))
			{//������������������
				if(App.countMap.containsKey(groupNumber.getGroupNumber()))
				{
					int count =   Integer.parseInt(App.countMap.get(groupNumber.getGroupNumber()).toString())+ 1;
					App.countMap.put(groupNumber.getGroupNumber(), count);
					App.countMap = Maputil.sortByValue(App.countMap);//���³��ִ�������������
				}
			}
		}
		
		//�жϵ�ǰ����Ƿ������ͬ��������ͬ��Ҫ�����ж�
		List<GroupNumber> maxgroup = this.findMaxTimesGroup(20);//20:ȡ10�����ݣ���limit�Ĳ���
		//�鿴��������������Ƿ��г��ִ�����ͬ�����
		int countEqual = (int) execQSLiumaFushi.judgeEqualCount(maxgroup,5).get("countEqual");//��ͬ������ִ���
		
		if(countEqual!=0)
		{//������ͬ��
			equallist.removeAll(equallist);
			for (int i=5;i<=countEqual+5;i++)
			{
				maxgroup.get(i).setCount(0);
				equallist.add(maxgroup.get(i));
			}
			if(flowbeans.size()>0)
			{
				gmax = this.findMaxCountGroupnumber(pre,equallist, newYuan.get(newYuan.size()-1).getIssueId(),execQSLiumaFushi);
			}
			else
			{//û�����������ˣ���Ĭ��ȡ��һ��ΪԤ������
				gmax = equallist.get(0);
			}
			
			
		}
		else
		{//û�����������ˣ���Ĭ��ȡ��һ��ΪԤ������
			gmax = maxgroup.get(0);
		}
		
		return gmax;
	}*/
	
	
	
	
	
	
	/**
	 * ��Ԥ�������뵽���ݿ���
	* @Title: insertToDB 
	* @Description: TODO(������һ�仰�����������������) 
	* @param @param gMaxGroup    �趨�ļ� 
	* @author banna
	* @date 2017��4��5�� ����3:10:01 
	* @return void    �������� 
	* @throws
	 */
	private void insertToDB(List<GroupNumber> gMaxGroup)
	{
		//�ں��Ǵ���������ںŵ���һ��
		String nextIssue = App.getNextIssueByCurrentIssue(App.maxIssueId);
		PreparedStatement pstmt = null;
		Connection conn = ConnectLTDb.getConnection();
	    String sql = "insert into " + App.predictionTbName + " "
	    		+ "(issue_number,CREATE_TIME,PREDICTION_TYPE,EXPERT_ID,GROUP1,GROUP2,GROUP3,GROUP4,GROUP5,GROUP6) "
	    		+ "values(?,?,?,?,?,?,?,?,?,?)";
	    try
	    {
	    	pstmt = (PreparedStatement)conn.prepareStatement(sql);
	 	    pstmt.setString(1, nextIssue);
	 	    pstmt.setTimestamp(2, new Timestamp(new Date().getTime()));
	 	    pstmt.setString(3, App.ptypeid);
	 	    pstmt.setString(4, App.beid);
	 	    
	 	    for(int i=1;i<=gMaxGroup.size();i++)
	 	    {//ѭ������group1~group6��6������
	 	    	pstmt.setString(i+4, gMaxGroup.get(i-1).getGroupNumber());
	 	    }
	 	    
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
	
	
	
	
	/**
	 * �ҳ����ִ�������һ��
	* @Title: findMaxTimesGroup 
	* @Description: TODO(������һ�仰�����������������) 
	* @param @return    �趨�ļ� 
	* @author banna
	* @date 2017��4��5�� ����2:27:35 
	* @return List<GroupNumber>    �������� 
	* @throws
	 */
	public List<GroupNumber> findMaxTimesGroup(int n)
	{
		List<GroupNumber> list = new ArrayList<GroupNumber>();
		Set keyset = App.countMap.keySet();
		Iterator<String> it = keyset.iterator();
		int c = 0;
		while(it.hasNext())
		{
			if(c < n)
			{
				GroupNumber groupNumber = new GroupNumber();
				groupNumber.setGroupNumber(it.next());
//				System.out.println(App.countMap.get(groupNumber.getGroupNumber())+"=="+groupNumber.getGroupNumber());
				groupNumber.setCount(App.countMap.get(groupNumber.getGroupNumber()));
				list.add(groupNumber);
				c++;
			}
			else
			{
				break;
			}
			
		}
		
		
		return list;
	}
	
	
	public void updateTimes(List<GroupNumber> list)
	{
		//��ÿ����ϵĳ��ִ���ͳ����countMap��
		for (GroupNumber groupNumber : list) 
		{
			if(App.countMap.containsKey(groupNumber.getGroupNumber()))
			{
				int count =   Integer.parseInt(App.countMap.get(groupNumber.getGroupNumber()).toString())+ 1;
//				App.countMap.remove(groupNumber.getGroupNumber());
				App.countMap.put(groupNumber.getGroupNumber(), count);
			}
		}
		//��map����
		App.countMap=Maputil.sortByValue(App.countMap);
		
		
	}
	
	
	
	//���ַ�������
	private String sortString(String str)
	{
		char[] chars = str.toCharArray();
		Arrays.sort(chars);
		return new String(chars);
	}
	
	/**
	 * ���ݿ������뽫ת��Ϊn�鸴ʽ
	* @Title: getFushiFromBeans 
	* @Description: TODO(������һ�仰�����������������) 
	* @param @param bean
	* @param @return    �趨�ļ� 
	* @author banna
	* @date 2017��4��5�� ����10:38:16 
	* @return List<String>    �������� 
	* @throws
	 */
	public List<GroupNumber> getFushiFromBeans(SrcFiveDataBean bean,int nmafushi)//nmafushi����ȡn�븴ʽ�����븴ʽ���Σ�2
	{
		List<GroupNumber> list = new ArrayList<GroupNumber>();
		
		List<String> kjhm = this.sortNumber(bean);
		
		int len = kjhm.size();
		String a1 ;
		String a2 ;
		String a3 ;
		String a4 ;
		String a5 ;
		String a6 ;
		for(int i1=0;i1<len;i1++)
		{
			a1 = kjhm.get(i1);
			
			for(int i2=i1+1;i2<len;i2++)
			{
				a2 = kjhm.get(i2);
				
				if(nmafushi == 2)
				{//���븴ʽ
					GroupNumber gnumber  = new GroupNumber();
					
					gnumber.setGroupNumber(a1+a2);
					
					list.add(gnumber);
				}
				else
				{
					for(int i3=i2+1;i3<len;i3++)
					{
						a3 = kjhm.get(i3);
						
						if(nmafushi == 3)
						{//���븴ʽ
							GroupNumber gnumber  = new GroupNumber();
							
							gnumber.setGroupNumber(a1+a2+a3);
							
							list.add(gnumber);
						}
						else
						{
							for(int i4=i3+1;i4<len;i4++)
							{
								a4 = kjhm.get(i4);
								
								if(nmafushi == 4)
								{//���븴ʽ
									GroupNumber gnumber  = new GroupNumber();
									
									gnumber.setGroupNumber(a1+a2+a3+a4);
									
									list.add(gnumber);
								}
								/*else
								{
									
								}*/
							}
						}
					}
				}
			}
		}
		
		
		
		
		return list;
	}
	
	/**
	 * �������������򣬲���ת��ΪAJQ
	* @Title: sortNumber 
	* @Description: TODO(������һ�仰�����������������) 
	* @param @param bean
	* @param @return    �趨�ļ� 
	* @author banna
	* @date 2017��4��5�� ����11:10:23 
	* @return String    �������� 
	* @throws
	 */
	public List<String> sortNumber(SrcFiveDataBean bean)
	{
		List<String> list = new ArrayList<String>();
		
		int[] arr = new int[5];
		arr[0] = bean.getNo1();
		arr[1] = bean.getNo2();
		arr[2] = bean.getNo3();
		arr[3] = bean.getNo4();
		arr[4] = bean.getNo5();
		Arrays.sort(arr);
		
		for (int i : arr) 
		{
			list.add(App.translate(i));
		}
		
		return list;
	}
	
	
	//���µ�ǰԤ���׼ȷ��
	public void updateStatus()
	{
		System.out.println("Ԥ��������ѡ6��׼ȷ��");
		//��ȡ���ڿ�������
		DataToDb dataToDb = new DataToDb();
		SrcFiveDataBean curIssue = dataToDb.getRecordByIssueCode(App.maxIssueId);
		List<String> numList = new ArrayList<String>();//��������
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
		//��ȡԤ������
		RenSanGroup renSanGroup = dataToDb.getRensanSixGroupYuceRecordByIssueNumber(curIssue.getIssueId(), App.predictionTbName);
	
		int status = 0;//��ǰ�г�������
		int checkint = 0;
		for(int i=1;i<=6;i++)
		{
			//ȡ������
				char[] rr = renSanGroup.map.get("group"+i).toCharArray();
				for(char yuce:rr)
				{
					for (String kjNum : numList) 
					{
						if(kjNum.equals(yuce+""))
						{
							checkint++;//һ���������
							break;
						}
					}
					
				}
				
				if(checkint == 3)
				{//Ԥ���������������ȫ�ڿ���������
					status++;
				}
				checkint = 0;//����ͳ��Ϊ0
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
					+ " STATUS='"+(status>0?1:0)+"' ,"
					+ " ZJGROUPS='"+status+"',"
					+ " DROWN_NUMBER='"+drownNumber+"' "
					+ " where"
					+ " ISSUE_NUMBER="+App.maxIssueId+" and "
					+ " EXPERT_ID='"+App.beid+"' and "
					+ " PREDICTION_TYPE='"+App.ptypeid+"' ");
			
			pstmt.executeUpdate(sql.toString());
			
			//3.�ж�ר�Ҹ���ָ��Ԥ��׼ȷ��
			//ȡ����ǰ�ڵ�Ԥ������Ȼ��ȡ��������ĸ�ר������Ԥ������Ȼ��ȡ��Ԥ��׼ȷ����������Ȼ����б������㣬�ٰѽ���������ݿ�
			int limitnumber = App.orderRule;
			double countAll = limitnumber;//all
			double countZJ = dataToDb.getCountOfexpertprediction("STATUS", false,limitnumber);//��ȡ�н�������6��
			double dudanZJL = countZJ/countAll;
			
			//����Ԥ�⵽��ǰ��Ϊֹ��ר�ҵ��н�����
			StringBuffer sqlzjl =new StringBuffer();
			sqlzjl.append("update "+App.predictionTbName+" set "
					+ " WIN_RATE="+dudanZJL+" "
					+ " where"
					+ " ISSUE_NUMBER="+App.maxIssueId+" and "
					+ " EXPERT_ID='"+App.beid+"' and "
					+ " PREDICTION_TYPE='"+App.ptypeid+"' ");
			
			pstmt.executeUpdate(sqlzjl.toString());
			
			
			//TODO:�����1�������н����жϵ�ǰר���Ƿ��շ�
			
			
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
