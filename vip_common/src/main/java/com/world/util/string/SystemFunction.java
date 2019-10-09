package com.world.util.string;
/**
 * 系统定义的一些全局函数,方便调用
 * @author Administrator
 *
 */
public class SystemFunction {

	/**
	 * 从一个int值转换返回一个boolean值
	 * @param values 参数值
	 * @return 是否是bool
	 */
	public static Boolean ToBool(int values)
	{
		//所有非0值都是boolean
		if(values==0)
			return false;
		else
		    return true;
	}
	/**
	 * 从一个int值转换返回一个boolean值
	 * @param values 参数值
	 * @return 是否是bool
	 */
	public static Boolean ToBool(String values)
	{
		if(values==null)
			return  false;
		String newValue=values.trim().toLowerCase();
		if(newValue.equals("false")||newValue.equals("0"))
		  return false;
		else
		  return true;
	}
}
