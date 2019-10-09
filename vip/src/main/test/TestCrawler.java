import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.world.util.crawler.balance.BalanceCrawlerUtil;
import com.world.util.crawler.balance.bean.CrawlerParseContext;
import com.world.util.crawler.balance.bean.CrawlerParseTypeEnum;
import com.world.util.request.HttpUtil;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.UnknownHostException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @ClassName TestCrawler
 * @Author hunter
 * @Date 2019-05-22 17:25
 * @Version v1.0.0
 * @Description
 */
public class TestCrawler {

    static Logger logger = LoggerFactory.getLogger(TestCrawler.class);

    /**
     * 类型1：直接爬取页面中单一的值，并允许过滤掉部分字符串
     * @throws Exception
     */
    public static void btc1() throws Exception{
        Document doc = Jsoup.connect("https://btc.com/17U9sWgueuZqtsPSc2arDKvzQKjX5Zq3Xi")
                .timeout(3000)
                .header("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3")
                .header("Accept-Language","zh,zh-CN;q=0.9,en-US;q=0.8,en;q=0.7")
                .header("Cookie","acw_tc=276aedd915581705560775066e52193d7029b6fa8ca3af8e232b7ee481f9cc; Hm_lvt_02d6fe6e6e4acce5c8d372b5b6e4ef18=1558170561; _ga=GA1.2.499893528.1558170561; _globalGA=GA1.2.2042149300.1558170561; intercom-id-yttqkdli=234212cf-f025-4df6-8d71-999240c76096; io=v8OmhTepEtTTMzE-Cq-t; _gid=GA1.2.1437225043.1558605834; _globalGA_gid=GA1.2.35469307.1558605834; acw_sc__v2=5ce7990ee08d0485c9f8a2e67928fd8bc85be46f; Hm_lpvt_02d6fe6e6e4acce5c8d372b5b6e4ef18=1558683344; acw_sc__v3=5ce79ed11bf6f79a1cadccd01267dc324da23b2a; XSRF-TOKEN=eyJpdiI6Im1pQ25ocTg4anRieHF5OUNpSFBSZEE9PSIsInZhbHVlIjoiWFJsczJqUGdmMlFMR3B5WVZaYW4ycTg1VU5WXC9KNkRNVGwrMWhyQTNmalRCUDVCTXFwTTRHTGRETHpoTmVhb0EiLCJtYWMiOiI1NTA5MzYyNjMwZDdlNmE2ZTE5ZGQ5NDc2ZjVlMTBkN2M5MDgxNDZkOWE2OWRkYTlhOGJjNmU5YzdkNzgyNjM3In0%3D; laravel_session=eyJpdiI6ImZCTmFZZFE2ckgwcXYrXC90OGdFOGd3PT0iLCJ2YWx1ZSI6ImUzWTViOHk4cUdCck5sVmg4b0dLXC85SHoxWllYVVR3RTJ1ZG9UY3dKZGc5bjlxNlVmUU9MOGxmNVhcLzdXQmZwcCIsIm1hYyI6ImFiNjg0NzMxZjBkMWNiY2VlOTQ2MWZiNTNlMTlhNGQwMWI2MWFjNzg5NWYxMzYyYWZmMWM0NDNiM2QyZjI5NWYifQ%3D%3D; 5cda1cnQ4hZuUCdyV54rw5YW4W71hpoREPnluCTZ=eyJpdiI6Ino3RXlRd3RQVlBqZFNhbVJ4bnpFZnc9PSIsInZhbHVlIjoiZGZzdFJ3Vnlsa0RDODNxek9Pc3ZFdVE2TDVVdEUwN0VpTFBlOVM2OXlxQ2NZVnVLKzBZejNiTzVNVGZEWWNhbEVCNW1pbktIN0crc1wvQzhqSTFVVzQ4b2VtQ1V2Mml3Z2FDVDVXemNlcmRCZkgzQ2ZIczB4c01nSTErem9tUXQ1MERMUzFcL0Z3RUE2QUFWanRDTUFPOURaQmhNYWVuY05SQnA4dGoyNjNtcldjR2NZWnVzb1JLUFVSeGRuQm02bEV2Sk93OHNrY29ncDM1NlJcL01KbjNhRkRvQTcrb1NWWTl3K3BqSmhlb1BxWGhsdmo2KzdLZlRQN2F6Wk1ZZ21sbytwSE1HbHZlQW1Md2JHbWJjVXlSelpmblRIdVIrN1p5WVJFRDV1ZWlLMDhlcml0UVdDVkE0anByN29sMk5PQ2VBM0FKbzd6cGJ2VUlTSXNjVXREUmFvUEM4Umh0ZzloYlVSR3Q1cFRvSXZvdXpyVCtOTnphMUx0K0dlYWI2QnZoYUlUSkVDdkZLY2QxTVwvSDV5S2dMSXc9PSIsIm1hYyI6ImE5NmQ0ZmYzYTRjODczYjYyMTlmZjg5NDllYTNiZmUxNzg1NjBmOTczNzAzNzBmNGMzYzVmYmJlOTY0MTVlOWMifQ%3D%3D")
                .userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/74.0.3729.157 Safari/537.36")
                .get();
        System.out.println(doc.toString());
        Elements elements = doc.select("body > div.main-body > div > div:nth-child(2) > div.panel.panel-bm.addressAbstract > div.panel-body > div > div:nth-child(1) > dl:nth-child(2) > dd");

        String prefixBalance = elements.text().replaceAll("BTC","").trim();

        System.out.println("prefixBalance="+prefixBalance);
    }

    /**
     *  JSON 情况 子数组某个对象用 xx[1],子对象直接 XX,字段直接用XX
     *  类型2：通过JSON报文爬取，需要配置JSON读取位置，并允许过滤掉部分字符串
     * @throws Exception
     */
    public static void ltc1() throws Exception{
        String jsonRes = HttpUtil.doGet("https://chainz.cryptoid.info/explorer/address.summary.dws?coin=ltc&id=40631662&r=1623491&fmt.js", null);
        JSONObject jsonObject = null;
        if(jsonRes!=null){
            jsonObject = JSONObject.parseObject(jsonRes);
        }
        System.out.println("jsonRes=="+jsonRes);
        System.out.println("balance=="+jsonObject.getString("balance"));
    }

    public static void etc1() throws Exception{
        Document doc = Jsoup.connect("http://gastracker.io/addr/0x0396577203bb9b046550cfa85604b1fd66786353")
                .timeout(3000)
                .userAgent("Mozilla")
                .get();
        Elements elements = doc.select("body > div.container.addr-details > div:nth-child(2) > div:nth-child(1) > dl > dd:nth-child(6)");
        String prefixBalance = elements.text().replaceAll("Ether","").trim();
        System.out.println("prefixBalance="+prefixBalance);
    }

    public static void eth1() throws Exception{
        Document doc = Jsoup.connect("https://www.yitaifang.com/accounts/0x0ad93988b24d72c51e6de092013611111e103918")
                .timeout(3000)
                .userAgent("Mozilla")
                .get();
        Elements elements = doc.select("body > div.wrapper > div.container > div > div.module.mod-information > div.bd > div.detail > div:nth-child(3) > table > tbody > tr:nth-child(1) > td:nth-child(2) > span");
        String prefixBalance = elements.text().replaceAll("ETH","").trim();
        System.out.println("prefixBalance="+prefixBalance);
    }

    public static BigDecimal requestHtml(String url, String filterStr, String selector,BigDecimal unit) throws Exception{
        logger.info("【钱包余额爬虫】requestHtml 开始爬取url={} filterStr={} selector={}",url,filterStr,selector);
        Document doc = null;
        try {
            doc = Jsoup.connect(url)
                    .timeout(3000)
                    .userAgent("Mozilla")
                    .get();
        }catch (IOException e) {
            logger.error("【钱包余额爬虫】requestHtml 由于网络原因，爬取失败url="+url,e);
            return null;
        }catch (Exception e1){
            logger.error("【钱包余额爬虫】requestHtml 发生非受控异常，url="+url,e1);
            return null;
        }
        logger.debug("【钱包余额爬虫】requestHtml 爬取到原报文信息为{}",doc.outerHtml());
        Elements elements = doc.select(selector);
        String content = elements.text().replaceAll(",","");
        logger.info("【钱包余额爬虫】requestHtml 爬取select后的报文为{}",content);
        if(content==null||"".equalsIgnoreCase(content)){
            return null;
        }
        String prefixBalance = null;
        if (filterStr!=null && !"".equalsIgnoreCase(filterStr)) {
            prefixBalance = content.replaceAll(filterStr,"").trim();
        }else{
            prefixBalance = content.trim();
        }
        if(unit==null){
            unit = new BigDecimal(1);
        }
        BigDecimal balance = new BigDecimal(prefixBalance).divide(unit);
        logger.info("【钱包余额爬虫】requestHtml 解析后的结果为{}",balance);
        return balance;
    }

    /**
     * 类型2：通过JSON报文爬取，需要配置JSON读取位置，并允许过滤掉部分字符串
     * @param url
     * @param filterStr
     * @param selector
     * @throws Exception
     */
    public static BigDecimal requstJson(String url, String filterStr, String selector,BigDecimal unit) throws Exception{
        logger.info("【钱包余额爬虫】requstJson开始爬取url={} filterStr={} selector={}",url,filterStr,selector);
        String jsonRes = null;
        try {
            jsonRes = HttpUtil.doGet(url, null);
        }catch (IOException e) {
            logger.error("【钱包余额爬虫】requstJson 由于网络原因，爬取失败url="+url,e);
            return null;
        }catch (Exception e1){
            logger.error("【钱包余额爬虫】requstJson 发生非受控异常，url="+url,e1);
            return null;
        }
        logger.info("【钱包余额爬虫】requstJson爬取到原报文信息为{}",jsonRes);
        JSON json = null;
        if(jsonRes!=null){
            json = JSONObject.parseObject(jsonRes);
        }
        String[] selectorArray = selector.split("\\>");
        String unFilterRs = null;
        if(selectorArray!=null){
            for(int i=0;i<selectorArray.length;i++){
               if(selectorArray[i]==null || selectorArray[i].equalsIgnoreCase("")){
                   throw new RuntimeException("爬虫读取配置错误，selector="+selector);
               }
                Pattern pattern = Pattern.compile("\\[\\d+\\]");
                Matcher matcher = pattern.matcher(selectorArray[i]);
                Boolean arrayFlag = matcher.find();
                if(arrayFlag){
                    String subSeqStr = matcher.group();
                    logger.info("subSeqStr=="+subSeqStr);
                    String arrayName = selectorArray[i].replaceAll(makeQueryStringAllRegExp(subSeqStr),"").trim();
                    Integer subSeq = Integer.valueOf(subSeqStr.replaceAll("\\[","").replaceAll("\\]","").trim());
                    logger.info("arrayName=="+arrayName);
                    logger.info("subSeq=="+subSeq);
                    JSONArray jsonArray = ((JSONObject) json).getJSONArray(arrayName);
                    json = jsonArray.getJSONObject(subSeq);
                }else{
                    //普通对象或者字段
                    if((i+1)==selectorArray.length){
                        //最后一个是字段
                        unFilterRs = ((JSONObject) json).getString(selectorArray[i].trim());
                    }else{
                        //是对象
                        json = ((JSONObject) json).getJSONObject(selectorArray[i].trim());
                    }
                }
            }
        }
        if(unFilterRs!=null){
            String filtedRs = null;
            if(filterStr!=null && !"".equalsIgnoreCase(filterStr)){
                filtedRs = unFilterRs.replaceAll(filterStr,"");
            }else{
                filtedRs = unFilterRs;
            }
            if(unit==null){
                unit = new BigDecimal(1);
            }
            BigDecimal balance = new BigDecimal(filtedRs).divide(unit);
            logger.info("【钱包余额爬虫】requstJson解析后的结果为{}",balance);
            return balance;
        }
        return null;
    }

    public static void main(String[] args) throws Exception{
        //BTC
//        BigDecimal btcBalance = requstJson("http://www.tokenview.com:8088/addr/b/BTC/17U9sWgueuZqtsPSc2arDKvzQKjX5Zq3Xi",null,"data");
//        logger.info("btcBalance==="+btcBalance.toPlainString());
//        BigDecimal btc1Balance = requestHtml("https://btc.com/17U9sWgueuZqtsPSc2arDKvzQKjX5Zq3Xi","BTC","body > div.main-body > div > div:nth-child(2) > div.panel.panel-bm.addressAbstract > div.panel-body > div > div:nth-child(1) > dl:nth-child(2) > dd");
//        logger.info("btc1Balance==="+btc1Balance.toPlainString());
        //LTC
//        BigDecimal ltcBalance = requstJson("https://chainz.cryptoid.info/explorer/address.summary.dws?coin=ltc&id=40631662&r=1623491&fmt.js",null,"balance");
//        logger.info("ltcBalance==="+ltcBalance.toPlainString());
//        BigDecimal ltc1Balance = requstJson("http://www.tokenview.com:8088/addr/b/LTC/LShWe6ZTCg88AB7F9QVAi5moimpv1ZEeKq",null,"data");
//        logger.info("ltc1Balance==="+ltc1Balance.toPlainString());

        //ETC
//        BigDecimal etcBalance = requestHtml("http://gastracker.io/addr/0x0396577203bb9b046550cfa85604b1fd66786353","Wei","body > div.container.addr-details > div:nth-child(2) > div:nth-child(1) > dl > dd:nth-child(8)",new BigDecimal(1E18));
//        logger.info("etcBalance==="+etcBalance.toPlainString());
//        BigDecimal etc1Balance = requstJson("http://www.tokenview.com:8088/addr/b/ETC/0x0396577203bb9b046550cfa85604b1fd66786353",null,"data",null);
//        logger.info("etc1Balance==="+etc1Balance.toPlainString());

        //ETH
//        BigDecimal eth1Balance = requstJson("http://www.tokenview.com:8088/addr/b/ETH/0x0ad93988b24d72c51e6de092013611111e103918",null,"data",null);
//        logger.info("eth1Balance==="+eth1Balance.toPlainString());
//        BigDecimal ethBalance = requstJson("https://api.yitaifang.com/index/accountInfo/?page=1&address=0x0ad93988b24d72c51e6de092013611111e103918",null,"data>info>balance",new BigDecimal(1E18));
//        logger.info("ethBalance==="+ethBalance.toPlainString());

        //DASH
//        BigDecimal dashBalance = requstJson("http://www.tokenview.com:8088/addr/b/DASH/XwBM8PNnSg9J1eaLhEmvmtp3U5JV6QWEWX",null,"data",null);
//        logger.info("dashBalance==="+dashBalance.toPlainString());
//        BigDecimal dash1Balance = requstJson("https://chainz.cryptoid.info/explorer/address.summary.dws?coin=dash&id=26756042&r=1062265&fmt.js",null,"balance",null);
//        logger.info("dash1Balance==="+dash1Balance.toPlainString());

        //USDT
//        BigDecimal usdt1Balance = requestHtml("https://blockchair.com/bitcoin/address/1PqFABJa9GKqvcSexwSMUYu4Ey7gk3F3Rp",null,"#__layout > div > div > div > div.row > div > div:nth-child(1) > div.col-md-4.table-responsive > table:nth-child(2) > tbody > tr > td:nth-child(2) > span",null);
//        logger.info("usdt1Balance==="+usdt1Balance.toPlainString());
//        BigDecimal usdtBalance = requstJson("http://www.tokenview.com:8088/addr/b/usdt/1PqFABJa9GKqvcSexwSMUYu4Ey7gk3F3Rp",null,"data",null);
//        logger.info("usdtBalance==="+usdtBalance.toPlainString());

//        CrawlerParseContext usdt3Context = new CrawlerParseContext();
//        usdt3Context.setCrawlerParseTypeEnum(CrawlerParseTypeEnum.SIMPLE_HTML);
//        usdt3Context.setSelector("#__layout > div > div > div > div.row > div > div:nth-child(1) > div.col-md-4.table-responsive > table:nth-child(2) > tbody > tr > td:nth-child(2) > span");
//        usdt3Context.setUrl("https://blockchair.com/bitcoin/address/1PqFABJa9GKqvcSexwSMUYu4Ey7gk3F3Rp");
//        BigDecimal usdt3Balance = BalanceCrawlerUtil.parseBalance(usdt3Context);
//        logger.info("usdt3Balance==="+usdt3Balance.toPlainString());

//        CrawlerParseContext usdt4Context = new CrawlerParseContext();
//        usdt4Context.setCrawlerParseTypeEnum(CrawlerParseTypeEnum.SIMPLE_HTML);
//        usdt4Context.setSelector("data");
//        usdt4Context.setUrl("http://www.tokenview.com:8088/addr/b/usdt/1PqFABJa9GKqvcSexwSMUYu4Ey7gk3F3Rp");
//        BigDecimal usdt4Balance = BalanceCrawlerUtil.parseBalance(usdt4Context);
//        logger.info("usdt4Balance==="+usdt4Balance.toPlainString());



        //VDS
//        CrawlerParseContext vds1Context = new CrawlerParseContext();
//        vds1Context.setCrawlerParseTypeEnum(CrawlerParseTypeEnum.SIMPLE_JSON);
//        vds1Context.setSelector("data>amount");
//        vds1Context.setUrl("https://vdsblock.io/index.php/api/wallet-address/VcSGHfHMPv6L5sdYH6LQ6MqsGwHgkar9zh8");
//        BigDecimal vds1Balance = BalanceCrawlerUtil.parseBalance( vds1Context);
//        logger.info("vds1Balance==="+vds1Balance.toPlainString());
//
//        CrawlerParseContext vds2Context = new CrawlerParseContext();
//        vds2Context.setCrawlerParseTypeEnum(CrawlerParseTypeEnum.SIMPLE_JSON);
//        vds2Context.setSelector("balance");
//        vds2Context.setUrl("https://www.vds.cool/api/BTC/livenet/address/VcSGHfHMPv6L5sdYH6LQ6MqsGwHgkar9zh8/balance");
//        vds2Context.setUnit(new BigDecimal(1E8));
//        BigDecimal vds2Balance = BalanceCrawlerUtil.parseBalance( vds2Context);
//        logger.info("vds2Balance==="+vds2Balance.toPlainString());

        //MGC
//        CrawlerParseContext mgc1Context = new CrawlerParseContext();
//        mgc1Context.setCrawlerParseTypeEnum(CrawlerParseTypeEnum.SIMPLE_JSON);
//        mgc1Context.setSelector("data>result[0]>balance");
//        mgc1Context.setUrl("https://api.yitaifang.com/index/tokenBalances/?address=0x174bfa6600bf90c885c7c01c7031389ed1461ab9&page=1&a=0x0ad93988b24d72c51e6de092013611111e103918");
//        mgc1Context.setUnit(new BigDecimal(1E18));
//        BigDecimal mgc1Balance = BalanceCrawlerUtil.parseBalance( mgc1Context);
//        logger.info("mgc1Balance==="+mgc1Balance.toPlainString());


//        CrawlerParseContext mgc2Context = new CrawlerParseContext();
//        mgc2Context.setCrawlerParseTypeEnum(CrawlerParseTypeEnum.SIMPLE_HTML);
//        mgc2Context.setSelector("#ContentPlaceHolder1_divFilteredHolderBalance");
//        mgc2Context.setFilterStr("Balance,MGC");
//        mgc2Context.setUrl("https://etherscan.io/token/0x174BfA6600Bf90C885c7c01C7031389ed1461Ab9?a=0x0ad93988b24d72c51e6de092013611111e103918");
//        BigDecimal mgc2Balance = BalanceCrawlerUtil.parseBalance(mgc2Context);
//        logger.info("mgc2Balance==="+mgc2Balance.toPlainString());


        //ZRX
//        CrawlerParseContext mgc1Context = new CrawlerParseContext();
//        mgc1Context.setCrawlerParseTypeEnum(CrawlerParseTypeEnum.SIMPLE_JSON);
//        mgc1Context.setSelector("data>result[0]>balance");
//        mgc1Context.setUrl("https://api.yitaifang.com/index/tokenBalances/?address=0xe41d2489571d322189246dafa5ebde1f4699f498&page=1&a=0x0ad93988b24d72c51e6de092013611111e103918");
//        mgc1Context.setUnit(new BigDecimal(1E18));
//        BigDecimal mgc1Balance = BalanceCrawlerUtil.parseBalance( mgc1Context);
//        logger.info("zrx1Balance==="+mgc1Balance.toPlainString());


//        CrawlerParseContext mgc2Context = new CrawlerParseContext();
//        mgc2Context.setCrawlerParseTypeEnum(CrawlerParseTypeEnum.SIMPLE_HTML);
//        mgc2Context.setSelector("#ContentPlaceHolder1_divFilteredHolderBalance");
//        mgc2Context.setFilterStr("Balance,ZRX");
//        mgc2Context.setUrl("https://etherscan.io/token/0xe41d2489571d322189246dafa5ebde1f4699f498?a=0x0ad93988b24d72c51e6de092013611111e103918");
//        BigDecimal mgc2Balance = BalanceCrawlerUtil.parseBalance(mgc2Context);
//        logger.info("zrx2Balance==="+mgc2Balance.toPlainString());


        //OMG
//        CrawlerParseContext mgc2Context = new CrawlerParseContext();
//        mgc2Context.setCrawlerParseTypeEnum(CrawlerParseTypeEnum.SIMPLE_HTML);
//        mgc2Context.setSelector("#ContentPlaceHolder1_divFilteredHolderBalance");
//        mgc2Context.setFilterStr("Balance,OMG");
//        mgc2Context.setUrl("https://etherscan.io/token/0xd26114cd6ee289accf82350c8d8487fedb8a0c07?a=0x0ad93988b24d72c51e6de092013611111e103918");
//        BigDecimal mgc2Balance = BalanceCrawlerUtil.parseBalance(mgc2Context);
//        logger.info("omgBalance==="+mgc2Balance.toPlainString());


        //QTUM
//        CrawlerParseContext mgc2Context = new CrawlerParseContext();
//        mgc2Context.setCrawlerParseTypeEnum(CrawlerParseTypeEnum.SIMPLE_JSON);
//        mgc2Context.setSelector("balance");
//        mgc2Context.setUrl("https://explorer.qtum.org/insight-api/addr/QfdcGKY9Gz1HCCqhpMWQihEG5d6TEKpp35/?noTxList=1");
//        BigDecimal mgc2Balance = BalanceCrawlerUtil.parseBalance(mgc2Context);
//        logger.info("QTUMBalance==="+mgc2Balance);

//        CrawlerParseContext mgc2Context = new CrawlerParseContext();
//        mgc2Context.setCrawlerParseTypeEnum(CrawlerParseTypeEnum.SIMPLE_HTML);
//        mgc2Context.setSelector("#app > div.background > section > div.card.section-card > div.card-body.info-table > div:nth-child(2) > div.column.info-value.monospace");
//        mgc2Context.setFilterStr("QTUM");
//        mgc2Context.setUrl("https://qtum.info/address/QfdcGKY9Gz1HCCqhpMWQihEG5d6TEKpp35");
//        BigDecimal mgc2Balance = BalanceCrawlerUtil.parseBalance(mgc2Context);
//        logger.info("QTUMBalance==="+mgc2Balance.toPlainString());



       //ELF
//        CrawlerParseContext mgc2Context = new CrawlerParseContext();
//        mgc2Context.setCrawlerParseTypeEnum(CrawlerParseTypeEnum.SIMPLE_HTML);
//        mgc2Context.setSelector("#ContentPlaceHolder1_divFilteredHolderBalance");
//        mgc2Context.setFilterStr("Balance,ELF");
//        mgc2Context.setUrl("https://etherscan.io/token/0xbf2179859fc6d5bee9bf9158632dc51678a4100e?a=0x0ad93988b24d72c51e6de092013611111e103918");
//        BigDecimal mgc2Balance = BalanceCrawlerUtil.parseBalance(mgc2Context);
//        logger.info("ELFBalance==="+mgc2Balance.toPlainString());


//        CrawlerParseContext mgc2Context = new CrawlerParseContext();
//        mgc2Context.setCrawlerParseTypeEnum(CrawlerParseTypeEnum.SIMPLE_JSON);
//        mgc2Context.setSelector("data>balanceOf");
//        mgc2Context.setUnit(new BigDecimal(1E18));
//        mgc2Context.setUrl("https://api.yitaifang.com/index/balanceOf/?contractAddress=0xbf2179859fc6d5bee9bf9158632dc51678a4100e&address=0x0ad93988b24d72c51e6de092013611111e103918&_=1559025237896");
//        BigDecimal mgc2Balance = BalanceCrawlerUtil.parseBalance(mgc2Context);
//        logger.info("ELFBalance==="+mgc2Balance.toPlainString());


        //LRC
//        CrawlerParseContext mgc2Context = new CrawlerParseContext();
//        mgc2Context.setCrawlerParseTypeEnum(CrawlerParseTypeEnum.SIMPLE_HTML);
//        mgc2Context.setSelector("#ContentPlaceHolder1_divFilteredHolderBalance");
//        mgc2Context.setFilterStr("Balance,LRC");
//        mgc2Context.setUrl("https://etherscan.io/token/0xef68e7c694f40c8202821edf525de3782458639f?a=0x0ad93988b24d72c51e6de092013611111e103918");
//        BigDecimal mgc2Balance = BalanceCrawlerUtil.parseBalance(mgc2Context);
//        logger.info("LRCBalance==="+mgc2Balance.toPlainString());


//        CrawlerParseContext mgc2Context = new CrawlerParseContext();
//        mgc2Context.setCrawlerParseTypeEnum(CrawlerParseTypeEnum.SIMPLE_JSON);
//        mgc2Context.setSelector("data>balanceOf");
//        mgc2Context.setUrl("https://api.yitaifang.com/index/balanceOf/?contractAddress=0xef68e7c694f40c8202821edf525de3782458639f&address=0x0ad93988b24d72c51e6de092013611111e103918&_=1559025237896");
//        BigDecimal mgc2Balance = BalanceCrawlerUtil.parseBalance(mgc2Context);
//        logger.info("LRCBalance==="+mgc2Balance.toPlainString());
//


       // KNC
//        CrawlerParseContext mgc2Context = new CrawlerParseContext();
//        mgc2Context.setCrawlerParseTypeEnum(CrawlerParseTypeEnum.SIMPLE_HTML);
//        mgc2Context.setSelector("#ContentPlaceHolder1_divFilteredHolderBalance");
//        mgc2Context.setFilterStr("Balance,KNC");
//        mgc2Context.setUrl("https://etherscan.io/token/0xdd974d5c2e2928dea5f71b9825b8b646686bd200?a=0x0ad93988b24d72c51e6de092013611111e103918");
//        BigDecimal mgc2Balance = BalanceCrawlerUtil.parseBalance(mgc2Context);
//        logger.info("KNCBalance==="+mgc2Balance);


//
//        CrawlerParseContext mgc2Context = new CrawlerParseContext();
//        mgc2Context.setCrawlerParseTypeEnum(CrawlerParseTypeEnum.SIMPLE_JSON);
//        mgc2Context.setSelector("data>balanceOf");
//        mgc2Context.setUnit(new BigDecimal(1E18));
//        mgc2Context.setUrl("https://api.yitaifang.com/index/balanceOf/?contractAddress=0xdd974d5c2e2928dea5f71b9825b8b646686bd200&address=0x0ad93988b24d72c51e6de092013611111e103918&_=1559031840164");
//        BigDecimal mgc2Balance = BalanceCrawlerUtil.parseBalance(mgc2Context);
//        logger.info("KNCBalance==="+mgc2Balance.toPlainString());



        // LINK
//        CrawlerParseContext mgc2Context = new CrawlerParseContext();
//        mgc2Context.setCrawlerParseTypeEnum(CrawlerParseTypeEnum.SIMPLE_HTML);
//        mgc2Context.setSelector("#ContentPlaceHolder1_divFilteredHolderBalance");
//        mgc2Context.setFilterStr("Balance,LINK");
//        mgc2Context.setUrl("https://etherscan.io/token/0x514910771af9ca656af840dff83e8264ecf986ca?a=0x0ad93988b24d72c51e6de092013611111e103918");
//        BigDecimal mgc2Balance = BalanceCrawlerUtil.parseBalance(mgc2Context);
//        logger.info("LINKBalance==="+mgc2Balance);

//         CrawlerParseContext mgc2Context = new CrawlerParseContext();
//        mgc2Context.setCrawlerParseTypeEnum(CrawlerParseTypeEnum.SIMPLE_JSON);
//        mgc2Context.setSelector("data>balanceOf");
//        mgc2Context.setUnit(new BigDecimal(1E18));
//        mgc2Context.setUrl("https://api.yitaifang.com/index/balanceOf/?contractAddress=0x514910771af9ca656af840dff83e8264ecf986ca&address=0x0ad93988b24d72c51e6de092013611111e103918&_=1559031840164");
//        BigDecimal mgc2Balance = BalanceCrawlerUtil.parseBalance(mgc2Context);
//        logger.info("LINKBalance==="+mgc2Balance);
//


//        // SNT
//        CrawlerParseContext mgc1Context = new CrawlerParseContext();
//        mgc1Context.setCrawlerParseTypeEnum(CrawlerParseTypeEnum.SIMPLE_HTML);
//        mgc1Context.setSelector("#ContentPlaceHolder1_divFilteredHolderBalance");
//        mgc1Context.setFilterStr("Balance,SNT");
//        mgc1Context.setUrl("https://etherscan.io/token/0x744d70fdbe2ba4cf95131626614a1763df805b9e?a=0x0ad93988b24d72c51e6de092013611111e103918");
//        BigDecimal mgc1Balance = BalanceCrawlerUtil.parseBalance(mgc1Context);
//        logger.info("SNTBalance==="+mgc1Balance);
//
//         CrawlerParseContext mgc2Context = new CrawlerParseContext();
//        mgc2Context.setCrawlerParseTypeEnum(CrawlerParseTypeEnum.SIMPLE_JSON);
//        mgc2Context.setSelector("data>balanceOf");
//        mgc2Context.setUnit(new BigDecimal(1E18));
//        mgc2Context.setUrl("https://api.yitaifang.com/index/balanceOf/?contractAddress=0x744d70fdbe2ba4cf95131626614a1763df805b9e&address=0x0ad93988b24d72c51e6de092013611111e103918&_=1559031840164");
//        BigDecimal mgc2Balance = BalanceCrawlerUtil.parseBalance(mgc2Context);
//        logger.info("SNTBalance==="+mgc2Balance);

//        // MANA
//        CrawlerParseContext mgc1Context = new CrawlerParseContext();
//        mgc1Context.setCrawlerParseTypeEnum(CrawlerParseTypeEnum.SIMPLE_HTML);
//        mgc1Context.setSelector("#ContentPlaceHolder1_divFilteredHolderBalance");
//        mgc1Context.setFilterStr("Balance,MANA");
//        mgc1Context.setUrl("https://etherscan.io/token/0x0f5d2fb29fb7d3cfee444a200298f468908cc942?a=0x0ad93988b24d72c51e6de092013611111e103918");
//        BigDecimal mgc1Balance = BalanceCrawlerUtil.parseBalance(mgc1Context);
//        logger.info("MANABalance==="+mgc1Balance);
//
//        CrawlerParseContext mgc2Context = new CrawlerParseContext();
//        mgc2Context.setCrawlerParseTypeEnum(CrawlerParseTypeEnum.SIMPLE_JSON);
//        mgc2Context.setSelector("data>balanceOf");
//        mgc2Context.setUnit(new BigDecimal(1E18));
//        mgc2Context.setUrl("https://api.yitaifang.com/index/balanceOf/?contractAddress=0x0f5d2fb29fb7d3cfee444a200298f468908cc942&address=0x0ad93988b24d72c51e6de092013611111e103918&_=1559031840164");
//        BigDecimal mgc2Balance = BalanceCrawlerUtil.parseBalance(mgc2Context);
//        logger.info("MANABalance==="+mgc2Balance);

//        // MCO
//        CrawlerParseContext mgc1Context = new CrawlerParseContext();
//        mgc1Context.setCrawlerParseTypeEnum(CrawlerParseTypeEnum.SIMPLE_HTML);
//        mgc1Context.setSelector("#ContentPlaceHolder1_divFilteredHolderBalance");
//        mgc1Context.setFilterStr("Balance,MCO");
//        mgc1Context.setUrl("https://etherscan.io/token/0xb63b606ac810a52cca15e44bb630fd42d8d1d83d?a=0x0ad93988b24d72c51e6de092013611111e103918");
//        BigDecimal mgc1Balance = BalanceCrawlerUtil.parseBalance(mgc1Context);
//        logger.info("MCOBalance==="+mgc1Balance);
//
//        CrawlerParseContext mgc2Context = new CrawlerParseContext();
//        mgc2Context.setCrawlerParseTypeEnum(CrawlerParseTypeEnum.SIMPLE_JSON);
//        mgc2Context.setSelector("data>balanceOf");
//        mgc2Context.setUnit(new BigDecimal(1E8));
//        mgc2Context.setUrl("https://api.yitaifang.com/index/balanceOf/?contractAddress=0xb63b606ac810a52cca15e44bb630fd42d8d1d83d&address=0x0ad93988b24d72c51e6de092013611111e103918&_=1559031840164");
//        BigDecimal mgc2Balance = BalanceCrawlerUtil.parseBalance(mgc2Context);
//        logger.info("MCOBalance==="+mgc2Balance);


        // DGD
        CrawlerParseContext mgc1Context = new CrawlerParseContext();
        mgc1Context.setCrawlerParseTypeEnum(CrawlerParseTypeEnum.SIMPLE_HTML);
        mgc1Context.setSelector("#ContentPlaceHolder1_divFilteredHolderBalance");
        mgc1Context.setFilterStr("Balance,DGD");
        mgc1Context.setUrl("https://etherscan.io/token/0xe0b7927c4af23765cb51314a0e0521a9645f0e2a?a=0x0ad93988b24d72c51e6de092013611111e103918");
        BigDecimal mgc1Balance = BalanceCrawlerUtil.parseBalance(mgc1Context);
        logger.info("DGDBalance==="+mgc1Balance);
//
//        CrawlerParseContext mgc2Context = new CrawlerParseContext();
//        mgc2Context.setCrawlerParseTypeEnum(CrawlerParseTypeEnum.SIMPLE_JSON);
//        mgc2Context.setSelector("data>balanceOf");
//        mgc2Context.setUrl("https://api.yitaifang.com/index/balanceOf/?contractAddress=0xe0b7927c4af23765cb51314a0e0521a9645f0e2a&address=0x0ad93988b24d72c51e6de092013611111e103918&_=1559031840164");
//        BigDecimal mgc2Balance = BalanceCrawlerUtil.parseBalance(mgc2Context);
//        logger.info("DGDBalance==="+mgc2Balance);


    }

    public static void test(){

        String oriStr = "ABC[1]";

        Pattern pattern = Pattern.compile("\\[\\d+\\]");
        Matcher matcher = pattern.matcher(oriStr);
        Boolean arrayFlag = matcher.find();
        if(arrayFlag){
            String subSeqStr = matcher.group();
            System.out.println(subSeqStr);
            String arrayName = oriStr.replaceAll(makeQueryStringAllRegExp(subSeqStr),"");
            Integer subSeq = Integer.valueOf(subSeqStr.replaceAll("\\[","").replaceAll("\\]",""));
            System.out.println(arrayName);
            System.out.println(subSeq);
        }else{

        }

    }


    /**
     * 转义正则特殊字符 （$()*+.[]?\^{}
     * \\需要第一个替换，否则replace方法替换时会有逻辑bug
     */
    public static String makeQueryStringAllRegExp(String str) {
        if(StringUtils.isBlank(str)){
            return str;
        }

        return str.replace("\\", "\\\\").replace("*", "\\*")
                .replace("+", "\\+").replace("|", "\\|")
                .replace("{", "\\{").replace("}", "\\}")
                .replace("(", "\\(").replace(")", "\\)")
                .replace("^", "\\^").replace("$", "\\$")
                .replace("[", "\\[").replace("]", "\\]")
                .replace("?", "\\?").replace(",", "\\,")
                .replace(".", "\\.").replace("&", "\\&");
    }


}
