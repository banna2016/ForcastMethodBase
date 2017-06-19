package com.byl.forcast.leFour;

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
import java.util.Random;

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
 * 乐享四期计划算法
* @Description: TODO(这里用一句话描述这个方法的作用) 
* @author banna
* @date 2017年4月6日 下午4:28:49
 */
public class ExecHappyFourPlan 
{

	//预测乐选四期
	public void execLexuanFourPlan(List<SrcFiveDataBean> yuanBeans)
	{
		//根据源码获取流码
		PredictionRepository pre = new PredictionRepository();
		List<SrcFiveDataBean> flowbeans = new ArrayList<SrcFiveDataBean>();
		flowbeans = pre.getFlowData(yuanBeans, App.nPlan);
		
		ExecRensanTopSixgroup execRensanTopSixgroup = new ExecRensanTopSixgroup();
		ExecQSLiumaFushi execQSLiumaFushi = new ExecQSLiumaFushi();
		List<GroupNumber> list = execRensanTopSixgroup.fushiFromBeans(flowbeans,4);//获取四码复式
		
		//先将统计表清空
		clearTongji();
		
		//将流码生成的前三六码复式更新到次数统计表中
		execQSLiumaFushi.updateTimes(list);
		
		//找出出现次数最多的一组
		List<GroupNumber> maxgroup = execRensanTopSixgroup.findMaxTimesGroup(10);//10:取10条数据，是limit的参数
		
		//查看次数最多的组合中是否有出现次数相同的组合
		int countEqual = (int) this.judgeEqualCount(maxgroup,0).get("countEqual");//相同号码出现次数
		GroupNumber gMaxGroup = new GroupNumber();//最多出现次数的组合
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
	
		
		//将预测结果插入到数据库中
		String nextIssue = App.getNextIssueByCurrentIssue(App.maxIssueId);
		String stopIssue = execQSLiumaFushi.getNextNIssuenumber(nextIssue, Integer.parseInt(App.nPlan));
		gMaxGroup.setStartIssue(nextIssue);
		gMaxGroup.setStopIssue(stopIssue);
		insertToDB(gMaxGroup);
		
	}
	//将统计表的count清0
	public void clearTongji()
	{
		for (String key : App.countMap.keySet()) {
			App.countMap.put(key, 0);
		}
			
	
	}
	
	/**
	 * 
	* @Title: findMaxCountGroupnumber 
	* @Description: TODO(筛选出现次数最多的组合) 
	* @param @param pre
	* @param @param equallist
	* @param @param smallYuanIssueId
	* @param @return    设定文件 
	* @author banna
	* @date 2017年4月5日 下午3:15:16 
	* @return GroupNumber    返回类型 
	* @throws
	 */
	public GroupNumber findMaxCountGroupnumber(PredictionRepository pre,List<GroupNumber> equallist,String smallYuanIssueId,
			ExecRensanTopSixgroup execRensanTopSixgroup)
	{
		GroupNumber gmax = new GroupNumber();
		
		List<SrcFiveDataBean> newYuan = pre.getOriginData(smallYuanIssueId);
		//找出新的流码
		List<SrcFiveDataBean> flowbeans = pre.getFlowData(newYuan, App.nPlan);
		//找出流码对应的四码复式
		List<GroupNumber> list = execRensanTopSixgroup.fushiFromBeans(flowbeans, 4);
		
		for (GroupNumber groupNumber : equallist) 
		{
			if(this.listContainValue(list, groupNumber))
			{//若包含，则更新其次数
				if(App.countMap.containsKey(groupNumber.getGroupNumber()))
				{
					int count =   Integer.parseInt(App.countMap.get(groupNumber.getGroupNumber()).toString())+ 1;
					App.countMap.put(groupNumber.getGroupNumber(), count);
					App.countMap = Maputil.sortByValue(App.countMap);//更新出现次数后重新排序
				}
			}
		}
		
		//判断当前组合是否次数不同，若还相同则要继续判断
		List<GroupNumber> maxgroup = execRensanTopSixgroup.findMaxTimesGroup(10);//10:取10条数据，是limit的参数
		//查看次数最多的组合中是否有出现次数相同的组合
		int countEqual = (int) this.judgeEqualCount(maxgroup,0).get("countEqual");//相同号码出现次数
		
		if(countEqual!=0)
		{//还有相同的
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
			{//没有流码数据了，则默认取第一条为预测数据
				gmax = equallist.get(0);
			}
			
			
		}
		else
		{//没有流码数据了，则默认取第一条为预测数据
			gmax = maxgroup.get(0);
		}
		
		return gmax;
	}
	//判断list中是否包含gnumber
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
	 * 将预测结果插入到数据库中
	* @Title: insertToDB 
	* @Description: TODO(这里用一句话描述这个方法的作用) 
	* @param @param gMaxGroup    设定文件 
	* @author banna
	* @date 2017年4月5日 下午3:10:01 
	* @return void    返回类型 
	* @throws
	 */
	private void insertToDB(GroupNumber gMaxGroup)
	{
		//期号是代码中最大期号的下一期
		DataToDb dataToDb = new DataToDb();
		//获取上一期的预测结果，无论是不是周期内，开始新的预测都是因为上一轮预测结果已经结束
		FushiYuce fushiYuce = dataToDb.getQiansanLiuFushiYuceRecordByIssueNumber(App.maxIssueId, App.predictionTbName);
		
		String nextIssue = App.getNextIssueByCurrentIssue(App.maxIssueId);
		PreparedStatement pstmt = null;
		Connection conn = ConnectLTDb.getConnection();
	    String sql = "insert into " + App.predictionTbName + " "
	    		+ "(issue_number,FUSHI,CREATE_TIME,PREDICTION_TYPE,EXPERT_ID,CYCLE,YUCE_ISSUE_START,YUCE_ISSUE_STOP, "
	    		+ "EXPERT_LEVEL,IS_CHARGE,MONEY,WIN_RATE,ZJLEVEL) "
	    		+ "values(?,?,?,?,?,?,?,?,?,?,?,?,?)";
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
	 	    pstmt.setString(9, null != fushiYuce.getEXPERT_LEVEL()?fushiYuce.getEXPERT_LEVEL():"4");
	 	    pstmt.setString(10, null != fushiYuce.getIS_CHARGE()?fushiYuce.getIS_CHARGE():"0");
	 	    pstmt.setString(11, null != fushiYuce.getMONEY()?fushiYuce.getMONEY():"0");
	 	    pstmt.setDouble(12, null != fushiYuce.getWIN_RATE()?fushiYuce.getWIN_RATE():0);
	 	    pstmt.setString(13, null != fushiYuce.getZJLEVEL()?fushiYuce.getZJLEVEL():"0");
	 	    
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
	* @Description: TODO(判断当前和出现最多六码出现次数相同的六码组合) 
	* @param @param countlist
	* @param @param start
	* @param @return    设定文件 
	* @author banna
	* @date 2017年4月5日 下午3:00:07 
	* @return Map<String,Object>    返回类型 
	* @throws
	 */
	public Map<String,Object> judgeEqualCount(List<GroupNumber> countlist,int start)
	{
		Map<String,Object> result = new HashMap<String,Object>();
		
		int count1  = countlist.get(start).getCount();//当前出现次数最多的六码组合的出现次数
		int countEqual = 0;//出现相同出现次数的号码个数
		for(int i=start+1;i<countlist.size();i++)
		{
			if(countlist.get(i).getCount() == count1)
			{//与第一位数字出现次数相同
				countEqual++;
			}
			else
			{//若第二位都和第一位的出现次数不同，则不需要再进行比较
				break;
			}
		}
		result.put("countEqual", countEqual);
		return result;
	}
	
	
	
	
	
	
	//更新当前预测的准确率
	public void updateStatus()
	{
		System.out.println("预测乐选四期准确率");
		//获取当期开奖号码
		DataToDb dataToDb = new DataToDb();
		SrcFiveDataBean curIssue = dataToDb.getRecordByIssueCode(App.maxIssueId);
		List<String> numList = new ArrayList<String>();//前三开奖号码
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
		//返回标记是否执行预测
		boolean yuceNextFlag = false;
		String zjLevel = "0";//中几等奖
		if(fushiYes == 3)
		{//若前三开奖号码都存在于预测结果中，则结果应该是3
			fushiStatus="1";
			yuceNextFlag = true;//若当前预测中出，则要进行下一周期的预测
			switch(fushiYes)
			{
				case 3:zjLevel="2";break;
				case 4:zjLevel="1";break;
				default:zjLevel="0";break;
			}
		}
		
		//更新准确率到数据库
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
					+ " ZJLEVEL='"+zjLevel+"', "
					+ " ISSUE_NUMBER='"+App.maxIssueId+"' "//中奖期号||计划最后一期期号
					+ " where"
					+ " ID='"+fushiYuce.getID()+"'");
			
			pstmt.executeUpdate(sql.toString());
			
			//3.判断专家各个指标预测准确率
			//取出当前期的预测结果，然后取出待计算的该专家所有预测结果，然后取出预测准确的数据量，然后进行比例计算，再把结果存入数据库
			int limitnumber = App.orderRule;
			double countAll = limitnumber;//all
			double countZJ = dataToDb.getCountOfexpertprediction("STATUS", false,limitnumber);//获取中奖的前三六码预测
			double dudanZJL = countZJ/countAll;
			
			//TODO:待完成：根据中奖率判断当前专家是否收费（乐选4四期计划）
			//乐选4四期计划：L1（对10）
			//乐选4四期计划：L2（对9）
			//乐选4四期计划：L3（对8）
			String expertLevel = "4";
			String money = "0";//收费钱数
			String isCharge = "0";//是否收费
			Random rand = new Random();
			if(dudanZJL>=1.0 )
			{
				expertLevel = "1";
				//收费标准取值范围是（0,3,5,8）
				int[] moneyArr = {3,5,8};
				int randNum = rand.nextInt(moneyArr.length);
				money = moneyArr[randNum]+"";//随机收费钱数
				if(!"0".equals(money))
				{
					isCharge = "1";//收费
				}
				
			}
			else
				if(dudanZJL>=0.9)
				{
					expertLevel = "2";
					//收费标准取值范围是（0,3,5）
					int[] moneyArr = {0,3,5};
					int randNum = rand.nextInt(moneyArr.length);
					money = moneyArr[randNum]+"";//随机收费钱数
					if(!"0".equals(money))
					{
						isCharge = "1";//收费
					}
				}
				else
					if(dudanZJL>=0.8 )
					{
						expertLevel = "3";
						//收费标准取值范围是（0,3,5）
						int[] moneyArr = {0,3};
						int randNum = rand.nextInt(moneyArr.length);
						money = moneyArr[randNum]+"";//随机收费钱数
						if(!"0".equals(money))
						{
							isCharge = "1";//收费
						}
					}
			
			//更新预测到当前期为止该专家的中奖几率
			StringBuffer sqlzjl =new StringBuffer();
			sqlzjl.append("update "+App.predictionTbName+" set "
					+ " IS_CHARGE="+isCharge+" ,"
					+ " MONEY="+money+" ,"
					+ " EXPERT_LEVEL="+expertLevel+" ,"
					+ " WIN_RATE="+dudanZJL+" "
					+ " where"
					+ " ID='"+fushiYuce.getID()+"'");
			
			pstmt.executeUpdate(sqlzjl.toString());
			
			
			
			//判断是否预测下一期(若当期中出，或者当期的期号是预测计划的最后一期，则要继续预测)
			if(yuceNextFlag || App.maxIssueId.equals(fushiYuce.getYUCE_ISSUE_STOP()) 
					|| Integer.parseInt(App.maxIssueId)>Integer.parseInt(fushiYuce.getYUCE_ISSUE_STOP()))
			{
				//判断完这期中奖率再预测下一期
				PredictionRepository pre = new PredictionRepository();
				List<SrcFiveDataBean> yuanBeans = pre.getOriginData(null);
				this.execLexuanFourPlan(yuanBeans);
			}
			else
			{//若没中出，且计划未到期，则继续，将issuenumber更新为下一期的期号（即：issuenum如果中出或者是计划结束期则为当期期号，若为计划内则为下期期号）
				String nextIssue = App.getNextIssueByCurrentIssue(App.maxIssueId);
				StringBuffer upSql = new StringBuffer();
				pstmt = (PreparedStatement)conn.prepareStatement(upSql.toString());
				upSql.append("update "+App.predictionTbName+" set "
						+ " ISSUE_NUMBER='"+nextIssue+"' "//中奖期号||计划最后一期期号||计划内下一期的期号
						+ " where"
						+ " ID='"+fushiYuce.getID()+"'");
				
				pstmt.executeUpdate(upSql.toString());
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
