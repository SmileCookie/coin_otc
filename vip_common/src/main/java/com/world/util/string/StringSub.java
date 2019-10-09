/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.world.util.string;

import org.apache.log4j.Logger;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 *
 * @author Administrator
 */
public class StringSub {

    private final static Logger log = Logger.getLogger(StringSub.class);

    public static int getStrCharNum(String sourceStr){
        int charNum=0;
        for(int i=0;i<sourceStr.length();i++){
            char cc=sourceStr.charAt(i);
            if(cc>=0&&cc<255){
                charNum+=1;
            }else{
                charNum+=2;
            }
        }
        return charNum;
    }

    /***
     * 
     * @param smallSubLength
     * @param sourceStr
     * @return
     */
    public static String getLengthStrSub(int smallSubLength, String sourceStr) {
    	
        int charNum = 0;
        int subLength = smallSubLength;
        int totLength = sourceStr.length();
        int requestLength=smallSubLength>=totLength?totLength:smallSubLength;
        for (int i = 0; i < requestLength; i++) {
            char cc = sourceStr.charAt(i);
            if (cc >= 0 && cc <= 255) {
                charNum += 1;
            } else {
                charNum += 2;
                if (subLength > smallSubLength / 2&&charNum<=smallSubLength) {
                    subLength -= 1;
                }
            }
        }
        if (charNum > smallSubLength) {
            return sourceStr.substring(0,subLength-1)+"...";
        } else {
        	if(smallSubLength<totLength){
        		return sourceStr.substring(0,smallSubLength-1);
        	}else{
        		return sourceStr;
        	}
        }
    }
    /**
     * 功能：返回替换后的引号，将英文引号换成中文引号，不然显示中会出现错乱
     * @param oldStr
     * @return
     */
    public static String ReplaceYinhao(String oldStr){
    	while(oldStr.indexOf("\"")>-1)
    	{
    		oldStr=oldStr.replaceAll("\"", "“");
    		if(oldStr.indexOf("\"")>-1)
    			oldStr=oldStr.replaceAll("\"", "”");
    	}
    	return oldStr;
    }
    
    /***
     * 返回标题集合
     * @param smallSubLength
     * @param sourceStr
     * @return
     */
    public static TitleBean getTitleBean(int smallSubLength, String sourceStr) { 
    	//这行是新增加的 ，不然前台显示有双引号，在ie下面会出现严重的变形问题
    	sourceStr=ReplaceYinhao(sourceStr);
        int charNum = 0;
        int subLength = smallSubLength;
        int totLength = sourceStr.length();
        int requestLength=smallSubLength>=totLength?totLength:smallSubLength;
        String shortTitle="";
        boolean isSame=false;
        for (int i = 0; i < requestLength; i++) {
            char cc = sourceStr.charAt(i);
            if (cc >= 0 && cc <= 255 && cc!=183) {////183  符号·[183]占两个字节
                charNum += 1;
            } else {
                charNum += 2;
                if (subLength > smallSubLength / 2&&charNum<=smallSubLength) {
                    subLength -= 1;
                }
            }
        }
        //shortTitle= WebUtil.HtmltoText(sourceStr.substring(0,subLength-1), true)+"…";//过滤空格
        if (charNum > smallSubLength) {
        	//shortTitle= sourceStr.substring(0,subLength-1)+"…";
        	shortTitle= sourceStr.substring(0,subLength-1).replace(" ", "")+"…";//过滤空格
        } else {
        	if(smallSubLength<totLength){
        		shortTitle= sourceStr.substring(0,smallSubLength-1);
        		isSame=false;
        	}else{
        		shortTitle= sourceStr;
        		isSame=true;
        	}
        }
        return new TitleBean(shortTitle,sourceStr,isSame);
    }
    
    public static String replaceBlank(String str) {
        String dest = "";
        if (str!=null) {
            Pattern p = Pattern.compile("\\s*|\t|\r|\n");
            Matcher m = p.matcher(str);
        dest = m.replaceAll("");
        }
        return dest;
    }

    public static void main(String[] args){
        String testStr="sbsbsb王可坡垃圾发生的拉升房价绝对实力肯定是0";
        String testStr2="1111上课1111111111111111111111111111";
        log.info("String length："+testStr.length());
        log.info("char length:"+getStrCharNum(testStr));
        char a='·';
        log.info("a:"+(int)a);

        //log.info("输出："+testStr2.substring(0, getStrSubLength(12, testStr2)));
    }
}
