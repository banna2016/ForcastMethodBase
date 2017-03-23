package com.byl.forcast;

import java.io.InputStream;
import java.util.Date;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;

public class App 
{
	public  static String lineCount;
	
	public static String descNumberTbName;
	
	public static String lastIssuenum;//当前统计的期号
	
	public static Integer originDataCount;//需要的源码期数
	
	public static Integer flowDataCount;//需要的流码期数
	
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
	    descNumberTbName = p.getProperty("descNumberTbName");
	    
	    //根据预测类型id查找当前预测类型对应的基础数据表和预测结果存储表以及相关参数
	    
	    
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
					
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
				
			}
		}, new Date(), 10000L);// 每隔10s输出
	}
}
