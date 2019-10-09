package com.tenstar;


//import org.apache.log4j.Logger;



public class Start {
	
//	public static Logger logger = Logger.getLogger(Start.class);
	 
	public static void main(String[] args) {
	//	testGetRecord();
	//	testEntrust();
	}
	
	public static void testEntrust(){
		long price=300000;
		for(int i=0;i<60;i++){
		 Message myObj = new Message();    
         myObj.setUserId(799);
         myObj.setUserName("测试的");
         myObj.setWebId(8); 
        // myObj.setUnitPrice(330000);
        // myObj.setNumbers(23432320000l);
         myObj.setTypes(1);
         //myObj.setUnitPrice(price);
         price-=1000;
         myObj.setStatus(0);
         myObj.setMessage("测试卖出");
        //String param:       guo=sdfegi&ddd=222
	//	String rtn=HTTPTcp.DoRequest(true,"/entrust",myObj);
	//	System.out.println(rtn);
	//	if(i%2000==0) {
	//		Message rtn2 =(Message)HTTPTcp.StringToObject(rtn);
	//		System.out.println(i+":::"+rtn2.getMessage());
	//	}
	 }

		ConnectionPool.releaseAllConnection();
	}
	public static void testGetRecord(){
		long price=300000;
		for(int i=0;i<1;i++){
		RecordMessage myObj = new RecordMessage();    
         myObj.setUserId(799);
         myObj.setAuth("");
         myObj.setMessage("测试获取列表");
         myObj.setWebId(8);
         myObj.setTypes(0);
         myObj.setPageindex(1);
       
		String rtn=HTTPTcp.DoRequest(true,"/userrecord",myObj);
	
		RecordMessage rtn2 =(RecordMessage)HTTPTcp.StringToObject(rtn);
			System.out.println(i+":::"+rtn2.getMessage());

	 }

		ConnectionPool.releaseAllConnection();
	}
	
	
	
}
