package io.github.thebesteric.framework.versioner.test;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class UserService {

    public App.User getUser() {
        App.User user = new App.User();
        user.setUsername("test_service");
        user.setPassword("123");
        user.setAge(12);
        user.setGender(1);
        List<App.Hobby> hobbies = new ArrayList<>();
        hobbies.add(new App.Hobby("swimming", "sport", 1));
        hobbies.add(new App.Hobby("football", "sport", 2));
        user.setHobbies(hobbies);
        List<String> alias = new ArrayList<>();
        alias.add("test1");
        alias.add("test2");
        alias.add("test3");
        alias.add("test4");
        user.setAlias(alias);
        Map<String, String> map = new HashMap<>();
        map.put("test1", "1");
        map.put("test2", "2");
        user.setMap(map);
        Map<String, App.School> domains = new HashMap<>();
        domains.put("s1", new App.School());
        domains.put("s2", new App.School());
        user.setDomains(domains);

        // VersionHelper.excludes("gender", "age", "address.city");
        // VersionHelper.excludes("hobbies.type", "hobbies.school.teacher", "hobbies[0].name", "hobbies[0].school.master", "alias[0]");
        // VersionHelper.excludes("domains.s1.grade", "domains.s2.master", "map.test2");
        //
        // VersionHelper.excludesWithUri("/testService2", "gender", "age", "address.city");

        return user;
    }

}
