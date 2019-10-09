package com.world.model.coin.service;

import com.world.model.coin.dao.CoinInfoDao;
import com.world.model.entity.coin.CoinInfo;
import net.sf.json.JSONObject;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;

/**
 * @ClassName CoinInfoService
 * @Description
 * @Author kinghao
 * @Date 2018/8/15   15:54
 * @Version 1.0
 * @Description
 */
public class CoinInfoService {

    private CoinInfoDao coinInfoDao = new CoinInfoDao();

    public CoinInfo getInfoCoin(CoinInfo coinInfo) {
        if (coinInfo.getCoinNameJson() == null || coinInfo.getCoinNameJson().length() < 3
                || coinInfo.getInternationalization() == null) {
            return null;
        }
        //国际化数据CN、EN、HK 转小写  对应数据库数据
        coinInfo.setInternationalization(coinInfo.getInternationalization().toLowerCase());
        //获取数据库对应的币种数据
        List<CoinInfo> coinInfos = coinInfoDao.getCoinIntroduction(coinInfo.getCoinNameJson().toUpperCase());
        if (CollectionUtils.isEmpty(coinInfos)) {
            return null;
        }
        for (CoinInfo coin : coinInfos) {
            JSONObject coinNameJson = JSONObject.fromObject(coin.getCoinNameJson());
            //验证数据库币种信息时候符合传入的币种信息
            if (coinInfo.getCoinNameJson().toUpperCase().equals(coinNameJson.get(coinInfo.getInternationalization()).toString())) {
                JSONObject coinContentJson = JSONObject.fromObject(coin.getCoinContentJson());
                JSONObject coinFullNameJson = JSONObject.fromObject(coin.getCoinFullNameJson());
                JSONObject introductionJson = JSONObject.fromObject(coin.getIntroductionJson());
                JSONObject urlJson = JSONObject.fromObject(coin.getUrlJson());
                // 处理当前币种传入的国际化 中文简体cn  中文繁体hk 英文cn
                coin.setCoinNameJson(coinNameJson.get(coinInfo.getInternationalization()).toString());
                coin.setCoinContentJson(coinContentJson.get(coinInfo.getInternationalization()).toString());
                coin.setCoinFullNameJson(coinFullNameJson.get(coinInfo.getInternationalization()).toString());
                coin.setIntroductionJson(introductionJson.get(coinInfo.getInternationalization()).toString());
                coin.setUrlJson(urlJson.get(coinInfo.getInternationalization()).toString());
                return coin;
            }
        }
        return null;
    }

    public static void main(String[] args) {


        CoinInfoService coinInfoService = new CoinInfoService();
        CoinInfo coinInfo = new CoinInfo();
        coinInfo.setCoinNameJson("BTC");
        coinInfo.setInternationalization("cn");
        CoinInfo coin = coinInfoService.getInfoCoin(coinInfo);
        JSONObject jsonObject = JSONObject.fromObject(coin);
        System.out.println("=====>>>>>>"+jsonObject.toString());
    }

}
