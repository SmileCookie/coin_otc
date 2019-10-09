package com.google.code.morphia.mapping.lazy.proxy;


import com.google.code.morphia.Key;

import java.util.Map;


/**
 * @author Uwe Schaefer, (us@thomas-daily.de)
 */
public interface ProxiedEntityReferenceMap extends ProxiedReference {
    //CHECKSTYLE:OFF
    void __put(Object key, Key<?> referenceKey);

    Map<Object, Key<?>> __getReferenceMap();
    //CHECKSTYLE:ON
}
