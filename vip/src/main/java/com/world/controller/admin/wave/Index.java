/*package com.world.controller.admin.wave;

import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.google.code.morphia.Datastore;
import com.google.code.morphia.query.Query;
import com.google.code.morphia.query.UpdateResults;
import com.world.data.database.DatabasesUtil;
import com.world.data.mysql.Bean;
import com.world.data.mysql.Data;
import com.world.model.entity.EnumUtils;
import com.world.model.entity.coin.CoinProps;
import com.world.model.entity.pay.WalletBean;
import com.world.model.entity.wave.Wave;
import com.world.model.entity.wave.WaveDao;
import com.world.model.entity.wave.WaveFactory;
import com.world.model.entity.wave.WaveType;
import com.world.model.financial.dao.FinanAccountDao;
import com.world.model.financial.entity.AccountType;
import com.world.model.financial.entity.FinanAccount;
import com.world.web.Page;
import com.world.web.action.AdminAction;
import com.world.web.convention.annotation.FunctionAction;


@FunctionAction(jspPath = "/admins/wave/" , des = "参数设置")
public class Index extends AdminAction{
	WaveDao waveDao = new WaveDao();
	@Page(Viewer=DEFAULT_INDEX)
	public void index(){
		//查询条件
		
		
		long total = waveDao.count();
		
		if(total>0){
			
			List<Wave> waves = waveDao.find().asList();
			
			setAttr("dataList", waves);
		}
	}
	
	@Page(Viewer = DEFAULT_AJAX)
	public void ajax(){
		index();
	}
	
	
	*//**
	 * 功能:响应添加的函数
	 *//*
	@Page(Viewer = DEFAULT_AORU)
	public void aoru() {
		try {
			String id = param("id");
			setAttr("id", id);
			if(id.length() > 0){
				Wave data = waveDao.getById(id);
				setAttr("curData", data);
				setAttr("types", EnumUtils.getAll(WaveType.class));
			}
		} catch (Exception ex) {
			log.error(ex.toString());
		}
	}

	*//**
	 * 功能:响应添加的函数
	 *//*
	@Page(Viewer = XML)
	public void doAoru() {
		try {
			   String id = param("id");
	           String waveV = param("waveVal");
	           String des = param("des");
	           int type = intParam("type");
	           
	           double waveVal = 0;
	           
	        try {
				waveVal = Double.parseDouble(waveV);
			} catch (Exception e) {
				log.error(e.toString(), e);
			}
	          
			int res = 0;
			Datastore ds = waveDao.getDatastore();
			if(id.length() > 0){
				Query<Wave> query = ds.find(Wave.class, "_id", id);   
				UpdateResults<Wave> ur = ds.update(query, ds.createUpdateOperations(Wave.class)
						.set("adminId", String.valueOf(adminId())).set("waveVal", waveVal).set("date", now()).set("des", des));
				if(!ur.getHadError()){
					res = 2;
				}
			}else{
				Wave m = new Wave(ds);
				m.setAdminId(String.valueOf(adminId()));
				m.setType(type);
				m.setWaveVal(waveVal);
				m.setDate(now());
				m.setDes(des);
				if(waveDao.save(m) != null){
					res = 2;
				}
			}
			if(res>0){
				WaveFactory.reInit();
				Write("操作成功",true,"");
				return;
			}
		} catch (Exception ex) {
			log.error(ex.toString(), ex);
		}
		Write("未知错误导致添加失败！",false,"");
	}
	
	///选择钱包
	@Page(Viewer = JSON)
	public void findAccount(){
		
		String coint = param("coint");
		CoinProps coin = DatabasesUtil.coinProps(coint);
		
		List<Bean> accounts = (List<Bean>)Data.Query("SELECT * FROM finanaccount WHERE isDel = false AND fundType = ? AND type = ? AND bankAccountId > 0 ORDER BY createTime", new Object[]{coin.getFundsType(), AccountType.withdraw.getKey()}, FinanAccount.class);
		JSONArray array = new JSONArray();
		if(accounts != null && accounts.size() > 0){
			for(Bean b : accounts){
				FinanAccount account = (FinanAccount)b;
				JSONObject obj = new JSONObject();
				obj.put("id", account.getId());
				obj.put("name", account.getName());
				obj.put("funds", account.getFunds());
				
				array.add(obj);
			}
		}
		
		json("", true, array.toString());
	}
	
	*//**
	 * 选择打币的账户之后
	 *//*
	@Page(Viewer = JSON)
	public void getWalletBalance() {
		int accountId = intParam("accountId");
		if(accountId == 0){
			json("notexsit", true, "");
			return;
		}
		FinanAccount fa = new FinanAccountDao().get(accountId);
		int walletId = fa.getBankAccountId();
		
		WalletBean bwb = (WalletBean) Data.GetOne("select * from btcwallet where walletId=?", new Object[]{walletId}, WalletBean.class);
		if(bwb == null){
			json("notexsit", true, "");
			return;
		}
		
		json("", true, "");
	}
}

*/