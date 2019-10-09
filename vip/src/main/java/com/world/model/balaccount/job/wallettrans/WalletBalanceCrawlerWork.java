package com.world.model.balaccount.job.wallettrans;

import com.alibaba.fastjson.JSONArray;
import com.world.model.balaccount.dao.ColdWalletBalanceDao;
import com.world.model.balaccount.dao.WalletBalanceCrawlConfigDao;
import com.world.model.balaccount.entity.WalletBalanceCrawlConfigBean;
import com.world.model.dao.task.Worker;
import com.world.util.crawler.balance.BalanceCrawlerUtil;
import com.world.util.crawler.balance.bean.CrawlerParseContext;
import com.world.util.crawler.balance.bean.CrawlerParseTypeEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.*;

/**
 * @ClassName WalletBalanceCrawlerWork
 * @Author hunter
 * @Date 2019-05-27 15:12
 * @Version v1.0.0
 * @Description
 */
public class WalletBalanceCrawlerWork extends Worker {

    WalletBalanceCrawlConfigDao walletBalanceCrawlConfigDao = new WalletBalanceCrawlConfigDao();

    ColdWalletBalanceDao coldWalletBalanceDao = new ColdWalletBalanceDao();

    private static Logger log = LoggerFactory.getLogger(WalletBalanceCrawlerWork.class);

    static String ADDRESS_REGEX = "{address}";

    public WalletBalanceCrawlerWork(String name, String des) {
        super(name, des);
    }

    @Override
    public void run() {
        /*记录核算开始时间*/
        long startTime = System.currentTimeMillis();
        /*现在时间获取*/
        log.info("【钱包余额爬虫】开始执行爬取任务,startTime={}",new Date());
        //查询数据库中配置的待查询余额地址
        Map<Integer, List<WalletBalanceCrawlConfigBean>> configBeanMap = walletBalanceCrawlConfigDao.findAllEnabledConfig();
        //爬虫获取数据
        Map<Integer, BigDecimal> coldWalletBalanceMap = batchCrawlBalance(configBeanMap);
        //入库到mysql
        coldWalletBalanceDao.insertOrUpdateBalance(coldWalletBalanceMap);

        long endTime = System.currentTimeMillis();
        log.info("【钱包余额爬虫】爬取钱包余额结束!!!【核算耗时：{}】", (endTime - startTime));
    }

    /**
     * 批量爬取余额数据
     * @param configBeanMap
     * @return
     */
    private Map<Integer, BigDecimal> batchCrawlBalance(Map<Integer, List<WalletBalanceCrawlConfigBean>> configBeanMap){
        Map<Integer, BigDecimal> coldWalletBalanceMap = new HashMap<>();
        List<Integer> failedList = new LinkedList<>();
        for(Integer fundsType:configBeanMap.keySet()){
            List<WalletBalanceCrawlConfigBean> configBeanList = configBeanMap.get(fundsType);
            if(configBeanList==null||configBeanList.size()==0){
                continue;
            }
            List<CrawlerParseContext> contextList = beanList2Context(configBeanList);
            BigDecimal balance = BalanceCrawlerUtil.parseBalance(contextList);
            if(balance==null){
                failedList.add(fundsType);
            }else{
                coldWalletBalanceMap.put(fundsType,balance);
            }
        }
        log.info("【钱包余额爬虫】爬取失败的币种信息：【{}】", JSONArray.toJSON(failedList).toString());
        return coldWalletBalanceMap;
    }

    /**
     * 配置bean转化为爬虫的context
     * @param configBeanList
     * @return
     */
    private List<CrawlerParseContext> beanList2Context(List<WalletBalanceCrawlConfigBean> configBeanList){
        List<CrawlerParseContext> contextList = new ArrayList<>(5);
        Iterator<WalletBalanceCrawlConfigBean> iterator = configBeanList.iterator();
        while(iterator.hasNext()){
            WalletBalanceCrawlConfigBean bean = iterator.next();
            CrawlerParseContext tempContext = new CrawlerParseContext();
            tempContext.setSelector(bean.getSelector());
            tempContext.setFilterStr(bean.getFilterStr());
            tempContext.setCrawlerParseTypeEnum(CrawlerParseTypeEnum.valueOf(bean.getCrawlerType()));
            tempContext.setUnit(bean.getUnit());
            if(bean.getCrawlerUrl()==null){
                continue;
            }
            if(bean.getWalletAddress()!=null){
                tempContext.setUrl(bean.getCrawlerUrl().replace(ADDRESS_REGEX,bean.getWalletAddress()));
            }else{
                tempContext.setUrl(bean.getCrawlerUrl());
            }
            //增加排序
            tempContext.setSort(bean.getSort());
            //组装post提交所需参数 params1:value1|params2:value2
            if (bean.getParams()!=null){
                String[] strs = bean.getParams().split("\\|");
                Map<String, String> m = new HashMap<String, String>();
                for(String s:strs){
                    String[] ms = s.split(":");
                    if(bean.getWalletAddress()!=null){
                        m.put(ms[0], ms[1].replace(ADDRESS_REGEX,bean.getWalletAddress()));
                    }else{
                        m.put(ms[0], ms[1]);
                    }
                }
                tempContext.setParams(m);
            }
            contextList.add(tempContext);
        }
        return contextList;
    }

    public static void main(String[] args) throws InterruptedException {
        WalletBalanceCrawlerWork walletBalanceCrawlerWork = new WalletBalanceCrawlerWork("WalletBalanceCrawlerWork", "【钱包余额爬虫】");
        walletBalanceCrawlerWork.run();
    }

}
