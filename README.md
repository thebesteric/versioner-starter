### Quick Start

- [Download By Maven Center](https://search.maven.org/search?q=g:io.github.thebesteric.framework.versioner)
### 配置
```yaml
sourceflag:
  versioner:
    enable: true # 是否启用
    app-version-name: x-app-version # 指定请求头或请求参数中的版本 key，后面可以从 VersionContext 中获取
```
### 注解的使用
- `@EnableVersioner`: 开启版本控制
- `@Versioner`: 作用于 Controller 层的请求方法上
- `@Versions`: 作用于 Domain 上，标记该 Domain 为一个版本控制类
- `@Version`: 作用于 Domain 的字段上，标记改字段对应的版本

PS: 如果返回值为通用类型，则需要指定返回值包装数据的 key，以及 key 所返回的类型
> @Versioner(value = "v1", key = "data", type = User.class)
```java
@RestController
public class UserController {
    @Versioner(value = "v1", key = "data", type = User.class)
    @GetMapping("/r/v1")
    public R getUserV1(String id) {
        User user = userService.getUser(id);
        return R.success(user);
    }
}
```

### Example

#### Controller
```java
@RestController
public static class TestController {
    @Versioner("v1")
    @GetMapping("/test/v1")
    public User testV1(String id) {
        return userService.getUser(id);
    }

    @Versioner("v2")
    @GetMapping("/test/v2")
    public User testV2(String id) {
        return userService.getUser(id);
    }
}
```

#### Domain Design
```java
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
    private Address address;
    private List<Hobby> hobbies;
    @Version({"v1"})
    private List<String> alias;
    @Version({"v2"})
    private Map<String, School> domains = new HashMap<>();
}

@Data
@Versions
public class Address {
    private String no = "999";
    @Version("v2")
    private String province = "Anhui";
    @Version("v1")
    private String city = "Hefei";
    private School school = new School();
}

@Data
@Versions
public class School {
    private String grade = "1";
    @Version("v1")
    private String teacher = "lucy";
    @Version("v2")
    private String master = "eric";
}

@Data
@Versions
public class Hobby {
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
```

#### Response: /test/v1 
```json
{
    "username": "test_v1",
    "password": "123",
    "gender": 1,
    "address": {
        "no": "999",
        "city": "Hefei",
        "school": {
            "grade": "1",
            "teacher": "lucy"
        }
    },
    "hobbies": [
        {
            "name": "swimming",
            "type": "sport",
            "school": {
                "grade": "1",
                "teacher": "lucy"
            }
        },
        {
            "name": "football",
            "type": "sport",
            "school": {
                "grade": "1",
                "teacher": "lucy"
            }
        }
    ],
    "alias": [
        "test1",
        "test2"
    ],
    "domains": {
        "s1": {
            "grade": "1",
            "teacher": "lucy"
        },
        "s2": {
            "grade": "1",
            "teacher": "lucy"
        }
    }
}
```

#### Response: /test/v2
```json
{
    "username": "test_v2",
    "age": 12,
    "gender": 1,
    "address": {
        "no": "999",
        "province": "Anhui",
        "school": {
            "grade": "1",
            "master": "eric"
        }
    },
    "hobbies": [
        {
            "name": "swimming",
            "level": 1
        },
        {
            "name": "football",
            "level": 2
        }
    ],
    "domains": {
        "s1": {
            "grade": "1",
            "master": "eric"
        },
        "s2": {
            "grade": "1",
            "master": "eric"
        }
    }
}
```

### VersionHelper
> 直接使用 VersionHelper 类，可以在代码内部完成版本控制  
> `VersionHelper.excludes("xxx", "yyy");` 针对所有请求进行版本控制  
> `VersionHelper.excludesWithUri("/xxx/xxx", "xxx", "yyy");` 针对所有指定请求进行版本控制

```java
@Service
public class UserService {
    public User getUser(String id) {
        // something to do
        
        // 所有请均会屏蔽的字段 
        VersionHelper.excludes("gender", "age", "address.city");
        VersionHelper.excludes("hobbies.type", "hobbies.school.teacher", "hobbies[0].name", "hobbies[0].school.master", "alias[0]");
        VersionHelper.excludes("domains.s1.grade", "domains.s2.master", "map.test2");
        
        // 针对 uri 屏蔽的字段
        VersionHelper.excludesWithUri("/test/v1", "gender", "age", "address.city");
        VersionHelper.excludesWithUri("/test/v2", "gender", "age");
        return user;
    }
}
```

### VersionContext
> 直接使用 VersionContext 类，可以获取当前版本信息，URI、客户端版本信息  
> `VersionContext.getVersion()` 获取当前方法版本  
> `VersionContext.getUri()` 获取当前访问 URI  
> `VersionContext.getUri()` 获取当前客户端版本
