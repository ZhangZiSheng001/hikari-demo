
# 简介

## HikariCP是什么？

HikariCP 本质上就是一个数据库连接池。

## HikariCP 解决了哪些问题？

创建和关闭数据库连接的开销很大，HikariCP 通过“池”来复用连接，减小开销。

## 为什么要使用 HikariCP？

1. HikariCP 是目前最快的连接池。就连风靡一时的  boneCP  也停止维护，主动让位给它。SpringBoot 也把它设置为默认连接池。

<img src="https://img2018.cnblogs.com/blog/1731892/202002/1731892-20200219095516084-1441290818.png" style="zoom: 50%;" />

2. HikariCP 非常轻量。本文用到的 4.0.3 版本的 jar 包仅仅只有 156 KB，它的源码真的非常精炼。

## 本文要讲什么？

本文将包含以下内容(因为篇幅较长，可根据需要选择阅读)：

1. 如何使用 HikariCP（入门、JMX 等）
2. 配置参数详解
3. 源码分析

# 如何使用 HikariCP

## 需求

使用 HikariCP 获取连接对象，对用户数据进行简单的增删改查。

## 项目环境

`JDK`：1.8.0_231

`maven`：3.6.3

`IDE`：Spring Tool Suite  4.6.1.RELEASE

`mysql-connector-java`：8.0.15

`mysql`：5.7.28 

`Hikari`：4.0.3

## 引入依赖

项目类型 Maven Project，打包方式 jar。

```xml
 		<!-- test -->
        <dependency>
            <groupId>junit</groupId>
            <artifactId>junit</artifactId>
            <version>4.12</version>
            <scope>test</scope>
        </dependency>
        <!-- hikari -->
        <dependency>
            <groupId>com.zaxxer</groupId>
            <artifactId>HikariCP</artifactId>
            <version>4.0.3</version>
        </dependency>
        <!-- mysql驱动 -->
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
            <version>8.0.15</version>
        </dependency>
        <!-- log -->
        <dependency>
            <groupId>ch.qos.logback</groupId>
            <artifactId>logback-core</artifactId>
            <version>1.2.3</version>
            <type>jar</type>
        </dependency>
        <dependency>
            <groupId>ch.qos.logback</groupId>
            <artifactId>logback-classic</artifactId>
            <version>1.2.3</version>
            <type>jar</type>
        </dependency>
```

## 编写 hikari.properties

本文使用配置文件的方式来配置 HikariCP，当然，我们也可以在代码中显式配置，但不提倡。因为是入门例子，这里我只给出了必需的参数，其他的参数后面会详细介绍。

```properties
jdbcUrl=jdbc:mysql://localhost:3306/github_demo?characterEncoding=utf8&serverTimezone=GMT%2B8
username=root
password=root
```

## 初始化连接池

初始化连接池时，我们可以在代码中显式指定配置文件，也可以通过启动参数配置。

```java
// 加载配置文件，也可以无参构造并使用启动参数 hikaricp.configurationFile 指定配置文件（不推荐，后面会说原因）
HikariConfig config = new HikariConfig("/hikari2.properties");
HikariDataSource dataSource = new HikariDataSource(config);
```

初始化连接池之后，我们可以通过`HikariDataSource.getConnection()`方法获取连接对象，然后进行增删改查操作，这部分内容这里就不展示了。

# 如何使用 JMX 管理连接池

## 需求

开启 JMX 功能，并使用 jconsole 管理连接池。

## 开启JMX

在入门例子的基础上增加配置。这要设置 registerMbeans 为 true，JMX 功能就会开启。

```properties
#-------------JMX--------------------------------
# 是否开启 JMX
# 默认 false
registerMbeans=true

# 是否允许通过 JMX 挂起和恢复连接池
# 默认为 false
allowPoolSuspension=true

# 连接池名称。
# 默认自动生成
poolName=zzsCP
```

## 启动连接池

为了查看具体效果，这里让主线程进入睡眠 20 分钟。

```java
    public static void main(String[] args) throws InterruptedException {
        HikariConfig config = new HikariConfig("/hikaricp_base.properties");
        HikariDataSource dataSource = new HikariDataSource(config);
        Thread.sleep(20 * 60 * 1000);
        dataSource.close();
    }
```

## 使用jconsole查看

运行 main 方法，使用 JDK 的工具 jconsole 连接我们的项目，在 MBean 选项卡可以看到我们的连接池。接下里，我们可以进行这样的操作：

1. 通过 PoolConfig 动态修改配置（只有部分参数允许修改）；
2. 通过 Pool 获取连接池的连接数（活跃、空闲和所有）、获取等待连接的线程数、挂起和恢复连接池、丢弃未使用连接等。

![hikaricp_jmx](https://img2020.cnblogs.com/blog/1731892/202105/1731892-20210521133856462-1080103699.png)

想了解更多 JMX 功能可以参考我的博客文章： [如何使用JMX来管理程序？](https://www.cnblogs.com/ZhangZiSheng001/p/12128915.html) 

# 配置参数详解

相比其他连接池，HikariCP 的配置参数非常简单，其中有几个功能需要注意：

1. HikariCP 借出连接时强制检查连接的活性，不像其他连接池一样可以选择不检查；
2. 默认会检查 idleTimeout、maxLifetime，可以选择禁用，但不推荐；
3. 默认不检查 keepaliveTime、leakDetectionThreshold，可以选择开启，推荐开启 leakDetectionThreshold 即可。

## 必需的参数

注意，这里 jdbcUrl 和 dataSourceClassName 二选一。

```properties
#-------------必需的参数--------------------------------
# JDBC 驱动中 DataSource 的实现类全限定类名。不支持 XA DataSource
# 如果指定， HikariCP 将使用 DataSouce.getConnection 获取连接而不是使用 DriverManager.getConnection，官方建议指定（mysql 除外）
# dataSourceClassName=

# 如果指定， HikariCP 将使用 DriverManager.getConnection 获取连接而不是使用 DataSouce.getConnection
jdbcUrl=jdbc:mysql://localhost:3306/github_demo?characterEncoding=utf8&serverTimezone=GMT%2B8

# 用户名和密码
username=root
password=root
```

## 常用的参数

```properties
# 从池中借出的连接是否默认自动提交事务
# 默认 true
autoCommit=true

# 当我从池中借出连接时，愿意等待多长时间。如果超时，将抛出 SQLException
# 默认 30000 ms，最小值 250 ms。支持 JMX 动态修改
connectionTimeout=30000

# 一个连接在池里闲置多久时会被抛弃
# 当 minimumIdle < maximumPoolSize 才生效
# 默认值 600000 ms，最小值为 10000 ms，0表示禁用该功能。支持 JMX 动态修改
idleTimeout=600000

# 多久检查一次连接的活性
# 检查时会先把连接从池中拿出来（空闲的话），然后调用isValid()或执行connectionTestQuery来校验活性，如果通过校验，则放回池里。
# 默认 0 （不启用），最小值为 30000 ms，必须小于 maxLifetime。支持 JMX 动态修改
keepaliveTime=0

# 当一个连接存活了足够久，HikariCP 将会在它空闲时把它抛弃
# 默认 1800000  ms，最小值为 30000 ms，0 表示禁用该功能。支持 JMX 动态修改
maxLifetime=1800000

# 用来检查连接活性的 sql，要求是一个查询语句，常用select 'x'
# 如果驱动支持 JDBC4.0，建议不设置，这时默认会调用  Connection.isValid() 来检查，该方式会更高效一些
# 默认为空
# connectionTestQuery=

# 池中至少要有多少空闲连接。
# 当空闲连接 < minimumIdle，总连接 < maximumPoolSize 时，将新增连接
# 默认等于 maximumPoolSize。支持 JMX 动态修改
minimumIdle=5

# 池中最多容纳多少连接（包括空闲的和在用的）
# 默认为 10。支持 JMX 动态修改
maximumPoolSize=10

# 用于记录连接池各项指标的 MetricRegistry 实现类
# 默认为空，只能通过代码设置
# metricRegistry=

# 用于报告连接池健康状态的 HealthCheckRegistry 实现类
# 默认为空，只能通过代码设置
# healthCheckRegistry=

# 连接池名称。
# 默认自动生成
poolName=zzsCP
```

## 很少用的参数

```properties
# 如果启动连接池时不能成功初始化连接，是否快速失败 TODO
# >0 时，会尝试获取连接。如果获取时间超过指定时长，不会开启连接池，并抛出异常
# =0 时，会尝试获取并验证连接。如果获取成功但验证失败则不开启池，但是如果获取失败还是会开启池
# <0 时，不管是否获取或校验成功都会开启池。
# 默认为 1
initializationFailTimeout=1

# 是否在事务中隔离 HikariCP 自己的查询。
# autoCommit 为 false 时才生效
# 默认 false
isolateInternalQueries=false

# 是否允许通过 JMX 挂起和恢复连接池
# 默认为 false
allowPoolSuspension=false

# 当连接从池中取出时是否设置为只读
# 默认值 false
readOnly=false

# 是否开启 JMX
# 默认 false
registerMbeans=true

# 数据库 catalog
# 默认由驱动决定
# catalog=

# 在每个连接创建后、放入池前，需要执行的初始化语句
# 如果执行失败，该连接会被丢弃
# 默认为空
# connectionInitSql=

# JDBC 驱动使用的 Driver 实现类
# 一般根据 jdbcUrl 判断就行，报错说找不到驱动时才需要加
# 默认为空
# driverClassName=

# 连接的默认事务隔离级别
# 默认值为空，由驱动决定
# transactionIsolation=

# 校验连接活性允许的超时时间
# 默认 5000 ms，最小值为 250 ms，要求小于 connectionTimeout。支持 JMX 动态修改
validationTimeout=5000

# 连接对象可以被借出多久
# 默认 0（不开启），最小允许值为 2000 ms。支持 JMX 动态修改
leakDetectionThreshold=0

# 直接指定 DataSource 实例，而不是通过 dataSourceClassName 来反射构造
# 默认为空，只能通过代码设置
# dataSource=

# 数据库 schema
# 默认由驱动决定
# schema=

# 指定连接池获取线程的 ThreadFactory 实例
# 默认为空，只能通过代码设置
# threadFactory=

# 指定连接池开启定时任务的 ScheduledExecutorService 实例（建议设置setRemoveOnCancelPolicy(true)）
# 默认为空，只能通过代码设置
# scheduledExecutor=

# JNDI 配置的数据源名
# 默认为空
# dataSourceJndiName=
```

# 源码分析

HikariCP 的源码少且精，可读性非常高。如果你没见过像诗一样的代码，可以来看看 HikariCP。

提醒一下，在阅读 HiakriCP 源码之前，需要掌握`CopyOnWriteArrayList`、`AtomicInteger`、`SynchronousQueue`、`Semaphore`、`AtomicIntegerFieldUpdater`、`LockSupport`等 JDK 自带类的使用。

注意，考虑到篇幅和可读性，以下代码经过删减。

## HikariCP为什么快？

数据库连接池已经发展了很久了，也算是比较成熟的技术，使用比较广泛的类库有 boneCP、DBCP、C3P0、Druid 等等。眼看着数据库连接池已经发展到了瓶颈，所谓的性能提升也仅仅是一些代码细节的优化，这个时候，HikariCP 出现并快速地火了起来，与其他连接池相比，它的快不是普通的快，而是跨越性的快。下面是 JMH 测试的结果（[测试项目地址]([brettwooldridge/HikariCP-benchmark: JHM benchmarks for JDBC Connection Pools (github.com)](https://github.com/brettwooldridge/HikariCP-benchmark))）。

<img src="https://img2020.cnblogs.com/blog/1731892/202105/1731892-20210521133932438-1230644418.png" alt="DataSource_performance_test" style="zoom: 80%;" />

HikariCP 为什么快？我看网上有很多的解释，例如，大量使用 JDK 并发包的工具来避免粗颗粒度的锁、FastList 等自定义类的使用、动态代理类等等。我觉得，这些都不是主要原因。

**HikariCP 之所以快，更多的还是由于抽象层面的优化**。

### 传统模型--中规中矩的模型

连接池，顾名思义，就是一个存放连接对象的池塘。几乎所有的连接池都会从代码层面抽象出一个池塘。池里的连接数量不是一成不变的，例如，连接失效了需要移除、新连接创建、用户借出或归还连接，等等，总结起来，**对连接池的操作不外乎四个：borrow、return、add、remove**。

连接池一般是这样设计的：borrow、remove 动作会将连接从池塘里拿出来，add、return 动作则会往池塘里添加连接。我把这种模型称为“传统模型”。

<img src="https://img2020.cnblogs.com/blog/1731892/202105/1731892-20210521133955130-887271655.png" alt="hikaricp_pool01" style="zoom:67%;" />

“传统模型”是比较中规中矩的模型，从抽象层面讲，它非常符合我们的现实生活，例如，某人借走我的钱，钱就不在我的钱包里了。我们熟知的 DBCP、C3P0、Druid 等等都是基于“传统模型”开发的。

### 标记模型--更少的锁

但是 HikariCP 就不一样了，它没有走老路，而是优化了“传统模型”，让连接池真正意义地实现了提速。

**在“传统模型”中，borrow、return、add、remove 四个动作都需要加同一把锁，即同一时刻只允许一个线程操作池，并发高时线程切换将非常频繁**。因为多个线程操作同一个池塘，连接出入池需要加锁来保证线程安全。针对这一点，我们是不是能做些什么呢？

HikariCP 是这样做的，borrow 的连接不会从池塘里取出，而是打上“已借出”的标记，return 的时候，再把这个连接的“已借出”标记去掉。我把这种做法称为“标记模型”。“标记模型”可以实现 borrow 和 return 动作不加锁。具体怎么做到的呢？

<img src="https://img2020.cnblogs.com/blog/1731892/202105/1731892-20210521134018541-1358935184.png" alt="hikaricp_pool02" style="zoom:67%;" />

首先，我要 borrow 时，我需要看看池塘里哪一个连接可以借出。这里就涉及到读连接池的操作，因为池塘里的连接数量不是一成不变的，为了一致性，我们就必须加锁。但是，HikariCP 没有加，为什么呢？**因为 HikariCP 容忍了读的不一致**。borrow 的时候，我们实际上读的不是真正的池塘，而是当前池塘的一份快照。我们看看 HikariCP 存放连接的地方，是一个`CopyOnWriteArrayList`对象，我们知道，`CopyOnWriteArrayList`是一个写安全、读不安全的集合。

```java
public class ConcurrentBag<T extends IConcurrentBagEntry> implements AutoCloseable {
   // 存放连接的集合
   private final CopyOnWriteArrayList<T> sharedList;
}
```

接着，当我们找到了一个可借出的连接时，需要给它打上借出的标记。注意，这时有可能出现多个线程都想给它打标记的情况，该怎么办呢？难道要加锁了吗？别忘了我们可以用 CAS 机制来更新连接的标记，这个时候就不需要加锁了。看看 HikariCP 就是这么实现的。

```java
   public T borrow(long timeout, final TimeUnit timeUnit) throws InterruptedException {
      final int waiting = waiters.incrementAndGet();
      try {
         for (T bagEntry : sharedList) {
            if (bagEntry.compareAndSet(STATE_NOT_IN_USE, STATE_IN_USE)) {
               return bagEntry;
            }
         }


         return null;
      }
      finally {
         waiters.decrementAndGet();
      }
   }
```

于是，**在“标记模型”里，只有 add 和 remove 才需要加锁，borrow 和 return 不需要加锁**。通过这种颠覆式的设计，连接池的性能得到极大的提高。

这就是我认为的 HikariCP 快的最主要原因。

## HikariCP主要的类

那么，我们来看看 HikariCP 的源码吧。

HikariCP 的类不多，最主要的就这几个，如图。不难发现，这种类结构和 DBCP2 很像。

<img src="https://img2020.cnblogs.com/blog/1731892/202105/1731892-20210521134109578-624604888.png" style="zoom: 80%;" />

这几个类可以分成四个部分：

1. 用户接口。用户一般会使用`DataSource.getConnection()`来获取连接对象。
2. JMX 支持。
2. 配置信息。使用`HikariConfig`加载配置文件，或手动配置`HikariConfig`的参数，它一般会作为入参来构造`HikariDataSource`对象；
3. 连接池。获取连接的过程为`HikariDataSource.getConnection()`->`HikariPool.getConnection()`->`ConcurrentBag.borrow(long, TimeUnit)`。**需要注意的是，`ConcurrentBag`才是真正的连接池，而`HikariPool`是用来管理连接池的**。

## ConcurrentBag--最核心的类

**`ConcurrentBag`可以算是 HikariCP 最核心的一个类，它是 HikariCP 底层真正的连接池，上面说的“标记模型”只要就是靠它来实现的**。如果大家不想看太多代码的话，只看它就足够了。

在设计上，`ConcurrentBag`是一个比较通用的资源池，它可以是数据库连接的池，也可以是其他对象的池，只要存放的资源对象实现了`IConcurrentBagEntry`接口即可。所以，如果我们的项目中需要自己构建池的话，可以直接拿这个现成的组件来用。

```java
public class ConcurrentBag<T extends IConcurrentBagEntry> implements AutoCloseable {
	private final CopyOnWriteArrayList<T> sharedList;
}
```

下面简单介绍下`ConcurrentBag`的几个字段：

![ConCurrentBagUml](https://img2020.cnblogs.com/blog/1731892/202105/1731892-20210521134137369-1541380316.png)

| 属性                            | 描述                                                   |
| ------------------------------- | ------------------------------------------------------ |
| CopyOnWriteArrayList sharedList | 存放着状态为使用中、未使用和保留三种状态的资源对象     |
| ThreadLocal threadList          | 存放着当前线程归还的资源对象                           |
| SynchronousQueue handoffQueue   | 这是一个无容量的阻塞队列，出队和入队都可以选择是否阻塞 |
| AtomicInteger waiters           | 当前等待获取元素的线程数                               |

这几个字段在`ConcurrentBag`中如何使用呢，这里拿`borrow`方法来说明下：

```java
   public T borrow(long timeout, final TimeUnit timeUnit) throws InterruptedException
   {
      // 1. 首先从threadList获取资源
       
      final List<Object> list = threadList.get();
      for (int i = list.size() - 1; i >= 0; i--) {
         final Object entry = list.remove(i);
         final T bagEntry = weakThreadLocals ? ((WeakReference<T>) entry).get() : (T) entry;
         if (bagEntry != null && bagEntry.compareAndSet(STATE_NOT_IN_USE, STATE_IN_USE)) {
            return bagEntry;
         }
      }

      // 等待获取连接的线程数+1
      final int waiting = waiters.incrementAndGet();
      try {
         // 2.如果还没获取到，会从sharedList中获取对象
         for (T bagEntry : sharedList) {
            if (bagEntry.compareAndSet(STATE_NOT_IN_USE, STATE_IN_USE)) {
               // 这一步我不是很懂，好像可有可无
               if (waiting > 1) {
                  listener.addBagItem(waiting - 1);
               }
               return bagEntry;
            }
         }
         // 从sharedList中获取不到资源，通知监听器创建资源（不一定会创建）
         listener.addBagItem(waiting);
        
         // 3.如果还没获取到，会堵塞等待空闲连接
         timeout = timeUnit.toNanos(timeout);
         do {
            final long start = currentTime();
            // 这里会出现三种情况，
            // 1.超时，返回null
            // 2.获取到资源，但状态为正在使用，继续循环
            // 3.获取到资源，元素状态为未使用，修改为已使用并返回
            final T bagEntry = handoffQueue.poll(timeout, NANOSECONDS);
            if (bagEntry == null || bagEntry.compareAndSet(STATE_NOT_IN_USE, STATE_IN_USE)) {
               return bagEntry;
            }
            timeout -= elapsedNanos(start);
         } while (timeout > 10_000);
         // 4.超时了还是没有获取到，返回null
         return null;
      }
      finally {
         // 等待获取连接的线程数-1
         waiters.decrementAndGet();
      }
   }
```

在上面的方法中，唯一会造成线程阻塞的就是`handoffQueue.poll(timeout, NANOSECONDS)`，除此之外，我们没有看到任何的 synchronized 和 lock。

## HikariPool--管理连接池

除了`ConcurrentBag`，`HikariPool`也是一个比较重要的类，它用来**管理连接池**。

![HikariPoolUML](https://img2020.cnblogs.com/blog/1731892/202105/1731892-20210521134215429-887278528.png)

HikariPool 的几个字段说明如下：

| 属性类型和属性名                                     | 说明                                                         |
| ---------------------------------------------------- | ------------------------------------------------------------ |
| DataSource dataSource                                | 用于获取原生连接对象的数据源。一般我们不指定的话，使用的是DriverDataSource |
| ThreadPoolExecutor addConnectionExecutor             | 执行创建连接任务的线程池。**只开启一个线程执行任务**。       |
| ThreadPoolExecutor closeConnectionExecutor           | 执行关闭原生连接任务的线程池。**只开启一个线程执行任务**。   |
| ScheduledExecutorService houseKeepingExecutorService | 用于执行检查 idleTimeout、leakDetectionThreshold、keepaliveTime、maxLifetime 等任务的线程池。 |

为了更清晰地理解上面几个字段的含义，我简单画了个图，不是很严谨，将就看下吧。在这个图中，客户端线程可以调用进行 borrow、requite 和 remove 操作，houseKeepingExecutorService 线程可以调用进行 remove 操作，只有 addConnectionExecutor 可以进行 add 操作。

<img src="https://img2018.cnblogs.com/blog/1731892/202002/1731892-20200219095745457-1165943303.png" alt="HikariPoolSimpleProcess" style="zoom:80%;" />

## 一些有趣的地方

掌握了上面的两个类，HikariCP 的整个源码视图应该就比较完整了。下面再说一些有趣的地方。

### 为什么HikariDataSource有两个HikariPool

在下面的代码中，`HikariDataSource`里竟然有两个`HikariPool`。

```java
public class HikariDataSource extends HikariConfig implements DataSource, Closeable
{
   private final HikariPool fastPathPool;
   private volatile HikariPool pool;
}
```

为什么要这样做呢？

首先，从性能方面考虑，使用 fastPathPool 来创建连接会比 pool 更好一些，因为 pool 被 volatile 修饰了，为了保证可见性不能使用缓存。那为什么还要用到 pool 呢？

我们打开`HikariDataSource.getConnection()`，可以看到，pool 的存在可以用来支持双重检查锁。这里我比较好奇的是，为什么不把 `HikariPool`的引用给 fastPathPool？？这个问题大家感兴趣可以研究一下。

```java
   public Connection getConnection() throws SQLException
   {
      if (fastPathPool != null) {
         return fastPathPool.getConnection();
      }

      HikariPool result = pool;
      if (result == null) {
         synchronized (this) {
            result = pool;
            if (result == null) {
               validate();
               pool = result = new HikariPool(this);
               this.seal();
            }
         }
      }

      return result.getConnection();
   }
```

其实，这两个`HikariPool`对象有两种取值情况：

取值一：`fastPathPool = pool = new HikariPool(this)`。当通过有参构造`new HikariDataSource(HikariConfig configuration)`来创建`HikariDataSource`就会出现这样取值；

取值二：`fastPathPool = null;pool = new HikariPool(this)`。当通过无参构造`new HikariDataSource()`来创建`HikariDataSource`就会出现这样取值。

所以，我更推荐使用`new HikariDataSource(HikariConfig configuration)`的方式，因为这样做的话，我们将使用 fastPathPool 来获取连接。


### 如何加载配置

HikariCP 加载配置的代码非常简洁。我们直接从`PropertyElf.setTargetFromProperties(Object, Properties)`方法开始看，如下。

```java
   // 这个方法就是将properties的参数设置到HikariConfig中
   public static void setTargetFromProperties(final Object target, final Properties properties)
   {
      if (target == null || properties == null) {
         return;
      }
    
      // 获取HikariConfig的所有方法
      List<Method> methods = Arrays.asList(target.getClass().getMethods());
      properties.forEach((key, value) -> {
         // 如果是dataSource.*的参数，直接加入到dataSourceProperties属性
         if (target instanceof HikariConfig && key.toString().startsWith("dataSource.")) {
            ((HikariConfig) target).addDataSourceProperty(key.toString().substring("dataSource.".length()), value);
         }
         else {
            // 找到参数对应的setter方法并赋值
            setProperty(target, key.toString(), value, methods);
         }
      });
   }
```

相比其他类库（尤其是 druid），HikariCP 加载配置的过程非常简洁，不需要按照参数名一个个地加载，这样后期会更好维护。当然，这种方式我们也可以运用到实际项目中。

另外，配置 HikariCP 的时候不允许写错参数或者添加一些无关的参数，否则会因为找不到对应的 setter 方法而报错。

以上基本讲完 HikariCP 的源码。后续发现其他有趣的地方再做补充，也欢迎大家指正不足的地方。

最后，感谢阅读。

# 参考资料

[HikariCP github](https://github.com/brettwooldridge/HikariCP)

> 2021-05-20 修改

> 相关源码请移步：[https://github.com/ZhangZiSheng001/hikari-demo](https://github.com/ZhangZiSheng001/hikari-demo)

>本文为原创文章，转载请附上原文出处链接： [https://www.cnblogs.com/ZhangZiSheng001/p/12329937.html ](https://www.cnblogs.com/ZhangZiSheng001/p/12329937.html )