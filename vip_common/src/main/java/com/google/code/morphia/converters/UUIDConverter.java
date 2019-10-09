package com.google.code.morphia.converters;


import com.google.code.morphia.mapping.MappedField;

import java.util.UUID;


/**
 * provided by http://code.google.com/p/morphia/issues/detail?id=320
 *
 * @author stummb
 * @author scotthernandez
 */
public class UUIDConverter extends TypeConverter implements SimpleValueConverter {

    public UUIDConverter() {
        super(UUID.class);
    }

    @Override
    public Object encode(final Object value, final MappedField optionalExtraInfo) {
        return value == null ? null : value.toString();
    }

    @Override
    public Object decode(final Class targetClass, final Object fromDBObject, final MappedField optionalExtraInfo) {
        return fromDBObject == null ? null : UUID.fromString((String) fromDBObject);
    }
}