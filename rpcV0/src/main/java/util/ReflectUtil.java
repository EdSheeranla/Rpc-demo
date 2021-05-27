package util;

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

public class ReflectUtil {
    public static String getStackTrace(){
        StackTraceElement[] stack = new Throwable().getStackTrace();
        return stack[stack.length-1].getClassName();
    }

    public static Set<Class<?>> getClasses(String packageName) {
        Set<Class<?>> classes = new LinkedHashSet<>();
        boolean recursive = true;
        String packageDirName = packageName.replace('.','/');
        Enumeration<URL> dirs;
        try {
            dirs = Thread.currentThread().getContextClassLoader().getResources(packageName);
            while(dirs.hasMoreElements()){
                URL url = dirs.nextElement();

                String protocol = url.getProtocol();
                //如果以文件的形式保存在服务器中
                if ("file".equals(protocol)){
                    String filePath = URLDecoder.decode(url.getFile(),"UTF-8");
                    findAndAddClassesInPackageFile(packageName,filePath,recursive,classes);
                }else if ("jar".equals(protocol)){
                    JarFile jar;
                    jar = ((JarURLConnection)url.openConnection()).getJarFile();
                    Enumeration<JarEntry> entries = jar.entries();

                    while(entries.hasMoreElements()){
                        JarEntry entry = entries.nextElement();
                        String name = entry.getName();
                        if (name.charAt(0) == '/'){
                            name = name.substring(1);
                        }
                        if (name.startsWith(packageDirName)){
                            int idx = name.lastIndexOf('/');
                            if (idx!=-1){
                                packageName = name.substring(0,idx).replace('/','.');
                            }
                            if( (idx!=-1) || recursive) {
                                //如果是一个.class文件 且不是目录
                                if (name.endsWith(".class")&& !entry.isDirectory()){
                                    String className = name.substring(packageName.length()+1,name.length()-6);
                                    try {
                                        classes.add(Thread.currentThread().getContextClassLoader().loadClass(packageName+'.'+className));

                                    } catch (ClassNotFoundException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return classes;
    }

    private static void findAndAddClassesInPackageFile(String packageName,String packagePath,final boolean recursive,Set<Class<?>> classes){

        File dir = new File(packagePath);

        if(!dir.exists() || !dir.isDirectory()){
            return;
        }
        File[] dirFiles = dir.listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                return (recursive && file.isDirectory()) || (file.getName().endsWith(".class"));
            }
        });
        for (File file:dirFiles){

            if (file.isDirectory()){
                findAndAddClassesInPackageFile(packageName+"."+file.getName(),file.getAbsolutePath(),recursive,classes);
            }else{
                String className = file.getName().substring(0,file.getName().length()-6);
                try {
                    classes.add(Thread.currentThread().getContextClassLoader().loadClass(packageName+"."+className));
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}