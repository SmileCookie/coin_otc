package com.world.util;

import java.io.Serializable;

import org.apache.log4j.Logger;

public class Log4j implements Serializable{
	private static final long serialVersionUID = -5170253023813822811L;
	
	protected static Logger log = Logger.getLogger(Log4j.class.getName());
}
