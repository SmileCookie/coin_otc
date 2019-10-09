package com.world.model.dao.vote;

import com.alibaba.fastjson.JSONObject;
import com.world.data.mysql.DataDaoSupport;
import com.world.data.mysql.Query;
import com.world.model.entity.vote.Coin;
import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class CoinDao extends DataDaoSupport<Coin> {
    Logger logger = Logger.getLogger(CoinDao.class);


    /**
     * 获取币种投票结果
     *
     * @param activityId
     * @param lan
     * @param orderBy
     * @return
     */
    public List<Coin> getList(String activityId, String lan, String orderBy) {

        List<Coin> coinList = new ArrayList<>();
        String sql = "";
        try {
            if (null == orderBy || "".equals(orderBy)) {
                sql = "SELECT a.activityId ,b.voteCount,b.realCount,c.* FROM activity a LEFT JOIN activityCoin b ON a.activityId = b.activityId LEFT JOIN coin c ON b.coinId = c.coinId where a.activityId = ? order by b.voteCount DESC ";
            } else {
                sql = "SELECT a.activityId ,b.voteCount,b.realCount,c.* FROM activity a LEFT JOIN activityCoin b ON a.activityId = b.activityId LEFT JOIN coin c ON b.coinId = c.coinId where a.activityId = ? order by c.coinId ASC ";
            }
            long totalCoin = count(sql, new Object[]{activityId});
            if (totalCoin > 0) {
                coinList = find(sql, new Object[]{activityId}, Coin.class);
                int count = 0;
                for (Coin coin : coinList) {
                    count = count + coin.getVoteCount();
                    if (!"".equals(lan) && null != lan) {
                        coin.setCoinNameJson((JSONObject.parseObject(coin.getCoinNameJson())).getString(lan));
                        coin.setCoinFullNameJson((JSONObject.parseObject(coin.getCoinFullNameJson())).getString(lan));
                        coin.setUrlJson((JSONObject.parseObject(coin.getUrlJson())).getString(lan));
                        coin.setCoinContentJson((JSONObject.parseObject(coin.getCoinContentJson())).getString(lan));
                    }
                }
                for (Coin coin2 : coinList) {
                    if (count != 0) {
                        double f1 = new BigDecimal((float) coin2.getVoteCount() / count).setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue();
                        double f2 = (float) (f1 * 100);
                        BigDecimal f = new BigDecimal(f2);
                        double f3 = f.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                        DecimalFormat df = new DecimalFormat("0.00");
                        coin2.setRate(df.format(f3));
                    } else {
                        coin2.setRate("0.00");
                    }

                }

            }

        } catch (Exception e) {
            logger.error("查询活动币库信息失败!", e);
        }
        return coinList;
    }

    /**
     * 更新投票
     *
     * @param activityId
     * @param voteId
     * @return
     */
    public Boolean update(String activityId, String voteId) {
        Boolean flag = false;
        try {
            Query coinQuery = getQuery();
            coinQuery.setSql("update activityCoin SET voteCount=voteCount+1,realCount=realCount+1 where activityId =? and coinId=?");
            coinQuery.setParams(new Object[]{activityId, Integer.parseInt(voteId)});
            int a = coinQuery.update();
            if (a != -1) {
                flag = true;
            }
            return flag;
        } catch (Exception e) {
            logger.error("投票失败", e);
            return false;
        }
    }

    /**
     * 刷票
     *
     * @param activityId
     * @param voteId
     * @param brashCount
     * @return
     */
    public int brashVote(String activityId, int voteId, int brashCount) {
        int a = -1;
        try {
            Query coinQuery = getQuery();
            coinQuery.setSql("update activityCoin SET voteCount=voteCount+? where activityId =? and coinId=?");
            coinQuery.setParams(new Object[]{brashCount, activityId, voteId});
            a = coinQuery.update();
        } catch (Exception e) {
            log.error("刷票失败", e);
            e.printStackTrace();
        }
        return a;
    }

    /**
     * 统计币库数量
     *
     * @return
     */
    public int coinCount() {
        int row = -1;
        try {
            Query coinQuery = getQuery();
            String sql = "select count(*) from coin";
            row = count(sql, new Object[]{});
        } catch (Exception e) {
            logger.error("统计币库失败", e);
            e.printStackTrace();
        }
        return row;
    }


    public static void main(String[] args) {
       /* int b=1099;
        int a=93;
        double f1 = new BigDecimal((float)a/b).setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue();
        System.out.println("ddd==="+f1);
        double f2=(float)(f1*100);
        BigDecimal  f= new BigDecimal(f2);
        double   f3   =   f.setScale(2,   BigDecimal.ROUND_HALF_UP).doubleValue();*/
        CoinDao coinDao = new CoinDao();
        System.out.println("++++++++++++" + coinDao.coinCount());

    }


}
