package com.world.model.entity.news;

public class LetterUtil {

	public static String[][] months = {
		{"1", "January"},
		{"2", "February"},
		{"3", "March"},
		{"4", "April"},
		{"5", "May"},
		{"6", "June"},
		{"7", "July"},
		{"8", "August"},
		{"9", "September"},
		{"10", "October"},
		{"11", "November"},
		{"12", "December"}
	};
	
	public static String[] getMonth(int index){
    	if(index>=0){
   		    return months[index];
    	}else{
    		return null;
    	}
	}

	public static String getMonthPre(int index){
		if(index>=0){
			return months[index][1].substring(0, 3);
		}else{
			return null;
		}
	}
	
}
