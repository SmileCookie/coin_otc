package com.world.model.entity.financial.fee;

import com.world.data.mysql.Bean;
import com.world.data.mysql.bean.BeanField;

import java.math.BigDecimal;

/**
 * 手续费用
 * @author guosj
 */
public class Fee extends Bean{
	
	private int id;
	/**
	 * 用户ID
	 */
	private long userId;
	/**
	 * 费用类型(1、交易手续费，2、借贷手续费，3、提现手续费)
	 */
	private int type;
	/**
	 * 货币类型(CNY、BTC、LTC、ETH、DAO)
	 */
	private String currency;
	/**
	 * 手续费金额
	 */
	private BigDecimal amount;
	/**
	 * 创建时间
	 */
	private long time;

    /**
     * 交易ID
     */
    private long transRecordId;

    private String market;
    private long transRecordTime;
    private String numberBi;
    private String exchangeBi;
    private BigDecimal totalPrice;
    private BigDecimal numbers;
    private BigDecimal unitPrice;
    /**
     * 手续费转成usdt的金额
     */
    @BeanField(persistence = false)
    private BigDecimal usdtAmount;
	
	public Fee(){}
	public Fee(int userId, int type, String currency, BigDecimal amount, long time){
		this.userId = userId;
		this.type = type;
		this.currency = currency;
		this.amount = amount;
		this.time = time;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public String getCurrency() {
		return currency;
	}
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	public BigDecimal getAmount() {
		return amount;
	}
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	public long getTime() {
		return time;
	}
	public void setTime(long time) {
		this.time = time;
	}

    public long getTransRecordId() {
        return transRecordId;
    }

    public void setTransRecordId(long transRecordId) {
        this.transRecordId = transRecordId;
    }

    public long getTransRecordTime() {
        return transRecordTime;
    }

    public void setTransRecordTime(long transRecordTime) {
        this.transRecordTime = transRecordTime;
    }

    public String getNumberBi() {
        return numberBi;
    }

    public void setNumberBi(String numberBi) {
        this.numberBi = numberBi;
    }

    public String getExchangeBi() {
        return exchangeBi;
    }

    public void setExchangeBi(String exchangeBi) {
        this.exchangeBi = exchangeBi;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public BigDecimal getNumbers() {
        return numbers;
    }

    public void setNumbers(BigDecimal numbers) {
        this.numbers = numbers;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    private String timestr;

	public String getTimestr() {
		return timestr;
	}
	public void setTimestr(String timestr) {
		this.timestr = timestr;
	}

    public BigDecimal getUsdtAmount() {
        return usdtAmount;
    }

    public void setUsdtAmount(BigDecimal usdtAmount) {
        this.usdtAmount = usdtAmount;
    }

    public String getMarket() {
        return market;
    }

    public void setMarket(String market) {
        this.market = market;
    }
}
