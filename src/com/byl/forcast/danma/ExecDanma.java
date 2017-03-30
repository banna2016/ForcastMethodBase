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
	//Ԥ��ǰ������ɱ��
	public void execDanma(List<SrcFiveDataBean> yuanBeans)
	{
		//����Դ���ȡ����
		PredictionRepository pre = new PredictionRepository();
		List<SrcFiveDataBean> flowbeans = new ArrayList<SrcFiveDataBean>();
		flowbeans = pre.getFlowData(yuanBeans, App.nPlan);
		
		//�����������Ԥ��
		int[] count = new int[Integer.parseInt(App.number)];
		
		for (SrcFiveDataBean bean : flowbeans) 
		{
			int[] numIntArr = { bean.getNo1(), bean.getNo2(), bean.getNo3() };
			
			for (int i : numIntArr)
			{
				count[(numIntArr[i] - 1)] += 1;
			}
		}
		
		//ͳ��ǰ�����뿪������
		List<FiveInCount> countlist = new ArrayList();
		for(int j=0;j<Integer.parseInt(App.number);j++)
		{
			FiveInCount fcount = new FiveInCount();
			
			fcount.setNumber(j+1);
			fcount.setCount1(count[j]);
			
			countlist.add(fcount);
		}
		
		Collections.sort(countlist);
		
		//�ж϶����ʹε��Լ��ε��������ִ����Ƿ���ͬ(���Ż����Ƚ���ͬ����ֱ��û�п����Ż���Ϊֹ)
		if(countlist.get(0).getCount1().equals(countlist.get(1).getCount1()))
		{//��������ʹε����ִ�����ͬ����ô��ȡ���뿴�������ֻ��Ǵε����֣����ֵ��Ƕ�����û���ֵ��Ǵε�
			int dudan ;
			int cidan;
			int[] dudanArr = new int[2];
			dudanArr[0] = countlist.get(0).getNumber();
			dudanArr[1] = countlist.get(1).getNumber();
			List<SrcFiveDataBean> newYuan = pre.getOriginData(yuanBeans.get(yuanBeans.size()-1).getIssueId());
			//�ҳ��µ�����
			flowbeans = pre.getFlowData(newYuan, App.nPlan);
			List<Integer> compare = new ArrayList<Integer>();
			for(int n = 0;n<flowbeans.size();n++)
			{
				compare.clear();
				compare.add(flowbeans.get(n).getNo1());
				compare.add(flowbeans.get(n).getNo2());
				compare.add(flowbeans.get(n).getNo3());
				
				if(compare.contains(dudanArr[0]) && !compare.contains(dudanArr[1]))
				{//��1û��2
					dudan = dudanArr[0];
					cidan = dudanArr[1];
					break;
				}
				else
					if(!compare.contains(dudanArr[0]) && compare.contains(dudanArr[1]))
					{//��1û��2
						dudan = dudanArr[1];
						cidan = dudanArr[0];
						break;
					}
					else
					{//������������������
						continue;
					}
				
			}
				
			
		}
		else
			if(countlist.get(1).getCount1().equals(countlist.get(2).getCount1()))
			{//����ε��ʹε��������ֵĴ�����ͬ�����ٴλ�ȡ����Ƚϣ����ֵ��Ǵε�
				int[] cidanArr = new int[2];
				cidanArr[0] = countlist.get(1).getNumber();
				cidanArr[1] = countlist.get(2).getNumber();
				
				int cidan;
				
				List<SrcFiveDataBean> newYuan = pre.getOriginData(yuanBeans.get(yuanBeans.size()-1).getIssueId());
				//�ҳ��µ�����
				flowbeans = pre.getFlowData(newYuan, App.nPlan);
				List<Integer> compare = new ArrayList<Integer>();
				for(int n = 0;n<flowbeans.size();n++)
				{
					compare.clear();
					compare.add(flowbeans.get(n).getNo1());
					compare.add(flowbeans.get(n).getNo2());
					compare.add(flowbeans.get(n).getNo3());
					
					if(compare.contains(cidanArr[0]) && !compare.contains(cidanArr[1]))
					{//��1û��2
						cidan  = cidanArr[0];
						break;
					}
					else
						if(!compare.contains(cidanArr[0]) && compare.contains(cidanArr[1]))
						{//��1û��2
							cidan = cidanArr[1];
							break;
						}
						else
						{//������������������
							continue;
						}
					
				}
			}
	}
	
	
	
}
