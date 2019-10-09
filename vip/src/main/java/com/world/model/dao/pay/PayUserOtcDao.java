package com.world.model.dao.pay;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.messi.user.vo.CoinTypeVO;
import com.world.cache.Cache;
import com.world.constant.Const;
import com.world.data.database.DatabasesUtil;
import com.world.data.mysql.Data;
import com.world.data.mysql.DataDaoSupport;
import com.world.model.dao.user.UserDao;
import com.world.model.entity.coin.CoinProps;
import com.world.model.entity.pay.PayUserOtcBean;
import com.world.model.entity.user.User;
import com.world.util.string.StringUtil;
import net.sf.json.JsonConfig;
import org.apache.commons.lang.StringUtils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Elysion
 * @Description:
 * @date 2018/7/25下午5:18
 */
public class PayUserOtcDao extends DataDaoSupport<PayUserOtcBean> {

    public List<PayUserOtcBean> getFunds(String userId){
        List<PayUserOtcBean> list = super.find("SELECT * FROM pay_user_otc WHERE userId = ? ORDER BY balance desc,coinTypeId", new Object[]{userId}, PayUserOtcBean.class);
        return list;
    }


    /***
     * 获取用户的资金，包含借贷的参数
     * @param userId 用户ID
     */
    public Map<String, PayUserOtcBean> getFundsLoanMap(String userId) {
        List<PayUserOtcBean> list = getFunds(userId);

        Map<String, PayUserOtcBean> userMaps = new LinkedHashMap<String, PayUserOtcBean>();

        List<CoinTypeVO> ctVoList = new ArrayList<>();
        try {
            String coinType = Cache.Get(Const.OTC_COIN_TYPE_KEY);
            if (StringUtils.isBlank(coinType)) {
                log.error("获取缓存中OTC币种配置为空");
                return userMaps;
            }
            net.sf.json.JSONArray jsonArray = net.sf.json.JSONArray.fromObject(coinType);
            ctVoList = net.sf.json.JSONArray.toList(jsonArray, new CoinTypeVO(), new JsonConfig());
        } catch (Exception e) {
            log.error("获取缓存中OTC币种失败", e);
        }

        for (CoinTypeVO coin : ctVoList) {
            boolean has = false;
            for (PayUserOtcBean payUser : list) {
                if (coin.getFundsType() == payUser.getCoinTypeId().intValue()) {
                    userMaps.put(coin.getCoinName().toLowerCase(), payUser);
                    has = true;
                    break;
                }
            }
            if (!has) {
                PayUserOtcBean payUser = getById(userId, coin.getFundsType());
                payUser.setBalance(BigDecimal.ZERO);
                payUser.setFrozenFee(BigDecimal.ZERO);
                payUser.setFrozenTrade(BigDecimal.ZERO);
                payUser.setFrozenWithdraw(BigDecimal.ZERO);
                payUser.setCoinTypeId(BigInteger.valueOf(coin.getCoinType()));
                userMaps.put(coin.getCoinName().toLowerCase(), payUser);
            }
        }
        return userMaps;
    }

    /***
     *
     * @param id
     * @return
     */
    //modify chendi
    public PayUserOtcBean getById(String id, int fundsType) {
        PayUserOtcBean pub = (PayUserOtcBean) Data.GetOne("select * from pay_user_otc where userId=? AND coinTypeId = ?", new Object[] { id, fundsType }, PayUserOtcBean.class);
        try {
            if (pub == null) {
                UserDao ud = new UserDao();
                User user = ud.get(id);
                if (user != null && StringUtil.exist(user.getUserName())) {
                    //BigDecimal loanLimit = getLoanLimit(fundsType);
                    //modify by xwz 用户注册时多次初始化，导致主键冲突，初始化语句加入ignore，防止主键冲突
                    String insertPayUser = "insert ignore into Pay_User_otc(userId,userName, coinTypeId) values(?,?,?)";
                    Data.Insert(insertPayUser, new Object[] { id, user.getUserName()+"", fundsType });
                    pub = (PayUserOtcBean) Data.GetOne("select * from pay_user_otc where userId=?", new Object[] { id }, PayUserOtcBean.class);
                }
            }
        } catch (Exception e) {
            log.error(e.toString(), e);
        }
        pub.setCoint(DatabasesUtil.coinProps(fundsType));
        return pub;
    }


    /***
     * 获取用户的资金，包含借贷的参数
     * @param userMaps
     */
    public JSONArray getFundsArray(Map<String, PayUserOtcBean> userMaps){
        JSONArray funds = new JSONArray();
        Map<String, CoinProps> coinMap = DatabasesUtil.getCoinPropMaps();
        for(Map.Entry<String, CoinProps> entry : coinMap.entrySet()){
            PayUserOtcBean payUser = userMaps.get(entry.getKey());
            if (payUser != null) {
                CoinProps coint = entry.getValue();
                JSONObject obj = new JSONObject();
                obj.put("balance", payUser.getBalance());//可用余额
                obj.put("freeze", payUser.getFrozenFee().add(payUser.getFrozenTrade()).add(payUser.getFrozenWithdraw()));//冻结余额
                obj.put("storeFreez", payUser.getStoreFreez());//可用余额
                obj.put("total", payUser.getTotal());//总金额
                obj.put("fundsType", payUser.getCoinTypeId());//资金类型
                obj.put("unitTag", coint.getUnitTag());//符号
                obj.put("propTag", coint.getPropTag());//属性标签
                obj.put("coinFullNameEn", coint.getPropEnName());//币种全称
                obj.put("imgUrl", coint.getImgUrl());//币种图标
                funds.add(obj);
            }
        }

        return funds;
    }



}
