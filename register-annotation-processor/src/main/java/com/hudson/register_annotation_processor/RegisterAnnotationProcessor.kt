package com.hudson.register_annotation_processor

import com.google.auto.service.AutoService
import com.hudson.register.annotation.SubPage
import com.squareup.javapoet.*
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.Modifier
import javax.lang.model.element.TypeElement
import javax.lang.model.util.Elements
import javax.lang.model.util.Types
import javax.tools.Diagnostic

/**
 * Created by Hudson on 2022/8/10.
 */
@AutoService(Processor::class)
@SupportedAnnotationTypes("com.hudson.register.annotation.SubPage")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
class RegisterAnnotationProcessor: AbstractProcessor() {
    private var elements: Elements? = null
    private var types: Types? = null
    private var messager: Messager? = null
    private var filer: Filer? = null

    private val pages = mutableListOf<PageInfo>()

    companion object{
        const val RegisterPkg = "com.hudson.apt.CustomViewSetPageRegister"
    }


    override fun init(processingEnv: ProcessingEnvironment?) {
        super.init(processingEnv)
        elements = processingEnv?.elementUtils
        types = processingEnv?.typeUtils
        messager = processingEnv?.messager
        filer = processingEnv?.filer
    }

    /**
     * 编写生成 Java 类的相关逻辑
     *
     * @param annotations              支持处理的注解集合
     * @param roundEnv 通过该对象查找指定注解下的节点信息
     * @return true: 表示注解已处理，后续注解处理器无需再处理它们；false: 表示注解未处理，可能要求后续注解处理器处理
     */
    override fun process(
        annotations: MutableSet<out TypeElement>?,
        roundEnv: RoundEnvironment?
    ): Boolean {
        messager?.printMessage(Diagnostic.Kind.NOTE, "开始处理了哈")
        if(annotations.isNullOrEmpty()) return false
        // 注意：上面的annotations是指支持的注解的集合，这里就是SubPage一个
        // 我们需要拿到对应被注解的元素，即类，方法，属性等
        // 处理注解
        val elements = roundEnv?.getElementsAnnotatedWith(SubPage::class.java) ?: emptySet()
        for(element in elements){
            if(element is TypeElement){
                // 我们只支持类上的注解
                // 收集类信息（通过javaPoet实现）
                val pageClazz = ClassName.get(element)
                // 拿到注解信息
                val annotation = element.getAnnotation(SubPage::class.java)

                messager?.printMessage(Diagnostic.Kind.NOTE, "类${pageClazz} 描述${annotation}")
                pages.add(PageInfo(pageClazz, annotation.desc))
            }
        }

        // 处理生成代码
        processGenFile()
        return true
    }

    /**
     * class PageCenter {
            Map<Class, String> getPages(){
                Map<Class, String> pages = new HashMap<>();
                pages.put(Object.class, "");

                return pages;
            }
        }
     */
    private fun processGenFile(){
        if(pages.isNotEmpty()){
            // 返回值 Map<Class, String>
            val methodReturn = ParameterizedTypeName.get(
                ClassName.get(MutableMap::class.java),
                ClassName.get(Class::class.java),
                ClassName.get(String::class.java)
            )
            // 方法签名等信息
            val methodSpecBuilder = MethodSpec.methodBuilder("getPages")
                .addModifiers(Modifier.PUBLIC)
                .returns(methodReturn)

            // 编写方法体
            // Map<Class, String> pages = new HashMap<>();
            methodSpecBuilder.addStatement(
                "\$T<\$T,\$T> \$N = new \$T<>()",
                ClassName.get(MutableMap::class.java),
                ClassName.get(Class::class.java),
                ClassName.get(String::class.java),
                "pages",
                ClassName.get(HashMap::class.java)
            )

            // pages.put(Object.class, "");
            pages.forEach {
                methodSpecBuilder.addStatement(
                    "\$N.put(\$T.class, \$S)",
                    "pages",
                    it.pageClazz,
                    it.desc
                )
            }

            // return pages;
            methodSpecBuilder.addStatement("return \$N", "pages")


            // 生成代码文件
            // HRouter_{Group}_Path_Repo
            val finalClassName = "CustomViewSetPageRegister"

            JavaFile.builder(
                "com.hudson.apt",
                TypeSpec.classBuilder(finalClassName)
                    .addModifiers(Modifier.PUBLIC)
                    .addMethod(methodSpecBuilder.build())
                    .build())
                .build()
                .writeTo(filer)
        }
    }
}