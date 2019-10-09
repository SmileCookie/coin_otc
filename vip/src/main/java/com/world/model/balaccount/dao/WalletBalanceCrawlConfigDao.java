package com.world.model.balaccount.dao;

import com.world.data.mysql.DataDaoSupport;
import com.world.model.balaccount.entity.WalletBalanceCrawlConfigBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName WalletBalanceCrawlConfigDao
 * @Author hunter
 * @Date 2019-05-27 13:39
 * @Version v1.0.0
 * @Description
 */
public class WalletBalanceCrawlConfigDao extends DataDaoSupport<WalletBalanceCrawlConfigBean> {

    private static final long serialVersionUID = 1L;

    static Logger logger = LoggerFactory.getLogger(WalletBalanceCrawlConfigDao.class);

    public Map<Integer,List<WalletBalanceCrawlConfigBean>> findAllEnabledConfig(){

        Map<Integer,List<WalletBalanceCrawlConfigBean>> configBeanMaps = new HashMap<>();

        String sql = "select * from walletBalanceCrawlConfig where status = 1 and walletType = 1";
        List<WalletBalanceCrawlConfigBean> configBeanList = null;
        try {
            configBeanList = super.find(sql,null, WalletBalanceCrawlConfigBean.class);
        } catch (Exception e) {
            logger.error("SQL查询出错：select * from walletBalanceCrawlConfig where status = 1 ",e);
        }
        if(configBeanList==null||configBeanList.size()==0){
            return configBeanMaps;
        }
        //按照fundsType进行分组
        configBeanMaps = configBeanList.stream().filter(bean->(bean!=null&&bean.getCrawlerType()!=null)).collect(Collectors.groupingBy(WalletBalanceCrawlConfigBean::getFundsType));
        return configBeanMaps;
    }

}
