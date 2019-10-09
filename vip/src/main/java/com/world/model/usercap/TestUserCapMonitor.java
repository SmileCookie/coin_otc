package com.world.model.usercap;

public class TestUserCapMonitor extends Thread {
	private UserCapMonitor userCapMonitor;

	public TestUserCapMonitor (UserCapMonitor userCapMonitor) {
		this.userCapMonitor = userCapMonitor;
	}

	@Override
	public void run() {
		userCapMonitor.run();
	}
	
}
