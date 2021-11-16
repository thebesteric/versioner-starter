package io.github.thebesteric.framework.versioner.test;

import io.github.thebesteric.framework.versioner.annotation.EnableVersioner;
import io.github.thebesteric.framework.versioner.annotation.Version;
import io.github.thebesteric.framework.versioner.annotation.Versioner;
import io.github.thebesteric.framework.versioner.annotation.Versions;
import io.github.thebesteric.framework.versioner.core.VersionContext;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
            user.setGender(1);
            List<Hobby> hobbies = new ArrayList<>();
            hobbies.add(new Hobby("swimming", "sport", 1));
            hobbies.add(new Hobby("football", "sport", 2));
            user.setHobbies(hobbies);
            List<String> alias = new ArrayList<>();
            alias.add("test1");
            alias.add("test2");
            user.setAlias(alias);
            Map<String, School> domains = new HashMap<>();
            domains.put("s1", new School());
            domains.put("s2", new School());
            user.setDomains(domains);
            System.out.println("Version = " + VersionContext.getVersion());
            System.out.println("AppVersion = " + VersionContext.getAppVersion());
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
            List<Hobby> hobbies = new ArrayList<>();
            hobbies.add(new Hobby("swimming", "sport", 1));
            hobbies.add(new Hobby("football", "sport", 2));
            user.setHobbies(hobbies);
            List<String> alias = new ArrayList<>();
            alias.add("test1");
            alias.add("test2");
            user.setAlias(alias);
            Map<String, School> domains = new HashMap<>();
            domains.put("s1", new School());
            domains.put("s2", new School());
            user.setDomains(domains);
            System.out.println("Version = " + VersionContext.getVersion());
            System.out.println("AppVersion = " + VersionContext.getAppVersion());
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
            List<Hobby> hobbies = new ArrayList<>();
            hobbies.add(new Hobby("swimming", "sport", 1));
            hobbies.add(new Hobby("football", "sport", 2));
            user.setHobbies(hobbies);
            List<String> alias = new ArrayList<>();
            alias.add("test1");
            alias.add("test2");
            user.setAlias(alias);
            Map<String, School> domains = new HashMap<>();
            domains.put("s1", new School());
            domains.put("s2", new School());
            user.setDomains(domains);
            System.out.println("Version = " + VersionContext.getVersion());
            System.out.println("AppVersion = " + VersionContext.getAppVersion());
            return user;
        }

        @GetMapping("/r")
        public R r() {
            User user = new User();
            user.setUsername("test");
            user.setPassword("123");
            user.setAge(12);
            user.setGender(1);
            List<Hobby> hobbies = new ArrayList<>();
            hobbies.add(new Hobby("swimming", "sport", 1));
            hobbies.add(new Hobby("football", "sport", 2));
            user.setHobbies(hobbies);
            List<String> alias = new ArrayList<>();
            alias.add("test1");
            alias.add("test2");
            user.setAlias(alias);
            Map<String, School> domains = new HashMap<>();
            domains.put("s1", new School());
            domains.put("s2", new School());
            user.setDomains(domains);
            System.out.println("Version = " + VersionContext.getVersion());
            System.out.println("AppVersion = " + VersionContext.getAppVersion());
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
            List<Hobby> hobbies = new ArrayList<>();
            hobbies.add(new Hobby("swimming", "sport", 1));
            hobbies.add(new Hobby("football", "sport", 2));
            user.setHobbies(hobbies);
            List<String> alias = new ArrayList<>();
            alias.add("test1");
            alias.add("test2");
            user.setAlias(alias);
            Map<String, String> map = new HashMap<>();
            map.put("test1", "1");
            map.put("test2", "2");
            user.setMap(map);
            Map<String, School> domains = new HashMap<>();
            domains.put("s1", new School());
            domains.put("s2", new School());
            user.setDomains(domains);
            System.out.println("Version = " + VersionContext.getVersion());
            System.out.println("AppVersion = " + VersionContext.getAppVersion());
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
            List<Hobby> hobbies = new ArrayList<>();
            hobbies.add(new Hobby("swimming", "sport", 1));
            hobbies.add(new Hobby("football", "sport", 2));
            user.setHobbies(hobbies);
            List<String> alias = new ArrayList<>();
            alias.add("test1");
            alias.add("test2");
            user.setAlias(alias);
            Map<String, String> map = new HashMap<>();
            map.put("test1", "1");
            map.put("test2", "2");
            user.setMap(map);
            Map<String, School> domains = new HashMap<>();
            domains.put("s1", new School());
            domains.put("s2", new School());
            user.setDomains(domains);
            System.out.println("Version = " + VersionContext.getVersion());
            System.out.println("AppVersion = " + VersionContext.getAppVersion());
            return R.success(user);
        }

        @Autowired
        private UserService userService;

        @GetMapping("/testService1")
        public User testService1() {
            return userService.getUser();
        }

        @GetMapping("/testService2")
        public User testService2() {
            return userService.getUser();
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
        private List<Hobby> hobbies = new ArrayList<>();
        @Version({"v1"})
        private List<String> alias = new ArrayList<>();
        @Version({"v2"})
        private Map<String, String> map = new HashMap<>();
        private Map<String, School> domains = new HashMap<>();
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

    @Data
    @Versions
    public static class Hobby {
        private String name;
        @Version("v1")
        private String type;
        @Version("v2")
        private Integer level;

        @Version("v1")
        private School school = new School();

        public Hobby(String name, String type, Integer level){
            this.name = name;
            this.type = type;
            this.level = level;
        }
    }
}
