package com.byl.forcast.danma;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.byl.forcast.App;
import com.byl.forcast.FiveInCount;
import com.byl.forcast.PredictionRepository;
import com.byl.forcast.SrcFiveDataBean;

public class ExecDanma 
{
	//预测前三胆码杀码
	public void execDanma(List<SrcFiveDataBean> yuanBeans)
	{
		//根据源码获取流码
		PredictionRepository pre = new PredictionRepository();
		List<SrcFiveDataBean> flowbeans = new ArrayList<SrcFiveDataBean>();
		flowbeans = pre.getFlowData(yuanBeans, App.nPlan);
		
		//根据流码进行预测
		int[] count = new int[Integer.parseInt(App.number)];
		
		for (SrcFiveDataBean bean : flowbeans) 
		{
			int[] numIntArr = { bean.getNo1(), bean.getNo2(), bean.getNo3() };
			
			for (int i : numIntArr)
			{
				count[(numIntArr[i] - 1)] += 1;
			}
		}
		
		//统计前三号码开奖次数
		List<FiveInCount> countlist = new ArrayList();
		for(int j=0;j<Integer.parseInt(App.number);j++)
		{
			FiveInCount fcount = new FiveInCount();
			
			fcount.setNumber(j+1);
			fcount.setCount1(count[j]);
			
			countlist.add(fcount);
		}
		
		Collections.sort(countlist);
		
		//判断独胆和次胆以及次胆后号码出现次数是否相同(可优化，比较相同次数直到没有可以优化的为止)
		if(countlist.get(0).getCount1().equals(countlist.get(1).getCount1()))
		{//如果独胆和次胆出现次数相同，那么再取流码看独胆出现还是次胆出现，出现的是独胆，没出现的是次胆
			int dudan ;
			int cidan;
			int[] dudanArr = new int[2];
			dudanArr[0] = countlist.get(0).getNumber();
			dudanArr[1] = countlist.get(1).getNumber();
			List<SrcFiveDataBean> newYuan = pre.getOriginData(yuanBeans.get(yuanBeans.size()-1).getIssueId());
			//找出新的流码
			flowbeans = pre.getFlowData(newYuan, App.nPlan);
			List<Integer> compare = new ArrayList<Integer>();
			for(int n = 0;n<flowbeans.size();n++)
			{
				compare.clear();
				compare.add(flowbeans.get(n).getNo1());
				compare.add(flowbeans.get(n).getNo2());
				compare.add(flowbeans.get(n).getNo3());
				
				if(compare.contains(dudanArr[0]) && !compare.contains(dudanArr[1]))
				{//有1没有2
					dudan = dudanArr[0];
					cidan = dudanArr[1];
					break;
				}
				else
					if(!compare.contains(dudanArr[0]) && compare.contains(dudanArr[1]))
					{//有1没有2
						dudan = dudanArr[1];
						cidan = dudanArr[0];
						break;
					}
					else
					{//都不包含这两个数字
						continue;
					}
				
			}
				
			
		}
		else
			if(countlist.get(1).getCount1().equals(countlist.get(2).getCount1()))
			{//如果次胆和次胆后号码出现的次数相同，则再次获取流码比较，出现的是次胆
				int[] cidanArr = new int[2];
				cidanArr[0] = countlist.get(1).getNumber();
				cidanArr[1] = countlist.get(2).getNumber();
				
				int cidan;
				
				List<SrcFiveDataBean> newYuan = pre.getOriginData(yuanBeans.get(yuanBeans.size()-1).getIssueId());
				//找出新的流码
				flowbeans = pre.getFlowData(newYuan, App.nPlan);
				List<Integer> compare = new ArrayList<Integer>();
				for(int n = 0;n<flowbeans.size();n++)
				{
					compare.clear();
					compare.add(flowbeans.get(n).getNo1());
					compare.add(flowbeans.get(n).getNo2());
					compare.add(flowbeans.get(n).getNo3());
					
					if(compare.contains(cidanArr[0]) && !compare.contains(cidanArr[1]))
					{//有1没有2
						cidan  = cidanArr[0];
						break;
					}
					else
						if(!compare.contains(cidanArr[0]) && compare.contains(cidanArr[1]))
						{//有1没有2
							cidan = cidanArr[1];
							break;
						}
						else
						{//都不包含这两个数字
							continue;
						}
					
				}
			}
	}
	
	
	
}
