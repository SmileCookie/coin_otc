package com.world.web;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.world.config.GlobalConfig;
import com.world.controller.Base;
import com.world.system.tips.SysTipsManager;
import com.world.util.WebUtil;
import com.world.util.path.FindClass;
import com.world.web.action.Action;
import com.world.web.competence.FunctionManager;
import com.world.web.convention.annotation.ActionCache;
import com.world.web.convention.annotation.FunctionAction;
import com.world.web.proxy.Invokers;

public class ViewCodeContainer implements Action {
    static Logger log = Logger.getLogger(ViewCodeContainer.class);

    public static Map<String, ViewCode> content = null;

    public static void AddContent(ViewCode code, String jspBasePkg, String des, boolean plate) {
        if (!content.containsKey(code.name)) {
            content.put(code.name, code);
            FunctionManager.putViewCode(code, jspBasePkg, des, plate);
        }
    }

    public static void LoadViewCode() {
        content = new HashMap<String, ViewCode>();
        Set<Class<?>> classes = FindClass.getClasses(Base.class.getPackage());
        Iterator<Class<?>> it = classes.iterator();
        int classesNum = 0;
        int pageCassNum = 0;

        //String ccss = "";
        while (it.hasNext()) {
            classesNum++;
            Class<?> cs = it.next();

            Annotation[] classAnnotations = cs.getAnnotations();
            String jspBasePkg = "";
            String des = "";
            boolean plate = false;
            if (classAnnotations.length > 0) {
                for (Annotation a : classAnnotations) {
                    if (a.annotationType().equals(FunctionAction.class)) {//jsppackage
                        FunctionAction jp = (FunctionAction) a;
                        jspBasePkg = jp.jspPath();
                        des = jp.des();
                        plate = jp.plate();
                    }
                }
            }
            //	log.info("class:" + cs);
            Method[] myMethodInfo = cs.getMethods();

            try {

                for (int i = 0; i < myMethodInfo.length; i++) {
                    boolean flag = myMethodInfo[i].isAnnotationPresent(Page.class);
                    if (!flag)
                        continue;
                    pageCassNum++;
                    //Page page1 = (Page)myMethodInfo[i].getAnnotation(Page.class);
                    ViewCode vc = new ViewCode();
                    vc.classType = cs;
                    vc.method = myMethodInfo[i];
                    //vc.basemethod = vc.classType.getMethod("BaseInit", new Class[] { Class.forName("javax.servlet.ServletContext"), Class.forName("javax.servlet.http.HttpServletRequest"), Class.forName("javax.servlet.http.HttpServletResponse"), Class.forName("com.world.web.UrlViewCode")});

                    vc.basemethod = vc.classType.getMethod("BaseInit", ServletContext.class, HttpServletRequest.class, HttpServletResponse.class, UrlViewCode.class);

                    vc.methodInvoker = Invokers.newInvoker(vc.method);
                    vc.sysTips = SysTipsManager.getSysTipsManager().getSysTipTypeByMethod(vc.method);
                    //vc.baseMethodInvoker = Invokers.newInvoker(vc.basemethod);
                    if (cs.getName().toLowerCase().endsWith(".index")) {
                        String pack = cs.getName().toLowerCase().substring(0, cs.getName().toLowerCase().indexOf(".index"));
                        //String ls2 = myMethodInfo[i].getName();
                        if (myMethodInfo[i].getName().toLowerCase().equals("index"))
                            vc.name = pack.toLowerCase();
                        else
                            vc.name = (pack + "." + myMethodInfo[i].getName()).toLowerCase();
                    } else if (myMethodInfo[i].getName().toLowerCase().equals("index")) {
                        vc.name = cs.getName().toLowerCase();
                    } else {
                        vc.name = (cs.getName() + "." + myMethodInfo[i].getName()).toLowerCase();
                    }
                    Page p = myMethodInfo[i].getAnnotation(Page.class);
                    ActionCache actionCache = myMethodInfo[i].getAnnotation(ActionCache.class);

                    vc.viewerPath = p.Viewer();
                    vc.viewerPathType = p.value();
                    vc.cacheTime = p.Cache();
                    vc.des = p.des();
                    vc.plate = p.plate();
                    vc.saveLog = p.saveLog();
                    vc.ipCheck = p.ipCheck();
                    vc.compress = p.compress();
                    vc.actionCache = actionCache;
                    if (vc.cacheTime > 0 || actionCache != null) {
                        vc.lock = new ReentrantLock();
                    }
                    //Method toMethod = null;//vc.basemethod = vc.classType.getMethod("BaseInit", new Class[] { Class.forName("javax.servlet.ServletContext"), Class.forName("javax.servlet.http.HttpServletRequest"), Class.forName("javax.servlet.http.HttpServletResponse"), Class.forName("com.world.web.UrlViewCode")});
                    if ((vc.viewerPath.toLowerCase().endsWith(".xsl")) || (vc.viewerPath.toLowerCase().endsWith(".xslt"))) {
                        vc.viewerType = ViewerType.XSLT;
                    } else if ((vc.viewerPath.toLowerCase().endsWith(".json"))) {
                        vc.viewerType = ViewerType.JSON;
                    } else if ((vc.viewerPath.toLowerCase().endsWith(".ftl"))) {
                        vc.viewerType = ViewerType.FREEMARKER;
                    } else if (vc.viewerPath.toLowerCase().endsWith(".jsp")) {
                        vc.viewerType = ViewerType.JSP;

                        if (vc.viewerPath.equals(DEFAULT_INDEX)) {// 默认
                            vc.viewerPath = jspBasePkg + DEFAULT_INDEX;
                        } else if (vc.viewerPath.equals(DEFAULT_AJAX)) {
                            vc.viewerPath = jspBasePkg + DEFAULT_AJAX;
                        } else if (vc.viewerPath.equals(DEFAULT_AORU)) {
                            vc.viewerPath = jspBasePkg + DEFAULT_AORU;
                        }
                        //toMethod = vc.classType.getMethod("ToJsp", null);
                    } else if (vc.viewerPath.toLowerCase().endsWith(".xml")) {
                        vc.viewerType = ViewerType.XML;
                        //toMethod = vc.classType.getMethod("ToXml", null);
                    }
//			if(toMethod != null){
//				//vc.toViewInvoker = Invokers.newInvoker(toMethod);
//			}
                    AddContent(vc, jspBasePkg, des, plate);

                    if (myMethodInfo[i].getName().toLowerCase() != "index")
                        continue;

                    vc = new ViewCode();
                    vc.classType = cs;
                    vc.method = myMethodInfo[i];
                    vc.name = cs.getName().toLowerCase();
                    AddContent(vc, jspBasePkg, des, plate);
                }

                //ccss = ccss + cs.toString();
            } catch (Exception e) {
                log.error(e.toString(), e);
            }
        }

        log.debug("加载了url系统,共有类" + classesNum + "个，page响应函数" + pageCassNum
                + "个，content共占用内存：" + WebUtil.objectSize(content) + "字节");


    }

    public static UrlViewCode GetViewGode(String url) {
        String webBasePath = GlobalConfig.basePckPath;
        String srcURL = url;
        url = url.toLowerCase();
        // log.debug("GET url:" + url);
        UrlViewCode uvc = new UrlViewCode();
        if (url.equals("/")) {
            ViewCode rtn0 = content.get(webBasePath);
            uvc.viewCode = rtn0;
            uvc.urlPramas = new String[0];
            return uvc;
        }
        String uPs = "";

        if (url.indexOf('?') > 0) {
            url = url.substring(0, url.indexOf('?'));
            srcURL = srcURL.substring(0, srcURL.indexOf('?'));
        }

        if (url.indexOf('.') > 0) {
            url = url.substring(0, url.indexOf('.'));
            srcURL = srcURL.substring(0, srcURL.indexOf('.'));
        }


        /**2014-07-31  下面的代码经测试都是没用的，可以去除**/

//    String uurl = url.substring(1);//.replace("/", ".");
//    if (uurl.indexOf('/') > 0){
//      uurl = uurl.substring(0, uurl.indexOf('/'));
//    }
//    if (uurl.indexOf('-') > 0){
//      uurl = uurl.substring(0, uurl.indexOf('-'));
//    }
//    if (!content.containsKey(webBasePath + "." + uurl)){
//      log.debug("不包含：" + webBasePath + "." + uurl);
//      url = url.substring(1);
//      if (url.indexOf('/') > 0)
//        url = url.substring(url.indexOf('/'));
//      url = url.replace('-', '.').replace('/', '.');
//      uPs = uurl;
//      if (url.indexOf('.') < 0) {
//        url = webBasePath + ".index";
//      }
//      else {
//        url = webBasePath  + url;
//      }
//
//    }else{
//      
//    }

        url = webBasePath + url.replace('-', '.').replace('/', '.');
        srcURL = webBasePath + srcURL.replace('-', '.').replace('/', '.');
        ViewCode rtn = null;
        String ls = url;
        String srcLs = srcURL;
        while (!content.containsKey(url)) {
            if (url.indexOf('.') <= 0) break;
            url = url.substring(0, url.lastIndexOf('.'));
            srcURL = srcURL.substring(0, srcURL.lastIndexOf('.'));
        }

        if ((content.containsKey(url)) && (!url.equals(webBasePath)))
            rtn = content.get(url);
        else {
            if (url.equals(webBasePath)) {//找不到时候返回首页
                rtn = content.get(webBasePath);
            } else {
                rtn = content.get(webBasePath + ".notfound");
            }
        }
        rtn.count += 1L;
        uvc.viewCode = rtn;
        ls = ls.substring(url.length());
        srcLs = srcLs.substring(srcURL.length());
        if (ls.indexOf('.') == 0) {
            ls = ls.substring(1);
            srcLs = srcLs.substring(1);
        }

        if (uPs.length() > 0)
            uvc.urlPramas = (uPs + "." + srcLs).split("\\.");
        else {
            uvc.urlPramas = srcLs.split("\\.");
        }
        return uvc;
    }

    public static UrlViewCode GetViewModuleGode(String url) {
        UrlViewCode uvc = new UrlViewCode();
        url = url.toLowerCase().replace("/", ".");
        if (url.indexOf('.') == 0) {
            url = url.substring(1);
        }

        if (content.containsKey(GlobalConfig.basePckPath + ".users.module." + url)) {
            uvc.viewCode = content.get(GlobalConfig.basePckPath + ".users.module." + url);
            log.debug("获取到模块:" + url);
            return uvc;
        }

        log.debug("不存在这个模块:" + url);
        return null;
    }
}