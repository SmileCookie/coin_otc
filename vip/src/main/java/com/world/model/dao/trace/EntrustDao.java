package com.world.model.dao.trace;

import com.api.util.DigitalUtil;
import com.world.data.mysql.Data;
import com.world.data.mysql.DataDaoSupport;
import com.world.model.entity.trace.Entrust;
import com.world.model.entity.trace.PlanEntrust;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EntrustDao  extends DataDaoSupport<Entrust>{

	/**
	 * 获取买卖盘前10个委托
	 * @author zhanglinbo 20161227
	 * @return Map<String,List<Entrust>>
	 */
	//xzhang 20170828 委托交易数据查询异常 JYPT-1258
	public Map<String,Object> getEntrustList(long entrustId,long userId,int currentPage,int pageSize,String dbName){

		Map<String,Object> map = new HashMap<String,Object>();
		try{
			if(currentPage ==0){
				currentPage = 1;
			}
			StringBuilder where=new StringBuilder();
			where.append(" and unitPrice>0 ");
			if(entrustId > 0){
				where.append(" and entrustId ="+entrustId+"");
			}
			if(userId > 0){
				where.append(" and userId = "+userId+" ");
			}
			String w=where.toString();
			if(w.length()>0) {
				w = " where " + w.substring(4);
			}
			String sql="select * from (select EntrustId,UnitPrice,Numbers,TotalMoney,CompleteNumber,CompleteTotalMoney,WebId,Types,UserId,STATUS,SubmitTime,FeeRate from entrust "+w+"  ";
			sql += "union all select EntrustId,UnitPrice,Numbers,TotalMoney,CompleteNumber,CompleteTotalMoney,WebId,Types,UserId,STATUS,SubmitTime,FeeRate from entrust_all "+w+" ) fa ";
			sql += " order by entrustId desc limit ?,?";
			log.info("sql显示："+sql);
			String sqlcount =" SELECT (select count(*) FROM entrust  "+w+" )+(select count(*) FROM entrust_all " +w+ ") from dual";
			List dataList = Data.Query(dbName,sql, new Object[]{(currentPage-1)*pageSize , pageSize});//委托记录
			List count = (List) Data.GetOne(dbName,sqlcount, new Object[]{});//委托记录数量
			List<Entrust> entrustList = new ArrayList<Entrust>();
			Entrust  entrust = null;
			for(Object b : dataList){
				List beb = (List) b;
				entrust = new Entrust();
				entrust.setEntrustId(beb.get(0) != null?Long.parseLong(beb.get(0).toString()):null);
				entrust.setUnitPrice(beb.get(1) != null?DigitalUtil.getBigDecimal(beb.get(1)):null);
				entrust.setNumbers(beb.get(2) != null?DigitalUtil.getBigDecimal(beb.get(2)):null);
				entrust.setTotalMoney(beb.get(3)!= null?DigitalUtil.getBigDecimal(beb.get(3)):null);
				entrust.setCompleteNumber(beb.get(4)!= null?DigitalUtil.getBigDecimal(beb.get(4)):null);
				entrust.setCompleteTotalMoney(beb.get(5)!= null?DigitalUtil.getBigDecimal(beb.get(5)):null);
				entrust.setWebId(beb.get(6) != null?Integer.parseInt(beb.get(6).toString()):null);//委托途径 5：APP 6：API 8：网站
				entrust.setTypes(beb.get(7) != null?Integer.parseInt(beb.get(7).toString()):null);//0:卖 1：买
				entrust.setUserId(beb.get(8) != null?Integer.parseInt(beb.get(8).toString()):null);//挂单用户ID
				entrust.setStatus(beb.get(9) != null?Integer.parseInt(beb.get(9).toString()):null);//挂单成交状态
				entrust.setSubmitTime(beb.get(10) != null?Long.parseLong(beb.get(10).toString()):null) ;//挂单委托时间
				entrust.setFeeRate(beb.get(11) != null?DigitalUtil.getBigDecimal(beb.get(11)):null) ;//手续费费率
				entrustList.add(entrust);
			}
			map.put("total", Integer.parseInt(count.get(0).toString()));
			map.put("dataList", entrustList);
		}catch(Exception e){
			map.put("total", 0);
			map.put("dataList", new ArrayList<Entrust>());
		}
		return map;
	}

	/**
	 * @Auther chendi 20170925
	 * @param entrustId
	 * @param userId
	 * @param currentPage
	 * @param pageSize
	 * @param dbName
	 * @return
	 */
	public Map<String,Object> getPlanEntrustList(long entrustId,int type,long timeFrom,long timeTo,long numberFrom,long numberTo,long priceFrom,long priceTo,long userId,int currentPage,int pageSize,String dbName,int status){
		Map<String,Object> map = new HashMap<String,Object>();
		try{
			if(currentPage ==0){
				currentPage = 1;
			}
			StringBuilder where=new StringBuilder();
			if(userId>0){
				where.append(" and userId = "+userId);
			}
			if(entrustId > 0){
				where.append(" and entrustId ="+entrustId+"");
			}
			if(status>0){
				if(status==2)//完成
					where.append(" and status=2");//仅仅显示这个状态的
				else if(status==1)//取消完成
					where.append(" and status=1");//仅仅显示这个状态的
				else if(status==3)
					where.append(" and status=3");//仅仅显示这个状态的
			}else if(status<0){
				where.append(" and status=-1");//未完成并且不等于取消命令
			}
			//status==0 是所有的意思

			if(type==0||type==1||type==-1){
				where.append(" and types="+type);//类型
			}

			if(timeFrom>0)
				where.append(" and submitTime>="+timeFrom);//时间从
			if(timeTo>0)
				where.append(" and submitTime<="+timeTo);//时间到
			if(numberFrom>0)
				where.append(" and numbers>="+numberFrom);//时间从
			if(numberTo>0)
				where.append(" and numbers<="+numberTo);//时间到

			if(priceFrom>0)
				where.append(" and unitPrice>="+priceFrom);//时间到
			if(priceTo>0)
				where.append(" and unitPrice<="+priceTo);//时间到

			String w=where.toString();
			if(w.length()>0) {
				w = " where " + w.substring(4);
			}
			String sql="select * from (select EntrustId,UnitPrice,Numbers,TotalMoney,CompleteNumber,CompleteTotalMoney,WebId,Types,UserId,STATUS,SubmitTime,FeeRate,TriggerPrice,TriggerPriceProfit,UnitPriceProfit,FormalEntrustId from  plan_entrust "+w+"  ";
			sql += "union all select EntrustId,UnitPrice,Numbers,TotalMoney,CompleteNumber,CompleteTotalMoney,WebId,Types,UserId,STATUS,SubmitTime,FeeRate,TriggerPrice,TriggerPriceProfit,UnitPriceProfit,FormalEntrustId from plan_entrust_all "+w+" ) fa ";
			sql += " order by entrustId desc limit ?,?";
			log.info("sql显示："+sql);
			String sqlcount =" SELECT (select count(*) FROM plan_entrust  "+w+" )+(select count(*) FROM plan_entrust_all " +w+ ") from dual";
			List dataList = Data.Query(dbName,sql, new Object[]{(currentPage-1)*pageSize , pageSize});//委托记录
			List count = (List) Data.GetOne(dbName,sqlcount, new Object[]{});//委托记录数量
			List<PlanEntrust> entrustList = new ArrayList<PlanEntrust>();
			PlanEntrust  entrust = null;
			for(Object b : dataList){
				List beb = (List) b;
				entrust = new PlanEntrust();
				entrust.setEntrustId(beb.get(0) != null?Long.parseLong(beb.get(0).toString()):null);
				entrust.setUnitPrice(beb.get(1) != null?DigitalUtil.getBigDecimal(beb.get(1)):null);
				entrust.setNumbers(beb.get(2) != null?DigitalUtil.getBigDecimal(beb.get(2)):null);
				entrust.setTotalMoney(beb.get(3)!= null?DigitalUtil.getBigDecimal(beb.get(3)):null);
				entrust.setCompleteNumber(beb.get(4)!= null?DigitalUtil.getBigDecimal(beb.get(4)):null);
				entrust.setCompleteTotalMoney(beb.get(5)!= null?DigitalUtil.getBigDecimal(beb.get(5)):null);
				entrust.setWebId(beb.get(6) != null?Integer.parseInt(beb.get(6).toString()):null);//委托途径 5：APP 6：API 8：网站
				entrust.setTypes(beb.get(7) != null?Integer.parseInt(beb.get(7).toString()):null);//0:卖 1：买
				entrust.setUserId(beb.get(8) != null?Integer.parseInt(beb.get(8).toString()):null);//挂单用户ID
				entrust.setStatus(beb.get(9) != null?Integer.parseInt(beb.get(9).toString()):null);//挂单成交状态
				entrust.setSubmitTime(beb.get(10) != null?Long.parseLong(beb.get(10).toString()):null) ;//挂单委托时间
				entrust.setFeeRate(beb.get(11) != null?DigitalUtil.getBigDecimal(beb.get(11)):null) ;//手续费费率
				entrust.setTriggerPrice(beb.get(12) != null?DigitalUtil.getBigDecimal(beb.get(12)):null); ;//触发价格
				entrust.setTriggerPriceProfit(beb.get(13) != null?DigitalUtil.getBigDecimal(beb.get(13)):null); ;//计划委托抄底/止盈触发价
				entrust.setUnitPriceProfit(beb.get(14) != null?DigitalUtil.getBigDecimal(beb.get(14)):null); ;//计划委托 抄底/止盈委托价
				entrust.setFormalEntrustId(beb.get(15) != null?Long.parseLong(beb.get(15).toString()):0);  //正式委托id
				entrustList.add(entrust);
			}
			map.put("total", Integer.parseInt(count.get(0).toString()));
			map.put("dataList", entrustList);
		}catch(Exception e){
			e.printStackTrace();
			map.put("total", 0);
			map.put("dataList", new ArrayList<Entrust>());
		}
		return map;
	}

    public List<Entrust> getEntrustListByUser(String userId, int currentPage, int pageSize, String dbName) {
        String sql = "select entrustId,unitPrice,numbers,totalMoney,completeNumber,completeTotalMoney,submitTime from entrust where userId=? and status!=1 order by entrustId desc limit ?,?";
        List<Entrust> dataList = Data.QueryT(dbName, sql, new Object[]{userId, (currentPage - 1) * pageSize, pageSize}, Entrust.class);
        if (CollectionUtils.isEmpty(dataList)) {
            return new ArrayList<>();
        }
        return dataList;
    }

    public List<Entrust> getEntrustListByUserAndId(String userId, int pageSize, long entrustId, String dbName) {
        String sql = "select entrustId,unitPrice,numbers,totalMoney,completeNumber,completeTotalMoney,submitTime from entrust where userId=? and status!=1 and entrustId<? order by entrustId desc limit ?";
        List<Entrust> dataList = Data.QueryT(dbName, sql, new Object[]{userId, entrustId, pageSize}, Entrust.class);
        if (CollectionUtils.isEmpty(dataList)) {
            return new ArrayList<>();
        }
        return dataList;
    }

    public List<Entrust> getEntrustAllListByUserAndId(String userId, long entrustId, int pageSize, String dbName) {
        String sql = "select entrustId,unitPrice,numbers,totalMoney,completeNumber,completeTotalMoney,submitTime from entrust_all where userId=? and status!=1 and entrustId<? order by entrustId desc limit ?";
        List<Entrust> dataList = Data.QueryT(dbName, sql, new Object[]{userId, entrustId, pageSize}, Entrust.class);
        if (CollectionUtils.isEmpty(dataList)) {
            return new ArrayList<>();
        }
        return dataList;
    }

    public Entrust getEntrustById(long entrustId, String dbName) {
        String sql = "select entrustId,unitPrice,numbers,totalMoney,completeNumber,completeTotalMoney,submitTime from entrust where entrustId=? ";
        List<Entrust> dataList = Data.QueryT(dbName, sql, new Object[]{entrustId}, Entrust.class);
        if (CollectionUtils.isEmpty(dataList)) {
            return null;
        }
        return dataList.get(0);
    }

    public Entrust getEntrustAllById(long entrustId, String dbName) {
        String sql = "select entrustId,unitPrice,numbers,totalMoney,completeNumber,completeTotalMoney,submitTime from entrust_all where entrustId=? ";
        List<Entrust> dataList = Data.QueryT(dbName, sql, new Object[]{entrustId}, Entrust.class);
        if (CollectionUtils.isEmpty(dataList)) {
            return null;
        }
        return dataList.get(0);
    }

    public List<Entrust> getEntrustByIds(List<Long> entrustIds, String dbName) {
        if (CollectionUtils.isEmpty(entrustIds) || entrustIds.size() > 50) {
            return new ArrayList<>();
        }
        String sql = "select entrustId,unitPrice,numbers,totalMoney,completeNumber,completeTotalMoney,submitTime from entrust where entrustId in (" +
                StringUtils.join(entrustIds, ",") + ") ";
        List<Entrust> dataList = Data.QueryT(dbName, sql, new Object[]{}, Entrust.class);
        if (CollectionUtils.isEmpty(dataList)) {
            return new ArrayList<>();
        }
        return dataList;
    }

    public List<Entrust> getEntrustALLByIds(List<Long> entrustIds, String dbName) {
        if (CollectionUtils.isEmpty(entrustIds) || entrustIds.size() > 50) {
            return new ArrayList<>();
        }
        String sql = "select entrustId,unitPrice,numbers,totalMoney,completeNumber,completeTotalMoney,submitTime from entrust_all where entrustId in (" +
                StringUtils.join(entrustIds, ",") + ") ";
        List<Entrust> dataList = Data.QueryT(dbName, sql, new Object[]{}, Entrust.class);
        if (CollectionUtils.isEmpty(dataList)) {
            return new ArrayList<>();
        }
        return dataList;
    }

    public Entrust getOneEntrustByUser(String userId, String dbName) {
        String sql = "select entrustId,unitPrice,numbers,totalMoney,completeNumber,completeTotalMoney,submitTime from entrust where userId=? and status!=1 order by entrustId desc limit 1";
        List<Entrust> dataList = Data.QueryT(dbName, sql, new Object[]{userId}, Entrust.class);
        if (CollectionUtils.isEmpty(dataList)) {
            return null;
        }
        return dataList.get(0);
    }

    public List<Entrust> getSellUnDoneEntrust(int pageSize, String dbName) {
        String sql = "select unitPrice, (numbers-completeNumber) numbers from entrust where types=0 and status=3 and (numbers-completeNumber)>0 and unitPrice>0 ORDER BY unitPrice ASC LIMIT ?";
        List<Entrust> dataList = Data.QueryT(dbName, sql, new Object[]{pageSize}, Entrust.class);
        if (CollectionUtils.isEmpty(dataList)) {
            return new ArrayList<>();
        }
        return dataList;
    }

	public static void main(String[] args){
		EntrustDao en=new EntrustDao();

		Map<String,Object> map=en.getPlanEntrustList(0L,0,0l,0l,0l,0l,0l,0l,0l,1,10,"btcusdtentrust",0);
		System.out.println("w完成");
	}




}
