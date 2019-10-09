package com.world.model.loan.product;

public class LevelUserProduct extends P2pProduct{
	public LevelUserProduct(String userId , long version) {
		this.userId = userId;
		this.version = version;
	}

	public String userId;
}
