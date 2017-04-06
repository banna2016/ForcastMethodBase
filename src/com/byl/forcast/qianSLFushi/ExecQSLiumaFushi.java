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
import java.util.List;
import java.util.Map;

import com.byl.forcast.App;
import com.byl.forcast.ConnectLTDb;
import com.byl.forcast.DataToDb;
import com.byl.forcast.FiveInCount;
import com.byl.forcast.GroupNumber;
import com.byl.forcast.PredictionRepository;
import com.byl.forcast.SrcFiveDataBean;
import com.byl.forcast.danma.DanmaYuce;

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
		clearTongji(App.liumatbName);
		
		//���������ɵ�ǰ�����븴ʽ���µ�����ͳ�Ʊ���
		this.updateTimes(list, App.liumatbName);
		
		//�ҳ����ִ�������һ��
		List<GroupNumber> maxgroup = this.findMaxTimesGroup(App.liumatbName,10);//10:ȡ10�����ݣ���limit�Ĳ���
		
		//�鿴��������������Ƿ��г��ִ�����ͬ�����
		int countEqual = (int) this.judgeEqualCount(maxgroup,0).get("countEqual");//��ͬ������ִ���
		GroupNumber gMaxGroup = new GroupNumber();//�����ִ��������
		if(countEqual != 0)
		{
			List<GroupNumber> equallist = new ArrayList<GroupNumber>();
			for (int i=0;i<countEqual;i++)
			{
				equallist.add(maxgroup.get(i));
			}
			
			
			gMaxGroup = this.findMaxCountGroupnumber(pre,equallist, yuanBeans.get(yuanBeans.size()-1).getIssueId(),App.liumatbName);
			
		}
		else
		{
			gMaxGroup = maxgroup.get(0);
		}
	
		
		//��Ԥ�������뵽���ݿ���
		insertToDB(gMaxGroup);
		
		
		
		
	}
	//��ͳ�Ʊ��count��0
	private void clearTongji(String tbName)
	{
		PreparedStatement pstmt = null;
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
		}
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
			groupNumber.setCount(0);
			if(list.contains(groupNumber))
			{//������������������
				updateTimesOfGnumber(groupNumber,countTbName);
			}
		}
		
		//�жϵ�ǰ����Ƿ������ͬ��������ͬ��Ҫ�����ж�
		List<GroupNumber> newEquallist = this.getEqualListcount(equallist, App.liumatbName);
		int countEqual = (int) this.judgeEqualCount(newEquallist,0).get("countEqual");//��ͬ������ִ���
		if(countEqual!=0)
		{//������ͬ��
			equallist.removeAll(equallist);
			for (int i=0;i<countEqual;i++)
			{
				equallist.add(newEquallist.get(i));
			}
			
			
			gmax = this.findMaxCountGroupnumber(pre,equallist, newYuan.get(newYuan.size()-1).getIssueId(),App.liumatbName);
			
		}
		
		return gmax;
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
	    		+ "(issue_number,FUSHI,CREATE_TIME,PREDICTION_TYPE,EXPERT_ID) "
	    		+ "values(?,?,?,?,?)";
	    try
	    {
	    	pstmt = (PreparedStatement)conn.prepareStatement(sql);
	 	    pstmt.setString(1, nextIssue);
	 	    pstmt.setString(2, gMaxGroup.getGroupNumber());
	 	    pstmt.setTimestamp(3, new Timestamp(new Date().getTime()));
	 	    pstmt.setString(4, App.ptypeid);
	 	    pstmt.setString(5, App.beid);
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
	private Map<String,Object> judgeEqualCount(List<GroupNumber> countlist,int start)
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
	public List<GroupNumber> findMaxTimesGroup(String tbName,int n)
	{
		List<GroupNumber> list = new ArrayList<GroupNumber>();
		
		Connection con = ConnectLTDb.getConnection();
		PreparedStatement pstmt = null;
		StringBuffer sql = new StringBuffer("SELECT groupnumber,COUNT FROM "+tbName+" ORDER BY COUNT DESC LIMIT "+n);
		 ResultSet rs = null;
		try 
		{
			 pstmt = (PreparedStatement)con.prepareStatement(sql.toString());
			rs = pstmt.executeQuery();
			 while (rs.next())
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
			ConnectLTDb.dbClose(con, pstmt, rs);
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
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Connection conn = ConnectLTDb.getConnection();
		StringBuffer sql = new StringBuffer();
		
		try 
		{
			pstmt = conn.prepareStatement("update "+tbName+" set count=count+1 where groupNumber=? ",
					ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
			conn.setAutoCommit(false);
			for (GroupNumber groupNumber : list) 
			{
				pstmt.setString(1, groupNumber.getGroupNumber());
				pstmt.addBatch();   
			}
			int[] t =pstmt.executeBatch(); 
			System.out.println(t.length);
			 conn.commit();
			 conn.setAutoCommit(true);//3,�ύ��ɺ�ظ��ֳ���Auto commit,��ԭΪtrue,   
		} 
		catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			 try {
				conn.rollback();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}  
		}
		finally
		{
			ConnectLTDb.dbClose(conn, pstmt, rs);
		}
		
		
		
		
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
	
	/**
	 * ����������С��������ת��ΪAJQ
	* @Title: changeSmallToBig 
	* @Description: TODO(������һ�仰�����������������) 
	* @param @param bean
	* @param @return    �趨�ļ� 
	* @author banna
	* @date 2017��4��5�� ����10:58:05 
	* @return String    �������� 
	* @throws
	 */
    public String changeSmallToBig(SrcFiveDataBean bean)
    {
    	String str = "";
    	
    	
    	
    	return str;
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
		
		if(fushiYes == 3)
		{//��ǰ���������붼������Ԥ�����У�����Ӧ����3
			fushiStatus="1";
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
					+ " STATUS="+fushiStatus+" "
					+ " where"
					+ " ISSUE_NUMBER="+App.maxIssueId+" and "
					+ " EXPERT_ID='"+App.beid+"' and "
					+ " PREDICTION_TYPE='"+App.ptypeid+"' ");
			
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
