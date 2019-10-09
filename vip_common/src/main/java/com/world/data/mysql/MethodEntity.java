package com.world.data.mysql;
import java.util.ArrayList;
/**
 * 功能:定义实体对象的方法实体对象
 * @author 凌晓
 *
 */
public class MethodEntity {

	//方法名称
	private String methodName;
	//重载方法个数
	private int repeatMethodNum=1;
	//方法参数类型列表
	private Class[] methodParamTypes;
	//存放重载方法参数
	private ArrayList repeatMethodsParamTypes;
	/**
	 * 功能:获取参数名称
	 * @return
	 */
	public String getMethodName(){
		return this.methodName;
	}
	/**
	 * 功能:获取方法参数类型列表
	 * @return
	 */
	public Class[] getMethodParamTypes(){
		return this.methodParamTypes;
	}
	/**
	 * 功能:设置参数名称
	 * @param string
	 */
	public void setMethodName(String string){
		this.methodName=string;
	}
	/**
	 * 功能:设置参数类型列表
	 * @param classes
	 */
	public void setMethodParamTypes(Class[] classes){
		this.methodParamTypes=classes;
	}
	/**
	 * 功能:获取重载方法个数
	 * @return
	 */
	public int getRepeatMethodNum(){
		return this.repeatMethodNum;
	}
	/**
	 * 功能:获取第i个重载方法参数列表
	 * @param i index
	 * @return
	 */
	public Class[] getRepeatMethodParamTypes(int i){
		int count=this.repeatMethodsParamTypes.size();
		if(i<count)
			return (Class[])this.repeatMethodsParamTypes.get(i);
		else
			throw new ArrayIndexOutOfBoundsException();
	}
	/**
	 * 功能:设置重载方法个数
	 * @param i index
	 */
	public void setRepeatMethodNum(int i){
		this.repeatMethodNum=i;
	}
	/**
	 * 功能:设置重载方法参数类型
	 * @param list 参数列表
	 */
	public void setRepeatMethodsParamTypes(ArrayList list){
		this.repeatMethodsParamTypes=list;
	}
	/**
	 * 功能:获取重载方法类型集合
	 * @return
	 */
	public ArrayList getRepeatMethodsParamTypes(){
		return this.repeatMethodsParamTypes;
	}
	/**
	 * 功能:设置重载参数类型列表
	 * @param paramTypes
	 */
	public void setRepeatMethodsParamTypes(Class[] paramTypes){
		if(this.repeatMethodsParamTypes==null)
			this.repeatMethodsParamTypes=new ArrayList();
		repeatMethodsParamTypes.add(paramTypes);
	}
}