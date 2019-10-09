package com.world.model.financial.dao;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.world.data.mongo.MongoDao;
import com.world.model.financial.entity.SettlementDetail;
import com.world.util.DigitalUtil;

public class SettlementDetailDao extends MongoDao<SettlementDetail, String>{

    public double[] stats(String currency, int isIn, int unusually, Timestamp start, Timestamp end){
        double total = 0;
        double mtotal = 0;
        try {
            //DBObject key = new BasicDBObject("userId",true);
            List<BasicDBObject> values = new ArrayList<>();
            if(start != null) {
                values.add(new BasicDBObject("date", new BasicDBObject("$gte", start)));
            }
            if(end != null) {
                values.add(new BasicDBObject("date", new BasicDBObject("$lte", end)));
            }

            values.add(new BasicDBObject("currency", currency));

            values.add(new BasicDBObject("isIn", isIn));

            if(unusually >=0){
                values.add(new BasicDBObject("unusually", unusually));
            }

            DBObject cond = new BasicDBObject("$and", values);

            DBObject initial = new BasicDBObject("money", 0);
            initial.put("mMoney", 0);

            String reduce = "function(item,prev){ if(item.money && item.money > 0){prev.money+=item.money;} if(item.mMoney && item.mMoney > 0){prev.mMoney+=item.mMoney;} }";

            DBObject group = this.getCollection().group(null, cond, initial, reduce);
            Iterator<String> it = group.keySet().iterator();
            while (it.hasNext()) {
                BasicDBObject l = (BasicDBObject) group.get(it.next());
                int scale = 8;
                total = DigitalUtil.add(total, DigitalUtil.round((Double) l.get("money"), scale));
                mtotal = DigitalUtil.add(mtotal, DigitalUtil.round((Double) l.get("mMoney"), scale));
            }


           /* DBObject groupFields = new BasicDBObject("_id", "");
            groupFields.put("count", new BasicDBObject( "$sum", "$money"));
            DBObject group = new BasicDBObject("$group", groupFields );
            AggregationOutput output = this.getCollection().aggregate(group, cond);
            log.info( output.getCommandResult() );*/
        } catch (Exception e) {
            log.error(e.toString(), e);
        }

        return new double[]{total, mtotal};
    }


    public static void main(String[] args) {
        double[] arr1 = new SettlementDetailDao().stats(
                "cny", 1, -1, Timestamp.valueOf("2016-10-25 00:00:00"), Timestamp.valueOf("2016-10-28 00:00:00"));
        log.info(arr1[0] + ", " + arr1[1]);

        double[] arr2 = new SettlementDetailDao().stats(
                "ltc", 0, -1, Timestamp.valueOf("2016-10-25 00:00:00"), Timestamp.valueOf("2016-10-28 00:00:00"));
        log.info(arr2[0] + ", " + arr2[1]);
    }
}
