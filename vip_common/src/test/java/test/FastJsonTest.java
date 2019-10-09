package test;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.alibaba.fastjson.JSON;

public class FastJsonTest {

	private final static Logger log = Logger.getLogger(FastJsonTest.class);

	@Test
	public void test1(){
		//JSON.parseObject(text, clazz)
		String json = "{age : 10 , name : \"lily\"}";
		
		JsonBeanTest jbt = new JsonBeanTest();
		jbt.setName("lily");
		jbt.setAge(100);
		
		String json1 = JSON.toJSONString(jbt);
		
		log.info(json1);
		
		JsonBeanTest jt = JSON.parseObject(json, JsonBeanTest.class);
		
		log.info(jt.getAge());
		
		
	}
	
}

class JsonBeanTest{
	private int age;
	private String name;
	
	
	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
}
