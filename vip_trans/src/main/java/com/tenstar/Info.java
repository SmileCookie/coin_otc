package com.tenstar;

public enum Info {
	
	NoMission(1,0,"没有委托任务"),
	DueError(2,0,"未知的处理异常"),
	//以下是提交委托相关
	DoEntrustSuccess(100,1,"委托成功。"),

	DoEntrustFaildUnKnowType(130,1,"委托失败-未知的委托类型"),
	DoEntrustFaildWrongParam(131,1,"委托失败-参数错误"),
	DoEntrustFaildDoubleSell(132,1,"委托失败-还有卖比特币委托已经达到您本次购买的报价"),
	DoEntrustFaildDoubleBuy(133,1,"委托失败-还有买比特币委托已经达到您本次卖出的报价"),
	DoEntrustFaildNotEnoughMoney(135,1,"委托失败-没有足够的资金"),
	DoEntrustFaildPromError(136,1,"委托失败-未捕获的系统异常"),
	DoEntrustFaildHighPeice(137,1,"委托价格偏离市场价格过高，请核实后再次尝试。"),
	DoEntrustFailDunplicate(138,1,"委托失败-重复提交"),
	DoEntrustFailNotAllowBuy(139,1,"委托失败-btq暂停购买"),
	DoEntrustFaildMinAmount(140,1,"委托失败-成交金额小于系统规定金额"),
	DoEntrustFaildMinNum(141,1,"委托失败-成交数量小于系统规定数量"),
	//以下是提交取消委托相关
	DoCancleSuccess(200,2,"撤销成功。"),
	DoCancleFaildNoOrder(211,2,"撤销失败。"),
	DoCancleFaildDoubleCancle(212,2,"委托取消单个失败-已经提交过取消请求"),
	DoCancleFaildPromError(213,2,"委托取消单个失败-取消程序"),

	DoCancleFaildNoOrderMul(221,2,"委托取消区间订单失败-区间没有可以取消的委托"),
	DoCancleFailPriceError(222,2,"委托取消区间订单失败-区间价格可能颠倒导致错误"),
	DoCancleFailPromErrorMul(223,3,"委托取消区间订单失败-事物处理失败"),
	
	//处理一笔委托
	DueEntrustSuccess(300,3,"处理委托成功"),
	DueEntrustSuccessUnDo(301,3,"处理委托成功-没有成交"),
	
	DueEntrustFaildUnKonwType(311,3,"处理委托失败-未知的委托类型"),
    DueEntrustFaildProError(312,3,"处理委托失败-事物处理失败"),
	
    
    DueCancleSuccess(400,4,"处理取消委托成功"),
    
    DueCancleFaildNoFouce(411,4,"处理取消委托失败-没有目标委托"),
    DueCancleFaildHasDued(412,4,"处理取消委托失败-已经成功委托"),
    DueCancleFaildPromError(413,4,"处理取消委托失败-事物处理失败"),
    
    
    
    
    GetMessageError(600,5,"获取信息失败")
    
	;
	
	private Info(int nums,int types,String messages){
	this.num=nums;
	this.type=types;
	this.message=messages;
    }

	private int num;//信息编码
	private int type;//信息类型
	private String message;//信息描述
	public int getNum() {
		return num;
	}
	public void setNum(int num) {
		this.num = num;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	
	

}
