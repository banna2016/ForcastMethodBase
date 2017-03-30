package com.byl.forcast;

import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;

public class App 
{
	public  static String lineCount;
	
	public static String srcNumberTbName;//基础彩种表
	
	public static String lastIssuenum;//当前统计的期号
	
	public static Integer originDataCount;//需要的源码期数
	
	public static Integer flowDataCount;//需要的流码期数
	
	public static String beid ;//专家id
	
	public static String ptypeid;//预测类型id
	
	public static String methodName;//预测方法名
	
	public static String ltptypetbName;
	
	public static String baseptypetbName;
	
	public static String originruleTbName;
	
	public static String lpId;//区域彩种id
	
	public static String predictionTbName;//预测方案存储表
	
	public static String liangmatbName;
	
	public static String liumatbName;
	
	public static String sanmatbName;
	
	public static String simatbName;
	
	public static String nPlan ;//n期计划
	
	public static String originIssueCount ;//源码要求数量
	
	public static String originId ;
	
	public static String type;
	
	public static String locationOrContain;
	
	public static String CI_LOCATION_NUMBER;
	
	public static String CI_RULE_FILED;
	
	public static String LI_LOCATION_NUMBER	;
	
	public static String LI_RULE_FILED;
	
	public static String maxIssueId;
	
	
	
	
	private static void initParam()
	  {
	    Properties p = new Properties();
	    InputStream is = App.class.getClassLoader().getResourceAsStream("db.properties");
	    try
	    {
	      p.load(is);
	      
	    }
	    catch (Exception e)
	    {
	      e.printStackTrace();
	    }
	    lineCount = p.getProperty("lineCount", "79");
	    srcNumberTbName = p.getProperty("srcNumberTbName");
	    
	    //根据预测类型id查找当前预测类型对应的基础数据表和预测结果存储表以及相关参数
	    beid = p.getProperty("beid");
	    ptypeid = p.getProperty("ptypeid");
	    
	    ltptypetbName = p.getProperty("ltptypetbName");
	    baseptypetbName = p.getProperty("baseptypetbName");
	    originruleTbName = p.getProperty("originRuletbName");
	  }
	
	
	public static void main(String args[])
	{
		initParam();//初始化需要的参数
		
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				try
				{
					exctPredict();
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
				
			}
		}, new Date(), 10000L);// 每隔10s输出
	}
	
	/**
	 * 开始预测
	* @Title: exctPredict 
	* @Description: TODO(这里用一句话描述这个方法的作用) 
	* @param     设定文件 
	* @author banna
	* @date 2017年3月29日 上午9:44:13 
	* @return void    返回类型 
	* @throws
	 */
	public static void  exctPredict()
	{
		InitParams.getMethodName();//初始化预测方法名
		
		DataToDb dataToDb = new DataToDb();
		String maxIssueNumber = dataToDb.findMaxIssueIdFromSrcDb();
		
		if(!App.maxIssueId.equals(maxIssueNumber))
		{
			App.maxIssueId = maxIssueNumber;
		}
		
		if(null != App.methodName && !"".equals(App.methodName ))
		{
			//通过反射进入预测方法
			PredictionRepository predictionRepository = new PredictionRepository();
			
			Class clazz = predictionRepository.getClass(); 
			
			try 
			{
				Method m2;
				m2 =  clazz.getDeclaredMethod(App.methodName);//使用反射获取方法名，这种使用的前提是已知类并且已经实例化类对象
				m2.invoke(predictionRepository);//使用反射执行方法
				
			} 
			catch (NoSuchMethodException e) 
			{
				e.printStackTrace();
			} 
			catch (SecurityException e) 
			{
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
			
		}
	}
	
	  public static String translate(int temp)
	  {
	    String rtn = null;
	    if (temp < 10) {
	      rtn = temp+"";
	    } else if (temp == 10) {
	      rtn = "A";
	    } else if (temp == 11) {
	      rtn = "J";
	    } else if (temp == 12) {
	      rtn = "Q";
	    }
	    return rtn;
	  }
}
