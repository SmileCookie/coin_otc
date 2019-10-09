package com.world.controller.service;
import com.world.util.qrcode.QRCodeGenerator;
import com.world.web.Page;
import com.world.web.action.BaseAction;
import com.world.web.convention.annotation.FunctionAction;

import java.io.OutputStream;
import java.net.URLDecoder;

@FunctionAction(jspPath = "/services/")
public class Index extends BaseAction {
	
//	@Page(Viewer = INDEX)
	public void index() {
	}
	
	@Page(Viewer = "")
	public void qrcode(){
		try{
			response.setContentType( "image/png" );
			response.setHeader( "Pragma", "No-cache" );
			response.setHeader( "Cache-Control", "no-cache" );
			response.setDateHeader( "Expires", 0 );
			
			OutputStream os = response.getOutputStream();
			
			int width = intParam("width");
			int height = intParam("height");
			if(param("code").length() == 0){
				return;
			}
			//参与生成二维码的文本
			String codec = URLDecoder.decode(param("code"), "utf-8");
			//生成二维码图片
			QRCodeGenerator.encode(os, codec, width, height);
			os.flush();
			os.close();
		}catch(Exception ex){
			log.error("内部异常", ex);
		}
	}

}

