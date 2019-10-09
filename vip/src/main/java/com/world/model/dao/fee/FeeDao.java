package com.world.model.dao.fee;

import com.world.data.mysql.DataDaoSupport;
import com.world.data.mysql.OneSql;
import com.world.model.entity.financial.fee.Fee;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class FeeDao extends DataDaoSupport{

	/**
	 * 获取收续费收益统计数据
	 * @param currency
	 * @param feeType
	 * @param surverType
	 * @param start
	 * @param end
	 * @return
	 */
	public String findListSql(String currency, int feeType, int surverType, Timestamp start, Timestamp end){
		String format = "";
		if(surverType == 1)
			format = "%Y年%m月%d日";
		if(surverType == 2)
			format = "%Y年%v周";
		if(surverType == 3)
			format = "%Y年%m月";
		if(surverType == 4)
			format = "%Y年";
		StringBuffer buffer = new StringBuffer();
		if(StringUtils.isNotEmpty(format)){
			buffer.append("select id,DATE_FORMAT(FROM_UNIXTIME(time/1000),'"+format+"') as timestr, currency, type, time, sum(amount) as amount from fee where 1=1");
			if(StringUtils.isNotEmpty(currency)){
				buffer.append(" and currency='").append(currency).append("'");
			}
			if(feeType != 0){
				buffer.append(" and type=" + feeType);
			}
			buffer.append(" and time between ");
			buffer.append(start.getTime());
			buffer.append(" and ");
			buffer.append(end.getTime());
			if(StringUtils.isNotEmpty(currency)){
				buffer.append(" group by timestr");
			}else{
				buffer.append(" group by currency,timestr");
			}
			buffer.append(" order by timestr,currency");
		}
		if(StringUtils.isNotEmpty(buffer.toString())){
			return buffer.toString();
		}
		return null;
	}

	/**
	 * 增加费用记录
	 * @param userId 用户ID
	 * @param type 费用类型(1、交易手续费，2、借贷手续费，3、提现手续费)
	 * @param amount 费用金额
	 * @param currency 货币类型(CNY、BTC、LTC、ETH、ETC)
	 * @author zhanglinbo 20160810
	 */
//	public OneSql addFee(int userId, int type, BigDecimal amount, String currency ) {
//
//		return	new OneSql("INSERT INTO Fee(userId,type,currency,amount,time) values(?,?,?,?,?) ", 1,
//				new Object[]{userId, type,currency, amount,System.currentTimeMillis()});
//	}


	/**
	 * 增加费用记录
	 * @param userId 用户ID
	 * @param type 费用类型(1、交易手续费，2、借贷手续费，3、提现手续费)
	 * @param amount 费用金额
	 * @param currency 货币类型(CNY、BTC、LTC、ETH、ETC)
	 * @author zhanglinbo 20160810
	 */
	/**
	 * start by xwz 20170704
	 * @param userId
	 * @param type
	 * @param amount
	 * @param currency
	 * @param market
     * @param flag
     * @return
     */
	public OneSql addFee(int userId, int type, BigDecimal amount, String currency,long transRecordId,String market,int flag) {
		return	new OneSql("INSERT INTO Fee(userId,type,currency,amount,time,transRecordId,market,flag) values(?,?,?,?,?,?,?,?) ", 1,
				new Object[]{userId, type,currency, amount,System.currentTimeMillis(),transRecordId,market,flag});
	}
	/*end*/

	public Fee getMaxIdByTypeAndCurrency(int type, String currency) {
		String sql = "select max(id) as id from fee where type=? and currency=? and flag=0";
		List<Fee> list = super.find(sql, new Object[]{type, currency}, Fee.class);
		if (CollectionUtils.isEmpty(list)) {
			return new Fee();
		}

		return list.get(0);
	}

	public Fee getSumFeeByMaxId(int type, String currency, int maxId) {
		String sql = "select sum(amount) as amount from fee where type=? and currency=? and id<=? and flag=0";
		List<Fee> list = super.find(sql, new Object[]{type, currency, maxId}, Fee.class);
		if (CollectionUtils.isEmpty(list)) {
			return new Fee();
		}

		return list.get(0);
	}

    public Fee getBackCapitalMaxId() {
        String sql = "select max(id) as id from fee where flag=0 and type=1";
        List<Fee> list = super.find(sql, new Object[]{}, Fee.class);
        if (CollectionUtils.isEmpty(list)) {
            return new Fee();
        }

        return list.get(0);
    }

    /**
     * 获取用于回购的手续费列表
     */
    public List<Fee> getBackCapitalFeeByMaxId(int maxId) {
        String sql = "select id,userId,currency,amount,transRecordId,transRecordTime,numberBi,exchangeBi,market,totalPrice,numbers,unitPrice from fee where flag=0 and type=1 and id<=?";
        List<Fee> list = super.find(sql, new Object[]{maxId}, Fee.class);
        if (CollectionUtils.isEmpty(list)) {
            return new ArrayList<>();
        }

        return list;
    }

    public OneSql updateFlagDone(int maxId){
        return new OneSql("update fee set flag=1 where flag=0 and type=1 and id<=? ", -2, new Object[]{maxId});
    }

    public OneSql updateFlagUnDone(int minId, int maxId){
        return new OneSql("update fee set flag=0 where flag=1 and type=1 and id>=? and id<=? ", -2, new Object[]{minId, maxId});
    }

}
