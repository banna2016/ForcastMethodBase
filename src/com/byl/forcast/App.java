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
	
	public static String lastIssuenum;//��ǰͳ�Ƶ��ں�
	
	public static Integer originDataCount;//��Ҫ��Դ������
	
	public static Integer flowDataCount;//��Ҫ����������
	
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
	    
	    //����Ԥ������id���ҵ�ǰԤ�����Ͷ�Ӧ�Ļ������ݱ��Ԥ�����洢���Լ���ز���
	    
	    
	  }
	
	
	public static void main(String args[])
	{
		initParam();//��ʼ����Ҫ�Ĳ���
		
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
		}, new Date(), 10000L);// ÿ��10s���
	}
}
