package com.cg.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented @Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface CgProperty {

    /** The name of the key to store the field in; Defaults to the field name. */
    String des() default "";
    boolean search() default false;//是否用于搜索
    boolean list() default false;//是否用于列表
}
