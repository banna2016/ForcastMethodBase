package com.byl.forcast;

import java.io.InputStream;
import java.util.Properties;

/**
 * 初始化数据方法
* @Description: TODO(这里用一句话描述这个方法的作用) 
* @author banna
* @date 2017年3月23日 上午10:52:16
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
