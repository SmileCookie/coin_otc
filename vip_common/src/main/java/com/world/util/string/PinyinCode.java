package com.world.util.string;

import java.io.Serializable;

public class  PinyinCode implements Serializable {
	public PinyinCode(String py, int cd) {
		pinyin = py;
		code = cd;
	}
	public String pinyin = null;
	public int code = 0;

}
