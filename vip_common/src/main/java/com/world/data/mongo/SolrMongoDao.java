package com.world.data.mongo;

import com.google.code.morphia.Key;

public class SolrMongoDao<T, K> extends MongoDao<T, K>
{
  public Key<T> add(T entity)
  {
    Key k = super.save(entity);

    return k;
  }
}