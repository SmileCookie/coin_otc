package com.cg.dao;

import com.cg.FileGenerator;
import com.cg.entity.TestBean;
import com.cg.module.ModuleType;

public class MainTest {

	public static void main(String[] args) {
		FileGenerator.generateJava(TestBean.class, ModuleType.DAO);
		FileGenerator.generateJava(TestBean.class, ModuleType.ACTION);
	}
}
