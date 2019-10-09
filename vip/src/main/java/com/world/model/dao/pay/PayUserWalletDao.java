package com.world.model.dao.pay;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.world.data.database.DatabasesUtil;
import com.world.data.mysql.Data;
import com.world.data.mysql.DataDaoSupport;
import com.world.model.dao.user.UserDao;
import com.world.model.entity.coin.CoinProps;
import com.world.model.entity.pay.PayUserWalletBean;
import com.world.model.entity.user.User;
import com.world.util.string.StringUtil;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Elysion
 * @Description:
 * @date 2018/7/25上午10:24
 */
public class PayUserWalletDao extends DataDaoSupport<PayUserWalletBean> {


    public List<PayUserWalletBean> getFunds(String userId) {
        List<PayUserWalletBean> list = super.find("SELECT * FROM pay_user_wallet WHERE userId = ? ORDER BY fundsType", new Object[]{userId}, PayUserWalletBean.class);
        return list;
    }


    /***
     * 获取用户的钱包资金，包含借贷的参数
     * @param userId 用户ID
     */
    public Map<String, PayUserWalletBean> getWalletFundsLoanMap(String userId) {
        List<PayUserWalletBean> list = getFunds(userId);
        Map<String, CoinProps> coinMap = DatabasesUtil.getCoinPropMaps();

        Map<String, PayUserWalletBean> userMaps = new LinkedHashMap<String, PayUserWalletBean>();
        for (Map.Entry<String, CoinProps> entry : coinMap.entrySet()) {
            boolean has = false;
            CoinProps coint = entry.getValue();
            for (PayUserWalletBean payUser : list) {
                payUser.setCoint(coint);
                if (coint.getFundsType() == payUser.getFundsType()) {
                    userMaps.put(coint.getStag(), payUser);
                    has = true;
                    break;
                }
            }
            if (!has) {
                PayUserWalletBean payUser = getById(Integer.parseInt(userId), coint.getFundsType());
                payUser.setCoint(coint);
                payUser.setBalance(BigDecimal.ZERO);
                payUser.setFreez(BigDecimal.ZERO);
                payUser.setFundsType(coint.getFundsType());
                userMaps.put(coint.getStag(), payUser);
            }
        }

        return userMaps;
    }

    /***
     *
     * @param id
     * @return
     */
    //modify by xwz 2016-06-10
    public PayUserWalletBean getById(int id, int fundsType) {
        PayUserWalletBean pub = (PayUserWalletBean) Data.GetOne("select * from pay_user_wallet where userId=? AND fundsType = ?", new Object[]{id, fundsType}, PayUserWalletBean.class);
        try {
            if (pub == null) {
                UserDao ud = new UserDao();
                User user = ud.get(String.valueOf(id));
                if (user != null && StringUtil.exist(user.getUserName())) {
                    //BigDecimal loanLimit = getLoanLimit(fundsType);
                    //modify by xwz 用户注册时多次初始化，导致主键冲突，初始化语句加入ignore，防止主键冲突
                    String insertPayUser = "insert ignore into Pay_User_wallet(userId,userName, fundsType) values(?,?,?)";
                    Data.Insert(insertPayUser, new Object[]{id, user.getUserName() + "", fundsType});
                    pub = (PayUserWalletBean) Data.GetOne("select * from pay_user_wallet where userId=?", new Object[]{id}, PayUserWalletBean.class);
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
    public JSONArray getFundsArray(Map<String, PayUserWalletBean> userMaps) {
        JSONArray funds = new JSONArray();
        Map<String, CoinProps> coinMap = DatabasesUtil.getCoinPropMaps();
        for (Map.Entry<String, CoinProps> entry : coinMap.entrySet()) {
            PayUserWalletBean payUser = userMaps.get(entry.getKey());
            if (payUser != null) {
                CoinProps coint = entry.getValue();
                JSONObject obj = new JSONObject();
                obj.put("balance", payUser.getBalance());//可用余额
                obj.put("freeze", payUser.getFreez().add(payUser.getWithdrawFreeze()));//冻结余额
                obj.put("total", payUser.getTotal());//总金额
                obj.put("fundsType", payUser.getFundsType());//资金类型
                obj.put("unitTag", coint.getUnitTag());//符号
                obj.put("propTag", coint.getPropTag());//属性标签
                obj.put("coinFullNameEn", coint.getPropEnName());//币种全称
                /*start by kinghao 20181121*/
                obj.put("imgUrl", coint.getImgUrl());//币种图标
                /*end*/
                obj.put("canCharge", coint.isCanCharge());
                obj.put("canWithdraw", coint.isCanWithdraw());
                funds.add(obj);
            }
        }

        return funds;
    }


}
