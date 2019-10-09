package com.world.model.financialproift;

import com.world.model.financialproift.userfininfo.NewUserDetailWork;
import com.world.model.financialproift.userfininfo.SuperNodeRewardDetailWork;
import com.world.model.financialproift.worker.EcoRewardAssignWork;

public class FinThreadTest extends Thread {
	
	 @Override
	 public void run() {
		 new NewUserDetailWork("","").run();
	 }

	public static void main(String[] args) {
		

	}

}
