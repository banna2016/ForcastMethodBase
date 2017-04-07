package com.byl.forcast.qianSLFushi;

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

/**
 * ����ǰ�����븴ʽԤ��
* @Description: TODO(������һ�仰�����������������) 
* @author banna
* @date 2017��4��5�� ����9:13:33
 */
public class ExecQSLiumaFushi
{
	//Ԥ��ǰ�����븴ʽ
	public void execQSLiumaFushi(List<SrcFiveDataBean> yuanBeans)
	{
		//����Դ���ȡ����
		PredictionRepository pre = new PredictionRepository();
		List<SrcFiveDataBean> flowbeans = new ArrayList<SrcFiveDataBean>();
		flowbeans = pre.getFlowData(yuanBeans, App.nPlan);
		
		
		List<GroupNumber> list = this.changeFushiFromFlowData(flowbeans, 6,3);//6:ǰ��6�븴ʽ
		
		//�Ƚ�ͳ�Ʊ����
		clearTongji();
		
		//���������ɵ�ǰ�����븴ʽ���µ�����ͳ�Ʊ���
		this.updateTimes(list, App.liumatbName);
		
		//�ҳ����ִ�������һ��
		List<GroupNumber> maxgroup = this.findMaxTimesGroup(10);//10:ȡ10�����ݣ���limit�Ĳ���
		
		//�鿴��������������Ƿ��г��ִ�����ͬ�����
		int countEqual = (int) this.judgeEqualCount(maxgroup,0).get("countEqual");//��ͬ������ִ���
		GroupNumber gMaxGroup = new GroupNumber();//�����ִ��������
		if(countEqual != 0)
		{
			List<GroupNumber> equallist = new ArrayList<GroupNumber>();
			for (int i=0;i<=countEqual;i++)
			{
				maxgroup.get(i).setCount(0);
				equallist.add(maxgroup.get(i));
			}
			
			
			gMaxGroup = this.findMaxCountGroupnumber(pre,equallist, yuanBeans.get(yuanBeans.size()-1).getIssueId(),App.liumatbName);
			
		}
		else
		{
			gMaxGroup = maxgroup.get(0);
		}
	
		
		//��Ԥ�������뵽���ݿ���
		String nextIssue = App.getNextIssueByCurrentIssue(App.maxIssueId);
		String stopIssue = this.getNextNIssuenumber(nextIssue, Integer.parseInt(App.nPlan));
		gMaxGroup.setStartIssue(nextIssue);
		gMaxGroup.setStopIssue(stopIssue);
		insertToDB(gMaxGroup);
		
	}
	//��ͳ�Ʊ��count��0
	public void clearTongji()
	{
		for (String key : App.countMap.keySet()) {
			App.countMap.put(key, 0);
		}
			
		/*PreparedStatement pstmt = null;
		ResultSet rs = null;
		Connection conn = ConnectLTDb.getConnection();
		StringBuffer sql = new StringBuffer();
		
		sql.append("update "+tbName+" set count=0 ");
		
		try 
		{
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.executeUpdate();
		} 
		catch (SQLException e) 
		{
			e.printStackTrace();
		}
		finally
		{
			ConnectLTDb.dbClose(conn, pstmt, rs);
		}*/
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
	public GroupNumber findMaxCountGroupnumber(PredictionRepository pre,List<GroupNumber> equallist,String smallYuanIssueId,String countTbName)
	{
		GroupNumber gmax = new GroupNumber();
		
		List<SrcFiveDataBean> newYuan = pre.getOriginData(smallYuanIssueId);
		//�ҳ��µ�����
		List<SrcFiveDataBean> flowbeans = pre.getFlowData(newYuan, App.nPlan);
		//�ҳ������Ӧ��ǰ��6�븴ʽ
		List<GroupNumber> list = this.changeFushiFromFlowData(flowbeans, 6,3);//6:ǰ��6�븴ʽ
		
		for (GroupNumber groupNumber : equallist) 
		{
//			groupNumber.setCount(0);
			if(this.listContainValue(list, groupNumber))
			{//������������������
//				updateTimesOfGnumber(groupNumber,countTbName);
				if(App.countMap.containsKey(groupNumber.getGroupNumber()))
				{
					int count =   Integer.parseInt(App.countMap.get(groupNumber.getGroupNumber()).toString())+ 1;
//					App.countMap.remove(groupNumber.getGroupNumber());
					App.countMap.put(groupNumber.getGroupNumber(), count);
					App.countMap = Maputil.sortByValue(App.countMap);//���³��ִ�������������
				}
			}
		}
		
		//�жϵ�ǰ����Ƿ������ͬ��������ͬ��Ҫ�����ж�
//		List<GroupNumber> newEquallist = this.getEqualListcount(equallist, App.liumatbName);
		List<GroupNumber> maxgroup = this.findMaxTimesGroup(10);//10:ȡ10�����ݣ���limit�Ĳ���
		//�鿴��������������Ƿ��г��ִ�����ͬ�����
		int countEqual = (int) this.judgeEqualCount(maxgroup,0).get("countEqual");//��ͬ������ִ���
		
		if(countEqual!=0)
		{//������ͬ��
			equallist.removeAll(equallist);
			for (int i=0;i<=countEqual;i++)
			{
				maxgroup.get(i).setCount(0);
				equallist.add(maxgroup.get(i));
			}
			if(flowbeans.size()>0)
			{
				gmax = this.findMaxCountGroupnumber(pre,equallist, newYuan.get(newYuan.size()-1).getIssueId(),App.liumatbName);
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
	}
	//�ж�list���Ƿ����gnumber
	public static boolean listContainValue(List<GroupNumber> list,GroupNumber gnumber)
	{
		boolean flag =  false;
		
		for (GroupNumber groupNumber : list) 
		{
			if(groupNumber.getGroupNumber().equals(gnumber.getGroupNumber()))
			{
				flag = true;
				break;
			}
				
		}
		
		return flag;
	}
	
	//��ȡ���ִ�����ͬ����ϵ�������
	public List<GroupNumber> getEqualListcount(List<GroupNumber> equallist,String countTbName)
	{
		List<GroupNumber> list = new ArrayList<GroupNumber>();
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Connection conn = ConnectLTDb.getConnection();
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT groupnumber,COUNT FROM T_LN_5IN11_LIUMANUMBER WHERE groupNumber IN (");
		
		for (int i=0;i<equallist.size();i++)
		{
			if(i==0)
			{
				sql.append("'"+equallist.get(i).getGroupNumber()+"'");
			}
			else
			{
				sql.append(",'"+equallist.get(i).getGroupNumber()+"'");
			}
		}
		
		sql.append(") ORDER BY COUNT DESC");
		
		 try 
		 {
			pstmt = (PreparedStatement)conn.prepareStatement(sql.toString());
			 rs = pstmt.executeQuery();
			 while(rs.next())
			 {
				 GroupNumber groupNumber = new GroupNumber();
				 groupNumber.setGroupNumber(rs.getString(1));
				 groupNumber.setCount(rs.getInt(2));
				 list.add(groupNumber);
				 
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
	    
		
		
		return list;
	}
	
	
	//���µ�ǰ��ϵĴ���
	public void updateTimesOfGnumber(GroupNumber groupNumber,String countTbName)
	{
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Connection conn = ConnectLTDb.getConnection();
		StringBuffer sql = new StringBuffer();
		
		sql.append("update "+countTbName+" set count=count+1 where groupNumber='"+groupNumber.getGroupNumber()+"'");
		
		try 
		{
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.executeUpdate();
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
	private void insertToDB(GroupNumber gMaxGroup)
	{
		//�ں��Ǵ���������ںŵ���һ��
		String nextIssue = App.getNextIssueByCurrentIssue(App.maxIssueId);
		PreparedStatement pstmt = null;
		Connection conn = ConnectLTDb.getConnection();
	    String sql = "insert into " + App.predictionTbName + " "
	    		+ "(issue_number,FUSHI,CREATE_TIME,PREDICTION_TYPE,EXPERT_ID,CYCLE,YUCE_ISSUE_START,YUCE_ISSUE_STOP) "
	    		+ "values(?,?,?,?,?,?,?,?)";
	    try
	    {
	    	pstmt = (PreparedStatement)conn.prepareStatement(sql);
	 	    pstmt.setString(1, nextIssue);
	 	    pstmt.setString(2, gMaxGroup.getGroupNumber());
	 	    pstmt.setTimestamp(3, new Timestamp(new Date().getTime()));
	 	    pstmt.setString(4, App.ptypeid);
	 	    pstmt.setString(5, App.beid);
	 	    pstmt.setString(6, App.nPlan);
	 	    pstmt.setString(7, gMaxGroup.getStartIssue());
	 	    pstmt.setString(8, gMaxGroup.getStopIssue());
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
	* @Title: judgeEqualCount 
	* @Description: TODO(�жϵ�ǰ�ͳ������������ִ�����ͬ���������) 
	* @param @param countlist
	* @param @param start
	* @param @return    �趨�ļ� 
	* @author banna
	* @date 2017��4��5�� ����3:00:07 
	* @return Map<String,Object>    �������� 
	* @throws
	 */
	public Map<String,Object> judgeEqualCount(List<GroupNumber> countlist,int start)
	{
		Map<String,Object> result = new HashMap<String,Object>();
		
		int count1  = countlist.get(start).getCount();//��ǰ���ִ�������������ϵĳ��ִ���
		int countEqual = 0;//������ͬ���ִ����ĺ������
		for(int i=start+1;i<countlist.size();i++)
		{
			if(countlist.get(i).getCount() == count1)
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
	
	/**
	 * ��Դ��ת��Ϊ���븴ʽ
	* @Title: changeFushiFromFlowData 
	* @Description: TODO(������һ�仰�����������������) 
	* @param @param flowbeans
	* @param @return    �趨�ļ� 
	* @author banna
	* @date 2017��4��5�� ����10:36:10 
	* @return List<String>    �������� 
	* @throws
	 */
	public List<GroupNumber> changeFushiFromFlowData(List<SrcFiveDataBean> flowbeans,int nmafushi,int qianNma)
	{
		List<GroupNumber> list = new ArrayList<GroupNumber>();
		
		for (SrcFiveDataBean bean : flowbeans) 
		{
			list.addAll(this.getQianNFushiFromBeans(bean, qianNma, 6,App.number));
		}
		
		
		return list;
	}
	
	public void updateTimes(List<GroupNumber> list,String tbName)
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
	
	
	/**
	 * ��ȡǰN��N�븴ʽ
	* @Title: getQianNFushiFromBeans 
	* @Description: TODO(������һ�仰�����������������) 
	* @param @param bean
	* @param @param nmafushi
	* @param @return    �趨�ļ� 
	* @author banna
	* @date 2017��4��5�� ����11:44:47 
	* @return List<GroupNumber>    �������� 
	* @throws
	 */
	public List<GroupNumber> getQianNFushiFromBeans(SrcFiveDataBean bean,int qianNma,int nmafushi,int number)//number:��ǰ�ĺ�������ֳ���
	{
		List<GroupNumber> list = new ArrayList<GroupNumber>();
		
		//ȡ��ǰN����
		List<String> qianNmastr = new ArrayList<String>();
		StringBuffer strqiansan = new StringBuffer();
		for(int i=1;i<=qianNma;i++)
		{
			qianNmastr.add(bean.numberMap.get("no"+i).toString());
			if(null != bean.numberMap.get("no"+i))
			{
				strqiansan.append(App.translate(Integer.parseInt(bean.numberMap.get("no"+i).toString())));
			}
		
		}
		
		int len = number-qianNma;//�����������Ϊ��ʽ��ϵĺ������
		//ɸѡ��������Ϊ��ϵĺ��룬�Ƴ�ǰN���������
		String[] couldArr = new String[len];
		int count =0;
		for(int i1=1;i1<=number;i1++)
		{
			boolean flag = true;
			for(int s=0;s<qianNmastr.size();s++)
			{
				if(qianNmastr.get(s).equals(i1+""))
				{
					flag = false;
				}
			}
			
			if(flag)
			{
				couldArr[count] = App.translate(i1);//������ת��ΪAJQ�ĸ�ʽ
				count++;
			}
		}
		
		//��ɸѡ�������N����ϣ�Ȼ���ǰN�������
		int nma = nmafushi - qianNma;//ʣ����Ҫ��ϵĸ�ʽλ��,eg:ǰ��6�븴ʽ����nma=6-3=3
		
		Map<String,Object> map = new HashMap<String,Object>();
		//ʹ��ɸѡ�������ɸ�ʽ
		List<GroupNumber> gefushi = this.generateFushi(nma, list, len, 1, couldArr,map);
		
		//����ǰbean���ɵ�ǰ�����븴ʽ����
		for (GroupNumber gNumber : list) 
		{
			gNumber.setGroupNumber(this.sortString(strqiansan.toString()+gNumber.getGroupNumber()));
			gNumber.setCount(0);
		}
		
		
		return list;
	}
	//���ַ�������
	private String sortString(String str)
	{
		char[] chars = str.toCharArray();
		Arrays.sort(chars);
		return new String(chars);
	}
	
	/**
	 * 
	* @Title: generateFushi 
	* @Description: TODO(������һ�仰�����������������) 
	* @param @param nma:��ϸ�ʽ��λ��
	* @param @param list
	* @param @param len��ȡֵ��Χ�����ָ���
	* @param @param ceng
	* @param @param couldArr��ȡֵ����
	* @param @param map
	* @param @return    �趨�ļ� 
	* @author banna
	* @date 2017��4��6�� ����9:10:05 
	* @return List<GroupNumber>    �������� 
	* @throws
	 */
	private List<GroupNumber> generateFushi(int nma,List<GroupNumber> list,int len,int ceng,
			String[] couldArr,Map<String,Object> map)
	{
		String a1 ;
		String a2 ;
		String a3 ;
		String a4 ;
		String a5 ;
		String a6 ;
		for(int i1=0;i1<len;i1++)
		{
			a1 = couldArr[i1];
			
			for(int i2=i1+1;i2<len;i2++)
			{
				a2 = couldArr[i2];
				
				if(nma == 2)
				{//���븴ʽ
					GroupNumber gnumber  = new GroupNumber();
					
					gnumber.setGroupNumber(a1+a2);
					
					list.add(gnumber);
				}
				else
				{
					for(int i3=i2+1;i3<len;i3++)
					{
						a3 = couldArr[i3];
						
						if(nma == 3)
						{//���븴ʽ
							GroupNumber gnumber  = new GroupNumber();
							
							gnumber.setGroupNumber(a1+a2+a3);
							
							list.add(gnumber);
						}
						else
						{
							for(int i4=i3+1;i4<len;i4++)
							{
								a4 = couldArr[i3];
								
								if(nma == 4)
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
	
	//��ȡn�ں���ں�
	public String getNextNIssuenumber(String issueNum,int nplan)
	{
		String nextNIssuenumber = issueNum;
		nplan = nplan-1;
		for(int i=0;i<nplan;i++)
		{
			if(i<nplan)
			{
				nextNIssuenumber = App.getNextIssueByCurrentIssue(nextNIssuenumber);
			}
		}
		System.out.println("nextNIssuenumber="+nextNIssuenumber);
		return  nextNIssuenumber;
	}
	
	//���µ�ǰԤ���׼ȷ��
	public void updateStatus()
	{
		System.out.println("Ԥ��ǰ�����븴ʽ׼ȷ��");
		//��ȡ���ڿ�������
		DataToDb dataToDb = new DataToDb();
		SrcFiveDataBean curIssue = dataToDb.getRecordByIssueCode(App.maxIssueId);
		List<String> numList = new ArrayList<String>();//ǰ����������
		numList.add(App.translate(curIssue.getNo1()));
		numList.add(App.translate(curIssue.getNo2()));
		numList.add(App.translate(curIssue.getNo3()));
		StringBuffer drownNumber = new StringBuffer();
		for (String string : numList) 
		{
			drownNumber.append(string);
		}
		FushiYuce fushiYuce = dataToDb.getQiansanLiuFushiYuceRecordByIssueNumber(curIssue.getIssueId(), App.predictionTbName);
		int fushiYes = 0;
		String fushiStatus="0";
		char[] fushiArr = fushiYuce.getFUSHI().toCharArray();
		for (String kjNum : numList) 
		{
			for(char yuce:fushiArr)
			{
				if(kjNum.equals(yuce+""))
				{
					fushiYes++;
					break;
				}
			}
		}
		//���ر���Ƿ�ִ��Ԥ��
		boolean yuceNextFlag = false;
		
		if(fushiYes == 3)
		{//��ǰ���������붼������Ԥ�����У�����Ӧ����3
			fushiStatus="1";
			yuceNextFlag = true;//����ǰԤ���г�����Ҫ������һ���ڵ�Ԥ��
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
					+ " STATUS="+fushiStatus+", "
					+ " DROWN_NUMBER='"+drownNumber+"', "
					+ " ISSUE_NUMBER='"+App.maxIssueId+"' "//�н��ں�||�ƻ����һ���ں�
					+ " where"
					+ " ID='"+fushiYuce.getID()+"'");
			
			pstmt.executeUpdate(sql.toString());
			
			//3.�ж�ר�Ҹ���ָ��Ԥ��׼ȷ��
			//ȡ����ǰ�ڵ�Ԥ������Ȼ��ȡ��������ĸ�ר������Ԥ������Ȼ��ȡ��Ԥ��׼ȷ����������Ȼ����б������㣬�ٰѽ���������ݿ�
			int limitnumber = App.orderRule;
			double countAll = limitnumber;//all
			double countZJ = dataToDb.getCountOfexpertprediction("STATUS", false,limitnumber);//��ȡ�н���ǰ������Ԥ��
			double dudanZJL = countZJ/countAll;
			
			//����Ԥ�⵽��ǰ��Ϊֹ��ר�ҵ��н�����
			StringBuffer sqlzjl =new StringBuffer();
			sqlzjl.append("update "+App.predictionTbName+" set "
					+ " WIN_RATE="+dudanZJL+" "
					+ " where"
					+ " ID='"+fushiYuce.getID()+"'");
			
			pstmt.executeUpdate(sqlzjl.toString());
			
			
			//TODO:�����1�������н����жϵ�ǰר���Ƿ��շ�
			
			//�ж��Ƿ�Ԥ����һ��(�������г������ߵ��ڵ��ں���Ԥ��ƻ������һ�ڣ���Ҫ����Ԥ��)
			if(yuceNextFlag || App.maxIssueId.equals(fushiYuce.getYUCE_ISSUE_STOP()))
			{
				//�ж��������н�����Ԥ����һ��
				PredictionRepository pre = new PredictionRepository();
				List<SrcFiveDataBean> yuanBeans = pre.getOriginData(null);
				this.execQSLiumaFushi(yuanBeans);
			}
			else
			{//��û�г����Ҽƻ�δ���ڣ������
				/*GroupNumber nextgnumber = new GroupNumber();
				this.insertToDB(nextgnumber);*/
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
