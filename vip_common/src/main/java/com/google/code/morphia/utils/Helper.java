package com.google.code.morphia.utils;


import com.google.code.morphia.Datastore;
import com.google.code.morphia.query.MorphiaIterator;
import com.google.code.morphia.query.Query;
import com.google.code.morphia.query.QueryImpl;
import com.google.code.morphia.query.UpdateOperations;
import com.google.code.morphia.query.UpdateOpsImpl;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;


/**
 * Exposes driver related DBObject stuff from Morphia objects
 *
 * @author scotthernandez
 */
public final class Helper {
    private Helper() {
    }

    public static DBObject getCriteria(final Query q) {
        return ((QueryImpl) q).getQueryObject();
    }

    public static DBObject getSort(final Query q) {
        return ((QueryImpl) q).getSortObject();
    }

    public static DBObject getFields(final Query q) {
        return ((QueryImpl) q).getFieldsObject();
    }

    public static DBCollection getCollection(final Query q) {
        return ((QueryImpl) q).getCollection();
    }

    public static DBCursor getCursor(final Iterable it) {
        return ((MorphiaIterator) it).getCursor();
    }

    public static DBObject getUpdateOperations(final UpdateOperations ops) {
        return ((UpdateOpsImpl) ops).getOps();
    }

    public static DB getDB(final Datastore ds) {
        return ds.getDB();
    }
}