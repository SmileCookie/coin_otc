package com.world.data.mysql;
/**
 * 功能:字段类型
 * @author 凌霄
 *
 */
public enum FeildType {
TInt,//java java.lang.Intege
TDOUBLE,//java.lang.Double
TBIT, //参数1 java.lang.Boolean    参数大于1 byte[]
TMEDIUMINT,//java java.lang.Intege 表现层上使用下拉框来展示
TBigInt,//java.lang.Long
TFloat,//java.lang.Float
TDateTime,//java.sql.Timestamp
TDate,//java.sql.Date
TTimeStamp,//java.sql.Timestamp
TTime,//java.sql.Time
TVarchar,//java.lang.String 
TText,//java.lang.String 
TCharacter//java.lang.String 
}
