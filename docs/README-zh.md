# 前言

Azure functions有Java运行时所以也可以使用kotlin来编写functions路由

# Azure原理

通过gradle打包或运行functions时，gradle会扫描代码内的注解并生成对应的json和入口点
以便worker通过反射加载，这个包是一个zip/jar，里面有app的配置、程序本体和依赖库，
也就是说部署包是一个fatjar。

# 插件原理

插件通过扫描注解并通过编译器插件在IR阶段修改字节码，提供了更方便、检测和易用的API

# 注意事项

在使用时尽量避免使用`反射`来加载jar内的资源, 如果有需要可以通过内置的API来访问

```kotlin
import cn.rtast.kazure.resources.Resources

fun test() {
    val plainText: String = Resources.readText("path/to/text/resources.txt")
    val bytesContent: ByteArray = Resoures.readBytes("path/to/bytes/resources.bin")
}
```