package com.world.util.poi;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;

public class ExcelManager
{
	//Excel工作簿
    private HSSFWorkbook workbook;
    //Excel工作表
    private HSSFSheet sheet;
    //Excel工作表的行 
    private HSSFRow row;
    //Excel表格样式
    private HSSFCellStyle style;
	
    //定制日期格式
    private static String DATE_FORMAT = "yyyy-MM-dd";  //  "m/d/yy h:mm"
    //定制浮点数格式
    private static String NUMBER_FORMAT = "#,##0.00";
    
    public ExcelManager(){
    	this.workbook = new HSSFWorkbook();	//初始化工作簿
        this.sheet = workbook.createSheet();	//新建工作表
    }

	/**
	 * 正常导出通用方法，不带合并、公式等特性
	 * 
	 * @param list	需要导出的LIST数据
	 * @param params	实体中字段数组
	 * @param tabTead	表头文字  长度应跟params的长度一至
	 * @throws NoSuchMethodException 
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 * @throws SecurityException 
	 * @throws IllegalArgumentException 
	 * @throws IOException 
	 * 
	 */
	public static HSSFWorkbook exportNormal(List<?> list,String [] column,String [] tabTead) throws IllegalArgumentException, SecurityException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, IOException{
		ExcelManager manager = new ExcelManager();
		int row = 0;
        manager.createRow(row); //创建第一行设置表头
        manager.row.setHeight((short)(15.625*28));
        manager.setNormalTabStyle(manager.workbook);
       // manager.setTabHeadStyle(manager.workbook);
		for (String string : tabTead){
			manager.setCell(row, string);	//设置表头
			manager.sheet.setColumnWidth((short) row, (short) (35.7*170));
			row++;
		}
		row = 1;	//第二行开始
		int index = 0;
		String arr[];
		//根据LIST获取数据并生成文件
		for(Object object : list){
			manager.createRow(row); //创建第一行设置表头
			manager.row.setHeight((short)(15.625*23));
			//manager.setNormalTabStyle(manager.workbook);
		    Class<?> clz = object.getClass();
			// 获取实体类的所有属性，返回Field数组
			Field[] fields = clz.getDeclaredFields();
			for (String string : column){
					if(string.indexOf(".") > -1){
						string = string.replace(".", ",");
						arr = string.split(",");
						string = arr[0];
					}else{	
						arr = null;
					}
					//属性判断
					for (Field field : fields) {
						  if(string.equals(field.getName())){
							   if(arr != null){
								   Object obj = manager.getClassObject(object, arr, 0);
								   manager.setCellValueByType(obj, field, arr[arr.length-1], manager, index); 
							   }else{
								   manager.setCellValueByType(object, field, string, manager, index); 
							   }
							   break;
						  }
					}
					index++;
			}
			row++;
			index = 0;
		}
		
		return manager.workbook;
	}

	/**
	 * 获取最后层级对象
	 * 
	 * @param object
	 * @param arr
	 * @return
	 * @throws NoSuchMethodException 
	 * @throws SecurityException 
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 * @throws IllegalArgumentException 
	 */
	public Object getClassObject(Object object,String[] arr,int index) throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException{
		if(arr.length - 1 >  index){
			Method m = (Method)object.getClass().getMethod(toGetMethodString(arr[index]));
			object = m.invoke(object);
			if(object != null && index != (arr.length - 2)){
				index++;
				object = getClassObject(object, arr, index);
			}
		}
		return object;
	}
	
	/**
	 * 根据类型设值到单元格
	 * 
	 * @param field
	 * @param method
	 * @param manager
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 * @throws IllegalArgumentException 
	 * @throws NoSuchMethodException 
	 * @throws SecurityException 
	 */
	public void setCellValueByType(Object object,Field field,String method,ExcelManager manager,int index) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, SecurityException, NoSuchMethodException{
		if(object == null){
			manager.setCell(index, "");
		}else{
			// 如果类型是String
			if(field.getGenericType().toString().equals("class java.lang.String")) { // 如果type是类类型，则前面包含"class "，后面跟类名
			     // 拿到该属性的gettet方法
			     Method m = (Method)object.getClass().getMethod(toGetMethodString(method));
			     String val = (String) m.invoke(object); // 调用getter方法获取属性值
			     if (val != null) {
			    	 manager.setCell(index, val);
			     }else{
			    	 manager.setCell(index, "");
			     }
			}else
			// 如果类型是Integer封装类
			if(field.getGenericType().toString().equals("class java.lang.Integer")) {
				 Method m = (Method)object.getClass().getMethod(toGetMethodString(method));
			     Integer val = (Integer) m.invoke(object);
			     if (val != null) {
			    	 manager.setCell(index, val);
			     }else{
			    	 manager.setCell(index, "");
			     }
			 }else
			// 如果类型是BigDecimal封装类
			if(field.getGenericType().toString().equals("class java.math.BigDecimal")) {
				 Method m = (Method)object.getClass().getMethod(toGetMethodString(method));
			     BigDecimal val = (BigDecimal) m.invoke(object);
			     DecimalFormat df = new DecimalFormat("#0.########");
			     if (val != null) {
			    	 manager.setCell(index, df.format(val));
			     }else{
			    	 manager.setCell(index, "");
			     }
			 }else
			// 如果类型是int
			if(field.getGenericType().toString().equals("int")) {
				 Method m = (Method)object.getClass().getMethod(toGetMethodString(method));
				 Integer val = (Integer) m.invoke(object);
			     if (val != null) {
			    	 manager.setCell(index, val); 
			     }else{
			    	 manager.setCell(index, "");
			     }
			 }else
			 // 如果类型是Double
			if(field.getGenericType().toString().equals("class java.lang.Double")) {
				 Method m = (Method)object.getClass().getMethod(toGetMethodString(method));
				 Double val = (Double) m.invoke(object);
				 DecimalFormat df = new DecimalFormat("#0.########");
			     if (val != null) {
			    	 manager.setCell(index, df.format(val));
			     }else{
			    	 manager.setCell(index, "");
			     }
			}else
			// 如果类型是double
			if(field.getGenericType().toString().equals("double")) {
				 Method m = (Method)object.getClass().getMethod(toGetMethodString(method));
			     Double val = (Double) m.invoke(object);
			     DecimalFormat df = new DecimalFormat("#0.########");
			     if (val != null) {
			    	 manager.setCell(index, df.format(val));
			     }else{
			    	 manager.setCell(index, "");
			     }
			}else
			// 如果类型是Long
			if(field.getGenericType().toString().equals("class java.lang.Long")) {
				 Method m = (Method)object.getClass().getMethod(toGetMethodString(method));
				 String val = m.invoke(object).toString();
			     if (val != null) {
			    	 manager.setCell(index, val);
			     }else{
			    	 manager.setCell(index, "");
			     }
			}else
			// 如果类型是long
			if(field.getGenericType().toString().equals("long")) {
				 Method m = (Method)object.getClass().getMethod(toGetMethodString(method));
			     String val = m.invoke(object).toString();
			     if (val != null) {
			    	 manager.setCell(index, val);
			     }else{
			    	 manager.setCell(index, "");
			     }
			}else
			// 如果类型是Date
			if (field.getGenericType().toString().equals("class java.util.Date")) {
				 Method m = (Method)object.getClass().getMethod(toGetMethodString(method));
				 Date date = (Date) m.invoke(object);
			     if (date != null) {
			    	 manager.setCell(index, date);
			     }else{
			    	 manager.setCell(index, "");	
			     }
			}else
				// 如果类型是Date
				if (field.getGenericType().toString().equals("class java.sql.Timestamp")) {
					 Method m = (Method)object.getClass().getMethod(toGetMethodString(method));
					 Timestamp date = (Timestamp) m.invoke(object);
					 SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				     if (date != null) {
				    	 manager.setCell(index, sdf.format(date));
				     }else{
				    	 manager.setCell(index, "");	
				     }
				}
			else{
				 // 拿到该属性的gettet方法
			     Method m = (Method)object.getClass().getMethod(toGetMethodString(method));
			     String val = (String) m.invoke(object); // 调用getter方法获取属性值
			     if (val != null) {
			    	 manager.setCell(index, val);
			     }else{
			    	 manager.setCell(index, "");
			     }
			}
		}
	}

  /**
   * 设置表头样式
   * @param workbook
   */
  public void setTabHeadStyle(HSSFWorkbook workbook){
	 //生成一个样式
     style = workbook.createCellStyle();
     //设置这些样式
     style.setFillForegroundColor(HSSFColor.SKY_BLUE.index);
     style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
     style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
     style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
     style.setBorderRight(HSSFCellStyle.BORDER_THIN);
     style.setBorderTop(HSSFCellStyle.BORDER_THIN);
     style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
     //生成一个字体
     HSSFFont font = workbook.createFont();
     font.setColor(HSSFColor.VIOLET.index);
     font.setFontHeightInPoints((short)12);
     font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
     //把字体应用到当前的样式
     style.setFont(font);
  }	
	
  /**
   * 设置普通样式
   * @param workbook
   */
  public void setNormalTabStyle(HSSFWorkbook workbook){
	 //生成一个样式
     style = workbook.createCellStyle();
     //设置这些样式
     style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
     style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
     style.setBorderRight(HSSFCellStyle.BORDER_THIN);
     style.setBorderTop(HSSFCellStyle.BORDER_THIN);
     style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
     //生成一个字体
     HSSFFont font = workbook.createFont();
     font.setFontHeightInPoints((short)10);
     //把字体应用到当前的样式
     style.setFont(font);
  }	
 
  
   /**
    * 增加一行
    * 
    * @param index行号
    */
  public void createRow(int index){
       this.row = this.sheet.createRow(index);
  }

   /**
    * 设置单元格
    *
    * @param index 列号
    * @param value 单元格填充值
    */
    public void setCell(int index,String value){
      HSSFCell cell = this.row.createCell((short) index);
      cell.setCellType(HSSFCell.CELL_TYPE_STRING);
      cell.setCellValue(value);
      cell.setCellStyle(style);
  }

    /**
     * 设置单元格
     *
     * @param index 列号
     * @param value 单元格填充值
     */
    public void setCell(int index,Date value){
      HSSFCell cell = this.row.createCell((short) index);
      SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT);
      cell.setCellType(HSSFCell.CELL_TYPE_STRING);
      cell.setCellValue(format.format(value));
      cell.setCellStyle(style);
   }

    /**
     * 设置单元格
     *
     * @param index 列号
     * @param value 单元格填充值
     */
    public void setCell(int index,int value){
      HSSFCell cell = this.row.createCell((short) index);
      cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
      cell.setCellValue(value);
      cell.setCellStyle(style);
    }


    /**
     * 设置单元格
     *
     * @param index 列号
     * @param value 单元格填充值
     */
    public void setCell(int index,double value){
      HSSFCell cell = this.row.createCell((short) index);
      cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
      cell.setCellValue(value);
      HSSFDataFormat format = workbook.createDataFormat();
      style.setDataFormat(format.getFormat(NUMBER_FORMAT));  //  设置cell样式为定制的浮点数格式
      cell.setCellStyle(style);  //  设置该cell浮点数的显示格式
    }
    
    
	/**
	 * 根据方法名设置符合JAVABEAN的GET方法
	 * 
	 * @param method
	 * @return
	 */
	public String toGetMethodString(String method){
		return "get" + ExcelManager.firstLetterToUpper(method);
	}
	
	/**
	 * 首字母转化成大写
	 * 
	 * @param str
	 * @return
	 */
	public static String firstLetterToUpper(String str){ 
		if(str == null || "".equals(str)){
			return "";
		}else{
		  char[] array = str.toCharArray();  
		  array[0] -= 32;  
		  return String.valueOf(array);
		}
	} 
}
