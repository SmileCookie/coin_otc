package com.world.controller;

//验证码相关
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.*;
import java.io.*;
import java.util.*;
import javax.imageio.ImageIO;

import com.world.cache.Cache;
import com.world.web.Page;
import com.world.web.Pages;
import com.world.web.sso.session.SsoSessionManager;
public class ImageCode extends Pages {

	/*
	 * shouye
	 */
	@Page(Auth = 2)
	public void Index() {
		try {
			response.getWriter().println("<html><body><h1>fgfg</h1></body></html>");

		} catch (Exception ex) {
		}
	}

	private static char mapTable[] = { 'a', 'b', 'c', 'd', 'e', 'h', 'j', 'k', 'm', 'n', 'p', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'H', 'J', 'K', 'M', 'N', 'P', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y' };

	@Page(Auth = 2)
	public void Get() {
		response.setHeader("Pragma", "No-cache");
		response.setHeader("Cache-Control", "no-cache");
		response.setDateHeader("Expires", 0);
		response.setHeader("Content-Type", "image/jpeg");
		// log.info("========="+);
		// 图片宽高
		int width = 95;
		int height = 38;
		int fontSize = 36;
		if (GetPrama(0).length() > 0) {
			fontSize = Integer.parseInt(GetPrama(0));
		}
		if (GetPrama(1).length() > 0) {
			width = Integer.parseInt(GetPrama(1));
		}
		if (GetPrama(2).length() > 0) {
			height = Integer.parseInt(GetPrama(2));
		}
		BufferedImage bimage = null;
		Graphics2D g = null;
//		HttpSession session = request.getSession();
		OutputStream out = null;
		String value = "";
		try {
			out = response.getOutputStream();
			bimage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
			Random rand = new Random(System.currentTimeMillis());
			g = bimage.createGraphics();
			// 设置随机背景色
			Color color = new Color(rand.nextInt(255), rand.nextInt(255), rand.nextInt(255));
			// 填充深色背景
			g.setColor(color.darker());
			g.fillRect(0, 0, width, height);
			// 设置字体
			g.setFont(new Font("Arial", Font.BOLD, fontSize));
			// 随机生成字符,根据截取的位数决定产生的数字
			// value =
			// UUID.randomUUID().toString().replace("-","").substring(0,4);
			for (int i = 0; i < 4; i++) {
				value += mapTable[(int) (mapTable.length * Math.random())];
			}
			int w = (g.getFontMetrics()).stringWidth(value);
			int d = (g.getFontMetrics()).getDescent();
			int a = (g.getFontMetrics()).getMaxAscent();
			int x = 0, y = 0;
			
			// 设置随机线条,15这个数值越大图片中线条越稀蔬
			for (int i = 0; i < height; i += 15) {
				g.setColor(new Color(rand.nextInt(255), rand.nextInt(255), rand.nextInt(255)));
				g.drawLine(x, y + i, width, y + i);
			}
			// reset x and y
			x = 0;
			y = 0;
			// 设置随机线条,15这个数值越大图片中线条越稀蔬
			for (int i = 0; i < height; i += 15) {
				g.setColor(new Color(rand.nextInt(255), rand.nextInt(255), rand.nextInt(255)));
				g.drawLine(x, y + d - i, width + w, height + d - i);
			}

			// Random random = new Random();
			// for(int i=0; i<10; i++ ) {
			// int xx = random.nextInt(width);
			// int yy = random.nextInt(height);
			// g.drawOval(xx, yy, rand.nextInt(10), rand.nextInt(10));
			// }
			// 展示验证码中颜色,随机
			g.setColor(new Color(rand.nextInt(255), rand.nextInt(255), rand.nextInt(255)).brighter());
			// 设置文字出现位置为中央
			x = width / 2 - w / 2;
			y = height / 2 + a / 2 - 2;
			// 文字变形设置
			AffineTransform fontAT = new AffineTransform();
			int xp = x - 2;
			// 每个文字都变形
			for (int c = 0; c < value.length(); c++) {
				// 产生弧度
				int rotate = rand.nextInt(15);
				fontAT.rotate(rand.nextBoolean() ? Math.toRadians(rotate) : -Math.toRadians(rotate / 2));

				Font fx = new Font("arial", Font.BOLD, fontSize).deriveFont(fontAT);

				g.setFont(fx);

				String ch = String.valueOf(value.charAt(c));

				int ht = rand.nextInt(2);

				// 打印字并移动位置

				g.drawString(ch, xp, y + (rand.nextBoolean() ? -ht : ht));
				// 移动指针.
				xp += g.getFontMetrics().stringWidth(ch) + 2;
			}

			// 打印出图片
			ImageIO.write(bimage, "jpg", out);
			// 图片验证码最多保存2小时
			SsoSessionManager.initSessionId(this);
			log.info("验证码：CodeImage_" + sessionId+"   "+value);
			Cache.Set("CodeImage_" + sessionId, value.toLowerCase(), 7200);
			//log.info(Cache.Get("CodeImage_" + sessionId));
		} catch (IOException ex) {
			log.error(ex.toString(), ex);
		} finally {
			if (g != null) {
				g.dispose();
			}
			if (out != null) {
				try {
					// 关闭OutputStream
					out.close();
				} catch (IOException ex) {
					log.error(ex.toString(), ex);
				}
			}
		}
	}
}
