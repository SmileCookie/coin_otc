package com.world.controller;

import com.atlas.BizException;
import com.google.code.morphia.Morphia;
import com.mongodb.Mongo;
import com.world.config.GlobalConfig;
import com.world.data.database.DatabasesUtil;
import com.world.data.mongo.CointMongoUtil;
import com.world.data.mongo.MorphiaMongo;
import com.world.data.mongo.SimpleMongoDao;
import com.world.data.mysql.DataDaoSupport;
import com.world.model.dao.task.TaskListener;
import com.world.model.entity.coin.CoinProps;
import com.world.system.tips.SysTipsManager;
import com.world.util.ClassUtil;
import com.world.util.callback.AsynMethodFactory;
import com.world.util.cookie.CookieUtil;
import com.world.util.ip.IpUtil;
import com.world.web.*;
import com.world.web.defense.IpDefense;
import com.world.web.sso.session.SsoSessionManager;
import freemarker.template.Configuration;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.MDC;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Locale;

public class Base extends HttpServlet {

	private static final long serialVersionUID = 9184533209939136097L;
	static Logger log = Logger.getLogger(Base.class);
	public static boolean systemStarted = false;
	private static final String MIME_OCTET_STREAM = "text/html";
	private static final int MAX_BUFFER_SIZE = 4096;
	
	private static SysTipsManager tipsManager = SysTipsManager.getSysTipsManager();
	private Configuration configuration; 

	public void init() {
		log.info("全局初始化开始");
		try {
			if (!systemStarted) {
				ViewCodeContainer.LoadViewCode();
//			lanStart();
				configuration = new Configuration();
				configuration.setClassForTemplateLoading(Base.class, "/templates");
				try {
					//configuration.setDirectoryForTemplateLoading(new File(dir));
					configuration.setEncoding(Locale.CHINA, "UTF-8");
				} catch (Exception e) {
					log.error(e.toString(), e);
				}
				systemStarted = true;
			}
		} catch (Exception e) {
			log.error(e.toString(), e);
		}
		
		log.info("全局初始化完成");
		log.info("服务启动完毕，耗时：" + (System.currentTimeMillis() - TaskListener.initTime) + " ms");
	}
	public void destroy() {
		log.info("准备销毁initservlet");
		try {
			WebContainer.shutdown();
		} catch (Exception e) {
			log.error("关闭任务失败" + e.toString(), e);
		}
		configuration = null;
		super.destroy();
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doPost(request, response);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try{

			/*start by xwz 20170930 为来源统计设置Cookie*/
			String utmSource = request.getParameter("utm_source");//来源
			String utmMedium = request.getParameter("utm_medium");//介质
			if(StringUtils.isNotBlank(utmSource)){
				Cookie utmSourceCookie = new Cookie("utmSource", utmSource);
				utmSourceCookie.setHttpOnly(true);
				utmSourceCookie.setMaxAge(60*60*6);//失效时间6小时
				utmSourceCookie.setPath("/");
				utmSourceCookie.setDomain(GlobalConfig.baseDomain);
				response.addCookie(utmSourceCookie);
			}
			if(StringUtils.isNotBlank(utmMedium)){
				Cookie utmMediumCookie = new Cookie("utmMedium", utmMedium);
				utmMediumCookie.setHttpOnly(true);
				utmMediumCookie.setMaxAge(60*60*6);//失效时间6小时
				utmMediumCookie.setPath("/");
				utmMediumCookie.setDomain(GlobalConfig.baseDomain);
				response.addCookie(utmMediumCookie);
			}
			/*end*/

			//清空log4j线程变量，解决线程池情况下线程重复利用，会使用上次线程的错误数据 add by buxianguan
			MDC.clear();

			long startTime = System.currentTimeMillis();
			String url = request.getRequestURI();

			if(!service(request, response, url)){
				return;
			}

			dispatch1(request, response, url);
			long times = (System.currentTimeMillis() - startTime);
			if(!"/manage/getAssetsDetail".equals(url) && !"/".equals(url)){
				log.info(url + "耗时：" + times +"ms");
			}
		}catch (Throwable e){
			log.error("doPost捕获到错误:", e);
			throw e;
		}
	}
	
	/***
	 * 判断是否继续提供程序服务
	 * @param request
	 * @param response
	 * @param url
	 * @return
	 */
	private boolean service(HttpServletRequest request, HttpServletResponse response , String url){
		try {
			//log.info("当前访问：" + url);
			if (url.indexOf('.') > 0 && url.indexOf(".jsp") < 0) {
				if(GlobalConfig.openStatics){
					DoStatic(request, response);
				}
				return false;
			}
			if (GlobalConfig.ipDefenseOpen) {
				String ip = IpUtil.getIp(request);
				if(!IpDefense.visit(ip)){
					response.setContentType("text/javascript;charset=utf-8");
					response.setCharacterEncoding("utf-8");
					response.getWriter().append(ip + "已被列入黑名单,如有疑问，请拨打客服电话。");
					response.getWriter().close();
					return false;
				}
			}
		} catch (Exception ex) {
			throw new BizException(ex);
		}
		return true;
	}
	
	/***
	 * 执行指定action并返回结果给用户
	 * @param request
	 * @param response
	 * @param url
	 */
	private void dispatch1(HttpServletRequest request, HttpServletResponse response , String url) throws IOException {
		UrlViewCode uvc = ViewCodeContainer.GetViewGode(url);
		ViewCode vc = uvc.viewCode;
		if (vc != null) {
			if(vc.cacheTime > 0 && vc.lock != null){
				try{
//					log.info("--3333访问缓存页面去除锁定：" + url);
					//vc.lock.lock();
					//判断当前路径是否有缓存
					dispatch(uvc, request, response);
				}finally{
					//vc.lock.unlock();
				}
			}else{
				dispatch(uvc, request, response);
			}
		}
	}
	
	private void dispatch(UrlViewCode uvc , HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		try {
			ViewCode vc = uvc.viewCode;

			//后台禁止访问并且报名包含后台admin路径则返回
			if (!GlobalConfig.adminPageOpen && vc.getClassType().getPackage().getName().contains(GlobalConfig.adminPath)) {
				return;
			}
			
			Object obj = vc.classType.newInstance();
			//log.info("databaseSelectOpen:" + GlobalConfig.databaseSelectOpen);
			if(GlobalConfig.databaseSelectOpen){//是否打开选mysql库功能
				String ct = request.getParameter("coint");
				if(ct == null && uvc.urlPramas.length > 0){
					ct = uvc.urlPramas[0];
				}
				if(ct == null){
					ct = "btc";
				}
				//if(ct != null){
					CoinProps coint = DatabasesUtil.coinProps(ct);
					Field[] declaredCls = vc.classType.getDeclaredFields();
					for(Field f : declaredCls){
						if(f.getType().getSuperclass() == DataDaoSupport.class){
							f.setAccessible(true);
							DataDaoSupport<?> o = (DataDaoSupport<?>)f.get(obj);
							o.setCoint(coint);//设置coint  database
							f.setAccessible(false);
						}else if(f.getType().getSuperclass() == SimpleMongoDao.class){
							log.info("current simplemongodao class:" + f.getType());
							f.setAccessible(true);
							SimpleMongoDao<?,?> o = (SimpleMongoDao<?,?>)f.get(obj);
							//依赖注入
							MorphiaMongo mm = CointMongoUtil.getMorphia(coint);
							
							if(mm != null){
								log.info("MorphiaMongo:" + coint.getStag() + ",classType:" + vc.classType + ",classType:" + vc.classType);
								o = (SimpleMongoDao<?, ?>) f.getType().getConstructor(Mongo.class, Morphia.class, String.class)
										.newInstance(mm.getMongo(),mm.getMorphia(),GlobalConfig.getValue(coint.getDatabaseKey() + "Db"));
								
								o.setCoint(coint);//设置coint  database
								
								ClassUtil.setValByField(vc.classType, f.getName(), obj, o);
							}
							f.setAccessible(false);
						}
					}
				//}
			}
			
			//Object result = vc.baseMethodInvoker.invoke(obj, new Object[] {getServletContext(), request, response , uvc});
			Pages p = (Pages) obj;
			String initRtn = p.BaseInit(this.getServletContext(), request, response , uvc);
			//完成初始化
			if ("false".equals(initRtn)) {
				return;
			}else if("true".equals(initRtn)){
				//执行逻辑action
				String methodName = vc.getMethod().getName();
				
				if(methodName.equals("index")){
					p.index();
				}else if(methodName.equals("ajax")){
					p.ajax();
				}else{
					vc.methodInvoker.invoke(obj, null);
				}
			}else if("device-false".equals(initRtn)){
				log.info("device-false, 我也不知道这是个啥");
			}
			
			if(!"caching".equals(initRtn)){//缓存的就直接返回好了
				//log.info("cach")
				//返回指定类型的结果
				if (vc.viewerType != ViewerType.DEFAULT) {
					if (vc.viewerType == ViewerType.XSLT){
						p.ToXsl();
					}else if (vc.viewerType == ViewerType.JSP) {
						p.ToJsp();
					} else if (vc.viewerType == ViewerType.XML){
						p.ToXml();
					}else if (vc.viewerType == ViewerType.JSON){
						p.toJson();
					}else if (vc.viewerType == ViewerType.FREEMARKER){
						p.toFtl(configuration);
					}
				}
				if(vc.sysTips != null && vc.sysTips.size() > 0){
					AsynMethodFactory.addWork(tipsManager, "sends", p);
					//SysTipsManager.getSysTipsManager().sends(vc.sysTips);
				}
			}
		} catch (Exception ex) {
			throw new BizException(ex);
		}
	}
	@Deprecated
	private void dispatch2(HttpServletRequest request, HttpServletResponse response , String url){
		UrlViewCode uvc = ViewCodeContainer.GetViewGode(url.toLowerCase());
		ViewCode vc = uvc.viewCode;
		if (vc != null) {
			try {
				Object obj = vc.classType.newInstance();
				
				if(GlobalConfig.databaseSelectOpen){//是否打开选mysql库功能
					String ct = request.getParameter("coint");
					if(ct == null && uvc.urlPramas.length > 0){
						ct = uvc.urlPramas[0];
					}
					CoinProps coint = DatabasesUtil.coinProps(ct);
					Field[] declaredCls = vc.classType.getDeclaredFields();
					for(Field f : declaredCls){
						if(f.getType().getSuperclass() == DataDaoSupport.class){
							f.setAccessible(true);
							
							DataDaoSupport o = (DataDaoSupport)f.get(obj);
							o.setDatabase(coint.database);
							o.coint = coint;
							f.setAccessible(false);
						}
					}
				}
				
				Object result = vc.basemethod.invoke(obj, new Object[] {getServletContext(), request, response , uvc});
				if (!Boolean.parseBoolean(result.toString())) {
					return;
				}
				
				vc.method.setAccessible(false);
				vc.method.invoke(obj, null);
				
				if (vc.viewerType != ViewerType.DEFAULT) {
					Method method1 = null;
					if (vc.viewerType == ViewerType.XSLT)
						method1 = vc.classType.getMethod("ToXsl", new Class[0]);
					else if (vc.viewerType == ViewerType.JSP) {
						method1 = vc.classType.getMethod("ToJsp", new Class[0]);
					} else if (vc.viewerType == ViewerType.XML)
						method1 = vc.classType.getMethod("ToXml", new Class[0]);
					else if (vc.viewerType == ViewerType.JSON)
						method1 = vc.classType.getMethod("toJson", new Class[] {});

					method1.setAccessible(false);
					method1.invoke(obj, null);
				}
				obj = null;
			} catch (Exception ex) {
				log.error("捕获到错误:" + ex.toString(), ex);
			}
		}
	}
	

	// 处理静态文件，在纯tomcat下面调试的时候用于处理静态文件
	private boolean DoStatic(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException {
		// log.info("你好");
		String url = request.getRequestURI();
		response.addHeader("ArtServer", "True");
		// url = url.substring(path.length());
		// log.info("路径："+request.getRequestURI());
		if (url.toUpperCase().startsWith("/WEB-INF/")) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return true;
		}
		int n = url.indexOf('?');
		if (n != (-1))
			url = url.substring(0, n);
		n = url.indexOf('#');
		if (n != (-1))
			url = url.substring(0, n);
		File f = null;
		try {
			f = new File(getServletContext().getRealPath(url));
		} catch (Exception e) {
			log.error(url + ":" + getServletContext().getRealPath(url), e);
		}
		
		if(f == null){
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return false;
		}
		// log.debug(getServletContext().getRealPath(url));
		if (!f.isFile()) {
			if (url.split("/").length == 5 && url.indexOf('-') > 0) {
				boolean due = true;// DueImage.DuePlan(url);
				if (due) {
					//
					log.info(getServletContext().getRealPath("/")
							+ url);
					f = new File(getServletContext().getRealPath("/") + url);
					// response.setContentType("text/xml");
					if (!f.isFile()) {
						response.sendRedirect("/404.png");
						return false;
					} else
						sendFile(f, response.getOutputStream());

					return true;
				} else {
					response.sendRedirect("/404.png");
					return false;
				}
			} else {
				response.sendError(HttpServletResponse.SC_NOT_FOUND);
				return false;
			}
		}
		long ifModifiedSince = request.getDateHeader("If-Modified-Since");
		long lastModified = f.lastModified();
		if (ifModifiedSince != (-1) && ifModifiedSince >= lastModified) {
			response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
			return false;
		}
		response.setDateHeader("Last-Modified", lastModified);
		response.setContentLength((int) f.length());
		if (url.endsWith(".xml")) {
			response.setContentType("text/xml");
		} else if (url.endsWith(".css")) {
			// response.setContentType(MimeHandler.getContentType(FilenameUtils.getExtension(url)));
			response.setContentType("text/css");
		}

		sendFile(f, response.getOutputStream());

		// log.info(getServletContext().getRealPath(url));
		return true;
	}

	String getMimeType(File f) {
		String mime = getServletContext().getMimeType(f.getName());
		return mime == null ? MIME_OCTET_STREAM : mime;
	}

	private void sendFile(File file, OutputStream output) throws IOException {
		InputStream input = null;
		try {
			input = new BufferedInputStream(new FileInputStream(file));
			byte[] buffer = new byte[MAX_BUFFER_SIZE];
			while (true) {
				int n = input.read(buffer);
				if (n == -1)
					break;
				output.write(buffer, 0, n);
			}
			output.flush();
		} catch (Exception localException) {
			if (input != null)
				try {
					input.close();
				} catch (IOException localIOException) {
					log.error(localIOException.toString(), localException);
				}
		} finally {
			if (input != null)
				try {
					input.close();
				} catch (IOException localIOException1) {
					log.error(localIOException1.toString(), localIOException1);
				}
		}
	}
}