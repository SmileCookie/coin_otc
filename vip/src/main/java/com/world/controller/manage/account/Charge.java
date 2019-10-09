package com.world.controller.manage.account;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.world.data.database.DatabasesUtil;
import com.world.model.dao.pay.KeyDao;
import com.world.model.entity.coin.CoinProps;
import com.world.model.entity.pay.KeyBean;
import com.world.web.Page;
import com.world.web.action.UserAction;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@SuppressWarnings("serial")
public class Charge extends UserAction {
	KeyDao keyDao = new KeyDao();

	@Page
	public void index() {
	    //TODO jsp页面跳转，之后下掉jsp
        try {
            response.sendRedirect(VIP_DOMAIN);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
	}

	@Page(Viewer = JSON)
	public void usenew(){
		initLoginUser();
		int userId=userId();
		List<KeyBean> btcKeys = keyDao.getRechargeKeys(userId, loginUser.getUserName());
		if(btcKeys.size() >= 3){
			json(L("您的充值地址数量已达上限!"), false, "");
			return;
		}

		if(keyDao.getNewKey(userId, loginUser.getUserName()) != null){
			json(L("新增钱包地址成功"), true, "");
		}else{
			json(L("更新地址失败"), false, "");
		}
	}

	/**
	 * 充值页面返回币种
	 */
	@Page(Viewer = JSON)
	public void rechargeCoinInfo() {
		initLoginUser();
		int userId = userId();
		Map<String, CoinProps> coinMaps = DatabasesUtil.getNewCoinPropMaps();//币种Map
		Map<String, Object> returnMap = new HashMap<>();
		List<Map<String, Object>> coinList = new ArrayList<>();
		List<Map<String, Object>> usdtCoinList = new ArrayList<>();

		try{
			if(coinMaps!=null){
				Iterator<Map.Entry<String,CoinProps>> iter = coinMaps.entrySet().iterator();
				while(iter.hasNext()){
					Map<String, Object> tmpReturnMap = new HashMap<>();
					Map.Entry<String,CoinProps> entry = iter.next();
					try{
						CoinProps tmpCoint = entry.getValue();
						String key = entry.getKey();
						if(StringUtils.isNotEmpty(key)){
							keyDao.setCoint(tmpCoint);
							KeyBean tmpKeyBean = keyDao.getRechargeKey(userId, loginUser.getUserName());//地址对象
							if(null != tmpKeyBean){
								tmpReturnMap.put("coinName", tmpCoint.getDatabaseKey());//币种名称
								tmpReturnMap.put("address", tmpKeyBean.getKeyPre());//币种地址
								tmpReturnMap.put("canCharge",tmpCoint.isCanCharge());
								/*Start by guankaili 20190108 添加地址标签 */
								tmpReturnMap.put("addressTag", tmpKeyBean.getAddressTag());//地址标签
								/*End*/
								tmpReturnMap.put("fundsType",tmpCoint.getFundsType());
								tmpReturnMap.put("inConfirmTimes", tmpCoint.getInConfirmTimes());//充值确认次数
								tmpReturnMap.put("outConfirmTimes", tmpCoint.getOutConfirmTimes());//允许提现的确认次数
								if(tmpCoint.getFundsType() == 10){
									usdtCoinList.add(tmpReturnMap);
								}else{
									coinList.add(tmpReturnMap);
								}
							}

						}
						if (tmpCoint.isERC()){
							KeyBean ethKeys = keyDao.getERCRechargeKey(userId, loginUser.getUserName());
							//如果该用户没有被分配eth充值地址  需要分配给该用户一个地址
							if (ethKeys == null) {
								if(keyDao.updateEthKeys(userId, loginUser.getUserName()) != null){
//									json(L("用户分配地址成功"), true, "");
									log.info("用户分配地址成功");
								}else{
//									json(L("用户分配地址失败"), false, "");
									log.info("用户分配地址失败");
								}
							}else {
								tmpReturnMap.put("coinName", tmpCoint.getDatabaseKey());//币种名称
								tmpReturnMap.put("address", ethKeys.getKeyPre());//币种地址
								/*Start by guankaili 20190108 添加地址标签 */
								tmpReturnMap.put("addressTag", ethKeys.getAddressTag());//地址标签
								/*End*/
								tmpReturnMap.put("canCharge",tmpCoint.isCanCharge());
								tmpReturnMap.put("inConfirmTimes", tmpCoint.getInConfirmTimes());//充值确认次数
								tmpReturnMap.put("fundsType",tmpCoint.getAgreement());
								tmpReturnMap.put("outConfirmTimes", tmpCoint.getOutConfirmTimes());//允许提现的确认次数
								if(tmpCoint.getFundsType() == 10){
									usdtCoinList.add(tmpReturnMap);
								}else{
									coinList.add(tmpReturnMap);
								}
							}
						}
					}catch (Exception e){
						log.info("获取用户"+entry.getKey()+"充值地址信息出错：" + e.toString());
					}

				}
			}
			returnMap.put("list",coinList);
			returnMap.put("usdtlist",usdtCoinList);
			if(!googleNoSetTips()){
				returnMap.put("noPhoneNoGoogle",true);
			}

			if(null != returnMap) {
				json("", true, com.alibaba.fastjson.JSON.toJSONString(returnMap, SerializerFeature.DisableCircularReferenceDetect));
			}else{
				json("", true, com.alibaba.fastjson.JSON.toJSONString(returnMap,SerializerFeature.DisableCircularReferenceDetect));
			}
		}catch(Exception e){
			e.printStackTrace();
			log.error("获取充值页面信息出错，错误信息：" + e.toString());
			json("", false, "");
		}

	}


}