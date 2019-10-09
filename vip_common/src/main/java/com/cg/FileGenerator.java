package com.cg;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.regex.Pattern;

import com.cg.annotation.CgEntity;
import com.cg.annotation.CgProperty;
import com.cg.module.ModuleType;
import com.cg.module.ModuleFactory;
import com.google.code.morphia.annotations.Entity;
import org.apache.log4j.Logger;

public class FileGenerator {

	private final static Logger log = Logger.getLogger(FileGenerator.class.getName());

	private static String importStr = "";
	
	public static void generateJsp(Class c){
		generateJsp(c, ModuleType.AORU);
		generateJsp(c, ModuleType.LIST);
		generateJsp(c, ModuleType.AJAX);
		generateJava(c, ModuleType.DAO);
	}
	
	public static void generateJsp(Class c , ModuleType type){
		String jsp = getCode(c , type);
		String currentPath = FileGenerator.class.getResource("").getPath();
		String jspFoldPath = currentPath.substring(0 , currentPath.indexOf("/WEB-INF"));
		
		Entity gp = (Entity) c.getAnnotation(Entity.class);
		String innerName = c.getSimpleName();
		if(gp != null && gp.value() != null){
			innerName = gp.value();
		}
		
		String savePath = jspFoldPath + "/cg/" + innerName; 
		
		File f = new File(savePath);
		if(!f.exists()){
			f.mkdirs();
		}
		
		String filePath = savePath + "/" + type.getPath();
		
		saveToDisk(filePath, jsp);
	}
	
	public static void generateJava(Class c , ModuleType type){
		String currentPath = FileGenerator.class.getResource("").getPath();
		log.info(currentPath);
		String srcFoldPath = currentPath.substring(0 , currentPath.indexOf("/WebRoot"));
		
		Entity gp = (Entity) c.getAnnotation(Entity.class);
		String innerName = c.getSimpleName();
		if(gp != null && gp.value() != null){
			innerName = gp.value();
		}
		
		Package p = c.getPackage();
		
		log.info(p.getName());
		
		String savePath = srcFoldPath + "/src/" + p.getName().replace(".", "/") + "/" + innerName; 
		
		File f = new File(savePath);
		if(!f.exists()){
			f.mkdirs();
		}
		String javaName = "";
		if(type.equals(ModuleType.DAO)){
			javaName = getDaoName(innerName) + ".java";
		}else{
			javaName = "Index.java";
		}
		
		String filePath = savePath + "/" + javaName;
		
		String code = getCode(c , type);
		
		saveToDisk(filePath, code);
	}
	
	public static String getDaoName(String innerName){
		String daoName = innerName.replaceFirst(innerName.substring(0, 1),innerName.substring(0, 1).toUpperCase()) +"Dao";
		return daoName;
	}
	
	public static String getCode(Class c , ModuleType type){
		String code = ModuleFactory.getModule(type);
		
		
		code = dealAoru(c , code , type);
		return code;
	}
	
	public static void saveToDisk(String filePath , String content){
		FileOutputStream fos = null;
		File newF = new File(filePath);
		try {
			fos = new FileOutputStream(newF);
			fos.write(content.getBytes("UTF-8"));
		} catch (FileNotFoundException e) {
			log.error(e.toString(), e);
		} catch (IOException e) {
			log.error(e.toString(), e);
		}finally{
			if(fos != null){
				try {
					fos.close();
				} catch (IOException e) {
					log.error(e.toString(), e);
				}
			}
		}
	}
	
	public static String dealAoru(Class cls , String content , ModuleType type){
		//Document doc = Jsoup.parse(jsp);
		//Element btn = doc.getElementById("formBtn");
		Package p = cls.getPackage();
		Entity gp = (Entity) cls.getAnnotation(Entity.class);
		String innerName = cls.getSimpleName();
		if(gp != null && gp.value() != null){
			innerName = gp.value();
		}
		String javaPackge = "package " + p.getName() + "." + innerName+";";
		javaPackge += "\n" + "import " + cls.getName() +";";
		
		CgEntity ce = (CgEntity) cls.getAnnotation(CgEntity.class);
		String des = cls.getSimpleName();
		if(ce != null && ce.des() != null){
			des = ce.des();
		}
		
		switch (type) {
			case AORU:
				String formLines = getFormByClassInfo(cls);
				content = content.replace("${FORMLINES}", formLines);
				break;
			case LIST:
				String search = getSearch(cls);
				content = content.replace("${FORMLINES}", search);
				content = content.replace("${DES}", des);
				break;
			case AJAX:
				content = content.replace("${TABLE}", getAjaxJsp(cls));
				break;
			case DAO:
				content = content.replace("${PACKAGE}", javaPackge).replace("${DAONAME}", getDaoName(innerName)).replace("${ENTITYNAME}", cls.getSimpleName());
				break;
			case ACTION:
				content = content.replace("${DES}", des)
								 .replace("${DAO}", getDaoName(innerName))
								 .replace("${ENTITYNAME}", cls.getSimpleName())
								 .replace("${SEARCHPARAMS}", getSearchParams(cls , true))
								 .replace("${UPDATEPARAMS}", getSearchParams(cls , false))
								 .replace("${SEARCHCONDITIONS}", getSearchConditions(cls))
								 .replace("${UPDATESET}", getUpdateSet(cls))
								 .replace("${ENTITYSET}", getEntitySet(cls));
				if(!importStr.equals("") && !javaPackge.contains(importStr)){
					javaPackge += "\n" + importStr;
				}
				content = content.replace("${PACKAGE}", javaPackge);
				break;
			default:
				break;
		}
		
		log.info(content);
		return content;
	}
	
	public static String getEntitySet(Class cls){
		String sets = "";
		Field[] fs = cls.getDeclaredFields();
		
		for(Field f : fs){
			CgProperty gp = f.getAnnotation(CgProperty.class);
			String des = "";
			if(gp != null){
				des = gp.des();
			}
			String fieldName = f.getName();
			
			if(f.getType().toString().equals("boolean")){
				sets += "\n\t\t\t\t\t\te.set"+fieldName.replace("is", "").replaceFirst(fieldName.substring(0, 1),fieldName.substring(0, 1).toUpperCase())+"("+f.getName()+");//" + des;
			}else{
				sets += "\n\t\t\t\t\t\te.set"+fieldName.replaceFirst(fieldName.substring(0, 1),fieldName.substring(0, 1).toUpperCase())+"("+f.getName()+");//" + des;
			}
		}
		return sets;
	}
	
	public static String getUpdateSet(Class cls){
		String sets = "";
		Field[] fs = cls.getDeclaredFields();
		
		for(Field f : fs){
			CgProperty gp = f.getAnnotation(CgProperty.class);
			String des = "";
			if(gp != null){
				des = gp.des();
			}
			sets += "\n\t\t\t\t\t\t.set(\""+f.getName()+"\" , "+f.getName()+")//" + des;
			
		}
		return sets;
	}
	
	public static String getSearchParams(Class cls , boolean isSearch){
		String search = "";
		Field[] fs = cls.getDeclaredFields();
		
		for(Field f : fs){
			CgProperty gp = f.getAnnotation(CgProperty.class);
			String des = "";
			if(gp != null){
				des = gp.des();
			}
			if(isSearch && (gp == null || !gp.search())){
				continue;
			}
			Class c = f.getType();
			String paramType = "String";
			String paramMethod = "param";
			if(c.getName().equals("java.lang.String")){
				paramType = "String";
			}else if(c.getName().equals("java.sql.Timestamp")){
				if(!importStr.contains("import java.sql.Timestamp;")){
					importStr += "import java.sql.Timestamp;";
				}
				paramType = "Timestamp";
				paramMethod = "dateParam";
			}else{
				paramMethod = c.getName() + "Param";
				paramType = c.getName();
			}
			search += "\t\t"+paramType+"  "+f.getName()+" = "+paramMethod+"(\""+f.getName()+"\");//" + des +"\n";
		}
		return search;
	}
	
	public static String getSearchConditions(Class cls){
		String search = "";
		Field[] fs = cls.getDeclaredFields();
		
		for(Field f : fs){
			CgProperty gp = f.getAnnotation(CgProperty.class);
			if(gp == null || !gp.search()){
				continue;
			}
			
			Class c = f.getType();
			String paramType = "String";
			String paramMethod = "param";
			log.info(c.isPrimitive());

			if(c.isPrimitive()){
				search += "\t\t\tif("+f.getName()+">0){"+"//"+gp.des()+"\n"+
		         "\t\t\t\tq.filter(\""+f.getName()+" =\", "+f.getName()+");"+"\n"+
		         "\t\t\t}"+"\n";
			}else if(c.getName().equals("java.lang.String")){
				search += "\t\t\tif("+f.getName()+".length()>0){"+"//"+gp.des()+"\n"+
		         "\t\t\t\tPattern pattern = Pattern.compile(\"^.*\"  + "+f.getName()+"+  \".*$\" ,  Pattern.CASE_INSENSITIVE);"+"\n"+
		         "\t\t\t\tq.filter(\""+f.getName()+"\", pattern);"+"\n"+
		         "\t\t\t}"+"\n";
			}
			
			
		}
		
		return search;
	}
	
	public static String getSearch(Class cls){
		String search = "";
		Field[] fs = cls.getDeclaredFields();
		
		for(Field f : fs){
			CgProperty gp = f.getAnnotation(CgProperty.class);
			if(gp == null || !gp.search()){
				continue;
			}
			search += "<p>"+"\n"+
				"\t\t\t\t\t"+"<span>"+gp.des()+"：</span>"+"\n"+
				"\t\t\t\t\t"+"<input id=\""+f.getName()+"\" mytitle=\""+gp.des()+"要求填写一个长度小于50的字符串\" name=\""+f.getName()+"\""+"\n"+
				"\t\t\t\t\t\t"+"	pattern=\"limit(0,50)\" size=\"20\" type=\"text\"/>"+"\n"+
				"\t\t\t\t"+"</p>"+"\n";
		}
		return search;
	}
	
	public static String getAjaxJsp(Class cls){
		String ajax = "\t<thead>"+ "\n" +
		"\t\t"+"<tr>"+ "\n" +
		"\t\t\t"+	"<th>编号</th>"+ "\n";
		
		Field[] fs = cls.getDeclaredFields();
		
		for(Field f : fs){
			CgProperty gp = f.getAnnotation(CgProperty.class);
			if(gp == null || !gp.search()){
				continue;
			}
			ajax += "\t\t\t"+"<th>" + gp.des() + "</th>"+ "\n";
		}
		ajax += "\t\t\t"+"<th>操作</th>"+ "\n";
		ajax += "\t\t"+"</tr>"+ "\n" +
		"\t"+"</thead>"+ "\n" +
		"\t\t"+"<c:choose>"+ "\n" +
		"\t\t\t"+	"<c:when test=\"${dataList!=null}\">"+ "\n" +
		"\t\t\t\t"+		"<c:forEach items=\"${dataList}\" var=\"list\">"+ "\n" +
		"\t\t\t\t\t"+		"<tbody class=\"item_list\" id=\"line_${list.id}\" >"+ "\n" +
		"\t\t\t\t\t\t"+				"<tr>"+ "\n" +
		"\t\t\t\t\t\t\t"+					"<td>" +"${list.id}" +"</td>"+ "\n";
		for(Field f : fs){
			CgProperty gp = f.getAnnotation(CgProperty.class);
			if(gp == null || !gp.search()){
				continue;
			}
			ajax += "\t\t\t\t\t\t\t"+"<td>${list."+f.getName()+"}</td>"+ "\n";
		}
		
		ajax +=				"\t\t\t\t\t\t\t"+"<td>"+ "\n" +
		"\t\t\t\t\t\t\t\t"+	"<a href=\"javascript:dsc.list.del({id : '${list.id }'});\">删除</a> &nbsp;"+ "\n" +
		"\t\t\t\t\t\t\t\t"+	"<a href=\"javascript:dsc.list.aoru({id : '${list.id }', width : 600 , height : 300});\">修改</a>"+ "\n" +
								"\t\t\t\t\t\t\t"+"</td>"+ "\n" +
							"\t\t\t\t\t\t"+	"</tr>"+ "\n" +
						"\t\t\t\t\t"+	"</tbody>"+ "\n" +
						"\t\t\t\t"+"</c:forEach>"+ "\n" +
						"\t\t\t\t"+"<tfoot>"+ "\n" +
						"\t\t\t\t\t"+"<tr>"+ "\n" +
						"\t\t\t\t\t\t"+"<td colspan=\"6\">"+ "\n" +
						"\t\t\t\t\t\t\t"+"<div class=\"page_nav\" id=\"pagin\">"+ "\n" +
						"\t\t\t\t\t\t\t\t"+     "<div class=\"con\">"+ "\n" +
						"\t\t\t\t\t\t\t\t\t"+       "<c:if test=\"${pager!=null}\">${pager}</c:if>"+ "\n" +
				        "\t\t\t\t\t\t\t\t"+    "</div>"+ "\n" +
				             "\t\t\t\t\t\t\t"+ "</div>"+ "\n" +
		                 "\t\t\t\t\t\t"+" </td>"+ "\n" +
					 "\t\t\t\t\t"+"</tr>"+ "\n" +
					"\t\t\t\t"+" </tfoot>"+ "\n" +
			  "\t\t\t"+"</c:when>"+ "\n" +
			  "\t\t\t"+"<c:otherwise>"+ "\n" +
			  "\t\t\t\t"+"<tbody>"+ "\n" +
			  "\t\t\t\t\t"+		"<tr>"+ "\n" +
			  "\t\t\t\t\t\t"+			"<td colspan=\"6\">"+ "\n" +
			  "\t\t\t\t\t\t\t"+				"<p>暂时没有符合要求的记录！</p>"+ "\n" +
				"\t\t\t\t\t\t"+			"</td>"+ "\n" +
				"\t\t\t\t\t"+		"</tr>"+ "\n" +
				"\t\t\t\t"+	"</tbody>"+ "\n" +
				"\t\t\t"+"</c:otherwise>"+ "\n" +
				"\t\t"+"</c:choose>"+ "\n";
		
		return ajax;
	}
	
	public static String getFormByClassInfo(Class cls){
//		Method[] methods = cls.getMethods();
//		Annotation[] classAnnotations = cls.getAnnotations();
		String form = "";
		
		Field[] fs = cls.getDeclaredFields();
		
		for(Field f : fs){
			CgProperty gp = f.getAnnotation(CgProperty.class);
			if(gp == null || !gp.search()){
				continue;
			}
			form += "\n\t\t\t<div class=\"form-line\">"+"\n"+
			"\t\t\t\t"+"<div class=\"form-tit\">";
			form += gp.des() + "：" +
			"</div>" +"\n"+
			"\t\t\t\t"+"<div class=\"form-con\">" +"\n"+
			"\t\t\t\t\t"+"<input id=\""+f.getName()+"\" mytitle=\"请填写"+gp.des()+"\" name=\""+f.getName()+"\" pattern=\"limit(0,50)\" size=\"20\" type=\"text\" value=\"${curData."+f.getName()+"}\" valueDemo=\""+gp.des()+"\" />" + "\n" +
			"\t\t\t\t"+"</div>" +"\n"+
			"\t\t\t"+"</div>"+"\n";
		}
		return form;
	}
}
