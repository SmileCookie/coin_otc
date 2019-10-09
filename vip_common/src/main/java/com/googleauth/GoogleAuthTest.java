package com.googleauth;


import org.apache.log4j.Logger;
import org.junit.Test;

/*
 * Not really a unit test- but it shows usage
 */
public class GoogleAuthTest  {

	private final static Logger log = Logger.getLogger(GoogleAuthTest.class);

	@Test
	public void genSecretTest() {
		String secret = GoogleAuthenticator.generateSecretKey();

		String url = GoogleAuthenticator.getQRBarcodeURL("xuyloong@163.com", "", secret);
		log.info("Please register " + url);

		log.info("Secret key is " + secret);
	}


	// Change this to the saved secret from the running the above test. 
	static String savedSecret = "CDM6KY4QCXKAHAXK";

	@Test 
	public void authTest()  {	
		// enter the code shown on device. Edit this and run it fast before the code expires!
		long code = 606405 ;
		long t = System.currentTimeMillis();
		GoogleAuthenticator ga = new GoogleAuthenticator();
		ga.setWindowSize(3);  //should give 5 * 30 seconds of grace...

		boolean r = ga.check_code(savedSecret, code, t);

		log.info("Check code = " + r);
	}


}
