package com.dynamic.param.field;

import com.google.code.morphia.Datastore;
import com.google.code.morphia.annotations.Entity;
import com.world.data.mongo.id.StrBaseLongIdEntity;
@Entity(value = "dynamic_field" , noClassnameStored = true)
public class FieldValDefine extends StrBaseLongIdEntity{
	public FieldValDefine() {
	}
	public FieldValDefine(Datastore ds) {
		super(ds);
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = 55715878575357533L;
	private String fieldName;
	private String min;
	private String max;
	private String entityId;
	private String val;
	
	
	public String getMin() {
		return min;
	}
	public void setMin(String min) {
		this.min = min;
	}
	public String getMax() {
		return max;
	}
	public void setMax(String max) {
		this.max = max;
	}
	public String getVal() {
		return val;
	}
	public void setVal(String val) {
		this.val = val;
	}
	public String getFieldName() {
		return fieldName;
	}
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}
	public String getEntityId() {
		return entityId;
	}
	public void setEntityId(String entityId) {
		this.entityId = entityId;
	}
}
