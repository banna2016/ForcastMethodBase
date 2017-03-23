package com.byl.forcast;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class InsertInitDatatoDB {

	public void renSifushiData()
	{
		Connection conn = ConnectSrcDb.getSrcConnection();
	    
	    String sql = "insert into " + InitBaseTableData.rensiTable + "() values(?,?)";
	    try 
	    {
		    conn.setAutoCommit(false);
		    PreparedStatement pstmt = (PreparedStatement)conn.prepareStatement(sql);
		    
		    List<GroupNumber> allList = this.generateList(11,4);//传入号码个数和当前要生成的组合中号码的个数
		    int i=1;
		    for (GroupNumber groupNumber : allList)
		    {
		    	System.out.println("groupNumber="+groupNumber.getGroupNumber()+"=="+i++);
				pstmt.setString(1, groupNumber.getGroupNumber());
				pstmt.setInt(2, 0);
			    pstmt.addBatch();
		    }
		    pstmt.executeBatch();
		    conn.commit();
		    conn.setAutoCommit(true);
	    
	    } catch (SQLException e) {
			e.printStackTrace();
		}
	     
	}
	/**
	 * 
	* @Title: generateList 
	* @Description: TODO(这里用一句话描述这个方法的作用) 
	* @param @param number:号码个数
	* @param @return    设定文件 
	* @author banna
	* @date 2017年3月23日 上午11:23:06 
	* @return List<GroupNumber>    返回类型 
	* @throws
	 */
	private List<GroupNumber> generateList(int number,int gnumber)
	{
		List<GroupNumber> groupNumbers = new ArrayList<GroupNumber>();
		
		int arr[] = new int[11];
		for(int i=1;i<=number;i++)
		{
			arr[i-1] = i;
		}
		
		int len = number-gnumber;
		int a1 ;
		int a2 ;
		int a3 ;
		int a4 ;
		for(int i1=0;i1<=len;i1++)
		{
			a1 = arr[i1];
			for(int i2=i1+1;i2<=len+1;i2++)
			{
				a2 = arr[i2];
				if(a1 != a2 )
				{
					for(int i3=i2+1;i3<=len+2;i3++)
					{
						a3 = arr[i3];
						if(a2 !=a3)
						{
							for(int i4=i3+1;i4<=len+3;i4++)
							{
								a4 = arr[i4];
								if(a3 != a4)
								{
									GroupNumber groupNumber = new GroupNumber();
									StringBuffer gn = new StringBuffer(""+this.translate(a1));
									gn.append(this.translate(a2));
									gn.append(this.translate(a3));
									gn.append(this.translate(a4));
									groupNumber.setGroupNumber(gn.toString());
									groupNumbers.add(groupNumber);
									System.out.println(groupNumber.getGroupNumber());
								}
								
								
							}
						}
						
						
					}
				}
				
				
			}
			
		}
		
		return groupNumbers;
	}
	
	public String translate(int temp)
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
