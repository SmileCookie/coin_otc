package com.google.code.morphia.mapping.lazy.proxy;


import com.google.code.morphia.Key;

import java.util.Collection;
import java.util.List;


/**
 * @author Uwe Schaefer, (us@thomas-daily.de)
 */
public interface ProxiedEntityReferenceList extends ProxiedReference {
    //CHECKSTYLE:OFF
    void __add(Key<?> key);

    void __addAll(Collection<? extends Key<?>> keys);

    List<Key<?>> __getKeysAsList();
    //CHECKSTYLE:ON
}
