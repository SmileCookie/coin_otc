package com.world.web.response;

import java.io.Serializable;

/*****
 * response
 * @author Administrator
 *
 */
public class DataResponse implements Serializable{

	private static final long serialVersionUID = -1747128615428008510L;

	public DataResponse() {
		super();
	}
	
	public DataResponse(String des, boolean isSuc, String dataStr) {
		super();
		this.des = des;
		this.isSuc = isSuc;
		this.dataStr = dataStr;
	}

	private String des;
	private boolean isSuc;
	private String dataStr;
	
	public String getDataStr() {
		return dataStr;
	}

	public void setDataStr(String dataStr) {
		this.dataStr = dataStr;
	}

	public String getDes() {
		return des;
	}
	public void setDes(String des) {
		this.des = des;
	}
	public boolean isSuc() {
		return isSuc;
	}
	public void setSuc(boolean isSuc) {
		this.isSuc = isSuc;
	}
	public String getJsonResponseStr(){
		if(dataStr == null || dataStr.equals("")){
			dataStr = "{}";
		}
		return "{\"des\" : \""+des+"\" , \"isSuc\" : "+isSuc+"  , \"datas\" : "+dataStr+"}";
	}

}
