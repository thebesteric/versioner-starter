package io.github.thebesteric.framework.versioner.test;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class AppTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    public void test() {
        App.TestController testController = applicationContext.getBean(App.TestController.class);
        // System.out.println(testController.test());
        // System.out.println(testController.testV1());
        // System.out.println(testController.testV2());
    }
}
