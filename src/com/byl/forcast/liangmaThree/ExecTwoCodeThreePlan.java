package com.byl.forcast.liangmaThree;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.byl.forcast.App;
import com.byl.forcast.ConnectLTDb;
import com.byl.forcast.DataToDb;
import com.byl.forcast.GroupNumber;
import com.byl.forcast.Maputil;
import com.byl.forcast.PredictionRepository;
import com.byl.forcast.SrcFiveDataBean;
import com.byl.forcast.qianSLFushi.ExecQSLiumaFushi;
import com.byl.forcast.qianSLFushi.FushiYuce;
import com.byl.forcast.renSLGroup.ExecRensanTopSixgroup;

/**
 * �������ڼƻ��㷨
* @Description: TODO(������һ�仰�����������������) 
* @author banna
* @date 2017��4��6�� ����4:29:46
 */
public class ExecTwoCodeThreePlan 
{


	//Ԥ����ѡ����
	public void execLexuanFourPlan(List<SrcFiveDataBean> yuanBeans)
	{
		//����Դ���ȡ����
		PredictionRepository pre = new PredictionRepository();
		List<SrcFiveDataBean> flowbeans = new ArrayList<SrcFiveDataBean>();
		flowbeans = pre.getFlowData(yuanBeans, App.nPlan);
		
		ExecRensanTopSixgroup execRensanTopSixgroup = new ExecRensanTopSixgroup();
		ExecQSLiumaFushi execQSLiumaFushi = new ExecQSLiumaFushi();
		List<GroupNumber> list = execRensanTopSixgroup.fushiFromBeans(flowbeans,2);//��ȡ���븴ʽ
		
		//�Ƚ�ͳ�Ʊ����
		clearTongji();
		
		//���������ɵ����븴ʽ���µ�����ͳ�Ʊ���
		execQSLiumaFushi.updateTimes(list);
		
		//�ҳ����ִ�������һ��
		List<GroupNumber> maxgroup = execRensanTopSixgroup.findMaxTimesGroup(10);//10:ȡ10�����ݣ���limit�Ĳ���
		
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
			
			
			gMaxGroup = this.findMaxCountGroupnumber(pre,equallist, yuanBeans.get(yuanBeans.size()-1).getIssueId(),execRensanTopSixgroup);
			
		}
		else
		{
			gMaxGroup = maxgroup.get(0);
		}
	
		
		//��Ԥ�������뵽���ݿ���
		String nextIssue = App.getNextIssueByCurrentIssue(App.maxIssueId);
		String stopIssue = execQSLiumaFushi.getNextNIssuenumber(nextIssue, Integer.parseInt(App.nPlan));
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
	public GroupNumber findMaxCountGroupnumber(PredictionRepository pre,List<GroupNumber> equallist,String smallYuanIssueId,
			ExecRensanTopSixgroup execRensanTopSixgroup)
	{
		GroupNumber gmax = new GroupNumber();
		
		List<SrcFiveDataBean> newYuan = pre.getOriginData(smallYuanIssueId);
		//�ҳ��µ�����
		List<SrcFiveDataBean> flowbeans = pre.getFlowData(newYuan, App.nPlan);
		//�ҳ������Ӧ�����븴ʽ
		List<GroupNumber> list = execRensanTopSixgroup.fushiFromBeans(flowbeans, 2);
		
		for (GroupNumber groupNumber : equallist) 
		{
			if(this.listContainValue(list, groupNumber))
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
		List<GroupNumber> maxgroup = execRensanTopSixgroup.findMaxTimesGroup(10);//10:ȡ10�����ݣ���limit�Ĳ���
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
				gmax = this.findMaxCountGroupnumber(pre,equallist, newYuan.get(newYuan.size()-1).getIssueId(),execRensanTopSixgroup);
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
	 	    System.out.println(gMaxGroup.getGroupNumber());
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
	
	
	
	
	
	
	//���µ�ǰԤ���׼ȷ��
	public void updateStatus()
	{
		System.out.println("Ԥ����������׼ȷ��");
		//��ȡ���ڿ�������
		DataToDb dataToDb = new DataToDb();
		SrcFiveDataBean curIssue = dataToDb.getRecordByIssueCode(App.maxIssueId);
		List<String> numList = new ArrayList<String>();//ǰ����������
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
		FushiYuce fushiYuce = dataToDb.getQiansanLiuFushiYuceRecordByIssueNumber(curIssue.getIssueId(), App.predictionTbName);
		int fushiYes = 0;
		String fushiStatus="0";
		char[] fushiArr = fushiYuce.getFUSHI().toCharArray();
		
		for(char yuce:fushiArr)
		{
			for (String kjNum : numList) 
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
		if(fushiYes == 2)
		{//������������������붼������Ԥ�����У�����Ӧ����2
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
				this.execLexuanFourPlan(yuanBeans);
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
