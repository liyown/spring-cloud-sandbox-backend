package com.lyw.springcloudstarter;

import com.lyw.springcloudstarter.common.MySecurityMananger;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

//@SpringBootTest
class SpringCloudStarterApplicationTests {

    @Test
    void contextLoads() throws IOException {
//        System.setSecurityManager(new MySecurityMananger());

        FileInputStream fileInputStream = new FileInputStream("D:\\github\\java\\spring-cloud-sandbox\\src\\main\\resources\\security-manager\\MySecurityMananger.class");
        byte[] bytes = new byte[1024];
        int read = fileInputStream.read(bytes);
        System.out.println(new String(bytes, 0, read, "UTF-8"));

    }

}
