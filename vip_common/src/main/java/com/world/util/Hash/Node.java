package com.world.util.Hash;

public class Node implements java.io.Serializable{
	 /**
	 * 
	 */
	public Node(){}
	 private static final long serialVersionUID = -5176688704261265192L;
	 String name;  
     String ip;  
     String forceUrl;
     public Node(String ip) {  
         this.ip = ip;  
     } 	 
     public Node(String name,String ip , String forceUrl) {  
         this.name = name;  
         this.ip = ip;  
         this.forceUrl = forceUrl;
     }  
     @Override  
     public String toString() {  
         return this.name+"-"+this.ip;  
     }  
     
     public String getUrl(){
    	 return forceUrl;
     }
}
