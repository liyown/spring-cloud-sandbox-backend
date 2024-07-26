package com.lyw.springcloudstarter.utils;

import cn.hutool.core.io.FileUtil;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.util.Objects;

/**
 * @author: liuyaowen
 * @poject: spring-cloud-sandbox
 * @create: 2024-07-25 22:05
 * @Description:
 */
@Slf4j
public class JavaCompilerUtils {

    static String templateDir = "templates";

    public static void main(String[] args) throws ClassNotFoundException, MalformedURLException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {

        String sourceCode = "public class Main {\n" +
                "    public static void main(String[] args) {\n" +
                "        dSystem.out.println(\"hello world\");\n" +
                "    }\n" +
                "}";
        CompilerResult<InvokeContext> compilerResult = compile(sourceCode);
        System.out.println(compilerResult.getData().process(new String[0]));
    }

    /**
     * 编译
     *
     * @param sourceCode 源码
     * @return Wrapper of the compiler result
     *  if success, return the run Command else return the compile message
     */
    public static CompilerResult<String> compiler(String sourceCode) {
        // 写入文件
        String classFilePath = rewriteToTemplate(sourceCode);

        // 编译
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        int result;
        result = compiler.run(null, null, byteArrayOutputStream, classFilePath);
        FileUtil.del(classFilePath);
        if (result == 0) {
            return new CompilerResult<>(true, "java -cp " + classFilePath.substring(0, classFilePath.lastIndexOf(File.separator)) + " Main ", null);

        } else {
            return new CompilerResult<>(false, null, byteArrayOutputStream.toString());
        }
    }


    /**
     * 编译
     *
     * @param sourceCode 源码
     * @return Wrapper of the invoke context
     */
    static public CompilerResult<InvokeContext> compile(String sourceCode) throws ClassNotFoundException, InvocationTargetException, InstantiationException, NoSuchMethodException, IllegalAccessException {
        // 写入文件
        String filePath = rewriteToTemplate(sourceCode);
        String path = filePath.substring(0, filePath.lastIndexOf(File.separator));
        // 获取编译器
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        // 编译
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        int result = 0;
        result = compiler.run(null, null, byteArrayOutputStream, filePath);
        if (result == 0) {
            // 加载类
            Class<?> main = new MyClassLoader(path).loadClass("Main");
            // 获取构造器
            Constructor<?> constructors = main.getConstructor();
            // 实例化
            Object o = constructors.newInstance();
            // 获取方法
            Method method = main.getMethod("main", String[].class);
            // 删除文件
            FileUtil.del(filePath);
            return new CompilerResult<>(true, new InvokeContext(main, o, method), null);

        } else {
            FileUtil.del(filePath);

            new CompilerResult<>(false, null, byteArrayOutputStream.toString());

            throw new RuntimeException("compile error");
        }
    }


    private static String rewriteToTemplate(String sourceCode) {
        String path = Objects.requireNonNull(JavaCompilerUtils.class.getClassLoader().getResource(templateDir)).getPath();
        String subPath = String.valueOf(System.currentTimeMillis());
        File mkdir = FileUtil.mkdir(path + File.separator + subPath);
        String filePath = mkdir.getPath() + File.separator + "Main.java";
        FileUtil.writeBytes(sourceCode.getBytes(), filePath);
        return filePath;

    }

    public static class CompilerResult<T> {
        private final Boolean success;
        @Getter
        private final T data;
        @Getter
        private final String compileMessage;


        private CompilerResult(Boolean success, T data, String compileMessage) {
            this.success = success;
            this.data = data;
            this.compileMessage = compileMessage;
        }

        public boolean isSuccess() {
            return success;
        }

    }

    public static class InvokeContext {
        private final Object object;
        private final Method method;
        private final Class<?> clazz;

        public InvokeContext(Class<?> clazz, Object object, Method method) {
            this.object = object;
            this.method = method;
            this.clazz = clazz;
        }

        public Object process(Object args) throws InvocationTargetException, IllegalAccessException {
            return method.invoke(object, args);
        }

    }

    static class MyClassLoader extends ClassLoader {
        private String classPath;

        public MyClassLoader(String classPath) {
            // 关闭双亲委派
            super(null);
            this.classPath = parserPath(classPath);
        }

        private String parserPath(String path) {
            if (path == null) {
                throw new IllegalArgumentException("classPath can not be null");
            }
            if (!path.endsWith(File.separator)) {
                path = path + File.separator;
            }
            return path;
        }

        private byte[] loadByte(String name) throws Exception {

            String path = classPath + name + ".class";
            System.out.println(path);
            return FileUtil.readBytes(path);
        }

        @Override
        protected Class<?> findClass(String name) throws ClassNotFoundException {
            try {
                byte[] data = loadByte(name);
                return defineClass(name, data, 0, data.length);
            } catch (Exception e) {
                throw new ClassNotFoundException(name);
            }
        }
    }
}
