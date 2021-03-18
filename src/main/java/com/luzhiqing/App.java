package com.luzhiqing;

import com.luzhiqing.service.CycleA;
import com.luzhiqing.service.impl.CycleAImpl;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cglib.core.DebuggingClassWriter;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import sun.misc.ProxyGenerator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author: luzhiqing
 * @date: 2021/3/18
 * @version:
 */
@SpringBootApplication(scanBasePackages = "com.luzhiqing")
//@EnableAsync
@EnableTransactionManagement
public class App {
    public static void main(String[] args) {
        System.setProperty("sun.misc.ProxyGenerator.saveGeneratedFiles", "true");
        //System.setProperty(DebuggingClassWriter.DEBUG_LOCATION_PROPERTY, "D:\\ccb\\bab\\aync-cycle");
        ConfigurableApplicationContext context = SpringApplication.run(App.class, args);
        CycleA bean = context.getBean(CycleA.class);
        Class<? extends CycleA> aClass = bean.getClass();
        testProxyGenetate(aClass.getSimpleName());
    }

    public static void testProxyGenetate(String clazz) {
        byte[] newProxyClass = ProxyGenerator.generateProxyClass(clazz, CycleAImpl.class.getInterfaces());
        System.out.println(newProxyClass);
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(new File("D:\\ccb\\bab\\test\\"+clazz+".class"));
            try {
                fileOutputStream.write(newProxyClass);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (fileOutputStream != null) {
                    try {
                        fileOutputStream.flush();
                        fileOutputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

}
