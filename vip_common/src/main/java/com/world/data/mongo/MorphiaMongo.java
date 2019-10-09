package com.world.data.mongo;

import com.google.code.morphia.Morphia;
import com.mongodb.MongoClient;

public class MorphiaMongo
{
  private MongoClient mongo;
  private Morphia morphia;

  public MorphiaMongo()
  {
  }

  public MorphiaMongo(MongoClient mongo, Morphia morphia)
  {
    this.mongo = mongo;
    this.morphia = morphia;
  }

  public MongoClient getMongo()
  {
    return this.mongo;
  }
  public void setMongo(MongoClient mongo) {
    this.mongo = mongo;
  }
  public Morphia getMorphia() {
    return this.morphia;
  }
  public void setMorphia(Morphia morphia) {
    this.morphia = morphia;
  }
}