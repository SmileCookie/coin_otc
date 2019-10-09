package com.world.model.dao.otc;

import cn.hutool.core.util.NumberUtil;
import com.world.data.database.FormatUtil;
import com.world.data.mysql.Data;
import com.world.data.mysql.DataDaoSupport;
import com.world.model.entity.otc.OtcFrozenBill;
import com.world.model.enums.TradeFrozenType;
import org.apache.commons.lang.StringUtils;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.List;

/**
 * <p>@Description: </p>
 *
 * @author guankaili
 * @date 2018/10/232:47 PM
 */
public class OtcFrozenBillDao extends DataDaoSupport<OtcFrozenBill> {

    /**
     * 查询冻结记录列表
     * @param pageIndex
     * @param pageSize
     * @param coinTypeId
     * @param userId
     * @return
     */
    public List<OtcFrozenBill> quertOtcFrozenBill(int pageIndex,int pageSize,String coinTypeId,String userId){

        String sql = "SELECT ofb.id id, ofb.coin_type_id coinTypeId, ofb.user_id userId, ofb.amount , ofb.frozen_amount frozenAmount, ofb.action , ofb.action_name actionName, ofb.refer , ofb.memo , ofb.add_time addTime, ofb.frozen_fee frozenFee, oct.coin_bix_dian coinBixDian , upper(oct.coin_name) coinTypeName FROM otc_frozen_bill ofb LEFT JOIN otc_coin_type oct ON oct.id = ofb.coin_type_id where  ofb.user_id = ?";
        StringBuffer sb = new StringBuffer(sql);
        if(StringUtils.isNotEmpty(coinTypeId)){
            sb.append(" and ofb.coin_type_id = "+Integer.valueOf(coinTypeId));
        }
        sb.append(" order by ofb.add_time desc limit ?,?");
        List<OtcFrozenBill> otcFrozenBills =  Data.QueryT("messi_otc",sb.toString(), new Object[]{userId,(pageIndex*pageSize),pageSize},OtcFrozenBill.class);
        log.info("OTC冻结记录SQL:"+sb.toString()+",冻结记录数："+String.valueOf(otcFrozenBills.size()));
        if(!CollectionUtils.isEmpty(otcFrozenBills)){
            for(OtcFrozenBill otcFrozenBill : otcFrozenBills){
                BigDecimal amount = FormatUtil.cutBigDecimal(NumberUtil.add(otcFrozenBill.getAmount(),otcFrozenBill.getFrozenFee()),otcFrozenBill.getCoinBixDian());
                BigDecimal currentAmount = FormatUtil.cutBigDecimal(otcFrozenBill.getFrozenAmount(),otcFrozenBill.getCoinBixDian());
                if(TradeFrozenType.AD_CANCEL.getKey() == otcFrozenBill.getAction() || TradeFrozenType.TRADE_CANCEL.getKey() == otcFrozenBill.getAction() || TradeFrozenType.TRADE_SUCCESS.getKey() == otcFrozenBill.getAction()){

                    otcFrozenBill.setAmountStr("-".concat(amount.toString()));
                }else {
                    otcFrozenBill.setAmountStr("+".concat(amount.toString()));
                }
                otcFrozenBill.setCurrAmountStr(currentAmount.toString());
            }
        }
        return otcFrozenBills;
    }
    /**
     * 查询冻结记录数量
     * @param coinTypeId
     * @param userId
     * @return
     */
    public int getOtcFrozenBillCount(String coinTypeId,String userId){
        int count = 0;
        String sql = "SELECT count(1) FROM otc_frozen_bill ofb LEFT JOIN otc_coin_type oct ON oct.id = ofb.coin_type_id where  ofb.user_id = ?";
        StringBuffer sb = new StringBuffer(sql);
        if(StringUtils.isNotEmpty(coinTypeId)){
            sb.append(" and ofb.coin_type_id = "+Integer.valueOf(coinTypeId));
        }
        List countList = (List) Data.GetOne("messi_otc",sb.toString(), new Object[]{userId});
        if(countList != null && countList.size() > 0){
            String countStr = countList.get(0).toString();
            if(StringUtils.isNotEmpty(countStr)){
                count = Integer.valueOf(countStr);
            }
        }
        return count;
    }

    /**
     * 获取冻结记录详情
     * @param id
     * @return
     */
    public OtcFrozenBill findOtcFrozenBill(String id) {
        String sql = "SELECT ofb.id , ofb.coin_type_id coinTypeId, ofb.user_id userId, ofb.amount , ofb.frozen_amount frozenAmount, ofb.action , ofb.action_name actionName, ofb.refer , ofb.memo , ofb.add_time addTime, ofb.frozen_fee frozenFee, oct.coin_bix_dian coinBixDian , upper(oct.coin_name) coinTypeName FROM otc_frozen_bill ofb LEFT JOIN otc_coin_type oct ON oct.id = ofb.coin_type_id where ofb.id = ?";
        OtcFrozenBill otcFrozenBill = (OtcFrozenBill) Data.GetOne("messi_otc",sql, new Object[]{id},OtcFrozenBill.class);
        if(null != otcFrozenBill){
            BigDecimal amount = FormatUtil.cutBigDecimal(NumberUtil.add(otcFrozenBill.getAmount(),otcFrozenBill.getFrozenFee()),otcFrozenBill.getCoinBixDian());
            BigDecimal currentAmount = FormatUtil.cutBigDecimal(otcFrozenBill.getFrozenAmount(),otcFrozenBill.getCoinBixDian());
            if(TradeFrozenType.AD_CANCEL.getKey() == otcFrozenBill.getAction() || TradeFrozenType.TRADE_CANCEL.getKey() == otcFrozenBill.getAction() || TradeFrozenType.TRADE_SUCCESS.getKey() == otcFrozenBill.getAction()){

                otcFrozenBill.setAmountStr("-".concat(amount.toString()));
            }else {
                otcFrozenBill.setAmountStr("+".concat(amount.toString()));
            }
            otcFrozenBill.setCurrAmountStr(currentAmount.toString());
        }
        return otcFrozenBill;
    }
}
