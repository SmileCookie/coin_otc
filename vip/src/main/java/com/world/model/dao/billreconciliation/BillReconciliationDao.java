package com.world.model.dao.billreconciliation;

import com.world.constant.Const;
import com.world.data.database.DatabasesUtil;
import com.world.data.mysql.Bean;
import com.world.data.mysql.Data;
import com.world.data.mysql.DataDaoSupport;
import com.world.data.mysql.OneSql;
import com.world.model.entity.billreconciliation.Billreconciliation;
import com.world.model.entity.coin.CoinProps;
import com.world.model.entity.reconciliation.Reconciliation;
import com.world.util.date.TimeUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.world.model.entity.bill.BillType.*;

public class BillReconciliationDao extends DataDaoSupport<Reconciliation> {

    Logger logger = Logger.getLogger(BillReconciliationDao.class);

    /**
     * 查询币币账户对账表
     *
     * @param beginTime
     * @param endTime
     * @return
     */
    public List<Billreconciliation> getList(Date beginTime, Date endTime, int fundsType) {

        List<Billreconciliation> billReconciliationList = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        try {

            /**
             * 查询范围为 messi_ods库 billreconciliation表 【昨日0点到24点】 的数据
             */
            Date begin = TimeUtil.dayBegin(beginTime);
            Date end = TimeUtil.dayEnd(endTime);

            StringBuilder where = new StringBuilder();
            where.append(" AND reportDate >='" + sdf.format(begin) + "'");
            where.append(" AND reportDate <'" + sdf.format(end) + "'");
            if (fundsType > 0) {
                where.append(" AND fundsType ='" + fundsType + "'");
            }
            String whereStr = where.toString();
            if (whereStr.length() > 0) {
                whereStr = " where " + whereStr.substring(4);
            }
            String billreconciliation_sql = "select * from billreconciliation" + whereStr + " ";
            log.info("billreconciliation_sql:"+billreconciliation_sql);

            billReconciliationList = Data.QueryT("messi_ods", billreconciliation_sql, new Object[]{}, Billreconciliation.class);
        } catch (Exception e) {
            logger.error(e.toString(), e);
        }
        return billReconciliationList;
    }

}
