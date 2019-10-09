package com.world.controller.lucky;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.world.cache.Cache;
import com.world.constant.Const;
import com.world.data.database.DatabasesUtil;
import com.world.model.dao.event.EventInfoDao;
import com.world.model.dao.lucky.LuckyEventDao;
import com.world.model.dao.lucky.LuckyQualifyDao;
import com.world.model.dao.lucky.LuckyRuleDao;
import com.world.model.dao.user.UserDao;
import com.world.model.dao.vote.VoteDao;
import com.world.model.entity.coin.CoinProps;
import com.world.model.entity.event.EventInfo;
import com.world.model.entity.lucky.*;
import com.world.model.entity.user.User;
import com.world.model.entity.vote.Activity;
import com.world.util.CommonUtil;
import com.world.util.ip.IpUtil;
import com.world.util.sign.RSACoder;
import com.world.util.string.MD5;
import com.world.util.string.StringUtil;
import com.world.web.Page;
import com.world.web.action.BaseAction;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @Title: 用户抽奖主体入口
 * @Description: 主要处理页面抽奖信息展示及用户实时抽奖功能
 * @Company: atlas
 * @author: xzhang
 */
public class Index extends BaseAction {

    EventInfoDao eventInfoDao = new EventInfoDao();
    LuckyEventDao luckyEventDao = new LuckyEventDao();
    LuckyQualifyDao luckyQualifyDao = new LuckyQualifyDao();
    LuckyRuleDao luckyRuleDao = new LuckyRuleDao();
    VoteDao voteDao = new VoteDao();
    UserDao userDao = new UserDao();

    /**
     * 控制前端页面加载方法
     *
     */
    //@Page(Viewer = "/cn/activity/lucky_draw.jsp")
    public void index() {

    }

    /**
     * 抽奖后台刷新
     * 临时接口
     */
    //@Page(Viewer = JSON)
    public void insertQualify() {
        String userId = param("userId");
        String activityId = param("activityId");
        String ip = IpUtil.getIp(request);
        Map map = luckyQualifyDao.insertQualify(userId,activityId,ip);
        json("success",true,JSONObject.toJSON(map).toString());
    }
    /**
     *
     * 进入主站，活动信息展示
     * 主要业务逻辑：
     *      判断web页面具体展示哪个活动信息（进行中->结束或暂停->未开始->无）,根据活动状态决定抽奖按钮是否展示
     *          1、未开始活动处理逻辑
     *              1.1查询未开始活动信息及规则
     *              1.2 查询活动规则展示小数点位数及关联活动ID
     *              1.3查询未开始活动规则中是否时关联其他活动，关联后查询其他活动是否进行中，那么展示投票按钮
     *          2、活动结束或暂停处理逻辑
     *              2.1 查询活动暂停或者已结束的活动的规则信息及发生额
     *              2.2 查询活动规则展示奖池大小及小数点位数及关联活动ID
     *              2.3查询活动结束或暂停规则中是否时关联其他活动，关联后查询其他活动是否进行中，那么展示投票按钮
     *              2.4 如果用户已登录，查询用户抽到的奖励金额并根据奖池规则展示金额小数位
     *          3、活动进行中处理逻辑
     *              3.1 查询进行中的活动规则信息及发生额
     *              3.2 查询活动规则展示奖池大小及小数点位数及关联活动ID
     *              3.3查询活动进行中是否时关联其他活动，关联后查询其他活动是否进行中，那么展示投票按钮
     *              3.4 如果用户未登录，直接返回抽奖页面
     *              3.5 如果用户已登录，查询抽奖规则类型，暂不处理规则一和规则二。
     *                  3.5.1 如果用户已登录，且抽奖规则为规则03或04。分组查询用户抽奖资格信息中已领取和未领取信息
     *                  3.5.2 如果用户资格不为空，未领取次数为0且已领取次数大于等于本次活动单个用户最大抽奖次数。则显示抽奖次数已用完并返回用户领取信息。
     *                  3.5.3 如果用户资格为空，则显示用户无权限抽奖
     *                  3.5.4 如果用户资格不为空，未领取次数不为0且已领取次数小于本次活动单个用户最大抽奖次数。则判断当前活动奖池是否已满，已满显示已领完界面
     *                  3.5.5 如果用户资格不为空，未领取次数不为0且已领取次数小于本次活动单个用户最大抽奖次数。且当前活动奖池未满，则显示用户抽奖页面
     *      查询无抽奖活动，不显示抽奖按钮
     */
    //@Page(Viewer = JSON)
    public void getLuckyInfo(){
        UUID uuid = UUID.randomUUID();
        String sid = MD5.toMD5(uuid.toString());
        Cache.Set(sessionId+"lucky",sid);
        String userId = userIdStr();
        VLuckyEvent vLuckyEvent =new VLuckyEvent();
        vLuckyEvent.setIsShow(Const.LUCKY_IS_SHOW);
        vLuckyEvent.setToken(sid);
        vLuckyEvent.setChance("0");//次数已用完显示
        //判断web页面具体展示哪个活动信息（进行中->结束或暂停->未开始->无）,根据活动状态决定抽奖按钮是否展示
        EventInfo eventInfo = eventInfoDao.getEventInfo(lan);
        if(eventInfo!= null){
            vLuckyEvent.setEventId(eventInfo.getEventId());
            vLuckyEvent.setEventTitleJson(eventInfo.getEventTitleJson());
            vLuckyEvent.setEventContentJson(eventInfo.getEventContentJson());
            vLuckyEvent.setEventRuleJson(eventInfo.getEventRuleJson());
            //1、未开始活动处理逻辑
            if(Const.EVENT_STATUS_UNSTART.equals(eventInfo.getStatus())){
                vLuckyEvent.setViewFlag(Const.LUCKY_VIEW_UNSTART);
                vLuckyEvent.setIsShow(Const.LUCKY_IS_UNSHOW);
                //1.1查询未开始活动信息及规则
                LuckyEvent luckyEvent = luckyEventDao.getUnStartLucky(eventInfo.getEventId());
                if(luckyEvent == null){
                    log.error("【抽奖】活动信息展示，根据活动ID："+eventInfo.getEventId()+"，查询未开始的活动信息为空");
                    json(L("内部异常"),false,"");
                    return;
                }
                //1.2 查询活动规则展示小数点位数及关联活动ID
                vLuckyEvent.setJackpotSize(CommonUtil.getAmountAddZERO(new BigDecimal(0),luckyEvent.getRadixPoint()));
                vLuckyEvent.setUserAmount( vLuckyEvent.getJackpotSize());
                vLuckyEvent.setRelateEventId(luckyEvent.getRelateEventId());
                //1.3查询未开始活动规则中是否时关联其他活动，关联后查询其他活动是否进行中，那么展示投票按钮
                vLuckyEvent.setIsVoteShow(Const.LUCKY_IS_UNSHOW);
                if(Const.LUCKY_RULE_ING.equals(luckyEvent.getCycleLimitType())||Const.LUCKY_RULE_END.equals(luckyEvent.getCycleLimitType())){
                    Activity vote = voteDao.getOne(vLuckyEvent.getRelateEventId(),"","");
                    if(vote != null&&Integer.parseInt(Const.LUCKY_IS_SHOW)==(vote.getState())){
                        vLuckyEvent.setIsVoteShow(Const.LUCKY_IS_SHOW);
                    }
                }
                json("success",true,JSONObject.toJSON(vLuckyEvent).toString());
            }else if(Const.EVENT_STATUS_SUSPEND.equals(eventInfo.getStatus())||Const.EVENT_STATUS_OVER.equals(eventInfo.getStatus())){
                //2、活动结束或暂停处理逻辑
                vLuckyEvent.setViewFlag(Const.LUCKY_VIEW_ALL);
                //2.1 查询活动暂停或者已结束的活动的规则信息及发生额
                LuckyEvent luckyEvent = luckyEventDao.getEndAndINGLucky(eventInfo.getEventId());
                if(luckyEvent == null){
                    log.error("【抽奖】活动信息展示，根据活动ID："+eventInfo.getEventId()+"，查询暂停或结束的活动信息为空");
                    json(L("内部异常"),false,"");
                    return;
                }
                //2.2 查询活动规则展示奖池大小及小数点位数及关联活动ID
                vLuckyEvent.setJackpotSize(CommonUtil.getAmountAddZERO(luckyEvent.getOccurAmount(),luckyEvent.getRadixPoint()));
                vLuckyEvent.setUserAmount(CommonUtil.getAmountAddZERO(new BigDecimal(0),luckyEvent.getRadixPoint()));
                vLuckyEvent.setRelateEventId(luckyEvent.getRelateEventId());
                //2.3查询活动结束或暂停规则中是否时关联其他活动，关联后查询其他活动是否进行中，那么展示投票按钮
                vLuckyEvent.setIsVoteShow(Const.LUCKY_IS_UNSHOW);
                if(Const.LUCKY_RULE_ING.equals(luckyEvent.getCycleLimitType())||Const.LUCKY_RULE_END.equals(luckyEvent.getCycleLimitType())){
                    Activity vote = voteDao.getOne(vLuckyEvent.getRelateEventId(),"","");
                    if(vote != null&&Integer.parseInt(Const.LUCKY_IS_SHOW)==(vote.getState())){
                        vLuckyEvent.setIsVoteShow(Const.LUCKY_IS_SHOW);
                    }
                }
                //2.4 如果用户已登录，查询用户抽到的奖励金额并根据奖池规则展示金额小数位
                if (StringUtil.exist(userId) && !"0".equals(userId)) {
                    BigDecimal userAmount = luckyQualifyDao.getUserLucky(eventInfo.getEventId(),userId);
                    vLuckyEvent.setUserAmount(CommonUtil.getAmountAddZERO(userAmount,luckyEvent.getRadixPoint()));
                }
                json("success",true,JSONObject.toJSON(vLuckyEvent).toString());
            }else if (Const.EVENT_STATUS_ING.equals(eventInfo.getStatus())){
                //3、活动进行中处理逻辑
                //3.1 查询进行中的活动规则信息及发生额
                LuckyEvent luckyEvent = luckyEventDao.getEndAndINGLucky(eventInfo.getEventId());
                if(luckyEvent == null){
                    log.error("【抽奖】活动信息展示，根据活动ID："+eventInfo.getEventId()+"，查询进行中的活动信息为空");
                    json(L("内部异常"),false,"");
                    return;
                }
                //3.2 查询活动规则展示奖池大小及小数点位数及关联活动ID
                vLuckyEvent.setJackpotSize(CommonUtil.getAmountAddZERO(luckyEvent.getOccurAmount(),luckyEvent.getRadixPoint()));
                vLuckyEvent.setUserAmount(CommonUtil.getAmountAddZERO(new BigDecimal(0),luckyEvent.getRadixPoint()));
                vLuckyEvent.setRelateEventId(luckyEvent.getRelateEventId());
                //3.3查询活动进行中是否时关联其他活动，关联后查询其他活动是否进行中，那么展示投票按钮
                vLuckyEvent.setIsVoteShow(Const.LUCKY_IS_UNSHOW);
                if(Const.LUCKY_RULE_ING.equals(luckyEvent.getCycleLimitType())||Const.LUCKY_RULE_END.equals(luckyEvent.getCycleLimitType())){
                    Activity vote = voteDao.getOne(vLuckyEvent.getRelateEventId(),"","");
                    if(vote != null&&Integer.parseInt(Const.LUCKY_IS_SHOW)==(vote.getState())){
                        vLuckyEvent.setIsVoteShow(Const.LUCKY_IS_SHOW);
                    }
                }
                if(StringUtil.exist(userId)&&!"0".equals(userId)){
                    BigDecimal userAmount = luckyQualifyDao.getUserLucky(eventInfo.getEventId(),userId);
                    vLuckyEvent.setUserAmount(CommonUtil.getAmountAddZERO(userAmount,luckyEvent.getRadixPoint()));
                }
//                //3.4判断当前活动奖池是否已满，已满显示已领完界面
                if(!luckyRuleDao.getJackpot(luckyEvent.getLuckyId())){
                    vLuckyEvent.setViewFlag(Const.LUCKY_VIEW_ALL);
                    vLuckyEvent.setChance("0");//次数已用完显示
                    json("success",true,JSONObject.toJSON(vLuckyEvent).toString());
                    return;
                }
                //3.4 如果用户未登录，直接返回抽奖页面
                if (!StringUtil.exist(userId) || "0".equals(userId)) {
                    vLuckyEvent.setViewFlag(Const.LUCKY_VIEW_USABLE);
                    json("success",true,JSONObject.toJSON(vLuckyEvent).toString());
                }else {
                    //3.5 如果用户已登录，查询抽奖规则类型，暂不处理规则一和规则二。
                    if(Const.LUCKY_RULE_DAY.equals(luckyEvent.getCycleLimitType())){
                        //暂不处理
                    }else if (Const.LUCKY_RULE_CYCLE.equals(luckyEvent.getCycleLimitType())){
                        //暂不处理
                    }else if(Const.LUCKY_RULE_ING.equals(luckyEvent.getCycleLimitType())||
                            Const.LUCKY_RULE_END.equals(luckyEvent.getCycleLimitType())){
                        //3.5.1 如果用户已登录，且抽奖规则为规则03或04。分组查询用户抽奖资格信息中已领取和未领取信息
                        List<LuckyQualify>  qualifyList = luckyQualifyDao.getQualifyDetail(luckyEvent.getLuckyId(),userId);
                        if(!CollectionUtils.isEmpty(qualifyList)){
                            int receive = 0;
                            int receiveED = 0;
                            for(LuckyQualify vo:qualifyList){
                                if(Const.LUCKY_RULE_USABLE.equals(vo.getIsReceive())){
                                    receive = Integer.parseInt(vo.getReceiveCount());
                                }
                                if(Const.LUCKY_RULE_DISABLE.equals(vo.getIsReceive())){
                                    receiveED = Integer.parseInt(vo.getReceiveCount());
                                }
                            }
                            //3.5.2 如果用户资格不为空，未领取次数为0且已领取次数大于等于本次活动单个用户最大抽奖次数。则显示抽奖次数已用完并返回用户领取信息。
                            if(receive==0||receiveED>=luckyEvent.getLimitCount()){
                                //vLuckyEvent.setViewFlag(Const.LUCKY_VIEW_USE_UP);
                                vLuckyEvent.setViewFlag(Const.LUCKY_VIEW_USABLE);
                                vLuckyEvent.setChance("0");//次数已用完显示
                                json("success",true,JSONObject.toJSON(vLuckyEvent).toString());
                            }else{
                                //3.5.5 如果用户资格不为空，未领取次数不为0且已领取次数小于本次活动单个用户最大抽奖次数。且当前活动奖池未满，则显示用户抽奖页面
                                vLuckyEvent.setViewFlag(Const.LUCKY_VIEW_USABLE);
                                vLuckyEvent.setChance(receive+"");//抽奖次数
                                json("success",true,JSONObject.toJSON(vLuckyEvent).toString());
                            }
                        }else{
                            vLuckyEvent.setViewFlag(Const.LUCKY_VIEW_USABLE);
                            vLuckyEvent.setChance("0");//次数已用完显示
                            json("success",true,JSONObject.toJSON(vLuckyEvent).toString());
                            //xzhang 20180206 改版，删除以下分支
                        }
                    }
                }
            }
        }else{
            //查询无抽奖活动，不显示抽奖按钮
            vLuckyEvent.setIsShow(Const.LUCKY_IS_UNSHOW);
            json(L("fail"), false, JSONObject.toJSON(vLuckyEvent).toString());
        }
    }
    /**
     * 抽奖方法
     * 1.判断web页面具体展示哪个活动信息（进行中->结束或暂停->未开始->无）,避免用户非法请求接口
     * 1.1查询活动暂停或者已结束或进行中的活动的规则信息及发生额,防止用户抽奖与活动结束同时发生
     * 1.2处理当前奖池数量及用户领取数量
     * 1.3查询活动当前的状态，如果为未开始，则提示用户为越权操作，如果为暂停或者停止，则提示用户为奖池已满
     * 1.4查询活动为活动中，则分组查询用户抽奖资格信息中已领取和未领取信息
     *      1.4.1 如果用户资格不为空，未领取次数为0且已领取次数大于等于本次活动单个用户最大抽奖次数。则显示抽奖次数已用完并返回用户领取信息。
     *      1.4.2 如果用户资格不为空，未领取次数不为0且已领取次数小于本次活动单个用户最大抽奖次数。则判断当前活动奖池是否已满，已满显示已领完界面
     *      1.4.3 如果用户资格不为空，未领取次数不为0且已领取次数小于本次活动单个用户最大抽奖次数。则判断当前活动奖池是否已满，未满进行抽奖逻辑
     *      抽奖明细逻辑：
     *          根据规则类型抽奖：
     *              1.1根据规则限制小数点位数，和随机数最大和最小值。随机产生符合条件的数字，
     *              1.2根据当前时间和领取状态，用户ID和活动id更新用户的领抽奖资格表
     *              1.3根据发生额和设定奖池大小大于等于当前发生额为条件更新奖池
     *          注意：
     *              1.一个事物执行该方法，执行失败。返回用户为未抽中。
     *              2.当随机数加已发生额大于奖池最大数。则发生额改为奖池大小减去已发生额。如果小于0，则改为0.提示用户未抽中
     */
    //@Page(Viewer = JSON)
    public void goodLucky(){
        String token = Cache.Get(sessionId+"lucky");
        if(!StringUtil.exist(token)||!token.equals(param("token"))){
            json("越权操作", false,"");
            return;
        }
        String ip = IpUtil.getIp(request);
        UUID uuid = UUID.randomUUID();
        String sid = MD5.toMD5(uuid.toString());
        Cache.Set(sessionId+"lucky",sid);
        VLuckyQualify vLuckyQualify = new VLuckyQualify();
        vLuckyQualify.setToken(sid);
        String userId = userIdStr();
        if (!StringUtil.exist(userId) || "0".equals(userId)) {
            vLuckyQualify.setIsLogin("0");
            json(L("请登录后再进行抽奖"), false, JSONObject.toJSON(vLuckyQualify).toString());
            return;
        }else{
            //1.判断web页面具体展示哪个活动信息（进行中->结束或暂停->未开始->无）,避免用户非法请求接口
            EventInfo eventInfo = eventInfoDao.getEventInfo(lan);
            if(eventInfo!= null){
                vLuckyQualify.setChance("0");
                //1.1查询活动暂停或者已结束或进行中的活动的规则信息及发生额,防止用户抽奖与活动结束同时发生
                LuckyEvent luckyEvent = luckyEventDao.getEndAndINGLucky(eventInfo.getEventId());
                if(luckyEvent == null){
                    log.error("【抽奖】抽奖方法，根据活动ID："+eventInfo.getEventId()+"，查询暂停或者已结束或进行中的活动信息为空");
                    json(L("内部异常"),false,"");
                    return;
                }
                //1.2处理当前奖池数量及用户领取数量
                vLuckyQualify.setJackpotSize(CommonUtil.getAmountAddZERO(luckyEvent.getOccurAmount(),luckyEvent.getRadixPoint()));
                BigDecimal userAmount = luckyQualifyDao.getUserLucky(eventInfo.getEventId(),userId);
                vLuckyQualify.setUserAmount(CommonUtil.getAmountAddZERO(userAmount,luckyEvent.getRadixPoint()));
                //1.3查询活动当前的状态，如果为未开始，则提示用户为越权操作，如果为暂停或者停止，则提示用户为奖池已满
                if(Const.EVENT_STATUS_UNSTART.equals(eventInfo.getStatus())){
                    json("越权操作", false, "");
                    return;
                }else if(!luckyRuleDao.getJackpot(luckyEvent.getLuckyId())){
                    vLuckyQualify.setViewFlag(Const.LUCKY_VIEW_ALL);
                    vLuckyQualify.setCurrAmount(CommonUtil.getAmountAddZERO(new BigDecimal(0),luckyEvent.getRadixPoint()));
                    vLuckyQualify.setChance("0");
                    json(L("很抱歉，奖项已全部发放"), true, JSONObject.toJSON(vLuckyQualify).toString());
                    return;
                }


                if(Const.LUCKY_RULE_DAY.equals(luckyEvent.getCycleLimitType())){
                    //暂不处理
                }else if (Const.LUCKY_RULE_CYCLE.equals(luckyEvent.getCycleLimitType())){
                    //暂不处理
                }else if(Const.LUCKY_RULE_ING.equals(luckyEvent.getCycleLimitType())||
                        Const.LUCKY_RULE_END.equals(luckyEvent.getCycleLimitType())){

                    List<LuckyQualify> luckyQualifys =  luckyQualifyDao.getUserDetails(luckyEvent.getLuckyId(),userId);
                    if(!CollectionUtils.isEmpty(luckyQualifys)&&luckyEvent.getLimitCount()<=luckyQualifys.size()){
                        vLuckyQualify.setViewFlag(Const.LUCKY_VIEW_LIMIT);
                        vLuckyQualify.setCurrAmount(CommonUtil.getAmountAddZERO(new BigDecimal(0),luckyEvent.getRadixPoint()));
                        json("success",true,JSONObject.toJSON(vLuckyQualify).toString());
                        return;
                    }

                    //1.4查询活动为活动中，则分组查询用户抽奖资格信息中已领取和未领取信息
                    List<LuckyQualify>  qualifyList = luckyQualifyDao.getQualifyDetail(luckyEvent.getLuckyId(),userId);
                    if(!CollectionUtils.isEmpty(qualifyList)){
                        int receive = 0;
                        int receiveED = 0;
                        for(LuckyQualify vo:qualifyList){
                            if(Const.LUCKY_RULE_USABLE.equals(vo.getIsReceive())){
                                receive = Integer.parseInt(vo.getReceiveCount());
                            }
                            if(Const.LUCKY_RULE_DISABLE.equals(vo.getIsReceive())){
                                receiveED = Integer.parseInt(vo.getReceiveCount());
                            }
                        }

                        if(receiveED>=luckyEvent.getLimitCount()){
                            vLuckyQualify.setViewFlag(Const.LUCKY_VIEW_LIMIT);
                            vLuckyQualify.setCurrAmount(CommonUtil.getAmountAddZERO(new BigDecimal(0),luckyEvent.getRadixPoint()));
                            json("success",true,JSONObject.toJSON(vLuckyQualify).toString());
                            return;
                        }

                        //1.4.1 如果用户资格不为空，未领取次数为0且已领取次数大于等于本次活动单个用户最大抽奖次数。则显示抽奖次数已用完并返回用户领取信息。
                        if(receive==0){
                            vLuckyQualify.setViewFlag(Const.LUCKY_VIEW_USE_UP);
                            vLuckyQualify.setCurrAmount(CommonUtil.getAmountAddZERO(new BigDecimal(0),luckyEvent.getRadixPoint()));
                            json("success",true,JSONObject.toJSON(vLuckyQualify).toString());
                        }else{
                            //1.4.2 如果用户资格不为空，未领取次数不为0且已领取次数小于本次活动单个用户最大抽奖次数。则判断当前活动奖池是否已满，已满显示已领完界面
                            List<LuckyRule>  luckyRuleList = luckyRuleDao.getRuleList(luckyEvent.getLuckyId());
                            if(CollectionUtils.isEmpty(luckyRuleList)){
                                vLuckyQualify.setViewFlag(Const.LUCKY_VIEW_ALL);
                                vLuckyQualify.setCurrAmount(CommonUtil.getAmountAddZERO(new BigDecimal(0),luckyEvent.getRadixPoint()));
                                json("success",true,JSONObject.toJSON(vLuckyQualify).toString());
                            }else {
                                //1.4.3 如果用户资格不为空，未领取次数不为0且已领取次数小于本次活动单个用户最大抽奖次数。则判断当前活动奖池是否已满，未满进行抽奖逻辑

                                Map<String,Object> map = luckyRuleDao.getLuckyTmp(userDao.getUserById(userId), luckyEvent.getLuckyId(), luckyRuleList,luckyEvent.getLimitCount(),eventInfo.getTitleOriginal(),ip);
                                vLuckyQualify.setViewFlag(Const.LUCKY_VIEW_USABLE);
                                vLuckyQualify.setChance((receive - 1)+"");
                                vLuckyQualify.setJackpotSize(CommonUtil.getAmountAddZERO((BigDecimal)map.get("occurAmout"),luckyEvent.getRadixPoint()));
                                vLuckyQualify.setCurrAmount(CommonUtil.getAmountAddZERO((BigDecimal)map.get("userAmout"),luckyEvent.getRadixPoint()));
                                vLuckyQualify.setUserAmount(CommonUtil.getAmountAddZERO(luckyQualifyDao.getUserLucky(eventInfo.getEventId(),userId),luckyEvent.getRadixPoint()));
                                json("success",true,JSONObject.toJSON(vLuckyQualify).toString());
                            }
                        }
                    }else{
                        List<LuckyQualify>  qualifyListNew =  luckyQualifyDao.getNewest(luckyEvent.getLuckyId(),userId);
                        if(!CollectionUtils.isEmpty(qualifyListNew)){
                            LuckyQualify qualifyNew = qualifyListNew.get(0);
                            if(Const.LUCKY_RULE_USABLE.equals(qualifyNew.getIsReceive())){
                                vLuckyQualify.setViewFlag(Const.LUCKY_VIEW_LOSE);
                                vLuckyQualify.setCurrAmount(CommonUtil.getAmountAddZERO(new BigDecimal(0),luckyEvent.getRadixPoint()));
                                json("success",true,JSONObject.toJSON(vLuckyQualify).toString());
                                return;
                            }
                        }
                        vLuckyQualify.setViewFlag(Const.LUCKY_VIEW_NO_ACCESS);
                        json("无权限抽奖", true,JSONObject.toJSON(vLuckyQualify).toString());
                        return;
                    }
                }
            }else{
                json("越权操作", false, "");
                return;
            }
        }
    }


    /**
     * 暂未启用
     * 判断是否还有抽奖机会
     * 1.判断web页面具体展示哪个活动信息（进行中->结束或暂停->未开始->无）,避免用户非法请求接口
     * 1.1查询活动暂停或者已结束或进行中的活动的规则信息及发生额,防止用户抽奖与活动结束同时发生
     * 1.2处理当前奖池数量及用户领取数量
     * 1.3查询活动当前的状态，如果为未开始，则提示用户为越权操作，如果为暂停或者停止，则提示用户为奖池已满
     * 1.4查询活动为活动中，则分组查询用户抽奖资格信息中已领取和未领取信息
     */
    //@Page(Viewer = JSON)
    public void getLuckyChance() {
        VLuckyQualify vLuckyQualify = new VLuckyQualify();
        String userId = userIdStr();
        if (!StringUtil.exist(userId) || "0".equals(userId)) {
            vLuckyQualify.setIsLogin("0");
            json(L("请登录后再进行抽奖"), false, JSONObject.toJSON(vLuckyQualify).toString());
            return;
        } else {
            EventInfo eventInfo = eventInfoDao.getEventInfo(lan);
            if (eventInfo != null) {
                //查询活动暂停或者已结束或进行中的活动的规则信息及发生额
                LuckyEvent luckyEvent = luckyEventDao.getEndAndINGLucky(eventInfo.getEventId());
                vLuckyQualify.setJackpotSize(CommonUtil.getAmountAddZERO(luckyEvent.getOccurAmount(), luckyEvent.getRadixPoint()));
                BigDecimal userAmount = luckyQualifyDao.getUserLucky(eventInfo.getEventId(), userId);
                vLuckyQualify.setUserAmount(CommonUtil.getAmountAddZERO(userAmount, luckyEvent.getRadixPoint()));
                if (Const.EVENT_STATUS_UNSTART.equals(eventInfo.getStatus())) {
                    json("越权操作", false, "");
                    return;
                } else if (Const.EVENT_STATUS_SUSPEND.equals(eventInfo.getStatus()) || Const.EVENT_STATUS_OVER.equals(eventInfo.getStatus())) {
                    vLuckyQualify.setViewFlag(Const.LUCKY_VIEW_ALL);
                    json(L("奖池已全部发放"), true, JSONObject.toJSON(vLuckyQualify).toString());
                    return;
                }

                if (Const.LUCKY_RULE_DAY.equals(luckyEvent.getCycleLimitType())) {
                    //暂不处理
                } else if (Const.LUCKY_RULE_CYCLE.equals(luckyEvent.getCycleLimitType())) {
                    //暂不处理
                } else if (Const.LUCKY_RULE_ING.equals(luckyEvent.getCycleLimitType()) ||
                        Const.LUCKY_RULE_END.equals(luckyEvent.getCycleLimitType())) {
                    //1.2处理当前奖池数量及用户领取数量
                    List<LuckyQualify> qualifyList = luckyQualifyDao.getQualifyDetail(luckyEvent.getLuckyId(), userId);
                    if (!CollectionUtils.isEmpty(qualifyList)) {
                        int receive = 0;
                        int receiveED = 0;
                        for (LuckyQualify vo : qualifyList) {
                            if (Const.LUCKY_RULE_USABLE.equals(vo.getIsReceive())) {
                                receive = Integer.parseInt(vo.getReceiveCount());
                            }
                            if (Const.LUCKY_RULE_DISABLE.equals(vo.getIsReceive())) {
                                receiveED = Integer.parseInt(vo.getReceiveCount());
                            }
                        }
                        if (receive == 0 || receiveED >= luckyEvent.getLimitCount()) {
                            vLuckyQualify.setViewFlag(Const.LUCKY_VIEW_USE_UP);
                            json("success", true, JSONObject.toJSON(vLuckyQualify).toString());
                        } else {
                            //查询活动当前的状态，如果为未开始，则提示用户为越权操作，如果为暂停或者停止，则提示用户为奖池已满
                            List<LuckyRule> luckyRuleList = luckyRuleDao.getRuleList(luckyEvent.getLuckyId());
                            if (CollectionUtils.isEmpty(luckyRuleList)) {
                                vLuckyQualify.setViewFlag(Const.LUCKY_VIEW_ALL);
                                json("success", true, JSONObject.toJSON(vLuckyQualify).toString());
                            } else {
                                vLuckyQualify.setViewFlag(Const.LUCKY_VIEW_USABLE);
                                json("success", true, JSONObject.toJSON(vLuckyQualify).toString());
                            }
                        }
                    } else {
                        json("越权操作", false, "");
                        return;
                    }
                }
            } else {
                json("越权操作", false, "");
                return;
            }
        }
    }

    /**
     *隐藏方法实际含义。
     * 后台通过http调用。
     */
    //@Page(Viewer = JSON)
    public void getLuckyUser() {
        JSONArray result = new JSONArray();
        try {
            // 加点防御措施
            String sign = param("sign");
            if (StringUtils.isBlank(sign)) {
                json(L("参数非法！"), false, "", true);
                return;
            }
            String priKey = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAI2+Fd2Xa1gjWV37QWy+q11cqdA5Abd5wE7CC+ltVepXRRVGuLoAdEvlr4VVDnnsCsSouXrvjXgWGO+HZ76CwuV5xe8wly6H+rwQR5Vrmv0+ZIfFNOTeJGZUgFRXmadLwWt3VWN2rc6mTY9nx/MZT9H2q/Wb0SeXUF1itmd8uuaHAgMBAAECgYB/kDQTgmOsJdwW1boSySJmWq/FYpil7B/jgYXA5ZJt3W6h8EztsNz5NVQateraVVF3nbWX6yGxkomMgJsgfIQzLVAL2yoFyMPYe37+0NSNlBiKtBSGhfvDKwhZrKU+NAg4OjYX31/LR6ezbgIr/IRbrXwIYesy0XDdu02GRpnjSQJBAOrl+4UVQli8L5x4DiUK0BhzMYsqg0Gpk9KaE8fBdTw7Ubf+8dkgUu3Ta4cnLgvBywqpBZoXBe1H8yXpSlJ09WUCQQCaecYx4t+4SyBA/uk3kwJES2M4GFZb2I520yoa0ujc/uEU9X8nyD/LnSqpqKf+1cbrWl3MAO/xPhZfNwnVIJN7AkEAsc1JmI/h+5belxqM4l8P6yHuw393gRFiMkysYky+d8wS7CpPWGHOQ/T/dHsksIONNFGCSwPYWaZXlz/CIS4kvQJACWLIxhMw4LO/2/MhHH1UL+4cszXXWXFJBrNB5atW9saNyoY4GaSzK537D5/txTAcDATLmi+cZJ4PIe3oLQjzrQJAPxnkLo0fUR4DvnsDL+4nL2xtunNce7TtSxbXgfvKseaC4yr/+y47lXL5V6Xof+WTXvBz29OcjRRcqNdFkjrXkg==";
            byte[] decodeSign = RSACoder.decryptByPrivateKey(RSACoder.decryptBASE64(sign), priKey);//解密
            if (!StringUtil.exist(new String(decodeSign))) {
                json(L("参数非法！"), false, "", true);
                return;
            }
            String userId = new String(decodeSign);
            User user = userDao.getUserById(userId);
            if(user == null){
                json("无当前用户信息", false, "", true);
                return;
            }
            luckyRuleDao.unEventFreez(userId);
        } catch (Exception e) {
            log.error("【奖金解冻】发生非受控性异常，异常信息为：", e);
            json("系统异常", false, "", true);
            return;
        }
        json("success", true, result.toString(), true);
    }
}
