package com.byl.forcast;

import java.io.InputStream;
import java.util.Properties;

/**
 * ��ʼ�����ݷ���
* @Description: TODO(������һ�仰�����������������) 
* @author banna
* @date 2017��3��23�� ����10:52:16
 */
public class InitBaseTableData 
{
	public static String rensiTable;
	public static String sanmaTable;
	public static String liangmaTable;
	public static String liumaTable;
	
	public static void initParams()
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
	    rensiTable = p.getProperty("rensiTable");
	    sanmaTable = p.getProperty("sanmaTable");
	    liangmaTable = p.getProperty("liangmaTable");
	    liumaTable = p.getProperty("liumaTable");
	}
	
	/*public static void main(String args[])
	{
		try{
//			initParams();
//			init();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}*/
	
	public static void init()
	{
		
		InsertInitDatatoDB datatoDB = new InsertInitDatatoDB();
		
//		datatoDB.renSifushiData();
//		datatoDB.sanmaData();
//		datatoDB.liangmaData();
		datatoDB.liumaData();
	}
	
	
}
