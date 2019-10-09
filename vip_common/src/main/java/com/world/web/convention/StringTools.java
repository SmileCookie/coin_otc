package com.world.web.convention;

import java.util.HashMap;
import java.util.Map;

import javax.naming.ConfigurationException;

public class StringTools {
	public static boolean contains(String[] strings, String value, boolean ignoreCase)
	  {
	    if (strings != null) {
	      for (String string : strings) {
	        if ((string.equals(value)) || ((ignoreCase) && (string.equalsIgnoreCase(value)))) {
	          return true;
	        }
	      }
	    }
	    return false;
	  }

	  public static Map<String, String> createParameterMap(String[] parms) throws ConfigurationException {
	    Map map = new HashMap();
	    int subtract = parms.length % 2;
	    if (subtract != 0) {
	      throw new ConfigurationException("'params' is a string array and they must be in a key value pair configuration. It looks like you have specified an odd number of parameters and there should only be an even number. (e.g. params = {\"key\", \"value\"})");
	    }

	    for (int i = 0; i < parms.length; i += 2) {
	      String key = parms[i];
	      String value = parms[(i + 1)];
	      map.put(key, value);
	    }

	    return map;
	  }
}
