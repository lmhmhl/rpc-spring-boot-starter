# rpc-spring-boot-starter
利用Netty实现的rpc框架。
* 改进功能点：
（1）除了 Java 序列化协议，增加了 Protobuf 和 Kryo 序列化协议，配置即用。 
（2）增加多种负载均衡算法（随机、轮询、加权轮询、平滑加权轮询），配置即用。 
（3）客户端增加本地服务列表缓存，提高性能。 
（4）修复高并发情况下，Netty导致的内存泄漏问题.
（5）由原来的每个请求建立一次连接，改为建立 TCP 长连接，并多次复用。 
（6）服务端增加线程池提高消息处理能力.
# 使用方法
1. 下载代码到本地
```bash
git clone https://github.com/lmhmhl/rpc-spring-boot-starter
```
2. 打包到本地
```bash
mvn  clean install -DskipTests=true
```
3. 添加maven依赖到你的项目中
 ```xml
   <dependency>
            <groupId>cn.lmh.rpc</groupId>
            <artifactId>rpc-spring-boot-starter</artifactId>
            <version>1.0.0-SNAPSHOT</version>
        </dependency>
 ```
 ## 客户端
 使用@InjectService注解注入远程方法
 ```java
 @RestController
@RequestMapping("test")
public class TestController {

    @InjectService
    private UserService userService;

    @GetMapping("/user")
    public ApiResult<User> getUser(@RequestParam("id")Long id){
        return userService.getUser(id);
    }
}
 ```
配置项：
|    属性 |含义      |  可选项   |
| --- | --- | --- |
|   lmh.rpc.protocol  | 消息序列化协议        |  java，protobuf，kryo   |
|    lmh.rpc.register-address |  注册中心地址      |  默认localhost:2181   |
|    lmh.rpc.load-balance |  负载均衡算法     | random<br>round<br>weightRound<br>smoothWeightRound|


 ## 服务端
 提供远程方法并注入IOC
 ```java
 import Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class UserServiceImpl implements UserService{

    private static  Logger logger = LoggerFactory.getLogger(UserService.class);


    @Override
    public ApiResult<User> getUser(Long id) {
        logger.info("现在是【2】号提供服务");
        User user = new User(1L,"XX",2,"www.aa.com");
        return ApiResult.success(user);
    }
}
 ```
 **注意：** 这里的@Service注解不是Spring的。
 
 配置项：
|    属性 |含义      |  可选项   |
| --- | --- | --- |
|   lmh.rpc.protocol  | 消息序列化协议        |  java，protobuf，kryo   |
|    lmh.rpc.register-address |  注册中心地址      |  默认localhost:2181   |
|    lmh.rpc.server-port |  服务端通信端口号     |  默认9999|
| lmh.rpc.weight | 权重 |默认1  |  

