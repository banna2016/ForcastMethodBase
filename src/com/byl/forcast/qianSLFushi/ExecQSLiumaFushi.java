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
 * 运行前三六码复式预测
* @Description: TODO(这里用一句话描述这个方法的作用) 
* @author banna
* @date 2017年4月5日 上午9:13:33
 */
public class ExecQSLiumaFushi
{
	//预测前三六码复式
	public void execQSLiumaFushi(List<SrcFiveDataBean> yuanBeans)
	{
		//根据源码获取流码
		PredictionRepository pre = new PredictionRepository();
		List<SrcFiveDataBean> flowbeans = new ArrayList<SrcFiveDataBean>();
		flowbeans = pre.getFlowData(yuanBeans, App.nPlan);
		
		
		List<GroupNumber> list = this.changeFushiFromFlowData(flowbeans, 6,3);//6:前三6码复式
		
		//先将统计表清空
		clearTongji();
		
		//将流码生成的前三六码复式更新到次数统计表中
		this.updateTimes(list, App.liumatbName);
		
		//找出出现次数最多的一组
		List<GroupNumber> maxgroup = this.findMaxTimesGroup(10);//10:取10条数据，是limit的参数
		
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
			
			
			gMaxGroup = this.findMaxCountGroupnumber(pre,equallist, yuanBeans.get(yuanBeans.size()-1).getIssueId(),App.liumatbName);
			
		}
		else
		{
			gMaxGroup = maxgroup.get(0);
		}
	
		
		//将预测结果插入到数据库中
		String nextIssue = App.getNextIssueByCurrentIssue(App.maxIssueId);
		String stopIssue = this.getNextNIssuenumber(nextIssue, Integer.parseInt(App.nPlan));
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
	public GroupNumber findMaxCountGroupnumber(PredictionRepository pre,List<GroupNumber> equallist,String smallYuanIssueId,String countTbName)
	{
		GroupNumber gmax = new GroupNumber();
		
		List<SrcFiveDataBean> newYuan = pre.getOriginData(smallYuanIssueId);
		//找出新的流码
		List<SrcFiveDataBean> flowbeans = pre.getFlowData(newYuan, App.nPlan);
		//找出流码对应的前三6码复式
		List<GroupNumber> list = this.changeFushiFromFlowData(flowbeans, 6,3);//6:前三6码复式
		
		for (GroupNumber groupNumber : equallist) 
		{
//			groupNumber.setCount(0);
			if(this.listContainValue(list, groupNumber))
			{//若包含，则更新其次数
//				updateTimesOfGnumber(groupNumber,countTbName);
				if(App.countMap.containsKey(groupNumber.getGroupNumber()))
				{
					int count =   Integer.parseInt(App.countMap.get(groupNumber.getGroupNumber()).toString())+ 1;
//					App.countMap.remove(groupNumber.getGroupNumber());
					App.countMap.put(groupNumber.getGroupNumber(), count);
					App.countMap = Maputil.sortByValue(App.countMap);//更新出现次数后重新排序
				}
			}
		}
		
		//判断当前组合是否次数不同，若还相同则要继续判断
//		List<GroupNumber> newEquallist = this.getEqualListcount(equallist, App.liumatbName);
		List<GroupNumber> maxgroup = this.findMaxTimesGroup(10);//10:取10条数据，是limit的参数
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
				gmax = this.findMaxCountGroupnumber(pre,equallist, newYuan.get(newYuan.size()-1).getIssueId(),App.liumatbName);
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
	
	//获取出现次数相同的组合的新排序
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
	
	
	//更新当前组合的次数
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
	
	/**
	 * 将源码转换为六码复式
	* @Title: changeFushiFromFlowData 
	* @Description: TODO(这里用一句话描述这个方法的作用) 
	* @param @param flowbeans
	* @param @return    设定文件 
	* @author banna
	* @date 2017年4月5日 上午10:36:10 
	* @return List<String>    返回类型 
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
	
	
	/**
	 * 获取前N码N码复式
	* @Title: getQianNFushiFromBeans 
	* @Description: TODO(这里用一句话描述这个方法的作用) 
	* @param @param bean
	* @param @param nmafushi
	* @param @return    设定文件 
	* @author banna
	* @date 2017年4月5日 上午11:44:47 
	* @return List<GroupNumber>    返回类型 
	* @throws
	 */
	public List<GroupNumber> getQianNFushiFromBeans(SrcFiveDataBean bean,int qianNma,int nmafushi,int number)//number:当前的号码池数字长度
	{
		List<GroupNumber> list = new ArrayList<GroupNumber>();
		
		//取出前N号码
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
		
		int len = number-qianNma;//计算出可以做为复式组合的号码个数
		//筛选出可以作为组合的号码，移除前N号码的数字
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
				couldArr[count] = App.translate(i1);//将数字转换为AJQ的格式
				count++;
			}
		}
		
		//将筛选号码进行N码组合，然后和前N进行组合
		int nma = nmafushi - qianNma;//剩余需要组合的复式位数,eg:前三6码复式则是nma=6-3=3
		
		Map<String,Object> map = new HashMap<String,Object>();
		//使用筛选号码生成复式
		List<GroupNumber> gefushi = this.generateFushi(nma, list, len, 1, couldArr,map);
		
		//整理当前bean生成的前三六码复式数据
		for (GroupNumber gNumber : list) 
		{
			gNumber.setGroupNumber(this.sortString(strqiansan.toString()+gNumber.getGroupNumber()));
			gNumber.setCount(0);
		}
		
		
		return list;
	}
	//将字符串排序
	private String sortString(String str)
	{
		char[] chars = str.toCharArray();
		Arrays.sort(chars);
		return new String(chars);
	}
	
	/**
	 * 
	* @Title: generateFushi 
	* @Description: TODO(这里用一句话描述这个方法的作用) 
	* @param @param nma:组合复式的位数
	* @param @param list
	* @param @param len：取值范围的数字个数
	* @param @param ceng
	* @param @param couldArr：取值数组
	* @param @param map
	* @param @return    设定文件 
	* @author banna
	* @date 2017年4月6日 上午9:10:05 
	* @return List<GroupNumber>    返回类型 
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
				{//两码复式
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
						{//三码复式
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
	
	//获取n期后的期号
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
	
	//更新当前预测的准确率
	public void updateStatus()
	{
		System.out.println("预测前三六码复式准确率");
		//获取当期开奖号码
		DataToDb dataToDb = new DataToDb();
		SrcFiveDataBean curIssue = dataToDb.getRecordByIssueCode(App.maxIssueId);
		List<String> numList = new ArrayList<String>();//前三开奖号码
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
		//返回标记是否执行预测
		boolean yuceNextFlag = false;
		
		if(fushiYes == 3)
		{//若前三开奖号码都存在于预测结果中，则结果应该是3
			fushiStatus="1";
			yuceNextFlag = true;//若当前预测中出，则要进行下一周期的预测
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
			
			//更新预测到当前期为止该专家的中奖几率
			StringBuffer sqlzjl =new StringBuffer();
			sqlzjl.append("update "+App.predictionTbName+" set "
					+ " WIN_RATE="+dudanZJL+" "
					+ " where"
					+ " ID='"+fushiYuce.getID()+"'");
			
			pstmt.executeUpdate(sqlzjl.toString());
			
			
			//TODO:待完成1：根据中奖率判断当前专家是否收费
			
			//判断是否预测下一期(若当期中出，或者当期的期号是预测计划的最后一期，则要继续预测)
			if(yuceNextFlag || App.maxIssueId.equals(fushiYuce.getYUCE_ISSUE_STOP()))
			{
				//判断完这期中奖率再预测下一期
				PredictionRepository pre = new PredictionRepository();
				List<SrcFiveDataBean> yuanBeans = pre.getOriginData(null);
				this.execQSLiumaFushi(yuanBeans);
			}
			else
			{//若没中出，且计划未到期，则继续
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
