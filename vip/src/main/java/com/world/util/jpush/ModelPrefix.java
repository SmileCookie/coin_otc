package com.world.util.jpush;

public enum ModelPrefix {

	development(0 , "dev_") , //开发环境
	production(1 , "pro_") , //生产环境
	test(2 , "test_") , //测试环境
	; 
	
	private ModelPrefix(int key, String value) {
		this.key = key;
		this.value = value;
	}

	public int key;
	public String value;

	public int getKey() {
		return key;
	}

	public String getValue() {
		return value;
	}
	
	// 普通方法
    public static String getPrefix(String key) {
        for (ModelPrefix mp : ModelPrefix.values()) {
            if ( (mp.getKey()+"").equals(key) ) {
                return mp.getValue();
            }
        }
        return null;
    }
}
