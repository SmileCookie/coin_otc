package com.world.model.daos.world;

import com.match.domain.TransRecordInfo;
import com.tenstar.timer.TransRecordBean;
import com.world.data.mysql.DataDaoSupport;
import com.world.data.mysql.OneSql;
import com.world.model.Market;

import java.math.BigDecimal;

public class FeeDao extends DataDaoSupport implements MysqlDatabase{





	/**
	 * 增加费用记录
	 * @param userId 用户ID
	 * @param type 费用类型(1、交易手续费，2、借贷手续费，3、提现手续费)
	 * @param amount 费用金额
	 * @param currency 货币类型(CNY、BTC、LTC、ETH、ETC)
	 * @author zhanglinbo 20160810
	 */
	public OneSql addFee(int userId, int type, BigDecimal amount, String currency, TransRecordBean transRecord, Market m, int flag) {

		return	new OneSql("INSERT INTO Fee(userId,type,currency,amount,time,transRecordId,transRecordTime,market,numberBi,exchangeBi,totalPrice,numbers,unitPrice,flag) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?) ", 1,
				new Object[]{userId, type, currency, amount, System.currentTimeMillis(), transRecord.getTransRecordId(), transRecord.getTimes(), m.db, m.numberBi, m.exchangeBi, transRecord.getTotalPrice(), transRecord.getNumbers(), transRecord.getUnitPrice(), flag}, WORLD_DATABASE);

	}

    public OneSql addFee(int userId, int type, BigDecimal amount, String currency, TransRecordInfo transRecord, Market m, int flag) {
        return	new OneSql("INSERT INTO Fee(userId,type,currency,amount,time,transRecordId,transRecordTime,market,numberBi,exchangeBi,totalPrice,numbers,unitPrice,flag) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?) ", 1,
                new Object[]{userId, type, currency, amount, System.currentTimeMillis(), transRecord.getTransRecordId(), transRecord.getTimes(), m.db, m.numberBi, m.exchangeBi, transRecord.getTotalPrice(), transRecord.getNumbers(), transRecord.getUnitPrice(), flag}, WORLD_DATABASE);

//        return	new OneSql("INSERT INTO Fee(userId,type,currency,amount,time,transRecordId,transRecordTime,market,numberBi,exchangeBi,totalPrice,numbers,unitPrice,flag) " +
//                "values(" + userId + "," + type + ",'" + currency + "'," + amount + "," + System.currentTimeMillis() + "," + transRecord.getTransRecordId() + "," + transRecord.getTimes() +
//                ",'" + m.db + "','" + m.numberBi + "','" + m.exchangeBi + "'," + transRecord.getTotalPrice() + "," + transRecord.getNumbers() + "," + transRecord.getUnitPrice() + "," + flag + ") ", 1,
//                new Object[]{}, WORLD_DATABASE);
    }
}
