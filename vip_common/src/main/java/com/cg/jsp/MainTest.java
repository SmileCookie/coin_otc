package com.cg.jsp;

import com.cg.FileGenerator;
import com.cg.entity.TestBean;

public class MainTest {

	public static void main(String[] args) {
		FileGenerator.generateJsp(TestBean.class);
	}
}
