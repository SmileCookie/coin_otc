package com.tenstar.timer.entrust;

import com.Lan;
import com.alibaba.fastjson.JSONObject;
import com.api.VipResponse;
import com.kafka.ProducerSend;
import com.match.domain.Entrust;
import com.match.entrust.MemEntrustDataProcessor;
import com.match.entrust.MemEntrustMatchProcessor;
import com.match.money.UserFundsUpdateProcessor;
import com.tenstar.HTTPTcp;
import com.tenstar.Info;
import com.tenstar.InfoEntrust;
import com.tenstar.SystemStatus;
import com.tenstar.TimeUtil;
import com.tenstar.UserConfig;
import com.tenstar.autoId;
import com.tenstar.robotConfig;
import com.tenstar.timer.auto.AutoShualiangTask;
import com.tenstar.timer.auto.AutoTaskUser;
import com.world.cache.Cache;
import com.world.data.mysql.Data;
import com.world.data.mysql.OneSql;
import com.world.data.mysql.transaction.TransactionObject;
import com.world.model.Market;
import com.world.model.daos.world.FundsUserDao;
import com.world.model.daos.world.WorldManager;
import com.world.model.entity.pay.PayUserBean;
import com.world.model.entitys.entrust.EntrustOtherBean;
import com.world.model.entitys.entrust.PlanEntrustBean;
import com.world.util.callback.AsynMethodFactory;
import com.world.util.string.StringUtil;
import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;


/**
 * 委托
 */
public class Interface {

	private static String lan = "en";

	public static String l(String key) {
		return Lan.Language(lan, key);
	}

	public static String l(String key, String str) {
		return Lan.LanguageFormat(lan, key, str);
	}

	public static String l(String lan, String key, String str) {
		return Lan.LanguageFormat(lan, key, str);
	}

	public static String getLan(String uid) {
		return StringUtil.exist(Cache.Get("user_lan_" + uid)) ? Cache.Get("user_lan_" + uid) : lan;
	}

	public static Logger log = Logger.getLogger(Interface.class);

	/**
	 * 进行委托
	 * @param type             是否为购买 0-卖;1-买
	 * @param userId           用户id
	 * @param number           交易数量   精确到最小单位取整数，不可以有小树
	 * @param unitPrice        单价 如果是人民币 精确到分，然后取整数   比特币*10*8取整
	 * @param sumToWeb         数据是否合并到项目id
	 * @param webId            当前webid
	 * @param isPlan           是否是计划委托 1是计划 0是正常真实
	 * @param mt               是否允许用户高价买低价卖或成交自己单据1=true,0=false, default 0;	guosj
	 * @param customerOrderId
	 * @param m
	 * @return                 信息结果
	 */
	public static InfoEntrust doEntrust(int type, int userId, BigDecimal number, BigDecimal unitPrice, int sumToWeb, int webId, int isPlan, int mt, String customerOrderId, Market m){
        long t1 = System.currentTimeMillis();
		number=Market.ffNumber(number,m);
		unitPrice=Market.ffMoney(unitPrice,m);

		if(number.compareTo(BigDecimal.ZERO) <= 0 || unitPrice.compareTo(BigDecimal.ZERO) <= 0||userId<=0){
			return new InfoEntrust(Info.DoEntrustFaildWrongParam,0);
		}
		if(unitPrice.compareTo(BigDecimal.valueOf(m.maxPrice)) >0){
			return  new InfoEntrust(Info.DoEntrustFaildHighPeice,0);
		}

		//下单委托增加判断检测成交金额与系统配置金额对比，否则不允许下单
        //成交金额小于知道的最小交易金额，返回委托失败
        BigDecimal totalMoney=Market.totalMoney(unitPrice,number);
        if(m.getMinAmount()>0 && totalMoney.compareTo(BigDecimal.valueOf(m.getMinAmount()))<0){
            Info info =  Info.DoEntrustFaildMinAmount;
			DecimalFormat df = new DecimalFormat("0.#########");
			info.setMessage(l(getLan(userId+""), "委托失败-成交金额小于系统规定金额", df.format(m.getMinAmount())+m.exchangeBi.toUpperCase()));
			return new InfoEntrust(info, 0, new VipResponse());
		}

		//买或者卖花费的币总数
        BigDecimal total = BigDecimal.ZERO;
		List<OneSql> sqls = new ArrayList<>();
		TransactionObject txObj = new com.world.data.mysql.transaction.TransactionObject();
		if(type==1){
			//计算购买所需资金总额
			total = Market.totalMoney(unitPrice, number);

			///购买时添加资产判断
			BigDecimal userTotal = BigDecimal.ZERO;
			//因为读取的时候如果exchangeBiFundsType不存在则设置为0,所以这里判断不等于0
			if(m.exchangeBiFundsType != 0){
				long s1 = System.currentTimeMillis();

                //只是取资金验证，无需用事务，也可以考虑从缓存获取验证
                PayUserBean pub = WorldManager.getUserBalance(userId, m.exchangeBiFundsType);
                if (null != pub) {
                    userTotal = pub.getBalance();
                }

				long s2 = System.currentTimeMillis();
				 log.info(" 委托下单查询用资产耗时："+(s2-s1)+" 毫秒。");
			}

			if(userTotal.compareTo(total) < 0){
				return new InfoEntrust(Info.DoEntrustFaildNotEnoughMoney,0);
			}

			WorldManager.buy(userId, total, sqls,m);
		}else if(type==0){
			//格式化卖出币数量
			total=number; 
			//卖出时添加资产判断
			BigDecimal userTotal = BigDecimal.ZERO;

			if(m.numberBiFundsType != 0){
                long s1 = System.currentTimeMillis();

                //只是取资金验证，无需用事务，也可以考虑从缓存获取验证
                PayUserBean pub = WorldManager.getUserBalance(userId, m.numberBiFundsType);
                if (null != pub) {
                    userTotal = pub.getBalance();
                }

                long s2 = System.currentTimeMillis();
                log.info("委托下单查询资产耗时：" + (s2 - s1) + " 毫秒。");
			}

			if(userTotal.compareTo(total) < 0){
				return new InfoEntrust(Info.DoEntrustFaildNotEnoughMoney,0);
			}

			WorldManager.sell(userId, total, sqls, m);
		} else { 
			return  new InfoEntrust(Info.DoEntrustFaildUnKnowType,0);
		}

		long entrustId = autoId.getId(m.getMarket()+"entrust",m.db);

        long submitTime = System.currentTimeMillis();
		  
		int status = isPlan==1 ? -1 : 0;
	
		sqls.add(new OneSql(
				"INSERT INTO entrust (entrustId, unitPrice, numbers, totalMoney, sumToWeb, webId, TYPES, userId, submitTime,status,feeRate) VALUES (?,?,?,?,?,?,?,?,?,?,?)",
				1,
				new Object[]{
						entrustId,
						unitPrice,
						number,
						totalMoney,//对于卖设置为0
						sumToWeb, 
						webId,
						type,
						userId,
						submitTime,
						status , //-1状态标示为计划委托状态 如果条件合适的时候，直接update该项为1就可以了
						0 //增加委托记录手续费费率
				},
				m.db));

		//撮合reload过程中，延迟下单，防止数据重复撮合
		if(!MemEntrustDataProcessor.reloadDBResult){
            try {
                if("btc_usdt".equalsIgnoreCase(m.market)){
                    Thread.sleep(3000);
                }else{
                    Thread.sleep(2000);
                }
            } catch (InterruptedException e) {
            }
        }
        //如果撮合reload还没有结束，拒绝下单
        if(!MemEntrustDataProcessor.reloadDBResult){
            log.info("委托下单，等待之后reload还未结束，委托被拒，entrustId:" + entrustId);
            return new InfoEntrust(Info.DoEntrustFaildPromError,0);
        }

        long tt0 = System.currentTimeMillis();
		 txObj.excuteUpdateList(sqls);
		 if(txObj.commit()){
			 long tt2  = System.currentTimeMillis();
			 log.info("下单事务耗时："+(tt2-tt0));

			 log.info("推送驾驶舱委托数据埋点开始："+entrustId);
			 JSONObject jsonObject = new JSONObject();
			 jsonObject.put("userid",userId);
			 jsonObject.put("entrustid",entrustId);
			 jsonObject.put("unitprice",unitPrice);
			 jsonObject.put("entrustsum",number);
			 jsonObject.put("entrustmarket",m.getMarket());
			 jsonObject.put("fundstype",m.getNumberBiFundsType());
			 jsonObject.put("clienttype",webId);
			 ProducerSend producerSend = new ProducerSend();
			 producerSend.sendMessage("entrust", jsonObject.toString());
			 log.info("推送驾驶舱委托数据埋点成功："+jsonObject);

             //更新缓存中的用户财务信息
//             AsynMethodFactory.addWork(FundsUserDao.class, "updateFundsByChange", new Object[]{userId});
//             fundsUserDao.updateFundsByPool(userId);
             UserFundsUpdateProcessor.add(userId);
			 if(isPlan==1){//更新用户委托数据
                 MemEntrustMatchProcessor.da.getTop(userId,m);
			 }else{
			     //添加到委托队列并更新单个交易缓存信息

                 //如果在reload过程中，不添加内存，reload来添加
                 if(MemEntrustDataProcessor.reloadResult){
                     Entrust entrust = new Entrust(entrustId, unitPrice, number, totalMoney, webId, userId, type, number, BigDecimal.ZERO, BigDecimal.ZERO, submitTime, status,BigDecimal.ZERO);
                     MemEntrustDataProcessor.addNoMatchingEntrust(entrust,m);
                 }
			 }
			 SystemStatus.setSystemStatus(m.market+"_"+SystemStatus.exchangeNewWork,true);
             log.info(" 委托下单"+m.market+"："+entrustId+"  耗时："+(System.currentTimeMillis()-t1)+" 毫秒。");
			 return new InfoEntrust(Info.DoEntrustSuccess,entrustId);
		 } else {
			 return new InfoEntrust(Info.DoEntrustFaildPromError,0);
		 }
	}

	/**
	 * 取消委托
	 * @param userId  用户id
	 * @param entrustId
	 * @param m
	 * @return 信息结果
	 */
	public static Info doCancle(int userId,long entrustId,Market m){ 
		if(entrustId <= 0 || userId <= 0){
			return Info.DoEntrustFaildWrongParam;
		}
		try{
		   Entrust originEntrust = Data.GetOneT(m.db,"select * from entrust where entrustId=? and unitPrice>0 and numbers>completeNumber and userid=? and (status=-1 or status=0 or status=3)",
			   new Object[]{entrustId,userId}, Entrust.class);
		   if(originEntrust==null){
			   if(MemEntrustDataProcessor.containsNoMatchingEntrust(entrustId,m)){
                   MemEntrustDataProcessor.removeNoMatchingEntrust(entrustId,m);
				   AsynMethodFactory.addWork(DataArray.class, "getTopThread2016", new Object[]{userId,m});
			   }else{
				   AsynMethodFactory.addWork(DataArray.class, "getTopThread2016", new Object[]{userId,m});
			   }
			   
			   return Info.DoCancleFaildNoOrder;
		   }
		  
		   //增加事务锁操作，防止重复取消操作，导致插入freezeId唯一索引 重复报错
		   TransactionObject txObj = new com.world.data.mysql.transaction.TransactionObject();
		   List<OneSql> sqls = new ArrayList<OneSql>();

		   //List c2=(List)Data.GetOne(m.db,"select * from entrust where freezeId=? and userid=? and types=-1", new Object[]{entrustId,userId});
//		   List c2=(List) txObj.excuteQuery(new OneSql("select * from entrust where freezeId=? and userid=? and types=-1 for update ",-2, new Object[]{entrustId,userId},m.db));
//		   if(c2!=null&&c2.get(0)!=null){
//			   txObj.rollback(entrustId+" freeze id is aleady exists!");
//			   return Info.DoCancleFaildDoubleCancle;
//		   }

		   //校验是否已经取消过了
            List c2 = (List) Data.GetOne(m.db, "select * from entrust where freezeId=? and userid=? and types=-1", new Object[]{entrustId, userId});
            if (c2 != null && c2.get(0) != null) {
                log.info(entrustId + " freeze id is aleady exists!");
                return Info.DoCancleFaildDoubleCancle;
            }

		   long submitTime = System.currentTimeMillis();
		   long entrustCancleId = autoId.getId(m.getMarket()+"entrust",m.db);
		   sqls.add(new OneSql("INSERT INTO entrust (entrustId, unitPrice, numbers, totalMoney, sumToWeb, webId, TYPES, userId, freezeId, submitTime) VALUES (?,?,?,?,?,?,?,?,?,?)",-1, new Object[]{
					entrustCancleId,
					 0,
					 0,
					 0,
					 0,
					 0, 
					-1,
					userId,
					entrustId,
					submitTime
			   },m.db));
		   txObj.excuteUpdateList(sqls);//执行事务语句
		   if(txObj.commit()){//提交成功
		       // 正数：表示购买；负数：表示卖出
		       BigDecimal originPrice = originEntrust.getTypes() == 1 ? originEntrust.getUnitPrice() : BigDecimal.ZERO.subtract(originEntrust.getUnitPrice());
			   Entrust entrust = new Entrust(entrustCancleId, originPrice, BigDecimal.ZERO, BigDecimal.ZERO, 0, userId, -1, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, submitTime, 0,BigDecimal.ZERO);
			   entrust.setFreezeId(entrustId);
               MemEntrustDataProcessor.addNoMatchingEntrust(entrust,m);
			   SystemStatus.setSystemStatus(m.market+"_"+SystemStatus.exchangeNewWork,true);
			   return Info.DoCancleSuccess; 
		   }else{//提交失败
			   return Info.DoCancleFaildPromError;
		   }
		}catch(Exception ex){
			return Info.DoCancleFaildPromError;
		}
		
	}

    /**
     * 取消委托,刷量用,忽略下单失败的日志告警，否则会出现事务执行失败的报警
     * @param userId  用户id
     * @param entrustId
     * @param m
     * @return 信息结果
     */
    public static Info doCancleForBrush(int userId, long entrustId, Market m) {
        if (entrustId <= 0 || userId <= 0) {
            return Info.DoEntrustFaildWrongParam;
        }
        try {
            Entrust originEntrust = Data.GetOneT(m.db, "select * from entrust where entrustId=? and unitPrice>0 and numbers>completeNumber and userid=? and (status=-1 or status=0 or status=3)",
                    new Object[]{entrustId, userId}, Entrust.class);
            if (originEntrust == null) {
                if (MemEntrustDataProcessor.containsNoMatchingEntrust(entrustId, m)) {
                    MemEntrustDataProcessor.removeNoMatchingEntrust(entrustId, m);
                    AsynMethodFactory.addWork(DataArray.class, "getTopThread2016", new Object[]{userId, m});
                } else {
                    AsynMethodFactory.addWork(DataArray.class, "getTopThread2016", new Object[]{userId, m});
                }

                return Info.DoCancleFaildNoOrder;
            }

            List c2 = (List) Data.GetOne(m.db, "select * from entrust where freezeId=? and userid=? and types=-1", new Object[]{entrustId, userId});
            if (c2 != null && c2.get(0) != null) {
                log.info(entrustId + " freeze id is aleady exists!");
                return Info.DoCancleFaildDoubleCancle;
            }
            long submitTime = System.currentTimeMillis();
            long entrustCancleId = autoId.getId(m.getMarket() + "entrust", m.db);

            int result = Data.InsertWithoutLog(m.db, "INSERT INTO entrust (entrustId, unitPrice, numbers, totalMoney, sumToWeb, webId, TYPES, userId, freezeId, submitTime) VALUES (?,?,?,?,?,?,?,?,?,?)", new Object[]{
                    entrustCancleId,
                    0,
                    0,
                    0,
                    0,
                    0,
                    -1,
                    userId,
                    entrustId,
                    submitTime
            });

            if (result != -1) {
                // 正数：表示购买；负数：表示卖出
                BigDecimal originPrice = originEntrust.getTypes() == 1 ? originEntrust.getUnitPrice() : BigDecimal.ZERO.subtract(originEntrust.getUnitPrice());
                Entrust entrust = new Entrust(entrustCancleId, originPrice, BigDecimal.ZERO, BigDecimal.ZERO, 0, userId, -1, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, submitTime, 0, BigDecimal.ZERO);
                entrust.setFreezeId(entrustId);
                MemEntrustDataProcessor.addNoMatchingEntrust(entrust, m);
                SystemStatus.setSystemStatus(m.market + "_" + SystemStatus.exchangeNewWork, true);
                return Info.DoCancleSuccess;
            } else {
                return Info.DoCancleFaildPromError;
            }
        } catch (Exception ex) {
            return Info.DoCancleFaildPromError;
        }

    }

    /**
	 * 保存机器人配置
	 * @param rc
	 */
	public static void setSystemAuto(robotConfig rc,Market m){
		
		List l=(List)Data.GetOne(m.db,"select * from autoConfig where names=? and isDefault=1", new Object[]{"systemDefault"});
		log.info(rc.getIsStart());
		String objs=HTTPTcp.ObjectToString(rc);
		if(l==null){
	
			
			Data.Insert(m.db,"INSERT INTO autoconfig (NAMES, objs, isDefault, notes, times)VALUES(?,?,?,?,?);" ,new Object[]{
					"systemDefault",
					objs,
					1,
					"系统创建",
					System.currentTimeMillis()
			});
			log.info("系统创建配置");
		}
		else{
			
			Data.Update(m.db,"update  autoconfig set objs=? where ids=?" ,new Object[]{
                  objs,Integer.parseInt(l.get(0).toString())
			});
			log.info("系统更新配置"+l.get(0).toString());
				
			}
		
		String cacheKey = "systemDefaultRobotConfig"+m.numberBiEn;
		if (!"cny".equalsIgnoreCase(m.exchangeBiEn) && !"rmb".equalsIgnoreCase(m.exchangeBiEn) && !"btq".equalsIgnoreCase(m.numberBiEn)) {
			cacheKey += m.exchangeBiEn;
		}
		Cache.Set(cacheKey, objs,24*3600);
			
		
		
	}
	
	/**
	 * 保存委托机器人配置
	 * @param rc
	 */
	public static void setSystemEnstrustAuto(com.tenstar.RobotEntrustConfig rc,Market m){
		
		List l=(List)Data.GetOne(m.db,"select * from autoEntrustConfig where names=? and isDefault=1", new Object[]{"systemDefault"});
		log.info(rc.getIsStart());
		String objs=HTTPTcp.ObjectToString(rc);
		if(l==null){
	
			
			Data.Insert(m.db,"INSERT INTO autoEntrustConfig (NAMES, objs, isDefault, notes, times)VALUES(?,?,?,?,?);" ,new Object[]{
					"systemDefault",
					objs,
					1,
					"系统创建",
					System.currentTimeMillis()
			});
			log.info("系统创建配置");
		}
		else{
			
			Data.Update(m.db,"update  autoEntrustConfig set objs=? where ids=?" ,new Object[]{
                  objs,Integer.parseInt(l.get(0).toString())
			});
			log.info("系统更新配置"+l.get(0).toString());
				
			}
		String cacheKey = "systemDefaultRobotEntrustConfig"+m.numberBiEn;
		if (!"cny".equalsIgnoreCase(m.exchangeBiEn) && !"rmb".equalsIgnoreCase(m.exchangeBiEn) && !"btq".equalsIgnoreCase(m.numberBiEn)) {
			cacheKey += m.exchangeBiEn;
		}
		Cache.Set(cacheKey, objs,24*3600);
			
		
		
	}
	
	/**
	 * 保存刷量配置
	 * @param rc
	 */
	public static void saveAutoShualiangParams(com.tenstar.RobotShualiangConfig rc,Market m){
		
		List l=(List)Data.GetOne(m.db,"select * from autoShualiangConfig where names=? and isDefault=1", new Object[]{"systemDefault"});
		log.info(rc.getIsStart());
		String objs=HTTPTcp.ObjectToString(rc);
		if(l==null){
			Data.Insert(m.db,"INSERT INTO autoShualiangConfig (NAMES, objs, isDefault, notes, times)VALUES(?,?,?,?,?);" ,new Object[]{
					"systemDefault",
					objs,
					1,
					"系统创建",
					System.currentTimeMillis()
			});
			log.info("系统创建配置");
			if(rc.getIsStart() == 1){
				//开启刷量线程
				Thread t = new Thread(new AutoShualiangTask(m));
				t.start();
			}
		}
		else{
			
			Data.Update(m.db,"update autoShualiangConfig set objs=? where ids=?" ,new Object[]{
                  objs,Integer.parseInt(l.get(0).toString())
			});
			log.info("系统更新配置"+l.get(0).toString());
			
			com.tenstar.RobotShualiangConfig cf = AutoShualiangTask.getInstance(m).getConfig();
			
			String cacheKey = "systemDefaultRobotShualiangConfig"+m.numberBiEn;
			if (!"cny".equalsIgnoreCase(m.exchangeBiEn) && !"rmb".equalsIgnoreCase(m.exchangeBiEn) && !"btq".equalsIgnoreCase(m.numberBiEn)) {
				cacheKey += m.exchangeBiEn;
			}
			
			if(cf.getIsStart() == 0){
				Cache.Set(cacheKey, objs,24*3600);
			}else{
				
//				//如果是开启，需要启动线程
//				if(rc.getIsStart() == 1 && cf.getIsStart() == 0){
//					Cache.Set("systemDefaultRobotShualiangConfig"+Market.numberBiEn, objs,24*3600);
//					//开启刷量线程
//					Thread t = new Thread(new AutoShualiangTask());
//					t.start();
//				}else{
//					Cache.Set("systemDefaultRobotShualiangConfig"+Market.numberBiEn, objs,24*3600);
//				}
				if(rc.getIsStart() == 1){
					Cache.Set(cacheKey, objs,24*3600);
					//开启刷量线程
//					Thread t = new Thread(new AutoShualiangTask());
//					t.start();
				}else{
					Cache.Set(cacheKey, objs,24*3600);
				}
			}
		}
	}

	/**
	 * 保存机器人配置
	 * @param uc
	 */
	public static void setUserAuto(UserConfig uc,Market m){
		
		List l=(List)Data.GetOne(m.db,"select * from autoConfig where names=? and isDefault=1", new Object[]{"userAutoConfig"+uc.getUserId()});
		
		String objs=HTTPTcp.ObjectToString(uc);
		if(l==null){
	
			
			Data.Insert(m.db,"INSERT INTO autoconfig (NAMES, objs, isDefault, notes, times,userId)VALUES(?,?,?,?,?,?);" ,new Object[]{
					"userAutoConfig"+uc.getUserId(),
					objs,
					1,
					"",
					System.currentTimeMillis(),
					uc.getUserId()
			});
			log.info("系统创建配置");
		} 
		else{
			
			Data.Update(m.db,"update  autoconfig set objs=? where ids=?" ,new Object[]{
                  objs,Integer.parseInt(l.get(0).toString())
			});
			log.info("系统更新配置"+l.get(0).toString());
				
			}
		//更新一下线上的
		AutoTaskUser.updateuserData(uc);
		
		String cacheKey = "userAutoConfig"+m.numberBiEn+uc.getUserId();
		if (!"cny".equalsIgnoreCase(m.exchangeBiEn) && !"rmb".equalsIgnoreCase(m.exchangeBiEn) && !"btq".equalsIgnoreCase(m.numberBiEn)) {
			cacheKey = "userAutoConfig"+m.numberBiEn+m.exchangeBiEn+uc.getUserId();
		}
		
		Cache.Set(cacheKey, objs,365*24*3600);

	}

    /**
     * 根据id列表取消
     */
    public static int doCancleByIdsForBrush(int userId, List<Long> ids, Market m) {
        int n = 0;
        for (long id : ids) {
            Info in = doCancleForBrush(userId, id, m);
            if (in == Info.DoCancleSuccess) {
                n++;
            }
        }
        return n;
    }


    /**
	 * 区间取消 批量取消会取消计划委托
	 * @param userId 用户id
	 * @param priceLow 高价格
	 * @param priceHigh 低价格
	 * @param type //0 按照区间设置 1取消买入  2取消卖出 3 取消所有
	 * @return
	 */
	public static int doCancle(int webId,int userId,BigDecimal priceLow,BigDecimal priceHigh,int type,Market m){
		int n=0;
		
		String where=" and  numbers>completeNumber and unitPrice>0 and (status=-1 or status=0 or status=3) ";//未完成并且不等于取消命令
		
		if(type==1)
			where+=" and types=1 ";//取消买入
		else if(type==2)
			where+=" and types=0 ";//取消卖出
		if(priceLow.compareTo(BigDecimal.ZERO)>0)
		{
			where+=" and unitPrice>="+priceLow+" ";
		}
		if(priceHigh.compareTo(BigDecimal.ZERO)>0)
		{
			where+=" and unitPrice<="+priceHigh+" ";
		}
		 
		
		List c=(List)Data.Query(m.db,"select * from entrust where  userid=? "+where, new Object[]{userId});
		
		if(c==null||c.size()==0){
			 return 0;
		  }

		   for(int i=0;i<c.size();i++){
		     List one=(List)c.get(i);
		     Info in=doCancle(userId,Long.parseLong(one.get(0).toString()),m); 
		     if(in==Info.DoCancleSuccess)
		    	 n++;
		    
		   }
		if (n>0) {
			return n;
		} else {
			return 0;
		}
	}

    /**
     * 区间取消 批量取消会取消计划委托，刷量使用，屏蔽了日志报警
     * @param userId 用户id
     * @param priceLow 高价格
     * @param priceHigh 低价格
     * @param type //0 按照区间设置 1取消买入  2取消卖出 3 取消所有
     * @return
     */
    public static int doCancleForBrush(int webId,int userId,BigDecimal priceLow,BigDecimal priceHigh,int type,Market m){
        int n=0;

        String where=" and  numbers>completeNumber and unitPrice>0 and (status=-1 or status=0 or status=3) ";//未完成并且不等于取消命令

        if(type==1)
            where+=" and types=1 ";//取消买入
        else if(type==2)
            where+=" and types=0 ";//取消卖出
        if(priceLow.compareTo(BigDecimal.ZERO)>0)
        {
            where+=" and unitPrice>="+priceLow+" ";
        }
        if(priceHigh.compareTo(BigDecimal.ZERO)>0)
        {
            where+=" and unitPrice<="+priceHigh+" ";
        }


        List c=(List)Data.Query(m.db,"select * from entrust where  userid=? "+where, new Object[]{userId});

        if(c==null||c.size()==0){
            return 0;
        }

        for(int i=0;i<c.size();i++){
            List one=(List)c.get(i);
            Info in=doCancleForBrush(userId,Long.parseLong(one.get(0).toString()),m);
            if(in==Info.DoCancleSuccess)
                n++;

        }
        if (n>0) {
            return n;
        } else {
            return 0;
        }
    }

	/**
	 * 给外部的交易记录
	 * @return
	 */
	public static String getTreadOuter(int since,Market m){
	     return MemEntrustMatchProcessor.da.GetTradeListOuter(since,m);
	     
	}

	/**
	 * 市场全部取消
	 * Market
	 * @return
	 */
	public static int doCancleAll(Market m){
		int n=0;
		List c=(List)Data.Query(m.db,"select entrustId,userId from entrust where numbers>completeNumber and unitPrice>0 and (status=-1 or status=0 or status=3)  order by userId asc", new Object[]{});
		if(c!=null||c.size()>0){
			for(int i=0;i<c.size();i++){
				List one=(List)c.get(i);
				log.info("【取消全部挂单-限价委托】当前市场："+m.getMarket()+"取消用户："+one.get(1).toString()+"限价委托订单："+one.get(0).toString()+"开始");
				Info in=doCancle(Integer.parseInt(one.get(1).toString()),Long.parseLong(one.get(0).toString()),m);
				if(in==Info.DoCancleSuccess){
					log.info("【取消全部挂单-限价委托】当前市场："+m.getMarket()+"取消用户："+one.get(1).toString()+"限价委托订单："+one.get(0).toString()+"结束");
					n++;
				}else{
					log.error("【取消全部挂单-限价委托】当前市场："+m.getMarket()+"取消用户："+one.get(1).toString()+"限价委托订单："+one.get(0).toString()+"失败");
				}
			}
		}
		List planC=(List)Data.Query(m.db,"select entrustId,userId from plan_entrust where status=-1 order by userId asc", new Object[]{});
		if(planC!=null||planC.size()>0){
			for(int i=0;i<planC.size();i++){
				List one=(List)planC.get(i);
				log.info("【取消全部挂单-计划委托】当前市场："+m.getMarket()+"取消用户："+one.get(1).toString()+"计划委托订单："+one.get(0).toString()+"开始");
				Info in=doCanclePlanEntrust(Integer.parseInt(one.get(1).toString()),Long.parseLong(one.get(0).toString()),m);
				if(in==Info.DoCancleSuccess){
					log.info("【取消全部挂单-计划委托】当前市场："+m.getMarket()+"取消用户："+one.get(1).toString()+"计划委托订单："+one.get(0).toString()+"结束");
					n++;
				}else{
					log.error("【取消全部挂单-计划委托】当前市场："+m.getMarket()+"取消用户："+one.get(1).toString()+"计划委托订单："+one.get(0).toString()+"失败");
				}
			}
		}
		if (n>0) {
			return n;
		} else {
			return 0;
		}
	}

	/**
	 * 批量委托
	 * @return
	 */
	public static void SynData(){
		//MemEntrustMatchProcessor.da.ReInit();
	}
	/**
	 * 批量委托
	 * @param userId 用户id
	 * @param priceLow 高价格
	 * @param priceHigh 低价格
	 * @param number 数量
	 * @param type //0 按照区间设置 1取消买入  2取消卖出 3 取消所有
	 * @return
	 */
	public static String doEntrustMore(int webId, int userId, BigDecimal priceLow, BigDecimal priceHigh, BigDecimal number, int type, Market m) {
		log.info("多个委托：" + webId + ":" + userId + ":" + priceLow + ":" + priceHigh + ":" + number + ":" + type);
		int dangwei = 10;
		BigDecimal sub = priceHigh.subtract(priceLow);
		while (sub.compareTo(BigDecimal.valueOf(3)) < 0) {

			sub = sub.multiply(BigDecimal.TEN);
		}

		long qujian = sub.longValue();

		if (qujian < 5)
			dangwei = (int) qujian;
		else if (number.compareTo(BigDecimal.valueOf(50)) >= 0)
			dangwei = 50;
		else if (number.compareTo(BigDecimal.valueOf(30)) >= 0)
			dangwei = 25;
		else if (number.compareTo(BigDecimal.valueOf(25)) >= 0)
			dangwei = 10;
		else
			dangwei = 5;
		boolean gaoWeiDuiQi = (type == 1);
		BigDecimal[][] entrustModel = Tools.GetRadom(priceLow, priceHigh, number, dangwei, 1, true, m);
		int succNumber = 0;
		BigDecimal compNumber = BigDecimal.ZERO;
		BigDecimal comMoney = BigDecimal.ZERO;
		for (int i = 0; i < dangwei; i++) {

			BigDecimal[] one = entrustModel[i];
			if (one[1] == null || (compNumber.add(one[1])).compareTo(number) > 0){
				break;
			}

			// add by suxinjie 20170930 批量委托添加最小交易数量的限制
			if (one[1].compareTo(new BigDecimal(m.bixMinNum)) >= 0) {
				InfoEntrust ie = doEntrust(type, userId, one[1], one[0], webId, webId, 0, 0, null, m);
				if (ie.in.equals(Info.DoEntrustSuccess)) {
					succNumber++;
					//compNumber+=one[1];
					compNumber = compNumber.add(one[1]);
					//comMoney+=one[1]*one[0];
					comMoney = comMoney.add(one[1].multiply(one[0]));
					log.info(ie.in.getMessage());
				} else {
					//modify by xwz 加日志
					log.info("批量委托单条委托挂单失败，结果：" + ie.in.getMessage());
					break;
				}
				//log.info("仅仅是档位数量多了，但是数量"+hasEntityNumber+"还不够"+maxEntityNumber+",随机增加"+(gaoWeiDuiQi?"低位":"高位")+"："+can[i]+":"+one[1]);
			} else {
				log.info("用户:" + userId + " 在市场:" + m.market + " 批量委托拆单后 [price:" + one[0] + ", num:" + one[1] + "] 小于最小交易数量:" + m.bixMinNum);
			}
			// add by suxinjie 20170930 批量委托添加最小交易数量的限制
			if (one[1].compareTo(new BigDecimal(m.bixMaxNum)) <= 0) {
				InfoEntrust ie = doEntrust(type, userId, one[1], one[0], webId, webId, 0, 0, null, m);
				if (ie.in.equals(Info.DoEntrustSuccess)) {
					succNumber++;
					//compNumber+=one[1];
					compNumber = compNumber.add(one[1]);
					//comMoney+=one[1]*one[0];
					comMoney = comMoney.add(one[1].multiply(one[0]));
					log.info(ie.in.getMessage());
				} else {
					//modify by xwz 加日志
					log.info("批量委托单条委托挂单失败，结果：" + ie.in.getMessage());
					break;
				}
				//log.info("仅仅是档位数量多了，但是数量"+hasEntityNumber+"还不够"+maxEntityNumber+",随机增加"+(gaoWeiDuiQi?"低位":"高位")+"："+can[i]+":"+one[1]);
			} else {
				log.info("用户:" + userId + " 在市场:" + m.market + " 批量委托拆单后 [price:" + one[0] + ", num:" + one[1] + "] 大于最大交易数量:" + m.bixMaxNum);
			}

		}
		if (compNumber.compareTo(number) < 0) {
			BigDecimal last = number.subtract(compNumber);
			BigDecimal lastPrice = (type == 1) ? priceHigh : priceLow;
			InfoEntrust ie = doEntrust(type, userId, last, lastPrice, webId, webId, 0, 0, null, m);
			if (ie.in.equals(Info.DoEntrustSuccess)) {
				succNumber++;
				//compNumber+=last;
				compNumber = compNumber.add(last);
				comMoney = comMoney.add(last.multiply(lastPrice));
				//comMoney+=last*lastPrice;

			}
			log.info(ie.in.getMessage());
		}

		String rtn = succNumber + ":" + Market.formatNumber(compNumber, m) + ":" + Market.formatMoney(comMoney, m);
		return rtn;
	}
	 
	
	
	/**
	 * 处理计划委托
	 * @param type 买卖类型
	 * @param userId  用户id
	 * @param number 交易数量   精确到最小单位取整数，不可以有小树
	 * @param unitPrice 单价 如果是人民币 精确到分，然后取整数   比特币*10*8取整
	 * @param sumToWeb 数据是否合并到项目id
	 * @param webId   当前webid
	 * @param isPlan 是否是计划委托 1是计划 0是正常真实
	 * @param mt 是否允许用户高价买低价卖或成交自己单据1=true,0=false, default 0;	guosj
	 * @param triggerPrice 计划委托增加触发价格  zhanglinbo 20160721
	 * @return 信息结果
	 */
	public static InfoEntrust doPlanEntrust(int type,int userId,BigDecimal number,BigDecimal unitPrice,int sumToWeb,int webId,int isPlan, int mt, String customerOrderId,BigDecimal triggerPrice,BigDecimal unitPriceProfit,BigDecimal triggerPriceProfit,BigDecimal totalMoney,Market m){
		
		
		if((number.compareTo(BigDecimal.ZERO) <= 0 && type==0) || (totalMoney.compareTo(BigDecimal.ZERO) <= 0 && type==1) ||userId<=0){
			return new InfoEntrust(Info.DoEntrustFaildWrongParam,0);
		}

		/*add by xwz 20170920 计划委托增加增加成交金额的限制
		* type=0:卖出，根据委托价格和委托数量计算成交金额
		* type=1:买入：根据成交金额和委托价格计算成交数量
		* */
		BigDecimal tmpTotalMoney = BigDecimal.ZERO;
		if(type == 0){
			if(unitPriceProfit.compareTo(BigDecimal.ZERO) <= 0){
				tmpTotalMoney = number.multiply(unitPrice);
			}else{
				tmpTotalMoney = number.multiply(unitPriceProfit);
			}
		}else{
			tmpTotalMoney = totalMoney;
		}
		log.info("tmpTotalMoney:" + tmpTotalMoney + ",totalMoney:" + totalMoney +",unitPriceProfit:" + unitPriceProfit + ",number:" + number);
		if(m.getMinAmount()>0 && tmpTotalMoney.compareTo(BigDecimal.valueOf(m.getMinAmount()))<0){//成交金额小于知道的最小交易金额，返回委托失败
			Info info =  Info.DoEntrustFaildMinAmount;
			DecimalFormat df = new DecimalFormat("0.#########");
			info.setMessage(l(getLan(userId+""), "委托失败-成交金额小于系统规定金额", df.format(m.getMinAmount())+m.exchangeBi.toUpperCase()));
			return new InfoEntrust(info, 0, new VipResponse());
		}
		/*end*/
		//尝试扣除资金.这里仅仅作为测试方法，返回资金服务端的id,实际这里需要远程调用
		BigDecimal total=BigDecimal.ZERO;//买单冻结总资产计算
		
		String method="";
		boolean[] changes = null;
		List<OneSql> sqls = new ArrayList<OneSql>();
		TransactionObject txObj = new com.world.data.mysql.transaction.TransactionObject();
		if(type==1){
			
			//total=Market.formatTotalMoneyDoule(unitPrice,number); 
			  total =  totalMoney ;//将长整形的资金转换为真实资金
			///购买时添加资产判断
				BigDecimal userTotal = BigDecimal.ZERO;
				
					List<Object> userObj = WorldManager.excuteQueryPayUserEntrust(txObj, userId,m.exchangeBiFundsType);
					if(userObj != null){
						//long tl = userObj.get(0) != null ? (Long)userObj.get(0) : 0;
						userTotal = userObj.get(0) != null ? (BigDecimal)userObj.get(0) : BigDecimal.ZERO;
					}
				 
				
				if(userTotal.compareTo(total)<0){
					txObj.rollback("userId:" + userId + ",funds is no insufficient or user is null");
					return new InfoEntrust(Info.DoEntrustFaildNotEnoughMoney,0);
				}
				
				changes = WorldManager.buy(userId, total, sqls,m);
		}else if(type==0){
				//卖委托冻结币
				total= number ;
				///卖出时添加资产判断
				BigDecimal userTotal = BigDecimal.ZERO;
				List<Object> userObj = WorldManager.excuteQueryPayUserEntrust(txObj, userId, m.numberBiFundsType);
				if(userObj != null){
					userTotal = userObj.get(0) != null ? (BigDecimal)userObj.get(0) : BigDecimal.ZERO;
				}
				
				
				if(userTotal.compareTo( total)<0){
					txObj.rollback("userId:" + userId + ",funds is no insufficient or user is null");
					return new InfoEntrust(Info.DoEntrustFaildNotEnoughMoney,0);
				}
				
				changes = WorldManager.sell(userId, total, sqls,m);
		} else { 
			return  new InfoEntrust(Info.DoEntrustFaildUnKnowType,0);
		}
		log.info(method+"::::"+total+":"+totalMoney);
		long entrustId = autoId.getDataId("plan_entrust",m.db);
		//委托单重复验证
		//如果没有给用户的委托单id，就不验证，给了的话，就根据查下用户上个委托单id是多少
		OneSql entrustOtherSql = null;
		if(customerOrderId!=null && customerOrderId.length() > 0){
			Long longId = Long.parseLong(customerOrderId);
			EntrustOtherBean other = Data.GetOneT(m.db,"select * from entrust_other where userId=?", new Object[]{userId}, EntrustOtherBean.class);
			
			//如果entrust_other表有记录，并且里面的customerOrderId大于传过来的id
			if(other!=null && other.getCustomerOrderId()>=longId){	
				//根据customerOrderId查出chbtc的orderid
				long existEntrustId = other.getEntrustId();
				return new InfoEntrust(Info.DoEntrustFailDunplicate,existEntrustId);	//提示重复
			}else{
				long entrustOtherId = autoId.getDataId("entrust_other",m.db);
				entrustOtherSql = new OneSql("insert into entrust_other(id, userId, customerOrderId, entrustId) values(?,?,?,?)", 1, new Object[]{entrustOtherId, userId, longId, entrustId},m.db);
			}
		}else{
			customerOrderId = null;
		}
		  
		if(entrustOtherSql!=null){	//增加委托单最终记录的bean
			sqls.add(entrustOtherSql);
		}
		  
		  long submitTime = System.currentTimeMillis();
		  
		  int status = -1;//计划委托初始状态 -1:计划中 1:已取消 2:已完成
		  //totalMoney = DigitalUtil.longMultiply(totalMoney, Market.numberBixShow);//转为长整形。乘以币种的位数
		  sqls.add(new OneSql(
					"INSERT INTO plan_entrust (entrustId, unitPrice, numbers, totalMoney, sumToWeb, webId, TYPES, userId, submitTime,status,triggerPrice,unitPriceProfit,triggerPriceProfit) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)",
					1,
					new Object[]{
							entrustId,
							unitPrice,//计划委托追高、止损委托价格
							number,
							totalMoney,//对于卖设置为0
							sumToWeb, 
							webId,
							type,
							userId,
							submitTime,
							status,  //-1状态标示为计划委托状态 如果条件合适的时候，直接update该项为1就可以了
							triggerPrice,//计划追高、止损委托触发价格
							unitPriceProfit,//计划委托抄底、止盈价格
							triggerPriceProfit//计划委托抄底、止盈触发价格
                      },m.db));
		  VipResponse chbtcrs1 = new VipResponse();
		 
		 txObj.excuteUpdateList(sqls);
		 ///2016-6-2 升级为独立事物    6-25升级为特殊业务事物
		 if(txObj.commit()){
             MemEntrustMatchProcessor.da.getTop(userId,m);//更新用户委托数据
			 if(changes != null){
				 AsynMethodFactory.addWork(FundsUserDao.class, "updateFundsByChange", new Object[]{userId});
			 }
			 MemPlanEntrustProcessor.addEntrust(triggerPriceProfit,triggerPrice,type, entrustId, new PlanEntrustRecord(entrustId,unitPrice, number,webId,userId,type,number, BigDecimal.ZERO,totalMoney, submitTime,status,triggerPrice,unitPriceProfit,triggerPriceProfit,totalMoney),m);
			// SystemStatus.exchangeNewWork=true;
			 SystemStatus.setSystemStatus(m.market+"_"+SystemStatus.exchangeNewWork,true);
			 return  new InfoEntrust(Info.DoEntrustSuccess,entrustId);
		 } else {
			 return  new InfoEntrust(Info.DoEntrustFaildPromError,0,chbtcrs1);
		 }
	}
	
	/**
	 * 取消计划委托
	 * @param userId  用户id
	 * @param entrustId 交易ID
	 * @param m
	 * @return 信息结果
	 */
	public static Info doCanclePlanEntrust(int userId,long entrustId,Market m){ 
		if(entrustId <= 0 || userId <= 0){
			return Info.DoEntrustFaildWrongParam;
		}
		try{
			BigDecimal amount= BigDecimal.ZERO;
			//1、在新表查询取消计划委托数据 ，如不存在了，返回
			//增加事务锁操作，防止重复取消操作，导致插入freezeId唯一索引 重复报错 20161011 zhanglinbo 
			TransactionObject txObj = new com.world.data.mysql.transaction.TransactionObject();
			
			PlanEntrustBean pEntrust= txObj.excuteQueryT(new OneSql("select * from plan_entrust where entrustId=? and userid=? and status=-1 for update",
				  -1, new Object[]{entrustId,userId},m.db),PlanEntrustBean.class);   
  		   
		   if(pEntrust==null || pEntrust.getEntrustId()<=0){
			   txObj.rollback(m.db +" userid: " + userId + ", 委托ID："+entrustId+" 已经取消 ，不能重复取消，操作回滚！");
			   return Info.DoCancleFaildNoOrder;
		   }
			 
		   //计算需要解冻的资金
		   if(pEntrust.getTypes()==1){
				amount= Market.formatTotalMoney(pEntrust.getUnitPrice(),pEntrust.getNumbers()); 
				if(amount.compareTo(BigDecimal.ZERO)<=0){//新模式计算是为0的，必须采用下委托时输入的总金额 
					amount = pEntrust.getTotalMoney();
				}
		   }else{
			   
			    amount= pEntrust.getNumbers();
			}
			
           log.info(amount+":"); 
           List<OneSql> sqls = new ArrayList<OneSql>();
           sqls.add(new OneSql("update plan_entrust set status = 1  where entrustId=? and userid=? and status=-1 ",1, new Object[]{entrustId,userId},m.db));
		 
           long start = TimeUtil.getNow().getTime();
		   boolean[] changes = null;
		   if(pEntrust.getTypes()==1){
			   changes = WorldManager.cancelBuy(userId, amount, sqls,m);
		   }else{
			   changes = WorldManager.cancelSell(userId, amount, sqls,m);
		   }
		   txObj.excuteUpdateList(sqls);//执行批量事务
		   if (txObj.commit()) {//提交事务
		        long end = TimeUtil.getNow().getTime();
		        //从缓存移除计划委托。防止继续匹配触发下单
		      	MemPlanEntrustProcessor.removeEntrust(pEntrust.getTypes(),pEntrust.getTriggerPriceProfit(),pEntrust.getTriggerPrice(),entrustId,m);
		      	//刷新用户资产
		      	AsynMethodFactory.addWork(FundsUserDao.class, "updateFundsByChange", new Object[]{userId});
			   log.info("取消单个计划委托事务耗时：" + (end - start));
			   //刷新用户委托数据
			   AsynMethodFactory.addWork(DataArray.class, "getTopThread2016", new Object[]{userId,m});
				return Info.DoCancleSuccess;
			}else {
				return Info.DoEntrustFaildPromError;
			}
			
		}catch(Exception ex){
			return Info.DoCancleFaildPromError;
		}
		
	}
	
	
	/**
	 * 区间取消 批量取消会取消计划委托
	 * @param userId 用户id
	 * @param priceLow 高价格
	 * @param priceHigh 低价格
//	 * @param isAll 是否为取消所有订单
	 * @param type //0 按照区间设置 1取消买入  2取消卖出 3 取消所有
	 * @return
	 */
	public static int doCancleMorePlanEntrust(int webId,int userId,BigDecimal priceLow,BigDecimal priceHigh,int type,Market m){
		int n=0;
		
		String where=" and   status=-1  ";//未完成并且不等于取消命令
		
		
		 
		
		List c=(List)Data.Query(m.db,"select * from plan_entrust where  userid=? "+where, new Object[]{userId});
		
		if(c==null||c.size()==0){
			 return 0;
		  }

		   for(int i=0;i<c.size();i++){
		     List one=(List)c.get(i);
		     Info in=doCanclePlanEntrust(userId,Long.parseLong(one.get(0).toString()),m); 
		     if(in==Info.DoCancleSuccess)
		    	 n++;
		    
		   }
		if (n>0) {
			return n;
		} else {
			return 0;
		}
	}
	
	

	
	/**
	 * 计划委托触发成为正式委托
	 * @param type 是否为购买  1：买 0：卖
	 * @param userId  用户id
	 * @param number 交易数量   精确到最小单位取整数，不可以有小数
	 * @param unitPrice 单价 如果是人民币 精确到分，然后取整数   比特币*10*8取整
	 * @param sumToWeb 数据是否合并到项目id
	 * @param webId   当前webid
	 * @param isPlan 是否是计划委托 1是计划 0是正常真实
	 * @return 信息结果
	 */
	public static InfoEntrust doPlan2Entrust(int type,int userId,BigDecimal number,BigDecimal unitPrice,int sumToWeb,int webId,int isPlan, BigDecimal totalMoney, long planEntrustId,Market m){
		
		number=Market.ffNumber(number,m);
		unitPrice=Market.ffMoney(unitPrice,m);
		
		if(number.compareTo(BigDecimal.ZERO) <= 0 || unitPrice.compareTo(BigDecimal.ZERO) <= 0||userId<=0){
			return new InfoEntrust(Info.DoEntrustFaildWrongParam,0);
		}
		if(unitPrice.compareTo(BigDecimal.valueOf(m.maxPrice))>0){
			return  new InfoEntrust(Info.DoEntrustFaildHighPeice,0);
		}
		
		//计划委托已经冻结过资金，此处转正式委托不再进行资金操作
		if(type==0){
			totalMoney=Market.formatTotalMoney(unitPrice,number);
		}
		List<OneSql> sqls = new ArrayList<OneSql>();
		TransactionObject txObj = new com.world.data.mysql.transaction.TransactionObject();
		
		long entrustId = autoId.getId(m.getMarket()+"entrust",m.db);
		long submitTime = System.currentTimeMillis();
		int status = 0;//正式委托

		//1、增加正式委托
			  sqls.add(new OneSql(
						"INSERT INTO entrust (entrustId, unitPrice, numbers, totalMoney, sumToWeb, webId, TYPES, userId, submitTime,status,feeRate) VALUES (?,?,?,?,?,?,?,?,?,?,?)",
						1,
						new Object[]{
								entrustId,
								unitPrice,
								number,
								totalMoney,//对于卖设置为0
								sumToWeb, 
								webId,
								type,
								userId,
								submitTime,
								status , //-1状态标示为计划委托状态 如果条件合适的时候，直接update该项为1就可以了
								0 //增加委托记录手续费费率
	                      },
						m.db));
		 
		  
		 //2、计划委托更改状态为已经处理
			  sqls.add(new OneSql("UPDATE plan_entrust SET status = 2 ,formalEntrustId=? WHERE entrustId=?  AND userid=? AND status=-1 ",
					  1,
					  new Object[]{
							  entrustId,
							  planEntrustId,
							  userId},m.db));
		  
		  
		 VipResponse vrs1 = new VipResponse();
		 
		 txObj.excuteUpdateList(sqls);
		 //20160808 事务提交
		 if(txObj.commit()){
				 //添加到委托队列并更新单个交易缓存信息
				 log.info("推送驾驶舱委托数据埋点开始："+entrustId);
				 JSONObject jsonObject = new JSONObject();
				 jsonObject.put("userid",userId);
				 jsonObject.put("entrustid",entrustId);
				 jsonObject.put("unitprice",unitPrice);
				 jsonObject.put("entrustsum",number);
				 jsonObject.put("entrustmarket",m.getMarket());
				 jsonObject.put("fundstype",m.getNumberBiFundsType());
				 jsonObject.put("clienttype",webId);
				 ProducerSend producerSend = new ProducerSend();
				 producerSend.sendMessage("entrust", jsonObject.toString());
				 log.info("推送驾驶舱委托数据埋点成功："+jsonObject);
                 MemEntrustMatchProcessor.da.getTop(userId,m);
				 Entrust entrust = new Entrust(entrustId, unitPrice, number, totalMoney, webId, userId, type, number,BigDecimal.ZERO, BigDecimal.ZERO, submitTime, status, BigDecimal.ZERO);
				 MemEntrustDataProcessor.addNoMatchingEntrust(entrust,m);
			 return  new InfoEntrust(Info.DoEntrustSuccess,entrustId);
		 } else {
			 return  new InfoEntrust(Info.DoEntrustFaildPromError,0,vrs1);
		 }
	}

}
