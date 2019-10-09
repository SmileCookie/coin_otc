package com.world.model.dao.robot;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.api.util.http.HttpUtil;
import com.file.config.FileConfig;
import com.world.cache.Cache;
import com.world.data.mysql.Data;
import com.world.model.entity.Market;
import com.world.model.entity.pay.PayUserBean;
import com.world.model.entity.robot.RobotConfig;
import com.world.util.DigitalUtil;
import com.world.util.sign.RSACoder;
import me.chanjar.weixin.common.util.StringUtils;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.math.BigDecimal;
import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class RobotHandler implements Runnable {

	
	private Logger log = Logger.getLogger(RobotHandler.class.getName());
	
	private RobotDao robotDao = new RobotDao();
	
	private RobotConfig robotConfig = null;//机器人配置对象
	
	private PayUserBean payUser = null;//挂单账号对象
	
	private String transUrl ="";//交易域名
	
	private JSONObject market=null;//市场对象
	
	private int connectTimeout = 3000;//http链接超时限制
	
	private int readTimeout = 3000;//http读超时限制
	
	private String pubKey="MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCNvhXdl2tYI1ld+0FsvqtdXKnQOQG3ecBOwgvpbVXqV0UVRri6AHRL5a+FVQ557ArEqLl67414Fhjvh2e+gsLlecXvMJcuh/q8EEeVa5r9PmSHxTTk3iRmVIBUV5mnS8Frd1Vjdq3Opk2PZ8fzGU/R9qv1m9Enl1BdYrZnfLrmhwIDAQAB";
	
	public RobotHandler(RobotConfig robotConfig){//构造函数
		this.robotConfig = robotConfig;
	}
	
	
	@Override
	public void run() {
		
		try{
			//1.初始化
			if(!init()){
				log.error("启动初始化失败，用户资产异常");
				robotConfig.setStatus(0);
				robotDao.save(robotConfig);
				return ;
			}
			while(true){
				robotConfig = robotDao.getById(robotConfig.getId());
				if(robotConfig.getStatus()==0){
					log.error(robotConfig.getTitle()+" 机器人停止，退出线程！");
					return;
				}
				//2.委托
				doEntrust();
				long sleepTime = robotConfig.getFreq();
				if(sleepTime<2000){//最小2000毫秒
					sleepTime = 2000;
				}
				//3.等待间隔
				Thread.sleep(sleepTime);
				//log.error("线程暂停sleepTime:"+sleepTime);
				
			}
			
		}catch(Exception e){
			log.error(e.toString(), e);
		}
	}
	
	/**
	 * 初始化账号信息
	 * @param
	 * @return
	 */
	public boolean init(){
		try{
			transUrl = FileConfig.getValue("trans");
			if(transUrl==null){
				return false;//交易域名未配置
			}else{
				transUrl = transUrl+"/entrust/doEntrustById-"+robotConfig.getCurrency();
			}
			if(robotConfig!=null){
				market = Market.getMarketByName(robotConfig.getCurrency());
				if(market==null) {
					log.error(robotConfig.getCurrency()+" 市场初始化失败！");
					return false;
				}
				int fundsType = market.getIntValue("exchangeBiFundsType");
				payUser = (PayUserBean)Data.GetOne("select * from Pay_User where userName = ?  and fundsType = ? ", new Object[]{robotConfig.getAccount(),fundsType},PayUserBean.class);
				if(payUser!=null && payUser.getBalance().compareTo(BigDecimal.ZERO)>0){//检测用户资产
					
					return true;
				}
			}
			
		}catch(Exception e){
			log.error(e.toString(), e);
		}
		return false;
	}
	
	/**
	 * 下单委托
	 */
	public void doEntrust(){
		
	
		try {
			String data = Cache.Get(robotConfig.getCurrency()+"_datachart5");//
			if(StringUtils.isNotBlank(data)){
				data = data.replaceAll("\\(\\[", "").replaceAll("\\]\\)", "");
			}
			JSONObject depth = JSONObject.parseObject(data);
			JSONArray buyArr = depth.getJSONArray("listDown");
			JSONArray sellArr = depth.getJSONArray("listUp");
			
			double buyOne = 0;//买一
			double sellOne =0;//卖一
			
			if(buyArr!=null && buyArr.size()>0){
				buyOne = buyArr.getJSONArray(0).getDoubleValue(0);
			}
			if(sellArr!=null && sellArr.size()>0){
				sellOne = sellArr.getJSONArray(0).getDoubleValue(0);
			}
			
			Map<String,String> params = generateParam(buyOne,sellOne);
			if(params==null){
				log.error("委托参数非法，不能进行委托！");
				return ;
			}
			String callback = HttpUtil.doPost(transUrl, params, connectTimeout, readTimeout);
			log.info("委托结果："+callback);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			log.error(e.toString(), e);
		}
		
	}
	
	/**
	 * 生成委托单的参数
	 * @return
	 */
	public Map<String,String> generateParam(double buyOne,double sellOne){
		try {
			int isBuy =  new java.util.Random().nextInt(10)%2;//对2进去取余  0卖 1买
			
			double minPirce = buyOne==0?robotConfig.getLowPrice():buyOne;
			double maxPirce = sellOne==0?robotConfig.getHighPrice():sellOne;
			//委托价格在盘口买卖一偏离10%进行，确保及时成交
			if(isBuy==0){//卖
				minPirce = DigitalUtil.mul(minPirce, 0.95,6);
			}else if(isBuy==1){//买
				maxPirce = DigitalUtil.mul(maxPirce, 1.05,6);
			}
			double unitPrice = this.generatePriceAndAmt(minPirce, maxPirce);
			double number = this.generatePriceAndAmt(robotConfig.getMinAmount(), robotConfig.getMaxAmount());
			
			if(unitPrice !=0 || number!=0 ){
				Map<String,String> params = new HashMap<String,String>();
				params.put("unitPrice", String.valueOf(DigitalUtil.round(unitPrice,market.getIntValue("exchangeBixDian"))));
				params.put("number", String.valueOf(DigitalUtil.round(number,market.getIntValue("numberBixDian"))));
				params.put("isBuy", String.valueOf(isBuy));

				String userId = RSACoder.encryptBASE64(RSACoder.encryptByPublicKey(String.valueOf(payUser.getUserId()).getBytes(), pubKey));
				params.put("userId", userId);
				
				return params;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			if(e instanceof InvalidKeySpecException){
				log.error("无效的公钥！", e);
				return null;
			}
			
			log.error(e.toString(), e);
		} 
		return null;
	}
	
	
	/**
	 * 根据 指定区间获取随机数
	 * @param min 最小值
	 * @param max 最大值
	 * @return 返回最小与最大值之间的随机数
	 * @author zhanglinbo 20161219
	 */
	public double generatePriceAndAmt(double min,double max){
		double num = 0d;
		try{
			java.util.Random rand = new Random();
			num = rand.nextDouble()*(max-min)+min;
		}catch(Exception e){
			log.error(e.toString(), e);
		}
		return num;
	}
	

}
