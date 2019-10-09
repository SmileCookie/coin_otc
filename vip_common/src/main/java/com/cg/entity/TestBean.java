package com.cg.entity;

import java.sql.Timestamp;

import com.cg.annotation.CgEntity;
import com.cg.annotation.CgProperty;
import com.google.code.morphia.Datastore;
import com.google.code.morphia.annotations.Entity;
import com.world.data.mongo.id.StrBaseLongIdEntity;

@CgEntity(des = "测试")
@Entity(noClassnameStored = true , value = "test")
public class TestBean extends StrBaseLongIdEntity{

	public TestBean(Datastore ds) {
		super(ds);
	}

	@CgProperty(des = "名字")
	private String name;
	@CgProperty(des = "年龄")
	private int age;
	@CgProperty(des = "成绩")
	private int score;
	@CgProperty(des = "生日")
	private Timestamp born;
	
	@CgProperty(des = "体重")
	private float weight;
	
	public float getWeight() {
		return weight;
	}
	public void setWeight(float weight) {
		this.weight = weight;
	}
	public Timestamp getBorn() {
		return born;
	}
	public void setBorn(Timestamp born) {
		this.born = born;
	}
	public int getScore() {
		return score;
	}
	public void setScore(int score) {
		this.score = score;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
	}
	
}
