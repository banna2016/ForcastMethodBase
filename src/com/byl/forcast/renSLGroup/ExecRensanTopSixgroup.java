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
 * 任三精选六组算法
* @Description: TODO(这里用一句话描述这个方法的作用) 
* @author banna
* @date 2017年4月6日 下午4:26:04
 */
public class ExecRensanTopSixgroup 
{

	//预测前三六码复式
	public void execRensanTopsixGroup(List<SrcFiveDataBean> yuanBeans)
	{
		//根据源码获取流码
		PredictionRepository pre = new PredictionRepository();
		List<SrcFiveDataBean> flowbeans = new ArrayList<SrcFiveDataBean>();
		flowbeans = pre.getFlowData(yuanBeans, App.nPlan);
		
		
		List<GroupNumber> list = this.fushiFromBeans(flowbeans,3);
		
		ExecQSLiumaFushi execQSLiumaFushi = new ExecQSLiumaFushi();
		//先将统计表清空
		execQSLiumaFushi.clearTongji();
		
		//将流码生成的任三号码更新到次数统计表中
		this.updateTimes(list);
		
		//找出出现次数最多的一组
		List<GroupNumber> maxgroup = this.findMaxTimesGroup(20);//10:取20条数据，是limit的参数
		
		List<GroupNumber> gSixMaxGroup = new ArrayList<GroupNumber>();//取出出现次数最多的6组
		for (int i=0;i<maxgroup.size();i++) 
		{
			if(i<6)
			{
				gSixMaxGroup.add(maxgroup.get(i));
			}
		}
		
		//将预测结果插入到数据库中
		insertToDB(gSixMaxGroup);
		
	}
	
	/**
	 * 获取流码对应的所有任三组合
	* @Title: fushiFromBeans 
	* @Description: TODO(这里用一句话描述这个方法的作用) 
	* @param @param flowbeans
	* @param @param fushi
	* @param @return    设定文件 
	* @author banna
	* @date 2017年4月6日 下午5:09:11 
	* @return List<GroupNumber>    返回类型 
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
	/*public GroupNumber findMaxCountGroupnumber(PredictionRepository pre,List<GroupNumber> equallist,String smallYuanIssueId,
			ExecQSLiumaFushi execQSLiumaFushi)
	{
		GroupNumber gmax = new GroupNumber();
		
		List<SrcFiveDataBean> newYuan = pre.getOriginData(smallYuanIssueId);
		//找出新的流码
		List<SrcFiveDataBean> flowbeans = pre.getFlowData(newYuan, App.nPlan);
		//找出流码对应的前三6码复式
		List<GroupNumber> list = this.fushiFromBeans(flowbeans, 3);
		
		for (GroupNumber groupNumber : equallist) 
		{
			if(ExecQSLiumaFushi.listContainValue(list, groupNumber))
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
		List<GroupNumber> maxgroup = this.findMaxTimesGroup(20);//20:取10条数据，是limit的参数
		//查看次数最多的组合中是否有出现次数相同的组合
		int countEqual = (int) execQSLiumaFushi.judgeEqualCount(maxgroup,5).get("countEqual");//相同号码出现次数
		
		if(countEqual!=0)
		{//还有相同的
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
			{//没有流码数据了，则默认取第一条为预测数据
				gmax = equallist.get(0);
			}
			
			
		}
		else
		{//没有流码数据了，则默认取第一条为预测数据
			gmax = maxgroup.get(0);
		}
		
		return gmax;
	}*/
	
	
	
	
	
	
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
	private void insertToDB(List<GroupNumber> gMaxGroup)
	{
		//期号是代码中最大期号的下一期
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
	 	    {//循环放入group1~group6的6组数据
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
	 * 找出出现次数最多的一组
	* @Title: findMaxTimesGroup 
	* @Description: TODO(这里用一句话描述这个方法的作用) 
	* @param @return    设定文件 
	* @author banna
	* @date 2017年4月5日 下午2:27:35 
	* @return List<GroupNumber>    返回类型 
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
		//将每个组合的出现次数统计在countMap中
		for (GroupNumber groupNumber : list) 
		{
			if(App.countMap.containsKey(groupNumber.getGroupNumber()))
			{
				int count =   Integer.parseInt(App.countMap.get(groupNumber.getGroupNumber()).toString())+ 1;
//				App.countMap.remove(groupNumber.getGroupNumber());
				App.countMap.put(groupNumber.getGroupNumber(), count);
			}
		}
		//将map排序
		App.countMap=Maputil.sortByValue(App.countMap);
		
		
	}
	
	
	
	//将字符串排序
	private String sortString(String str)
	{
		char[] chars = str.toCharArray();
		Arrays.sort(chars);
		return new String(chars);
	}
	
	/**
	 * 根据开奖号码将转换为n组复式
	* @Title: getFushiFromBeans 
	* @Description: TODO(这里用一句话描述这个方法的作用) 
	* @param @param bean
	* @param @return    设定文件 
	* @author banna
	* @date 2017年4月5日 上午10:38:16 
	* @return List<String>    返回类型 
	* @throws
	 */
	public List<GroupNumber> getFushiFromBeans(SrcFiveDataBean bean,int nmafushi)//nmafushi：获取n码复式，两码复式传参：2
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
				{//两码复式
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
						{//三码复式
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
								{//四码复式
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
	 * 将开奖号码排序，并且转换为AJQ
	* @Title: sortNumber 
	* @Description: TODO(这里用一句话描述这个方法的作用) 
	* @param @param bean
	* @param @return    设定文件 
	* @author banna
	* @date 2017年4月5日 上午11:10:23 
	* @return String    返回类型 
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
	
	
	//更新当前预测的准确率
	public void updateStatus()
	{
		System.out.println("预测任三精选6组准确率");
		//获取当期开奖号码
		DataToDb dataToDb = new DataToDb();
		SrcFiveDataBean curIssue = dataToDb.getRecordByIssueCode(App.maxIssueId);
		List<String> numList = new ArrayList<String>();//开奖号码
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
		//获取预测内容
		RenSanGroup renSanGroup = dataToDb.getRensanSixGroupYuceRecordByIssueNumber(curIssue.getIssueId(), App.predictionTbName);
	
		int status = 0;//当前中出的组数
		int checkint = 0;
		for(int i=1;i<=6;i++)
		{
			//取出号码
				char[] rr = renSanGroup.map.get("group"+i).toCharArray();
				for(char yuce:rr)
				{
					for (String kjNum : numList) 
					{
						if(kjNum.equals(yuce+""))
						{
							checkint++;//一个号码相等
							break;
						}
					}
					
				}
				
				if(checkint == 3)
				{//预测号码中三个号码全在开奖号码中
					status++;
				}
				checkint = 0;//重置统计为0
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
					+ " STATUS='"+(status>0?1:0)+"' ,"
					+ " ZJGROUPS='"+status+"',"
					+ " DROWN_NUMBER='"+drownNumber+"' "
					+ " where"
					+ " ISSUE_NUMBER="+App.maxIssueId+" and "
					+ " EXPERT_ID='"+App.beid+"' and "
					+ " PREDICTION_TYPE='"+App.ptypeid+"' ");
			
			pstmt.executeUpdate(sql.toString());
			
			//3.判断专家各个指标预测准确率
			//取出当前期的预测结果，然后取出待计算的该专家所有预测结果，然后取出预测准确的数据量，然后进行比例计算，再把结果存入数据库
			int limitnumber = App.orderRule;
			double countAll = limitnumber;//all
			double countZJ = dataToDb.getCountOfexpertprediction("STATUS", false,limitnumber);//获取中奖的任三6组
			double dudanZJL = countZJ/countAll;
			
			//更新预测到当前期为止该专家的中奖几率
			StringBuffer sqlzjl =new StringBuffer();
			sqlzjl.append("update "+App.predictionTbName+" set "
					+ " WIN_RATE="+dudanZJL+" "
					+ " where"
					+ " ISSUE_NUMBER="+App.maxIssueId+" and "
					+ " EXPERT_ID='"+App.beid+"' and "
					+ " PREDICTION_TYPE='"+App.ptypeid+"' ");
			
			pstmt.executeUpdate(sqlzjl.toString());
			
			
			//TODO:待完成1：根据中奖率判断当前专家是否收费
			
			
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
