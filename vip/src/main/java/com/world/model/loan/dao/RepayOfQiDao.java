package com.world.model.loan.dao;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.world.data.database.DatabasesUtil;
import com.world.data.mysql.Bean;
import com.world.data.mysql.Data;
import com.world.data.mysql.DataDaoSupport;
import com.world.data.mysql.OneSql;
import com.world.model.dao.fee.FeeDao;
import com.world.model.dao.pay.FundsDao;
import com.world.model.entity.bill.BillType;
import com.world.model.entity.coin.CoinProps;
import com.world.model.loan.entity.LoanRecord;
import com.world.model.loan.entity.P2pUser;
import com.world.model.loan.entity.RepayOfQi;
import com.world.model.loan.entity.RepayOfQiStatus;
import com.world.web.response.DataResponse;

public class RepayOfQiDao extends DataDaoSupport{
	
	private static final long serialVersionUID = 1L;
	///归还指定币种的欠息
	public DataResponse repqyOfFunds(List<Bean> list, int fundsType){
		DataResponse dr = new DataResponse();
		int sucCount = 0;
		for (Bean b : list) {
			RepayOfQi roq = (RepayOfQi) b;
			if(roq.getFundsType() == fundsType && repayInterest(roq).isSuc()){
				sucCount++;
			}
		}
		if(sucCount > 0){
			dr.setDes("成功还息了"+sucCount+"条记录！");
			dr.setSuc(true);
			return dr;
		}else{
			dr.setDes("还息失败！");
			dr.setSuc(false);
			return dr;
		}
	}

	/***
	 * 逾期还息
	 * @param roq
	 * @param userId
	 * @return
	 */
	public DataResponse repayInterest(RepayOfQi roq){
		log.info("对用户["+roq.getUserName()+"]进行预期还息操作");
		
		List<OneSql> sqls = new ArrayList<OneSql>();
		DataResponse dr = new DataResponse();
		LoanRecord lr = (LoanRecord) super.get("select * from LoanRecord where id = ? and inUserId=?", new Object[]{roq.getLoanRecordId(), roq.getUserId()}, LoanRecord.class);
		
		if(lr == null){
			dr.setSuc(false);
			dr.setDes("未找到对应的借款！");
			return dr;
		}
		
		CoinProps coint = DatabasesUtil.coinProps(lr.getFundsType());
		P2pUserDao p2pUserDao = new P2pUserDao();
		FundsDao fundsDao = new FundsDao();
		FeeDao feeDao = new FeeDao();
		
		p2pUserDao.setCoint(coint);
		P2pUser p2pUser = p2pUserDao.initLoanUser(lr.getInUserId());
		BigDecimal available =  p2pUser.getFunds().get(coint.getStag()).getBalance();
		BigDecimal totalLx = roq.getLiXi().add(roq.getYuQiLiXi());//利息 + 逾期利息
		boolean canRepayInterest = totalLx.compareTo(available) <= 0;//有能力还息
		if(canRepayInterest){//对应资产足以还息
			try {
				BigDecimal sxf = totalLx.multiply(lr.getFwfScale());
				sqls.add(new OneSql("update LoanRecord set arrearsLx=arrearsLx-? where id=?" , 1 , 
						new Object[]{roq.getLiXi() , lr.getId() }));
				
				sqls.add(new OneSql("update RepayOfQi set status=?,actureDate=? where id=? and status=?" , 1 , 
						new Object[]{RepayOfQiStatus.yuqiyihuan.getKey() , now() , roq.getId() , RepayOfQiStatus.yanshi.getKey()}));
				///
				p2pUserDao.subOverdraft(roq.getLiXi(), roq.getYuQiLiXi(),roq.getUserId(), sqls);
				String outUserId = roq.getOutUserId() == null ? lr.getOutUserId() : roq.getOutUserId();//做到前后版本兼容
				//扣除借入者的资产
				sqls.addAll(fundsDao.subtractMoney(totalLx, lr.getInUserId(), lr.getInUserName(), coint.getPropTag()+"还息", BillType.repayInterestOutToP2p.getKey(), coint.getFundsType(), BigDecimal.ZERO, "0", true));
				
				//增加借出者的资产
				sqls.addAll(fundsDao.addMoney(totalLx.subtract(sxf), outUserId, lr.getOutUserName(), coint.getPropTag()+"收益", BillType.repayInterestFromP2pIn.getKey(), coint.getFundsType(), BigDecimal.ZERO, "0", true));
				sqls.add(feeDao.addFee(Integer.parseInt(outUserId), 2, sxf, coint.getPropTag(),lr.getId(),"vip_main",1));
				
				if(Data.doTrans(sqls)){
					dr.setDes("成功还息！");
					dr.setSuc(true);
					return dr;
				}else{
					dr.setDes("还息失败！");
					dr.setSuc(false);
					return dr;
				}
			} catch (Exception e) {
				log.error(e.toString(), e);
				dr.setSuc(false);
				dr.setDes("资金操作异常！");
				return dr;
			}
		}else{
			dr.setDes("您的"+coint.getPropTag() + "余额不足，不足以偿还利息。");
			dr.setSuc(false);
			return dr;
		}
	}
	
	
	/***
	 * 批量逾期还息
	 * @param roq
	 * @param userId
	 * @return
	 */
	public DataResponse batchRepayInterest(List<RepayOfQi> list, String userId){
		DataResponse dr = new DataResponse();
		int sucCount = 0;
		for (RepayOfQi roq : list) {
			if(repayInterest(roq).isSuc()){
				sucCount++;
			}
		}
		if(sucCount > 0){
			dr.setDes("成功还息了"+sucCount+"条记录！");
			dr.setSuc(true);
			return dr;
		}else{
			dr.setDes("还息失败！");
			dr.setSuc(false);
			return dr;
		}
	}
	
	public List<Bean> getNoRepaysOfQi(String userId){
		return Data.Query("select * from repayOfQi where userId=? and status=?", new Object[]{userId , RepayOfQiStatus.yanshi.getKey()}, RepayOfQi.class);
	}
	
	/**
	 * @see 查询总抵扣利息
	 * @param loanRecordId
	 * @param inUserId
	 * @return
	 */
	public Bean getSumIdKey(int loanRecordId, String inUserId) {//sumDeglx
		Bean bean= Data.GetOne("SELECT IFNULL(SUM(amountDegLiXi),0) sumDeglx FROM repayofqi WHERE loanRecordId=? AND userId=?", new Object[] { loanRecordId, inUserId }, RepayOfQi.class);
		return (RepayOfQi) bean;
	}
	
}
