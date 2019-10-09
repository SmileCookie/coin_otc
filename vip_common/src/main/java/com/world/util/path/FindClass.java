package com.world.util.path;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.log4j.Logger;

/** 
 * ����:����ָ������
 * @author Administrator
 *
 */
public class FindClass {

	private final static Logger log =Logger.getLogger(FindClass.class);

    public static Set<Class<?>> getClasses(Package pack){
    	//log.info("x1");
   	         //��һ��class��ļ���
   	         Set<Class<?>> classes = new LinkedHashSet<Class<?>>();
   	         //�Ƿ�ѭ�����
   	         boolean recursive = true;
   	         //��ȡ������� �������滻
   	         String packageName = pack.getName();
   	         String packageDirName = packageName.replace('.', '/');
   	         //����һ��ö�ٵļ��� ������ѭ�����������Ŀ¼�µ�things
   	       //  log.info("��ʼѰ�������ࣺ"+packageDirName);
   	        // log.info("Ѱ��·����"+Netpet.Web.Pages.class.getPackage().getName());
   	         Enumeration<URL> dirs;
   	         try {
   	             dirs = Thread.currentThread().getContextClassLoader().getResources(packageDirName);
   	          //log.info("x2");
   	        	//dirs =this..getContextClassLoader().getResources(packageDirName);
   	             int number=0;
   	             //ѭ�������ȥ
   	             while (dirs.hasMoreElements()){
   	            	number++;
   	            	//log.info("�ҵ�Ŀ¼��"+dirs.toString());
   
   	                 //��ȡ��һ��Ԫ��
   	                 URL url = dirs.nextElement();
   	                 //�õ�Э������
   	                 String protocol = url.getProtocol();
   	             // log.info("�ҵ�Ŀ¼:"+packageDirName+"�����ͣ�"+protocol);
   	                 //��������ļ�����ʽ�����ڷ�������
   	                 
   	                 if ("file".equals(protocol)) {
   	                	//log.info("x2 file");
   	                     //��ȡ�������·��
   	                     String filePath = URLDecoder.decode(url.getFile(), "UTF-8");
   	                     //���ļ��ķ�ʽɨ��������µ��ļ� ����ӵ�������
   	                    // log.info(packageName+":"+filePath);
   	                     findAndAddClassesInPackageByFile(packageName, filePath, recursive, classes);
   	                 } else if ("jar".equals(protocol)){
   	                	log.info("x2 jar");
   	                     //����� jar���ļ� 
   	                     //����һ��JarFile
   	                     JarFile jar;
   	                     try {
   	                         //��ȡjar
   	                         jar = ((JarURLConnection) url.openConnection()).getJarFile();
   	                         // �Ӵ�jar�� �õ�һ��ö����
   	                         Enumeration<JarEntry> entries = jar.entries();
   	                         // ͬ��Ľ���ѭ�����
   	                         while (entries.hasMoreElements()) {
   	                             //��ȡjar���һ��ʵ�� ������Ŀ¼ ��һЩjar����������ļ� ��META-INF���ļ�
   	                             JarEntry entry = entries.nextElement();
   	                             String name = entry.getName();
   	                             //�������/��ͷ��
   	                             if (name.charAt(0) == '/') {
   	                                 //��ȡ������ַ�
   	                                 name = name.substring(1);
   	                             }
   	                             //���ǰ�벿�ֺͶ���İ�����ͬ
   	                             if (name.startsWith(packageDirName)) {
   	                                 int idx = name.lastIndexOf('/');
   	                                 //�����"/"��β ��һ����
   	                                 if (idx != -1) {
   	                                     //��ȡ���� ��"/"�滻��"."
   	                                     packageName = name.substring(0, idx).replace('/', '.');
   	                                 }
   	                                 //�����Ե����ȥ ������һ����
   	                                 if ((idx != -1) || recursive){
   	                                     //�����һ��.class�ļ� ���Ҳ���Ŀ¼
   	                                     if (name.endsWith(".class") && !entry.isDirectory()) {
   	                                         //ȥ�������".class" ��ȡ���������
   	                                         String className = name.substring(packageName.length() + 1, name.length() - 6);
   	                                         try {
   	                                             //��ӵ�classes
   	                                             classes.add(Class.forName(packageName + '.' + className));
   	                                         } catch (ClassNotFoundException e) {
   	                                            // log.error("����û��Զ�����ͼ����� �Ҳ��������.class�ļ�");
   	                                             log.error(e.toString(), e);
   	                                         }
   	                                       }
   	                                 }
   	                             }
   	                         }
   	                     } catch (IOException e) { 
   	                        // log.error("��ɨ���û�������ͼʱ��jar���ȡ�ļ�����");
   	                         log.error(e.toString(), e);
   	                     } 
   	                 }
   	             }
   	           //  log.debug("���ҵ�Ŀ¼����Ϊ��"+number);
   	         } catch (IOException e) {
   	             log.error(e.toString(), e);
   	         }

   	         return classes;
   	     }
    public static Set<Class<?>> getClasses2(Package pack)
    {
    	 //��ȡ������� �������滻 
	         String packageName = pack.getName();
	         String packageDirName = packageName.replace('.', '/');
	         //�Ƿ�ѭ�����
   	         boolean recursive = true;
   	         //��һ��class��ļ���
   	         Set<Class<?>> classes = new LinkedHashSet<Class<?>>();
   	      URL jsc=com.world.web.Pages.class.getProtectionDomain().getCodeSource().getLocation();
   	         String path=jsc.toString();
   	        // log.info(path);
   	         boolean isJar=false;
   	         if(path.endsWith(".jar")) 
   	        	isJar=true;
   	       // 	JarURLConnection.
   	         if(isJar){
   	        	 JarFile jar;
                    try { 
                        //��ȡjar
                    	 URL url = new URL("jar:"+path+"!/");
                    	 JarURLConnection jarConnection = (JarURLConnection)url.openConnection();

                        jar =jarConnection.getJarFile();
                        // �Ӵ�jar�� �õ�һ��ö����
                        Enumeration<JarEntry> entries = jar.entries();
                        // ͬ��Ľ���ѭ�����
                        while (entries.hasMoreElements()) {
                            //��ȡjar���һ��ʵ�� ������Ŀ¼ ��һЩjar����������ļ� ��META-INF���ļ�
                            JarEntry entry = entries.nextElement();
                            String name = entry.getName();
                            //�������/��ͷ��
                            if (name.charAt(0) == '/') {
                                //��ȡ������ַ�
                                name = name.substring(1);
                            }
                            //���ǰ�벿�ֺͶ���İ�����ͬ
                            if (name.startsWith(packageDirName)) {
                                int idx = name.lastIndexOf('/');
                                //�����"/"��β ��һ����
                                if (idx != -1) {
                                    //��ȡ���� ��"/"�滻��"."
                                    packageName = name.substring(0, idx).replace('/', '.');
                                }
                                //�����Ե����ȥ ������һ����
                                if ((idx != -1) || recursive){
                                    //�����һ��.class�ļ� ���Ҳ���Ŀ¼
                                    if (name.endsWith(".class") && !entry.isDirectory()) {
                                        //ȥ�������".class" ��ȡ���������
                                        String className = name.substring(packageName.length() + 1, name.length() - 6);
                                        try {
                                            //��ӵ�classes
                                            classes.add(Class.forName(packageName + '.' + className));
                                        } catch (ClassNotFoundException e) {
                                           // log.error("����û��Զ�����ͼ����� �Ҳ��������.class�ļ�");
                                            log.error(e.toString(), e);
                                        }
                                      }
                                }
                            }
                        }
                    } catch (IOException e) { 
                       // log.error("��ɨ���û�������ͼʱ��jar���ȡ�ļ�����");
                        log.error(e.toString(), e);
                    } 
                    return classes;
   	         }
   	         else
   	        	 return getClasses(pack);
   	     
   	

   	        
   	     }
       /**
          * ���ļ�����ʽ����ȡ���µ�����Class
          * @param packageName
          * @param packagePath
          * @param recursive
          * @param classes
          */
         public static void findAndAddClassesInPackageByFile(String packageName, String packagePath, final boolean recursive, Set<Class<?>> classes){
             //��ȡ�˰��Ŀ¼ ����һ�� File
        	 //log.info(packagePath);
             File dir = new File(packagePath);
             //�����ڻ��� Ҳ����Ŀ¼��ֱ�ӷ���
             if (!dir.exists() || !dir.isDirectory()) {
                // log.warn("�û�������� " + packageName + " ��û���κ��ļ�");
                 return;
             }
             //������ �ͻ�ȡ���µ������ļ� ����Ŀ¼
             File[] dirfiles = dir.listFiles(new FileFilter() {
             //�Զ�����˹��� ������ѭ��(����Ŀ¼) ��������.class��β���ļ�(����õ�java���ļ�)
                   public boolean accept(File file) {
                     return (recursive && file.isDirectory()) || (file.getName().endsWith(".class"));
                   }
                 });
             //ѭ�������ļ�
             for (File file : dirfiles) {
                 //�����Ŀ¼ �����ɨ��
                 if (file.isDirectory()) {
                     findAndAddClassesInPackageByFile(packageName + "." + file.getName(),
                                           file.getAbsolutePath(),
                                           recursive,
                                           classes);
                 }
                 else {
                     //�����java���ļ� ȥ�������.class ֻ��������
                     String className = file.getName().substring(0, file.getName().length() - 6);
                     try {
                         //��ӵ�������ȥ
                         classes.add(Class.forName(packageName + '.' + className));
                     } catch (ClassNotFoundException e) {
                         //log.error("����û��Զ�����ͼ����� �Ҳ��������.class�ļ�");
                         log.error(e.toString(), e);
                     }
                 }
             }
         }
 
}
