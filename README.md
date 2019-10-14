# BroadcastReceiver
[![](https://jitpack.io/v/cbfg5210/BroadcastReceiver.svg)](https://jitpack.io/#cbfg5210/BroadcastReceiver)

为了简化广播接收处理，最近封装了一下广播接收器，下面和大家分享一下封装后的使用方法，还望各位看官多多指点!

## 引入依赖
### Step 1. Add the JitPack repository to your build file
```gradle
allprojects {
	repositories {
	  ...
	  maven { url 'https://jitpack.io' }
    }
}
```
### Step 2. Add the dependency
```gradle
dependencies {
       implementation "org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlin_version"
       implementation 'androidx.appcompat:appcompat:1.1.0'
       implementation 'com.github.cbfg5210:BroadcastReceiver:0.1'
}
```

## 使用

### 1、普通用法
```java
    BcstReceiver()
            // 添加 action 等
            .withFilter { intentFilter ->
                intentFilter.addAction(Intent.ACTION_TIME_CHANGED)
                intentFilter.addAction(Intent.ACTION_TIME_TICK)
            }
            // 设置回调
            .setCallback { context, intent -> Log.e("***", "${System.currentTimeMillis()}") }
            // 自定义回调处理
            //.setBcstWatcher(BcstWatcher)
            //.bind(this, lifecycle) // 默认在 onCreate 注册广播接收器,在 onDestroy 注销
            //.bind(this,lifecycle,Lifecycle.Event.ON_START) // 在 onStart 注册广播接收器,在 onStop 注销
            .bind(this, lifecycle, Lifecycle.Event.ON_RESUME) // 在 onResume 注册广播接收器,在 onPause 注销
```

### 2、自定义回调处理

以时间广播为例，我们在收到时间广播后，往往需要获取当前的时间并且对其进行格式化，这时候可以实现 BcstWatcher 的接口，
在其中对时间进行处理再对外提供以外回调方法以供使用即可。

[BcstWatcher.kt](https://github.com/cbfg5210/BroadcastReceiver/blob/master/bcstreceiver/src/main/java/com/bcstreceiver/BcstWatcher.kt) :

```java
interface BcstWatcher {
    /**
     * 创建 BcstReceiver 广播接收器回调
     */
    fun create(): (context: Context, intent: Intent) -> Unit

    /**
     * 注册广播接收器时调用
     */
    fun triggerAtOnce(context: Context)
}
```

[TimeWatcher.kt](https://github.com/cbfg5210/BroadcastReceiver/blob/master/bcstreceiver/src/main/java/com/bcstreceiver/watcher/TimeWatcher.kt) :

```java
class TimeWatcher(format: String? = null, locale: Locale? = null) : BcstWatcher {
    private var dateFormat: SimpleDateFormat? = null
    private lateinit var action: (timeMills: Long, formattedTime: String?) -> Unit

    init {
        format?.run { dateFormat = SimpleDateFormat(this, locale ?: Locale.CHINA) }
    }

    fun onTimeEvent(cb: (timeMills: Long, formattedTime: String?) -> Unit): TimeWatcher {
        this.action = cb
        return this
    }

    override fun create(): (context: Context, intent: Intent) -> Unit {
        return { _, _ -> handle() }
    }

    override fun triggerAtOnce(context: Context) {
        handle()
    }

    private fun handle() {
        val timeMills = System.currentTimeMillis()
        val formattedTime = dateFormat?.format(timeMills)
        action.invoke(timeMills, formattedTime)
    }
}
```

#### 使用 TimeWatcher:

```java
   val bcstWatcher = TimeWatcher("yyyy-MM-dd HH:mm:ss").onTimeEvent { timeMills, formattedTime ->
            Log.e("***", "timeMills=$timeMills,formattedTime=$formattedTime")
        }

   BcstReceiver()
          .withFilter { intentFilter ->
              intentFilter.addAction(Intent.ACTION_TIME_CHANGED)
              intentFilter.addAction(Intent.ACTION_TIME_TICK)
          }
          .setBcstWatcher(bcstWatcher)
          .bind(this, lifecycle)
```

为了便利使用以及减少重复代码，依赖库中对时间广播、home 键广播、电量广播、网络广播自定义了回调处理:
![capture_1.png](https://raw.githubusercontent.com/cbfg5210/BroadcastReceiver/master/captures/capture_1.png)

具体使用可以看[这里](https://github.com/cbfg5210/BroadcastReceiver/blob/master/app/src/main/java/com/bcst/receiver/MainActivity.kt)
