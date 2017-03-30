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
	
	public static String srcNumberTbName;//�������ֱ�
	
	public static String lastIssuenum;//��ǰͳ�Ƶ��ں�
	
	public static Integer originDataCount;//��Ҫ��Դ������
	
	public static Integer flowDataCount;//��Ҫ����������
	
	public static String beid ;//ר��id
	
	public static String ptypeid;//Ԥ������id
	
	public static String methodName;//Ԥ�ⷽ����
	
	public static String ltptypetbName;
	
	public static String baseptypetbName;
	
	public static String originruleTbName;
	
	public static String lpId;//�������id
	
	public static String predictionTbName;//Ԥ�ⷽ���洢��
	
	public static String liangmatbName;
	
	public static String liumatbName;
	
	public static String sanmatbName;
	
	public static String simatbName;
	
	public static String nPlan ;//n�ڼƻ�
	
	public static String originIssueCount ;//Դ��Ҫ������
	
	public static String originId ;
	
	public static String type;
	
	public static String locationOrContain;
	
	public static String CI_LOCATION_NUMBER;
	
	public static String CI_RULE_FILED;
	
	public static String LI_LOCATION_NUMBER	;
	
	public static String LI_RULE_FILED;
	
	public static String maxIssueId;
	
	public static int cycle;
	
	public static String number;//��ǰ���ֵĺ���غ������
	
	
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
	    
	    //����Ԥ������id���ҵ�ǰԤ�����Ͷ�Ӧ�Ļ������ݱ��Ԥ�����洢���Լ���ز���
	    beid = p.getProperty("beid");
	    ptypeid = p.getProperty("ptypeid");
	    
	    ltptypetbName = p.getProperty("ltptypetbName");
	    baseptypetbName = p.getProperty("baseptypetbName");
	    originruleTbName = p.getProperty("originRuletbName");
	    number = p.getProperty("number");
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
					exctPredict();
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
				
			}
		}, new Date(), 10000L);// ÿ��10s���
	}
	
	/**
	 * ��ʼԤ��
	* @Title: exctPredict 
	* @Description: TODO(������һ�仰�����������������) 
	* @param     �趨�ļ� 
	* @author banna
	* @date 2017��3��29�� ����9:44:13 
	* @return void    �������� 
	* @throws
	 */
	public static void  exctPredict()
	{
		InitParams.getMethodName();//��ʼ��Ԥ�ⷽ����
		
		DataToDb dataToDb = new DataToDb();
		String maxIssueNumber = dataToDb.findMaxIssueIdFromSrcDb();
		
		if(!App.maxIssueId.equals(maxIssueNumber))
		{
			App.maxIssueId = maxIssueNumber;
		}
		
		if(null != App.methodName && !"".equals(App.methodName ))
		{
			//ͨ���������Ԥ�ⷽ��
			PredictionRepository predictionRepository = new PredictionRepository();
			
			Class clazz = predictionRepository.getClass(); 
			
			try 
			{
				Method m2;
				m2 =  clazz.getDeclaredMethod(App.methodName);//ʹ�÷����ȡ������������ʹ�õ�ǰ������֪�ಢ���Ѿ�ʵ���������
				m2.invoke(predictionRepository);//ʹ�÷���ִ�з���
				
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
