package com.world.model.financialproift.userfininfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.redis.RedisUtil;
import com.world.data.mysql.Bean;
import com.world.data.mysql.Data;
import com.world.data.mysql.OneSql;
import com.world.data.mysql.transaction.TransactionObject;
import com.world.model.dao.task.Worker;
import com.world.model.entity.financialproift.FinTeamUser;
import com.world.model.entity.financialproift.UserFinancialInfo;

import me.chanjar.weixin.common.util.StringUtils;

public class SetUserInvitaionNum extends Worker {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/*此轮定时任务结束标识*/
    private static boolean workFlag = true;
    /*查询SQL*/
	private String sql = "";
	public SetUserInvitaionNum(String name, String des) {
		super(name, des);
	}
	
	@Override
	public void run() {
		if (workFlag) {
			/*记录核算开始时间*/
	        long startTime = System.currentTimeMillis();
			try {
				/*任务是否可执行 invitationTotalNum */
		      	workFlag = false;
		      	/*查询已支付的用户 实体类 UserFinancialInfo */
		      	sql = "select userId, userName from fin_userfinancialinfo where authPayFlag = 2";
		      	log.info(" sql = " + sql);
		      	List<Bean> listUserFinancialInfo = (List<Bean>) Data.Query("vip_financial", sql, null, UserFinancialInfo.class);
		      	UserFinancialInfo userFinancialInfo = null;
//		      	String userName = "";
		      	int userId = 0;
		      	String invitationTotalNum = "1";
		      	if (null != listUserFinancialInfo && listUserFinancialInfo.size() > 0) {
		      		for (int i = 0;i < listUserFinancialInfo.size(); i++) {
		      			userFinancialInfo = (UserFinancialInfo) listUserFinancialInfo.get(i);
		      			if(!StringUtils.isEmpty(userFinancialInfo.getUserName())) {
//		      				userName = userFinancialInfo.getUserName();
		      				userId = userFinancialInfo.getUserId();
		      				try {
//		      					/*调用用户信息*/
//		      					String urlFinancial = ApiConfig.getValue("urlfinancial.url");
//		      					urlFinancial += "/getAllOrgNum?username=" + userName;
//		      					log.info("urlFinancial = " + urlFinancial);
//		      					String resultInterface = "";
//		      					/*接口返回码和返回消息*/
//		      					resultInterface = doPostData(urlFinancial, null);
//		      					JSONObject jsonResultInterface = com.alibaba.fastjson.JSONObject.parseObject(resultInterface);
////		      					log.info("jsonResultInterface = " + jsonResultInterface);
//		      					if (null != jsonResultInterface) {
//		      						if (null != jsonResultInterface.getString("totalNum")) {
//		      							invitationTotalNum = jsonResultInterface.getString("totalNum");
//		      						}
//		      					}
//		      					if (StringUtils.isEmpty(invitationTotalNum)) {
//		      						invitationTotalNum = "0";
//		      					}
		      					invitationTotalNum = getAllTeamUserNum(Long.valueOf(userId + "")) + "";
		      					if (StringUtils.isEmpty(invitationTotalNum)) {
		      						invitationTotalNum = "1";
		      					}
		      					log.info("financial_invitationTotalNum_" + userId + " = " + invitationTotalNum);
		      					RedisUtil.set("financial_invitationTotalNum_" + userId, invitationTotalNum, 0);
		      				} catch (Exception e) {
		      					log.info("理财报警INTERFACE:设置用户层级人数异常", e);
		      				}
		      			}
		      		}
		      	}
			} catch (Exception e) {
				log.info("理财报警INTERFACE:设置用户层级人数异常", e);
			}finally {
				workFlag = true;
			}
			long endTime = System.currentTimeMillis();
    		log.info("理财报警INTERFACE:【设置用户层级人数】结束!!!【扫描耗时：{" + (endTime - startTime) + "}】");
		} else {
			log.info("理财报警INTERFACE:设置用户层级人数，上一轮任务还没有结束!");
		}
	}
	
	public int getAllTeamUserNum (Long userId) {
		List<OneSql> sqls = new ArrayList<>();
		TransactionObject txObj = new TransactionObject();
		List<Bean> listFinTeamUser = getAllTeamUser(userId);
		int num = listFinTeamUser.size();
		FinTeamUser teamUser = null;
		/**
		 * hierarchyLevel 
		 * hierarchyTotalNum 总位置 
		 * userOccupyNum 已占位置
		 * userEmptyNum 空缺
		 * userActiveNum  已激活
		 * userNoActiveNum 未激活
		 */
		int countLevel = 1;
		long countLevelTotalNum = 0;
		long countLevelOccupyNum = 0;
		long countLevelNoActiveNum = 0;
		long countLevelActiveNum = 0;
		long countLevelEmptyNum = 0;
		int nextNum = 0;// 下一层个数
		/* 用户状态 */
		int userState = 0;
		if(null != listFinTeamUser && listFinTeamUser.size() > 0) {
			String deletesql = "delete from t_user_lnvite_rela where `user_id` =" + userId;
			sqls.add(new OneSql(deletesql, -2, null, "vdsapollo"));
			String insertsql = "insert into t_user_lnvite_rela (user_id, hierarchyLevel, hierarchyTotalNum, userOccupyNum , userEmptyNum, userActiveNum, userNoActiveNum) values ";
			for (int i = 0; i < listFinTeamUser.size(); i++) {
				teamUser = (FinTeamUser) listFinTeamUser.get(i);
				if (i == num ) {
					/* 在变化层级之前保存 */
					countLevelTotalNum = new Double(Math.pow(3, countLevel)).longValue();
					countLevelEmptyNum = countLevelTotalNum - countLevelOccupyNum;
					countLevelActiveNum = countLevelOccupyNum - countLevelNoActiveNum;
					insertsql += "(" + userId + "," + (countLevel) + "," + countLevelTotalNum + "," + countLevelOccupyNum + ","
							+ countLevelEmptyNum + ","+countLevelActiveNum+"," + countLevelNoActiveNum + "),";
					log.info("用户【"+userId+"】当前层级：" + (countLevel) + ", 层级总位置：" + countLevelTotalNum + "--" + countLevelActiveNum
							+ "--" + countLevelNoActiveNum + "--" + countLevelEmptyNum);
					countLevel++;
					num += nextNum;
					nextNum = 0;
					// 重新赋值
					countLevelTotalNum = 0;
					countLevelActiveNum = 0;
					countLevelNoActiveNum = 0;
					countLevelEmptyNum = 0;
					countLevelOccupyNum = 0;
				}
				if (i == listFinTeamUser.size() - 1 ) {
					if(i == listFinTeamUser.size()-1)
						countLevelOccupyNum++;
					/* 在变化层级之前保存 */
					countLevelTotalNum = new Double(Math.pow(3, countLevel)).longValue();
					countLevelEmptyNum = countLevelTotalNum - countLevelOccupyNum;
					countLevelActiveNum = countLevelOccupyNum - countLevelNoActiveNum;
					insertsql += "(" + userId + "," + (countLevel) + "," + countLevelTotalNum + "," + countLevelOccupyNum + ","
							+ countLevelEmptyNum + ","+countLevelActiveNum+"," + countLevelNoActiveNum + "),";
					log.info("用户【"+userId+"】当前层级：" + (countLevel) + ", 层级总位置：" + countLevelTotalNum + "--" + countLevelActiveNum
							+ "--" + countLevelNoActiveNum + "--" + countLevelEmptyNum);
					countLevel++;
					num += nextNum;
					nextNum = 0;
					// 重新赋值
					countLevelTotalNum = 0;
					countLevelActiveNum = 0;
					countLevelNoActiveNum = 0;
					countLevelEmptyNum = 0;
					countLevelOccupyNum = 0;
				}
				countLevelOccupyNum++;
				userState = teamUser.getState();
				if (0 == userState) {
					countLevelNoActiveNum++;
				}
				List<Bean> temp = getAllTeamUser(teamUser.getUserNo());
				nextNum += temp.size();
				listFinTeamUser.addAll(temp);
				listFinTeamUser.set(i, new FinTeamUser());
			}
			insertsql = insertsql.substring(0, insertsql.length() - 1);
			sqls.add(new OneSql(insertsql, -2, null, "vdsapollo"));
			txObj.excuteUpdateList(sqls);
			if (txObj.commit()) {
				log.info("理财报警INTERFACE:【设置用户"+userId+"层级人数】成功!!!】");
			}else {
				log.info("理财报警INTERFACE:【设置用户"+userId+"层级人数】失败!!!");
			}
			log.info("insertsql = " + insertsql);
			log.info("num = " + num);
			return num + 1 ;
		}else {
			return 1;
		}
		
	}
	
	public List<Bean> getAllTeamUser(Long userId) {
		sql = "SELECT u.id,u.id AS 'userNo',u.username,un.node_id AS 'nodename',un.node_position AS 'nodePartition', u.regtime, "
			+ "ur.rec_id AS 'recName',u.state,u.level FROM t_user u LEFT JOIN t_user_relation ur ON u.id = ur.user_id "
			+ "LEFT JOIN t_user_node un ON u.id = un.user_id WHERE un.node_id = " + userId + " ORDER BY u.regtime DESC";
		//log.info("sql = " + sql);
		List<Bean> listFinTeamUser = (List<Bean>) Data.Query("vdsapollo", sql, null, FinTeamUser.class);
		return listFinTeamUser;
	}
	
	public static void main (String[] args) {
		SetUserInvitaionNum svpn = new SetUserInvitaionNum("", "");
//		svpn.run();
		svpn.getAllTeamUserNum(1779384L);
	}
	
	public String doPostData(String url, Map<String,Object> objectMap) throws Exception {
		String result = "";
		try {
			String dataJson = com.alibaba.fastjson.JSON.toJSONString(objectMap);
	        jodd.http.HttpRequest request = jodd.http.HttpRequest.post(url);
	        request.query("data",dataJson);
	        jodd.http.HttpResponse response = request.send();
	        result = response.bodyText();
		} catch (Exception e) {
			throw new Exception(e);
		}
		return result;
	}
}
