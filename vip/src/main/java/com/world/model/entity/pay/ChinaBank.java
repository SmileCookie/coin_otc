package com.world.model.entity.pay;

import java.math.BigDecimal;
import java.util.EnumSet;
import java.util.Iterator;

import com.world.util.DigitalUtil;

public enum ChinaBank {
	zfb(1,"支付宝", "zfb", "/statics/img/cn/yh/zfb.png",20000.00, ""),
	gsyh(2,"中国工商银行","ICBC","/statics/img/cn/yh/yh_1.png",50000.00 , "1002"),
	yzyh(3,"中国邮政银行","PSBC","/statics/img/cn/yh/yh_13.png",50000.00 , "1028"),
	jsyh(4,"中国建设银行","CCB","/statics/img/cn/yh/yh_5.png",50000.00 , "1003"),
	nyyh(5,"中国农业银行","ABC","/statics/img/cn/yh/yh_4.png",50000.00,"1005"),
	zsyh(6,"中国招商银行","CMB","/statics/img/cn/yh/yh_2.png",50000.00,"1001"),
	zhongGuo(7,"中国银行","BOC","/statics/img/cn/yh/yh_3.png",50000.00,"1052"),
	jiaoTong(8,"交通银行","BOCOM","/statics/img/cn/yh/yh_6.png",50000.00,"1020"),
	guangFa(9,"广东发展银行","GDB","/statics/img/cn/yh/yh_7.png",50000.00,"1027"),
	zhongXin(10,"中信银行","CNCB","/statics/img/cn/yh/yh_8.png",50000.00,"1021"),
	guangDa(11,"光大银行","CEB","/statics/img/cn/yh/yh_9.png",50000.00,"1022"),
	pufa(12,"浦发银行","SPDB","/statics/img/cn/yh/yh_10.png",50000.00,"1004"),
	shenZhenFaZhan(13,"深圳发展银行","SDB","/statics/img/cn/yh/yh_11.png",50000.00,"1008"),
	minSheng(14,"中国民生银行","CMBC","/statics/img/cn/yh/yh_12.png",50000.00,"1006"),
	xingYe(15,"兴业银行","CIB","/statics/img/cn/yh/yh_14.png",50000.00,"1009"),
	pingAn(16,"平安银行","PAB","/statics/img/cn/yh/yh_15.png",50000.00,"1010"),
	beiJing(17,"北京银行","BCCB","/statics/img/cn/yh/yh_16.png",50000.00,"1032"),
	huaXia(18,"华夏银行","HXB","/statics/img/cn/yh/yh_17.png",50000.00,"1025"),
	
	gsyh_(2,"中国工商银行","ICBC","/statics/img/cn/yh/yh_1.png",50000.00 , "1002",true),
	yzyh_(3,"中国邮政银行","PSBC","/statics/img/cn/yh/yh_13.png",50000.00 , "1028",true),
	jsyh_(4,"中国建设银行","CCB","/statics/img/cn/yh/yh_5.png",50000.00 , "1003",true),
	nyyh_(5,"中国农业银行","ABC","/statics/img/cn/yh/yh_4.png",50000.00,"1005",true),
	zsyh_(6,"中国招商银行","CMB","/statics/img/cn/yh/yh_2.png",50000.00,"1001",true),
	zhongGuo_(7,"中国银行","BOCSH","/statics/img/cn/yh/yh_3.png",50000.00,"1052",true),
	jiaoTong_(8,"交通银行","BOCOM","/statics/img/cn/yh/yh_6.png",50000.00,"1020",true),
	guangFa_(9,"广东发展银行","GDB","/statics/img/cn/yh/yh_7.png",50000.00,"1027",true),
	zhongXin_(10,"中信银行","CNCB","/statics/img/cn/yh/yh_8.png",50000.00,"1021",true),
	guangDa_(11,"光大银行","CEB","/statics/img/cn/yh/yh_9.png",50000.00,"1022",true),
	pufa_(12,"浦发银行","SPDB","/statics/img/cn/yh/yh_10.png",50000.00,"1004",true),
	shenZhenFaZhan_(13,"深圳发展银行","SDB","/statics/img/cn/yh/yh_11.png",50000.00,"1008",true),
	minSheng_(14,"中国民生银行","CMBC","/statics/img/cn/yh/yh_12.png",50000.00,"1006",true),
	xingYe_(15,"兴业银行","CIB","/statics/img/cn/yh/yh_14.png",50000.00,"1009",true),
	pingAn_(16,"平安银行","PAB","/statics/img/cn/yh/yh_15.png",50000.00,"1010",true),
	beiJing_(17,"北京银行","BCCB","/statics/img/cn/yh/yh_16.png",50000.00,"1032",true),
	huaXia_(18,"华夏银行","HXB","/statics/img/cn/yh/yh_17.png",50000.00,"1025",true),
	shangHai_(19,"上海银行","BOS","/statics/img/cn/yh/yh_19.png",50000.00,"1026",true),
	shangHaiNongShang_(20,"上海农商银行","SRCB","/statics/img/cn/yh/yh_20.png",50000.00,"1027",true),
	yinLianWuKa_(21,"银联无卡支付","NOCARD","/statics/img/cn/yh/yh_21.png",50000.00,"1028",true),
	yinLian_(22,"银联选择行","UNIONPAY","/statics/img/cn/yh/yh_22.png",50000.00,"1029",true),
	zhongguoBig_(24,"中国银行（大额）","BOC","/statics/img/cn/yh/yh_3.png",50000.00,"1030",true),
	
	cft(23,"财付通","cft","/statics/img/cn/yh/cft.png",20000.00 , ""),
	taobao(50, "淘宝","taobao","/statics/img/cn/yh/taobao.png",100000.00,""),
	other(9999,"其他银行","OTHER","/statics/img/cn/yh/other.png",20000.00,""),
	;
	private ChinaBank(int id, String value, String tag, String img, double withdrawLimit, String cftTag) {
		this.id = id;
		this.value = value;
		this.tag = tag;
		this.img = img;
		this.withdrawLimit = DigitalUtil.getBigDecimal(withdrawLimit);
		this.cftTag = cftTag;
	}
	private ChinaBank(int id, String value, String tag, String img, double withdrawLimit, String cftTag,boolean isMobileDevice) {
		this.id = id;
		this.value = value;
		this.tag = tag;
		this.img = img;
		this.withdrawLimit = DigitalUtil.getBigDecimal(withdrawLimit);
		this.cftTag = cftTag;
		this.isMobileDevice = isMobileDevice;
	}
	private int id;
	private String value;
	private String tag;
	private String cftTag;
	private boolean isMobileDevice;//false:PC true:手机,因为手机上面没办法显示那么大的图片，故而作此区分
	private BigDecimal withdrawLimit = BigDecimal.ZERO;
	
	public static EnumSet<ChinaBank> getAll(){
		return EnumSet.allOf(ChinaBank.class);
	}
	
	public static ChinaBank getChinaBankById(int id){
		Iterator<ChinaBank> it= getAll().iterator();
		
		while(it.hasNext()){
			ChinaBank chinaBank = it.next();
			if(chinaBank.getId() == id){
				return chinaBank;
			}
		}
		return null;
	}
	
	public boolean isMobileDevice() {
		return isMobileDevice;
	}
	public void setMobileDevice(boolean isMobileDevice) {
		this.isMobileDevice = isMobileDevice;
	}
	public String getCftTag() {
		return cftTag;
	}
	public void setCftTag(String cftTag) {
		this.cftTag = cftTag;
	}
	public BigDecimal getWithdrawLimit() {
		return withdrawLimit;
	}
	public void setWithdrawLimit(BigDecimal withdrawLimit) {
		this.withdrawLimit = withdrawLimit;
	}
	public String getTag() {
		return tag;
	}
	public void setTag(String tag) {
		this.tag = tag;
	}
	public String getImg() {
		return img;
	}
	public void setImg(String img) {
		this.img = img;
	}
	private String img;

	public int getId() {
		return id;
	}
	public void setId(int key) {
		this.id = key;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
}
