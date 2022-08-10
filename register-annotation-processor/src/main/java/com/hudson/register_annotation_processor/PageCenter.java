package com.hudson.register_annotation_processor;

import java.util.HashMap;
import java.util.Map;

/**
 * 要生成的类的伪代码
 * Created by Hudson on 2022/8/10.
 */
class PageCenter {
   Map<Class, String> getPages(){
      Map<Class, String> pages = new HashMap<>();
      pages.put(Object.class, "");

      return pages;
   }
}
