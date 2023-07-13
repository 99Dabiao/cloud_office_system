package com.office.auth;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @ClassName Test
 * @Description TODO
 * @Author Dabiao
 * @Date 2023/3/24 20:48
 * @Version 1.0
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class SpringTest {

    @Test
    public void password(){
        String passwordPre = "123456";
        String passwordBe = new BCryptPasswordEncoder().encode(passwordPre);
        System.out.println( new BCryptPasswordEncoder().matches(passwordPre,passwordBe));
    }



}
