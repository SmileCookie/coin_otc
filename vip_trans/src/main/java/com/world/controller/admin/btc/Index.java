package com.world.controller.admin.btc;

import java.util.Random;
import com.world.model.dao.account.EncryptionPhoto;
import com.world.web.Page;
import com.world.web.UrlViewCode;
import com.world.web.ViewCodeContainer;
import com.world.web.action.AdminAction;
import com.world.web.convention.annotation.FunctionAction;

@FunctionAction(jspPath = "/admins/" , des = "BTC管理")
public class Index extends AdminAction {
	/*
	 * shouye
	 */
	@Page(Viewer = "/admins/index.jsp")
	public void index() {
		try {

		} catch (Exception ex) {
		}
	}

	@Page(Viewer = "/admins/index.jsp")
	public void manage() {
		try {

		} catch (Exception ex) {
		}
	}


	/**
	 * 返回一个指定长度的随机字符串
	 * 
	 * @param length
	 * @return
	 */
	public static String GetRadomStr() {
		String[] str = { "a", "B", "c", "D", "e", "f", "G", "h", "i", "J", "k", "L", "m", "n", "o", "P", "q", "r", "s", "T", "u", "v", "w", "X", "y", "Z", "0", "1", "2", "3", "4", "5", "6", "7", "8", "!", "@", "#", "$", "%" };
		Random r = new Random();
		int length = r.nextInt(6);// 先取出一个长度不固定的,小于6的,太长了也没用
		if (length <= 0)
			length = 1;
		String ls = "";
		for (int j = 0; j < length; j++) {
			// for(int i=0;i <length;i++){
			int a = r.nextInt(str.length);
			ls += str[a];
			// log.info(str[a]);
			// }
		}
		return ls;
	}

	// 获取
	@Page
	public void getsafemima() {
		try {
			String userid = request.getParameter("userid");
			String photo = request.getParameter("photo");
			String filename = EncryptionPhoto.encrypt(photo);

			response.getWriter().write("<a href='http://img.vip.com/" + userid + "/" + photo + filename + "' target='_blank'>http://img.vip.com/" + userid + "/" + photo + filename + "</a>");
		} catch (Exception ex) {
			try {
				response.getWriter().write("获取失败" + ex.toString());
			} catch (Exception e) {
			}
		}
	}


	// 获取当前系统中的存在路径
	@Page(Viewer = "/admins/code.jsp")
	public void codes() {

	}

	// 获取具体一个路径
	@Page
	public void GetCodeOne() {
		try {
			String url = request.getParameter("url");
			url = url.toLowerCase().trim();
			if (url.indexOf("http://") == 0) {
				url = url.substring(9);
				url = url.substring(url.indexOf('/'));
			}
			UrlViewCode uc = ViewCodeContainer.GetViewGode(url);
			response.getWriter().write(uc.viewCode.viewerPath + " &nbsp;本页访问次数：" + uc.viewCode.count + "   &nbsp;所有页面总访问次数：" + totalPageCount);
		} catch (Exception ex) {

		}
	}

}

