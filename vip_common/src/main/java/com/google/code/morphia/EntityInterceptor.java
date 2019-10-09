package com.google.code.morphia;


import com.google.code.morphia.mapping.Mapper;
import com.mongodb.DBObject;


/**
 * Interface for intercepting @Entity lifecycle events
 */
public interface EntityInterceptor {
    /**
     * @see {@link com.google.code.morphia.annotations.PostPersist}
     */
    void prePersist(Object ent, DBObject dbObj, Mapper mapper);

    /**
     * @see {@link com.google.code.morphia.annotations.PreSave}
     */
    void preSave(Object ent, DBObject dbObj, Mapper mapper);

    /**
     * @see {@link com.google.code.morphia.annotations.PostPersist}
     */
    void postPersist(Object ent, DBObject dbObj, Mapper mapper);

    /**
     * @see {@link com.google.code.morphia.annotations.PreLoad}
     */
    void preLoad(Object ent, DBObject dbObj, Mapper mapper);

    /**
     * @see {@link com.google.code.morphia.annotations.PostLoad}
     */
    void postLoad(Object ent, DBObject dbObj, Mapper mapper);
}
