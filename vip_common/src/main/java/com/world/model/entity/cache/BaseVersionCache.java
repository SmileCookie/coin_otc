package com.world.model.entity.cache;

import java.io.Serializable;

/***
 * 给予版本的缓存
 * @author Administrator
 *
 */
public class BaseVersionCache implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -822421290469923350L;
	public long version = 0;
	public String value = "";

}
