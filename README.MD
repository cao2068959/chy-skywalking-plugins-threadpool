# 1. 简介

`skywalking` 的线程池支持插件，可以支持`lamdba`表达式

问： `skywalking` 已经提供了多线程的支持插件，为什么还要自己造轮子
答：官方提供的插件，切入点是`Runnable`的实现类，那如果直接写的`lamdba`表达式，就无法织入。

# 2.快速使用

### 2.1 开箱及食
本仓库提供的插件是基于`skywalking7.0`的版本，如果你用的是该版本，可以在本仓库目录`/chy/plugins` 下载作者已经编译好的插件。

__`注意`__： 因为要织入`ThreadPool`，作者这里稍稍修改了`apm-agent`，所以要使用我提供的`apm-agent.jar`作为`agent`的入口包
也可以在`/chy/`目录下找到。

把`apm-chy-threading-plugin-7.0.0.jar`放入`plugins`文件夹，使用我提供的`apm-agent.jar`就能能够正常工作了

### 2.2 手动编译
如果需要二次修改那可以使用手动编译

`clone`本仓库到本地后，使用`maven`命令编译打包

```java
mvn clean package -DskipTests -Dcheckstyle.skip=true
```
运行上面maven命令后，也是在项目目录下的`chy`文件中可以找到编译后的`jar`

# 3.效果演示
```java
@GetMapping("/ajax/{setId}/async")
    public List<DoctorListVO> getDoctorListasync(@PathVariable Integer setId) throws ExecutionException, InterruptedException {
        
        //线程池访问
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<List<DoctorListVO>> submit = executor.submit(() -> {
            //远程调用了别的服务
            return doctorSetAdminService.queryDoctorList(setId);
        });
        Future<List<DoctorListVO>> submit2 = executor.submit(() -> {
            return doctorSetAdminService.queryDoctorList(setId);
        });

        //和上面 queryDoctorList 一样只是queryDoctorListAsy() 方法都是打了 `@Async` 注解
        doctorSetAdminService.queryDoctorListAsy(setId);
        doctorSetAdminService.queryDoctorListAsy(setId);


        List<DoctorListVO> doctorListVOS = submit.get();
        List<DoctorListVO> doctorListVOS2 = submit2.get();
        return doctorListVOS;
    }
```

![e1](https://img-blog.csdnimg.cn/20200514174520622.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3UwMTA5Mjg1ODk=,size_16,color_FFFFFF,t_70)

从上面可以看出，`SwCallableWapper`就是我插件的`span`标识，并且多线程可以正常追踪

# 4.实现原理
简单概括就是，代理了`ThreadPoolExecutor`类的`execute`和`submit`方法，然后替换了入参`Runnable`和`Callable`成包装类

具体插件的实现细节可以看包`com.chy.skywalking.apm.plugin.ttl.threading`

### 4.1 织入 ThreadPoolExecutor的问题

因为 原生的`apm-agent.jar`，在`bytebuddy`修改`ThreadPoolExecutor`字节码前就已经把`ThreadPoolExecutor`加入到虚拟机(写入`log`的时候会启动线程池)，导致织入失败
所以需要使用我提供的`apm-agent.jar`来让`ThreadPoolExecutor`不要过早加载

具体细节可以看 `org.apache.skywalking.apm.agent.SkyWalkingAgent`


