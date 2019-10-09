package com.tenstar.timer.entrust;

import com.tenstar.TimeUtil;
import com.world.data.mysql.Data;
import com.world.model.Market;

/**
 * Created by renfei on 17/5/24.
 */
public enum CaesarDataManager {
    INSTANCE;

    public void saveTrades(Market m){
        String sql = "INSERT INTO transrecord (unitPrice, totalPrice, numbers, entrustIdBuy, userIdBuy, entrustIdSell, userIdSell, types, times, timeMinute,webIdBuy,webIdSell,actStatus) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)";
//        Data.Insert(m.db, sql, new Object[] {
//                er.getPrice(),
//                thisMoney,
//                thisNumbers,
//                er.getId(),
//                er.getUserId(),
//                beb.getId(),
//                beb.getUserId(),
//                beb.getTypes(),//当前记录是买行为还是卖行为
//                TimeUtil.getNow().getTime(),
//                TimeUtil.getMinuteFirst().getTime(),
//                er.getWebId(),
//                beb.getWebId(),
//                1
//        })
    }

}
