package com.byl.forcast;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

public class ConnectLTDb {
	  
	 private static ConnectLTDb instance = null;
	  
	  public static synchronized Connection getConnection()
	  {
	    if (instance == null) {
	      instance = new ConnectLTDb();
	    }
	    return instance._getConnection();
	  }
	  
	  private Connection _getConnection()
	  {
	    try
	    {
	      String driver = "com.mysql.jdbc.Driver";
	      String url = "";
	      String username = "";
	      String password = "";
	      Properties p = new Properties();
	      InputStream is = getClass().getClassLoader()
	        .getResourceAsStream("db.properties");
	      p.load(is);
	      driver = p.getProperty("driver", "");
	      url = p.getProperty("lp.url", "");
	      username = p.getProperty("username", "");
	      password = p.getProperty("password", "");
	      
	      Properties pr = new Properties();
	      pr.put("user", username);
	      pr.put("password", password);
	      pr.put("characterEncoding", "UTF-8");
	      pr.put("useUnicode", "TRUE");
	      
	      Class.forName(driver).newInstance();
	      return DriverManager.getConnection(url, pr);
	    }
	    catch (Exception se) {
	    	se.printStackTrace();
	    	return null;
	    }
	    
	  }
	  
	  public static void dbClose(Connection conn, PreparedStatement ps, ResultSet rs)
	  {
		  try { 
			    if ((rs != null) && (!rs.isClosed())) {
			      
					rs.close();
			    }
			    if ((ps != null) && (!ps.isClosed())) {
			      ps.close();
			    }
			    if ((conn != null) && (!conn.isClosed())) {
			      conn.close();
			    }
	    
		  } catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	  }
}
