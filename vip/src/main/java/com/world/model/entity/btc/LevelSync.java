package com.world.model.entity.btc;

public class LevelSync{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private long price;
	private long levelEntrust; //level中委托的数量
	private long realEntrust;//实际委托的数量
	
	public boolean isNeedSync(){
		if(levelEntrust != realEntrust){
			return true;
		}
		return false;
	}


	public long getPrice() {
		return price;
	}

	public void setPrice(long price) {
		this.price = price;
	}

	public long getLevelEntrust() {
		return levelEntrust;
	}

	public void setLevelEntrust(long levelEntrust) {
		this.levelEntrust = levelEntrust;
	}

	public long getRealEntrust() {
		return realEntrust;
	}

	public void setRealEntrust(long realEntrust) {
		this.realEntrust = realEntrust;
	}
	
}
