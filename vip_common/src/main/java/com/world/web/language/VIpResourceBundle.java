package com.world.web.language;

import java.io.IOException;
import java.io.Reader;
import java.util.Enumeration;

public class VIpResourceBundle extends VipPropertyResourceBundle {

	/***
	 * 语言处理
	 * @param reader
	 * @throws IOException
	 */

	public VIpResourceBundle(Reader reader) throws IOException {
		super(reader);
	}

	@Override
	public Enumeration<String> getKeys() {
		return null;
	}
	
	public String getVString(String key) {
        return (String) getVObject(key);
    }
	
	public final Object getVObject(String key) {
        Object obj = handleGetObject(key);
        if (obj == null) {
            if (parent != null) {
                obj = parent.getObject(key);
            }
            if (obj == null){
            	return key;
            }
        }
        return obj;
    }
}
