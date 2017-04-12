package com.byl.forcast;

import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
	
	
	public static String originId ;
	
	public static String type;
	
	public static String locationOrContain;
	
	public static String CI_LOCATION_NUMBER;
	
	public static String CI_RULE_FILED;
	
	public static String LI_LOCATION_NUMBER	;
	
	public static String LI_RULE_FILED;
	
	public static String maxIssueId ;
	
	public static int cycle;
	
	public static int number;//��ǰ���ֵĺ���غ������
	
	public static boolean flag = false;//�Ƿ��ж��н���
	
	public static int orderRule;//׼ȷ�ʼ������ݻ���
	
	public static Map<String,Integer> countMap = new HashMap<String,Integer>();
	
	
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
	    number = Integer.parseInt(p.getProperty("number"));
	    
	    /**��ʼ��map���ݣ���Ҫ���ã�**/
	    InsertInitDatatoDB datatoDB = new InsertInitDatatoDB();
	    List<GroupNumber> allList = datatoDB.generateList(App.number,2);//�����������͵�ǰҪ���ɵ�����к���ĸ���
	    for (GroupNumber groupNumber : allList)
	    {
	    	countMap.put(groupNumber.getGroupNumber(), 0);
		}
	    
	    
	  }
	
	
	public static void main(String args[])
	{
		
		try
		{
			//��Ҫ�������ݣ�
			/**
			 * 1.�����ļ��е�srcNumberTbName��Դ���
			 * lineCount = 83����ǰ���ֵ����������
				beid = 1 ��ר��id
				ptypeid������Ԥ������id
				number������صĺ������
				2.�����Ƿ���Ҫ��ʼ��countMap���ڳ�ʼ�������У���������Ҫ���ɵĸ�ʽλ��
			 */
			initParam();
			InitParams.getMethodName();//��ʼ��Ԥ�ⷽ����
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
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
		}, new Date(), 20000L);// ÿ��10s���
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
		
		DataToDb dataToDb = new DataToDb();
		String maxIssueNumber = dataToDb.findMaxIssueIdFromSrcDb();
		boolean maxflag = false;
		if( null == App.maxIssueId||!App.maxIssueId.equals(maxIssueNumber))
		{
			App.maxIssueId = maxIssueNumber;
			System.out.println("maxissueId="+App.maxIssueId);
			maxflag = true;
		}
		if(maxflag&&null != App.methodName && !"".equals(App.methodName ))
		{
//			App.maxIssueId="17041148";
			maxflag = false;
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
	
	public static String getNextIssueByCurrentIssue(String issueNumber)
	  {
	    String issueCode = issueNumber.substring(issueNumber.length() - 2, issueNumber.length());
	    int issue = Integer.parseInt(issueCode);
	    int nextIssue = (issue + 1) % Integer.parseInt(lineCount);
	    if (nextIssue > 9) 
	    {
	      return issueNumber.substring(0, issueNumber.length() - 2) + nextIssue;
	    }
	    if (nextIssue == 0)
	    {
	      return issueNumber.substring(0, issueNumber.length() - 2) + lineCount;
	    }
	    if (nextIssue == 1) 
	    {
	      return DateUtil.getNextDay(issueNumber.substring(0, issueNumber.length() - 2)) + "01";
	    }
	    return issueNumber.substring(0, issueNumber.length() - 2) + "0" + nextIssue;
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
