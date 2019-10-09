package com.tenstar;

import java.util.Random;

public class radom {

	static Random commonRandom = new Random();
	/**
	 * 返回一个大于0的随机数字
	 * @param max
	 * @return
	 */
	public static int radomInt(int max){
		if(max<0)
			max=-max;
		if(max==0)
			max=100;
		 int rm =0;
		 while(rm==0)
			 rm= commonRandom.nextInt(max);
		 return rm;
	}

	
	
	/**
	 * 返回一个比当前值大或者小的一个随机数
	 * @param max 最大百分比
	 * @return
	 */
	public static long radomLong(long old,int max){
		if(max<0)
			max=-max; 
		if(max==0)
			max=100;
		 int rm= 2*commonRandom.nextInt(max);
		 
		 max=rm-max;//带正负号了
		long rtn=old+old*max/100;
		if(rtn<0)
			return old;
		else
		 return rtn;
		 
		
		 
	}

	/**
	 * 返回一个大于0的随机数字
	 * @param max
	 * @return
	 */
	public static int radomInt(int min,int max){
		int rtn=0;
		if(min==0)
			min=1;
		while(rtn<min||rtn>max)
		    rtn=radomInt(max);

		 
		 return rtn;
		 
	}
	
	public static int[] GetRadomsFromArray(int[] ids,int num){
	  Random r=new Random();  
      int strarray[]=new int[num];  
      int index=0;  
      int okPrice=0;
      for(int i=0;i<ids.length;i++){
    	  if(ids[i]!=0)
    		  okPrice++;
    	  else
    		  break;
      }
      for(int i=0;i<num;i++)  
      {  
          //刚开始从数组中随机抽取一个  
          //而后将抽取的元素后面的元素向前推进到随机的位置[index位置]  
          //随着循环的继续,逐渐抛弃后面的元素  
          index=r.nextInt(okPrice-i);  
          strarray[i]=ids[index];  
          //元素向前推进到随机[index]的位置  
          for(int j=index;j<okPrice-i-1;j++){  
              ids[j]=ids[j+1];  
          }  
      } 
      return strarray;
      
	}
	
	
	
}
