package com.yc.mongo;

import com.google.code.morphia.Morphia;
import com.mongodb.Mongo;

/*****
 * ����Morphia��mongodb�Ĳ���
 * @author Administrator
 */
public class MorphiaMongo {

	public MorphiaMongo(){}
	public MorphiaMongo(Mongo mongo, Morphia morphia, String dbName) {
		this.mongo = mongo;
		this.morphia = morphia;
		this.dbName = dbName;
	}
	private Mongo mongo;//���ӵ�mongo
	private Morphia morphia;//
	private String dbName;//��ݿ����
	
	public String getDbName() {
		return dbName;
	}
	public void setDbName(String dbName) {
		this.dbName = dbName;
	}
	public Mongo getMongo() {
		return mongo;
	}
	public void setMongo(Mongo mongo) {
		this.mongo = mongo;
	}
	public Morphia getMorphia() {
		return morphia;
	}
	public void setMorphia(Morphia morphia) {
		this.morphia = morphia;
	}
}
