package com.world.controller.manage.account;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.api.config.ApiConfig;
import com.messi.user.core.FeignContainer;
import com.messi.user.feign.CapitalTransferApiService;
import com.messi.user.vo.CoinTypeVO;
import com.world.cache.Cache;
import com.world.constant.Const;
import com.world.data.mysql.Data;
import com.world.model.dao.pay.PayUserOtcDao;
import com.world.model.entity.pay.PayUserBean;
import com.world.model.entity.pay.PayUserOtcBean;
import com.world.web.Page;
import com.world.web.action.UserAction;
import net.sf.json.JsonConfig;
import org.apache.commons.lang.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Elysion
 * @Description: 资金划转
 * @date 2018/8/1上午11:02
 */
public class Transfer extends UserAction {

    private static PayUserOtcDao putDao = new PayUserOtcDao();
    /**
     * 配合前端改造资金划转
     */
    @Page(Viewer = JSON)
    public void transfer() {
        int from = intParam("from");
        int to = intParam("to");
        BigDecimal amount = BigDecimal.ZERO;
        try {
            amount = new BigDecimal(param("amount"));
        }catch (Exception e){
            json(L("划转失败"), false, "", true);
            return;
        }
        if (amount.compareTo(BigDecimal.ZERO) != 1) {
            json(L("划转失败"), false, "", true);
            return;
        }
        int fundsType = intParam("coinTypeId");
        String userId = userIdStr();
        if (0 == from) {
            json(L("划转失败"), false, "", true);
            return;
        }
        if (0 == to) {
            json(L("划转失败"), false, "", true);
            return;
        }
        if (StringUtils.isEmpty(userId)) {
            json(L("划转失败"), false, "", true);
            return;
        }
        if (0 == fundsType) {
            json(L("划转失败"), false, "", true);
            return;
        }
        String url = ApiConfig.getValue("usecenter.url");
        FeignContainer container = new FeignContainer(url+"/capitalTransfer");
        CapitalTransferApiService capitalTransferApiService = container.getFeignClient(CapitalTransferApiService.class);
        Boolean flag = capitalTransferApiService.transfer(from, to, amount, fundsType, userId);
        if (flag) {
            json(L("划转成功"), true, "", true);
        } else {
            json(L("划转失败"), false, "", true);
        }

    }

    @Page(Viewer = JSON)
    public void transferOtc() {
        String userId = userIdStr();
        List<PayUserOtcBean> list = putDao.getFunds(userId);
        List<CoinTypeVO> ctVoList = new ArrayList<>();
        String coinType = Cache.Get(Const.OTC_COIN_TYPE_KEY);
        net.sf.json.JSONArray jsonArray = net.sf.json.JSONArray.fromObject(coinType);
        ctVoList = net.sf.json.JSONArray.toList(jsonArray,new CoinTypeVO(),new JsonConfig());
        if(null != list && list.size() > 0){
            for(PayUserOtcBean bean : list){
                for(CoinTypeVO vo : ctVoList){
                    if(bean.getCoinTypeId().intValue() == vo.getFundsType()){
                        vo.setBalance(bean.getBalance());
                        break;
                    }
                }
            }
        }
        json("",true,net.sf.json.JSONArray.fromObject(ctVoList).toString());

    }









    @Page(Viewer = JSON)
    public void transferView() {
        String fromPayId = param("fromPayId");
        String toPayId = param("toPayId");
        String fundsType = param("fundsType");
        String userId = userIdStr();
        JSONArray result = new JSONArray();
        if (StringUtils.isEmpty(fundsType)) {
            json("请出入正确的币种", false, "", true);
            return;
        }
        String dbName = Const.payUserTypeMap.get(fromPayId);
        String sql = " where fundsType = ";
        if(Const.payUserTypeOtc.equals(dbName)){
            sql = " where coinTypeId = ";
        }
        if (StringUtils.isNotEmpty(dbName)) {
            PayUserBean payUserBean = (PayUserBean) Data.GetOne("select balance from " + dbName + sql + fundsType + " and userId = " + userId, null,PayUserBean.class);
            if(null == payUserBean){
                payUserBean = new  PayUserBean();
                payUserBean.setBalance(BigDecimal.ZERO);
            }
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("fromPayUser", payUserBean.getBalance().toPlainString());
            result.add(jsonObject);
        } else {
            json("请出入正确的钱包", false, "", true);
            return;
        }
        String dbToName = Const.payUserTypeMap.get(toPayId);
        String sqls = " where fundsType = ";
        if(Const.payUserTypeOtc.equals(dbToName)){
            sqls = " where coinTypeId = ";
        }
        if (StringUtils.isNotEmpty(dbToName)) {
            PayUserBean payUserBean = (PayUserBean) Data.GetOne("select balance from " + dbToName + sqls + fundsType + " and userId = " + userId, null,PayUserBean.class);
            if(null == payUserBean){
                payUserBean = new  PayUserBean();
                payUserBean.setBalance(BigDecimal.ZERO);
            }
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("toPayUser", payUserBean.getBalance().toPlainString());
            result.add(jsonObject);
        } else {
            json("请出入正确的钱包", false, "", true);
            return;
        }
        json("success", true, result.toString(), true);
        return;

    }


}
