package com.byl.forcast.danma;

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
import java.util.Random;

import com.byl.forcast.App;
import com.byl.forcast.ConnectLTDb;
import com.byl.forcast.DataToDb;
import com.byl.forcast.FiveInCount;
import com.byl.forcast.PredictionRepository;
import com.byl.forcast.SrcFiveDataBean;
import com.mysql.jdbc.PreparedStatement;

public class ExecDanma 
{
	//预测前三胆码杀码
	public void execDanma(List<SrcFiveDataBean> yuanBeans)
	{
		//根据源码获取流码
		PredictionRepository pre = new PredictionRepository();
		List<SrcFiveDataBean> flowbeans = new ArrayList<SrcFiveDataBean>();
		flowbeans = pre.getFlowData(yuanBeans, App.nPlan);
		
		//根据流码进行预测
		int[] count = new int[App.number];
		
		for (SrcFiveDataBean bean : flowbeans) 
		{
			int[] numIntArr = {bean.getNo1(),bean.getNo2(),bean.getNo3()};
				
			for (int i : numIntArr)
			{
				count[i - 1] += 1;
			}
		}
		
		//统计前三号码开奖次数
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
		//1.判断是否有和独胆出现次数相同的数字
		int countEqual = (int) this.judgeEqualCount(countlist,0).get("countEqual");//相同号码出现次数
		if(countEqual != 0)
		{//有相同出现次数的号码
//			int[] dudanArr = new int[countEqual];
			List<Integer> dudanArr = new ArrayList<Integer>();
			for(int s = 0;s<=countEqual;s++)
			{
//				dudanArr[s] = countlist.get(s).getNumber();//获取和独胆出现次数相同的号码
				dudanArr.add(countlist.get(s).getNumber());
			}
			//获取源码
			List<SrcFiveDataBean> newYuan = pre.getOriginData(yuanBeans.get(yuanBeans.size()-1).getIssueId());
			//找出新的流码
			flowbeans = pre.getFlowData(newYuan, App.nPlan);
			//调用方法获取胆码
			danList = this.findDanma(dudanArr, flowbeans,2,pre,yuanBeans.get(yuanBeans.size()-1).getIssueId(),danList);
//			inserToDb(danList);
		}
		else
		{//
			danList.add(countlist.get(0));//放入独胆数据
			
			int ciCountEqual = (int) this.judgeEqualCount(countlist,1).get("countEqual");//次胆相同号码出现次数
			
			if(ciCountEqual != 0)
			{
//				int[] cidanArr = new int[ciCountEqual];
				List<Integer> cidanArr = new ArrayList<Integer>();
				for(int s = 1;s <=ciCountEqual+1;s++)
				{
//					cidanArr[s-1] = countlist.get(s).getNumber();//获取和独胆出现次数相同的号码
					cidanArr.add(countlist.get(s).getNumber());
				}
				//获取源码
				List<SrcFiveDataBean> newYuan = pre.getOriginData(yuanBeans.get(yuanBeans.size()-1).getIssueId());
				//找出新的流码
				flowbeans = pre.getFlowData(newYuan, App.nPlan);
				//调用方法获取胆码
				List<FiveInCount> cidanlist =  new ArrayList<FiveInCount>();
				cidanlist = this.findDanma(cidanArr, flowbeans,1,pre,yuanBeans.get(yuanBeans.size()-1).getIssueId(),cidanlist);//次胆有相同次数只需要找一个次胆号码即可
				
				danList.add(cidanlist.get(0));//向胆码list中添加次胆数据
			}
			else
			{
				danList.add(countlist.get(1));//放入次胆数据
			}
		}
		
		//杀码统计
		List<FiveInCount> shalist = new ArrayList<FiveInCount>();
		int countShaEqual = (int) this.judgeShaEqualCount(countlist,countlist.size()).get("countEqual");//相同号码出现次数
		if(countShaEqual != 0 && countShaEqual > 2)
		{//有相同出现次数的号码
//			int[] shamaArr = new int[countShaEqual];
			List<Integer> shamaArr = new ArrayList<Integer>();
			for(int s = countlist.size()-1;s>=countlist.size()-countShaEqual;s--)
			{
				shamaArr.add(countlist.get(s).getNumber());
//				shamaArr[s] = countlist.get(s).getNumber();//获取和独胆出现次数相同的号码
			}
			//获取源码
			List<SrcFiveDataBean> newYuan = pre.getOriginData(yuanBeans.get(yuanBeans.size()-1).getIssueId());
			//找出新的流码
			flowbeans = pre.getFlowData(newYuan, App.nPlan);
			//调用方法获取胆码
			shalist = this.findShama(shamaArr, flowbeans,3,pre,yuanBeans.get(yuanBeans.size()-1).getIssueId(),shalist);
//			inserToDb(danList);
		}
		else
		{
			shalist.add(countlist.get(countlist.size()-1));//放入杀一码数据
			shalist.add(countlist.get(countlist.size()-2));//放入杀二码数据
			countShaEqual = (int) this.judgeShaEqualCount(countlist,countlist.size()-2).get("countEqual");//杀三码相同号码出现次数
			if(countShaEqual != 0)
			{
//					int[] shasanArr = new int[countShaEqual];
				List<Integer> shasanArr = new ArrayList<Integer>();
				int size = countlist.size();
				int s1 = 0;
				for(int s = size-1-2;s>size-1-2-countShaEqual;s--)//开始位置是杀三码开始的位置
				{
//						shasanArr[s1] = countlist.get(s).getNumber();//获取和独胆出现次数相同的号码
//						s1++;
					shasanArr.add(countlist.get(s).getNumber());
				}
				//获取源码
				List<SrcFiveDataBean> newYuan = pre.getOriginData(yuanBeans.get(yuanBeans.size()-1).getIssueId());
				//找出新的流码
				flowbeans = pre.getFlowData(newYuan, App.nPlan);
				//调用方法获取胆码
				List<FiveInCount> shasanlist = new ArrayList<FiveInCount>();
				shasanlist = this.findShama(shasanArr, flowbeans,1,pre,
						yuanBeans.get(yuanBeans.size()-1).getIssueId(),shasanlist);//杀三码
				
				shalist.add(shasanlist.get(0));//向胆码list中添加次胆数据
			}
			else
			{
				shalist.add(countlist.get(countlist.size()-1-2));//放入杀三码
			}
		
		}
		
		//插入数据库
		inserToDb(danList,shalist);
		
	}
	
	//查找胆码
	private List<FiveInCount> findDanma(List<Integer> duArr,List<SrcFiveDataBean> flowData,int dancount,
			PredictionRepository pre,String smallIssueId,List<FiveInCount> list)//dancount:获取胆码个数
	{
//		List<FiveInCount> list = new ArrayList<FiveInCount>();
		
		for (SrcFiveDataBean bean : flowData) 
		{
			//取出号码转换为AJQ格式字符串
			StringBuffer flowstr = new StringBuffer(App.translate(bean.getNo1()));
			flowstr.append(App.translate(bean.getNo2()));
			flowstr.append(App.translate(bean.getNo3()));
			
			
			boolean flag = false;//判断当期流码是否包含一个胆码待选，包含一个是true
			List<Integer> newarr = new ArrayList<Integer>();//新筛选出的数字
			
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
						{//如果之前已经有加入的新筛选数字，则当期流码中存在多个胆码待选，则要继续使用流码筛选
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
					duArr.remove(newarr.get(0));//移除已经筛选出的胆码
					
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
					break;//已获取到独胆和次胆号码，结束流码循环
				}
			}
			else
			{//重置胆码筛选
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
			//获取源码
			List<SrcFiveDataBean> newYuan = pre.getOriginData(smallIssueId);
			//找出新的流码
			List<SrcFiveDataBean> flowbeans = pre.getFlowData(newYuan, App.nPlan);
			if(flowbeans.size() == 0)
			{//如果已经找不到符合条件的流码，则默认返回现在筛选不出的数据
				for (Integer duint : duArr) 
				{
					FiveInCount count = new FiveInCount();
					count.setNumber(duint);
					list.add(count);
				}
			}
			else
			{
				list = this.findDanma(duArr, flowbeans, dancount, pre, newYuan.get(newYuan.size()-1).getIssueId(),list);
			}
			
		}
		
		return list;
	}
	
	//查找杀码
	private List<FiveInCount> findShama(List<Integer> shaArr,List<SrcFiveDataBean> flowData,int shacount,
			PredictionRepository pre,String smallIssueId,List<FiveInCount> list)//shacount:获取杀码个数
	{
//		List<FiveInCount> list = new ArrayList<FiveInCount>();
		
		
		List<Integer> linshi = null;
		for (SrcFiveDataBean bean : flowData) 
		{
			//取出号码转换为AJQ格式字符串
			StringBuffer flowstr = new StringBuffer(App.translate(bean.getNo1()));
			flowstr.append(App.translate(bean.getNo2()));
			flowstr.append(App.translate(bean.getNo3()));
			
			boolean flag = true;//判断当期流码是否不包含一个杀码待选，包含一个是true
			List<Integer> newarr = new ArrayList<Integer>();//新筛选出的数字
			
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
			{//有杀码待选出现在list中,则移除
				if(shaArr.size() == shacount)
				{//若杀码待选数组的长度与要获取的杀码个数相同，则跳出循环
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
					{//移除出现的数字
						linshi = new ArrayList<Integer>();
						//备份筛选号码，如果全部移除则要重新赋值
						for (Integer shalin : shaArr) {
							linshi.add(shalin);
						}
						for (Integer integer : newarr) 
						{
							shaArr.remove(integer);
						}
						if(shaArr.size()<shacount && shaArr.size()!=0)//
						{
							for (Integer integer : shaArr) 
							{
								FiveInCount fcount = new FiveInCount();
								fcount.setNumber(integer);
								list.add(fcount);
							}
							
							//将剩余的号码继续给数组去判断
							for (Integer shaint : shaArr) 
							{
								linshi.remove(shaint);//从临时中移除已经确认为杀码的数字
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
									//因为所有待选的杀码都出现在这一期流码中，则重新比较
									shaArr = linshi;
								}
					}
					else
					{
						if(list.size()<shacount)//还需要筛选数据
						{
							linshi = new ArrayList<Integer>();
							//备份筛选号码，如果全部移除则要重新赋值
							for (Integer shalin : shaArr) {
								linshi.add(shalin);
							}
							for (Integer integer : newarr) 
							{
								shaArr.remove(integer);
							}
							
							if((shaArr.size()+list.size())<=shacount)//若符合条件的杀码个数和已经筛选出的杀码个数的和是要求获取杀码的数量，则直接将剩余杀码存储返回值
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
								//将剩余的号码继续给数组去判断
								for (Integer shaint : shaArr) 
								{
									linshi.remove(shaint);//从临时中移除已经确认为杀码的数字
								}
								shaArr = linshi;
							}
							
							
						}
					}
				}
			}
			
		}
		if(list.size()<shacount)
		{//没有取到足够的杀码
			//获取源码
			List<SrcFiveDataBean> newYuan = pre.getOriginData(smallIssueId);
			//找出新的流码
			List<SrcFiveDataBean> flowbeans = pre.getFlowData(newYuan, App.nPlan);
			
			if(flowbeans.size()==0)
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
				this.findShama(shaArr, flowbeans, shacount,pre,newYuan.get(newYuan.size()-1).getIssueId(),list);
			}
			
		}
		
		
		return list;
	}
	
	/**
	 * 判断统计结果中相同次数的号码
	* @Title: judgeEqualCount 
	* @Description: TODO(这里用一句话描述这个方法的作用) 
	* @param @param countlist
	* @param @return    设定文件 
	* @author banna
	* @date 2017年3月31日 上午9:29:12 
	* @return Map<String,Object>    返回类型 
	* @throws
	 */
	private Map<String,Object> judgeEqualCount(List<FiveInCount> countlist,int start)
	{
		Map<String,Object> result = new HashMap<String,Object>();
		
		int count1  = countlist.get(start).getCount1();//当前出现次数第一位的号码的出现次数
		int countEqual = 0;//出现相同出现次数的号码个数
		for(int i=start+1;i<countlist.size();i++)
		{
			if(countlist.get(i).getCount1() == count1)
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
	//判断杀码相同出现次数的号码
	private Map<String,Object> judgeShaEqualCount(List<FiveInCount> countlist,int end)
	{
		Map<String,Object> result = new HashMap<String,Object>();
		
		int count1  = countlist.get(end-1).getCount1();//当前出现次数第一位的号码的出现次数
		int countEqual = 0;//出现相同出现次数的号码个数
		for(int i=end-1 ;i >=0; i--)
		{
			if(countlist.get(i).getCount1() == count1)
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
	
	//插入胆码预测结果到数据库
	private void inserToDb(List<FiveInCount> danlist,List<FiveInCount> shalist)
	{
		//期号是代码中最大期号的下一期
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
	
	//更新当前预测的准确率
	public void updateDanAndShaStatus()
	{
		//获取当期开奖号码
		DataToDb dataToDb = new DataToDb();
		SrcFiveDataBean curIssue = dataToDb.getRecordByIssueCode(App.maxIssueId);
		List<String> numList = new ArrayList<String>();
		numList.add(App.translate(curIssue.getNo1()));
		numList.add(App.translate(curIssue.getNo2()));
		numList.add(App.translate(curIssue.getNo3()));
		StringBuffer drownNumber = new StringBuffer();
		for (String string : numList) 
		{
			drownNumber.append(string);
		}
		//1.判断胆码中奖率
		//1.获取胆码
		DanmaYuce danmaYuce = dataToDb.getYuceRecordByIssueNumber(curIssue.getIssueId(), App.predictionTbName);
		String dudanstatus = "0";//0:未中，1：正确
		String shuangdanstatus = "0";//0:未中，1：正确
		String danstatus = "0";//0:未中，1：正确
		String dantwo[] = danmaYuce.getDANMA_TWO().split("");
		List<String> dantwolist = new ArrayList<String>();
		for (String str : dantwo)
		{
			dantwolist.add(str);
		}
		if(numList.contains(danmaYuce.getDANMA_ONE()))
		{//判断独胆是否中出，若中出，则双胆也中出
			dudanstatus = "1";
			shuangdanstatus = "1";
			//判断双胆第二码是否中出，若中出则全中
			dantwolist.remove(danmaYuce.getDANMA_ONE());//移除中出的独胆
			boolean flag = false;
			for (int i=0;i<dantwolist.size();i++) 
			{
				if(numList.contains(dantwolist.get(i)))
				{
					flag = true;
				}
				else
				{
					if(i != 0)
					{
						flag = false;
					}
				}
			}
			if(flag)
			{
				danstatus = "1";//双胆全部中出
			}
		}
		else
		{
			dantwolist.remove(danmaYuce.getDANMA_ONE());//移除中出的独胆
			boolean flag = false;
			for (int i=0;i<dantwolist.size();i++) 
			{
				if(numList.contains(dantwolist.get(i)))
				{
					flag = true;
				}
				else
				{
					if(i != 0)
					{
						flag = false;
					}
				}
			}
			if(flag)
			{
				shuangdanstatus = "1";//独胆未中，双胆中一则正确
			}
		}
		
		
		//2.判断杀码中奖率
		String shasan[] = danmaYuce.getSHAMA_TWO().split("");//杀三码
		String shaer[] = danmaYuce.getSHAMA_ONE().split("");//杀二码
		List<String> shasanlist = new ArrayList<String>();
		List<String> shaerlist = new ArrayList<String>();

		for (String ss : shasan) 
		{
			shasanlist.add(ss);
		}
		
		for (String ss1 : shaer) 
		{
			shaerlist.add(ss1);
		}
		
		String flagshasan = "1";//杀三码是否中出
		String flagshaer = "1";//杀二码是否中出
		for (String string : shasanlist) 
		{
			if(numList.contains(string))
			{
				flagshasan = "0";//只要包含一个前三开奖号码，则杀三码预测失败
				if(shaerlist.contains(string))
				{//若杀二码中也包含这个号码，则杀二也预测失败
					flagshaer = "0";
				}
				break;
			}
		}
		if(flagshaer.equals("1"))
		{
			for (String string : shaerlist) 
			{
				if(numList.contains(string))
				{
					flagshaer = "0";//只要包含一个前三开奖号码，则杀二码预测失败
					break;
				}
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
					+ " DUDAN_STATUS="+dudanstatus+" ,"
					+ " SHUANGDAN_STATUS="+shuangdanstatus+" ,"
					+ " DANMA_STATUS="+danstatus+" ,"
					+ " SHAMAER_STATUS="+flagshaer+" ,"
					+ " SHAMASAN_STATUS="+flagshasan+" ,"
					+ " DROWN_NUMBER='"+drownNumber+"' "
					+ " where"
					+ " ISSUE_NUMBER="+App.maxIssueId+" and "
					+ " EXPERT_ID='"+App.beid+"' and "
					+ " PREDICTION_TYPE='"+App.ptypeid+"' ");
			
			pstmt.executeUpdate(sql.toString());
			
			//3.判断专家各个指标预测准确率
			//取出当前期的预测结果，然后取出待计算的该专家所有预测结果，然后取出预测准确的数据量，然后进行比例计算，再把结果存入数据库
			//1)计算胆码准确率
			int limitnumber = App.orderRule;
			double countAll = limitnumber;//all
			double countZJ = dataToDb.getCountOfexpertprediction("DUDAN_STATUS", false,limitnumber);//获取中奖的独胆
			double dudanZJL = countZJ/countAll;
			//2)计算双胆准确率
			countZJ = dataToDb.getCountOfexpertprediction("SHUANGDAN_STATUS", false,limitnumber);//获取中奖的
			double shuangdanZJL = countZJ/countAll;
			//3)计算双胆全对准确率
			countAll = limitnumber;//all
			countZJ = dataToDb.getCountOfexpertprediction("DANMA_STATUS", false,limitnumber);//获取中奖的
			double danmaZJL = countZJ/countAll;
			
			//4)计算杀二码全对准确率
			countAll = limitnumber;//all
			countZJ = dataToDb.getCountOfexpertprediction("SHAMAER_STATUS", false,limitnumber);//获取中奖的
			double shaerZJL = countZJ/countAll;
			//5)计算杀三码全对准确率
			countAll = limitnumber;//all
			countZJ = dataToDb.getCountOfexpertprediction("SHAMASAN_STATUS", false,limitnumber);//获取中奖的
			double shasanZJL = countZJ/countAll;
			
			
			//TODO:待完成：根据中奖率判断当前专家是否收费（前三胆码，杀码）
			//前三胆码：L1（独胆对7，双胆对9或杀二对9或杀三对8）
			//前三胆码：L2（独胆对6，双胆对8或杀二对8或杀三对6,7）
			//前三胆码：L3（独胆对4,5，双胆对7或杀二对7或杀三对5）
			String expertLevel = "4";
			String money = "0";//收费钱数
			String isCharge = "0";//是否收费
			Random rand = new Random();
			if(dudanZJL>=0.7 || shuangdanZJL>=0.9 || shaerZJL>=0.9 || shasanZJL>=0.8 )
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
				if(dudanZJL>=0.6 || shuangdanZJL>=0.8 || shaerZJL>=0.8 || shasanZJL>=0.6 )
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
					if(dudanZJL>=0.4 || shuangdanZJL>=0.7 || shaerZJL>=0.7 || shasanZJL>=0.5 )
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
					+ " WIN_RATE_DUDAN="+dudanZJL+" ,"
					+ " WIN_RATE_SHUANGDAN="+shuangdanZJL+" ,"
					+ " WIN_RATE_DANMA="+danmaZJL+" ,"
					+ " WIN_RATE_SHAER="+shaerZJL+" ,"
					+ " WIN_RATE_SHASAN="+shasanZJL+" "
					+ " where"
					+ " ISSUE_NUMBER="+App.maxIssueId+" and "
					+ " EXPERT_ID='"+App.beid+"' and "
					+ " PREDICTION_TYPE='"+App.ptypeid+"' ");
			
			pstmt.executeUpdate(sqlzjl.toString());
				
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
