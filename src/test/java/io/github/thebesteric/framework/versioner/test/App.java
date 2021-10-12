package io.github.thebesteric.framework.versioner.test;

import io.github.thebesteric.framework.versioner.annotation.EnableVersioner;
import io.github.thebesteric.framework.versioner.annotation.Version;
import io.github.thebesteric.framework.versioner.annotation.Versioner;
import io.github.thebesteric.framework.versioner.annotation.Versions;
import lombok.Data;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@EnableVersioner
public class App {
    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

    @RestController
    public static class TestController {

        @GetMapping("/test")
        public User test() {
            User user = new User();
            user.setUsername("test");
            user.setPassword("123");
            user.setAge(12);
            return user;
        }

        @Versioner("v1")
        @GetMapping("/test/v1")
        public User testV1() {
            User user = new User();
            user.setUsername("test_v1");
            user.setPassword("123");
            user.setAge(12);
            user.setGender(1);
            return user;
        }

        @Versioner("v2")
        @GetMapping("/test/v2")
        public User testV2() {
            User user = new User();
            user.setUsername("test_v2");
            user.setPassword("123");
            user.setAge(12);
            user.setGender(1);
            return user;
        }

        @GetMapping("/r")
        public R r() {
            User user = new User();
            user.setUsername("test");
            user.setPassword("123");
            user.setAge(12);
            user.setGender(1);
            return R.success(user);
        }

        @Versioner(value = "v1", key = "data", type = User.class)
        @GetMapping("/r/v1")
        public R rV1() {
            User user = new User();
            user.setUsername("test_r_v1");
            user.setPassword("123");
            user.setAge(12);
            user.setGender(1);
            return R.success(user);
        }

        @Versioner(value = "v2", key = "data", type = User.class)
        @GetMapping("/r/v2")
        public R rV2() {
            User user = new User();
            user.setUsername("test_r_v2");
            user.setPassword("123");
            user.setAge(12);
            user.setGender(1);
            return R.success(user);
        }

    }

    @Data
    @Versions
    public static class User {
        private String username;
        @Version("v1")
        private String password;
        @Version("v2")
        private Integer age;
        @Version({"v1", "v2"})
        private Integer gender;
        private Address address = new Address();
    }

    @Data
    @Versions
    public static class Address {
        private String no = "999";
        @Version("v1")
        private String city = "Hefei";
        @Version("v2")
        private String province = "Anhui";
        private School school = new School();
    }

    @Data
    @Versions
    public static class School {
        private String grade = "1";
        @Version("v1")
        private String teacher = "lucy";
        @Version("v2")
        private String master = "eric";
    }
}
