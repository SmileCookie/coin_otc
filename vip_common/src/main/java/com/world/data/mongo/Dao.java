package com.world.data.mongo;

import com.world.config.GlobalConfig;

public abstract interface Dao
{
  public static final String DEFAULT_DBNAME = GlobalConfig.mongodb_1;
}