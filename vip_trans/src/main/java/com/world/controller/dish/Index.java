package com.world.controller.dish;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.tenstar.timer.dish.DishDataManager;
import com.world.dish.DishDataCacheService;
import com.world.model.Market;
import com.world.util.string.StringUtil;
import com.world.web.Page;
import com.world.web.action.UserAction;

import java.io.IOException;

public class Index extends UserAction  {

    @Page
    public void index(){

        try {
            response.sendRedirect("/btc");
        } catch (IOException e) {
            log.error(e.toString(), e);
        }

    }

    /**
     * 接收参数： length 档位长度 5，10，20，50
     * 		   depth 深度 0.01,0.1,1
     *         jsoncallback js回调函数
     * 调用示例：/dish/data-btc_cny?depth=0.01&length=5
     * 获取委托和交易历史记录数据，用于档位显示和深度显示 如果深度为0.01 则按档位数返回数据，如果非0.01 按合并深度返回数据
     * {"buys":[[150627,0.1,0.1],[149020,14.625,1901.713],[149019,14.307,1916.02],[149018,5.425,1921.445]],
     * 		"sells":[[150629,11.881,11.881],[149017,11.633,1933.078]]} 数据格式：[价格,数量,累计深度]
     * add by zhanglinbo
     * 2016-04-14
     */
    @Page(Viewer = JSON )
    public void data(){
        try {

            //json回调函数
            String jsoncallback = request.getParameter("jsoncallback");
            //档位查询 5,10,20,50
            int length=intParam("length");
            //深度合并查询 0.01， 0.1， 1
            double depth = doubleParam("depth");
            String userId = userIdStr();
            //市场名称
            String marketName = GetPrama(0);
            //获取市场参数
            Market m=Market.getMarkeByName(marketName);
            if(m==null){
                Response.append(jsoncallback+"([{error market}])");
                return;
            }

            String strDepth = DishDataManager.getValidDepth(marketName, depth);

            //从缓存获取5档深度合并交易委托数据
            /**
             * // TODO: 2017/7/6 suxinjie add
             *
             * 1. 先获取合并的盘口数据,默认是0.001
             * 2. 如果盘口合并深度为0,则对数据内容进行替换
             * 3. 获取没有合并的盘口数据(没有交易量,涨跌幅等信息)
             * 4. 将没有合并的盘口数据的listUp和listDown替换到第一步获取的数据中
             *
             * 暂时只能这么做,减少前端改动量.
             */

            String dish = DishDataCacheService.getMegerDepthData(marketName, strDepth);
            if(dish==null){
                Response.append(jsoncallback+"([{no data}])");
                return;
            }

            if (depth <= 0d) {

                // 先取出合并数据{"currentIsBuy":true,"dayNumber":0.2,"exchangeRate":{"CNY":17667.78000,"EUR":2291.24368,"USD":2600.0,"AUD":3421.53495,"GBP":2009.82629},"lastTime":1499313513658,"currentPrice":0.020720000,"high":0.02072,"totalBtc":0.00414,"low":0.02072,"listUp":[[0.03,2.431,2.431]],"listDown":[[0.01,9.753,9.753]]}
                // 再取出没合并数据 ([{"lastTime":1499313513662,"currentPrice":0.020720000,"high":0.02072,"low":0.02072,"currentIsBuy":true,"dayNumber":0.2,"totalBtc":0.2,"listUp":[[0.02072,0.146],[0.02081,2.285]],"listDown":[[0.01899,7.882],[0.01853,0.62],[0.01852,0.971],[0.0185,0.28]],"transction":[]}])
                // 再替换listUp和listDown
                // FIXME: 2017/8/4 因为需要隐藏市场深度,这里返回50条,前段截断25条
                String dish20 = DishDataCacheService.getDishDepthData(marketName, 60);
                if (dish20 == null) {
                    Response.append(jsoncallback+"([{no data}])");
                    return;
                }

                //替换listUp和listDown
//                dish20 = dish20.substring(1,dish20.length()-1);
                JSONObject data = JSONObject.parseObject(dish20);
                Object listUp = data.get("listUp");
                Object listDown = data.get("listDown");

                JSONObject jsonObject = JSONObject.parseObject(dish);
                jsonObject.put("listUp", listUp);
                jsonObject.put("listDown", listDown);

                dish = jsonObject.toJSONString();
            }
			/*start by xzhang 20171215 交易页面三期PRD:遍历订单信息，显示该用户是否下单*/
            JSONObject jsonObject = JSONObject.parseObject(dish);
            JSONArray listUp = (JSONArray)jsonObject.get("listUp");
            for(Object arrObj:listUp){
                JSONArray arr = (JSONArray)arrObj;
                if(depth <= 0d){
                    if(StringUtil.exist(userId)&&userId != "0"){
                        if(arr.get(2).toString().contains(userId)){
                            arr.set(2,"1");
                        }else{
                            arr.set(2,"0");
                        }
                    }else{
                        arr.set(2,"0");
                    }
                }else{
                    if(StringUtil.exist(userId)&&userId != "0"){
                        if(arr.get(3).toString().contains(userId)){
                            arr.set(3,"1");
                        }else{
                            arr.set(3,"0");
                        }
                    }else{
                        arr.set(3,"0");
                    }
                }

            }
            jsonObject.put("listUp", listUp);
            JSONArray listDown = (JSONArray)jsonObject.get("listDown");
            for(Object arrObj:listDown){
                JSONArray arr = (JSONArray)arrObj;
                if(depth <= 0d){
                    if(StringUtil.exist(userId)&&userId != "0"){
                        if(arr.get(2).toString().contains(userId)){
                            arr.set(2,"1");
                        }else{
                            arr.set(2,"0");
                        }
                    }else{
                        arr.set(2,"0");
                    }
                }else{
                    if(StringUtil.exist(userId)&&userId != "0"){
                        if(arr.get(3).toString().contains(userId)){
                            arr.set(3,"1");
                        }else{
                            arr.set(3,"0");
                        }
                    }else{
                        arr.set(3,"0");
                    }
                }
            }
            jsonObject.put("listDown", listDown);
            dish = jsonObject.toJSONString();
            Response.append(jsoncallback+"(["+dish+"])");
        }catch(Exception e){
            log.error(e.toString(), e);
        }


    }

}