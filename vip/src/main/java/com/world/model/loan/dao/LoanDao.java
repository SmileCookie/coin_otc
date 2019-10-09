package com.world.model.loan.dao;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.world.data.mysql.Bean;
import com.world.data.mysql.Data;
import com.world.data.mysql.DataDaoSupport;
import com.world.data.mysql.OneSql;
import com.world.model.loan.entity.Loan;
import com.world.model.loan.entity.LoanStatus;
/**
 * 借贷的实体Dao   
 * 2014-01-26
 * @author Administrator
 *
 */
public class LoanDao extends DataDaoSupport{
	
	/**
	 * 有一个成功投票之后，更新借入表
	 * @param curIn
	 * @param amount
	 * @param inBalanceAmount
	 * @return
	 */
	public OneSql updateBidSqls(Loan curIn, BigDecimal amount, BigDecimal inBalanceAmount){
		
		String otherSet = "";
		if(inBalanceAmount.compareTo(new BigDecimal(0)) <= 0){
			otherSet += ",status="+LoanStatus.success.getKey();
		}else{
			otherSet += ",status="+LoanStatus.part.getKey();
		}
		return new OneSql("update Loan set hasAmount=hasAmount+?,inTimes=inTimes+1 " + otherSet + " where id=? and hasAmount+? <= amount and status in(0,1)", 1, 
						new Object[]{amount , curIn.getId() , amount});
	}

	/**
	 * 成功还款之后，重新增加
	 * @param curIn
	 * @param amount
	 * @param inBalanceAmount
	 * @return
	 */
	public OneSql updateBidSqls(Loan curIn, BigDecimal amount){
		
		String otherSet = "";
		if(amount.compareTo(curIn.getHasAmount()) < 0){
			otherSet += ",status="+LoanStatus.part.getKey();
		}else{
			otherSet += ",status="+LoanStatus.waiting.getKey();
		}
		return new OneSql("update Loan set amount=amount+? " + otherSet + " where id=? and isLoop = ?", 1, 
				new Object[]{amount , curIn.getId() , true});
	}

	/**
	 * 还款更新Loan
	 * @param id
	 * @return
     */
	public OneSql repayLoan(int id,BigDecimal amount){
		String getAllLoanStr = "select amount,(select sum(loanrecord.hasRepay) from loanrecord where loanId = ?) repayAmount from loan where id = ?";
		List<BigDecimal> list = (List<BigDecimal>) Data.GetOne(getAllLoanStr,new Object[]{id,id});

		BigDecimal totalAmount = list.get(0);
		BigDecimal hasRepay = list.get(1);
		if(hasRepay.add(amount).compareTo(totalAmount) == 0){
			return new OneSql("update loan set lastRepayDate = now(), status = ? where id = ?", 1, new Object[]{LoanStatus.allRepay.getKey(),id});
		}else{
			return new OneSql("update loan set lastRepayDate = now() where id = ?", 1, new Object[]{id});
		}
	}


	/**
	 * @param in
	 * @return
	 */
	
	/**
	 * 插入方法，传入实体 
	 * @param in
	 * @return   返回 OneSql对象
	 */
	public OneSql insertSql(Loan in){
		return super.getTransInsertSql(in);
	}
	
	/**
	 * 更改借入的资金和类型
	 * @param money
	 * @param fundType
	 * @return    返回 OneSql对象
	 */
	public OneSql updateSql(int status, int id){
		return new OneSql("UPDATE Loan SET status = ? WHERE id = ? and status <= 1", 1, new Object[]{status, id});
	}

	/**
	 * 取消时更改状态和借出金额
	 * @param status
	 * @param id
	 * @param amount
     * @return
     */
	public OneSql updateSql(int status, int id,BigDecimal amount){
		return new OneSql("UPDATE Loan SET amount = ?, status = ? WHERE id = ? and status = 1", 1, new Object[]{amount,status, id});
	}

	public OneSql cancelLoopSql(int id){
		return new OneSql("UPDATE Loan SET isLoop = 0 WHERE id = ? ", 1, new Object[]{ id});
	}
	public Map<Integer , Loan> getLoanMapByIds(String ids){
		Map<Integer , Loan> maps = new LinkedHashMap<Integer, Loan>();
		List<Bean> beans = (List<Bean>)super.find("SELECT * FROM loan WHERE id IN ("+ids+")", new Object[]{}, Loan.class);
		if(beans != null && beans.size() > 0){
			for(Bean b : beans){
				Loan a = (Loan)b;
				maps.put(a.getId(), a);
			}
		}
		return maps;
	}
	
}
