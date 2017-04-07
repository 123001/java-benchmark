# Java Benchmark

> 本项目主要包含，基于[jmh](http://openjdk.java.net/projects/code-tools/jmh/)对java应用作基准测试，大部分测试用例来自[官方](http://hg.openjdk.java.net/code-tools/jmh/file/tip/jmh-samples/src/main/java/org/openjdk/jmh/samples/)。


### 如何使用jmh？

+ 推荐在[maven](http://maven.apache.org/)项目中使用jmh；

+ 引入pom依赖：

	```xml
	<dependency>
        <groupId>org.openjdk.jmh</groupId>
        <artifactId>jmh-core</artifactId>
        <version>1.18</version>
    </dependency>
    <dependency>
        <groupId>org.openjdk.jmh</groupId>
        <artifactId>jmh-generator-annprocess</artifactId>
        <version>1.18</version>
    </dependency>
	```

+ 在需要基准测试类中声明`main`方法，如：

	```java
	public static void main(String[] args) throws RunnerException {
   		Options opt = new OptionsBuilder()
                .include(yourClassName.class.getSimpleName())
                .forks(1)
                .build();

    	new Runner(opt).run();
    }
	```

+ 在需要测试的方法上加上`@Benchmark`注解，如：

	```java
	@Benchmark
    public void wellHelloThere() {
        System.out.println("Hello World.");
    }
	```

+ 可以参考下这个[JMHSample_01_HelloWorld](src/main/java/me/hao0/benchmark/JMHSample_01_HelloWorld.java)。

### 如何运行基准测试？

有三种方式：

+ 通过命令行：
	
	```bash
	mvn clean install
	java -jar target/benchmarks.jar yourClassName
	```

+ 将JMH与测试代码一起打包，然后使用以下命令：

	```bash
	java -jar target/benchmarks.jar -h
	```
	
+ 通过Java API，如上述的程序实例。

### 注解用法

#### @Benchmark

+ **用途**：指定需要作基准测试的方法；

+ **作用域**：方法；

+ **约束**不仅限于：

	+ 方法为`public`；
	+ 方法中的参数必须带有`@State`注解；
	+ 如果`@State`注解的是**内部静态类**，则该方法需要加`synchronized`。

+ 实例可见[JMHSample_01_HelloWorld](src/main/java/me/hao0/benchmark/JMHSample_01_HelloWorld.java)。

#### @BenchmarkMode

+ **用途**：指定基准测试方法的运行模式；

+ **作用域**：方法；

+ **运行模式类型**：

	+ **Mode.Throughput**
	  
	   > 计算时间单位内操作次数，该模式是基于时间的，直到迭代时间过期才会停止运行。
	
	+ **Mode.AverageTime**

		> 计算单次操作的平均时间，该模式是基于时间的，直到迭代时间过期才会停止运行。
	
	+ **Mode.SampleTime**

		> 计算每次操作的时间(包含百分比)，该模式是基于时间的，直到迭代时间过期才会停止运行。
	
	+ **Mode.SingleShotTime**

		> 方法仅运行一次(不会执行预热动作)。
	
	+ 除上述以外，也可以以多种组合模式运行测试方法。

+ 实例可见[JMHSample_02_BenchmarkModes](src/main/java/me/hao0/benchmark/JMHSample_02_BenchmarkModes.java)。

#### @OutputTimeUnit

+ **用途**：指定测试方法输出的时间单位；
+ **作用域**：方法或类。

#### @State

+ **用途**：定义给定类对象的可用范围；
+ **作用域**：公共类或静态内部类；
+ 类作为测试方法入参的条件：
	+ 有无参构造函数；
	+ 是公共类或静态内部类；
	+ 该类必须使用@State注解。

+ **Scope类型**：

	+ **Scope.Thread**
	
		> 默认状态。实例将分配给运行测试方法的每个线程，不会共享。
	
	+ **Scope.Benchmark**
	
		> 运行相同测试的所有线程将共享实例，可用来测试状态对象的多线程性能。
	
	+ **Scope.Group**
	
		> 实例分配给每个线程组，在同一线程组的线程会共享该变量。

+ 实例可见[这里](src/main/java/me/hao0/benchmark/JMHSample_03_States.java)。

#### @Setup和@TearDown

+ **用途**：定义状态对象的生命周期方法；
+ **作用域**：具有`@State`注解的类中的方法上；
+ 实例可见[JMHSample_05_StateFixtures](src/main/java/me/hao0/benchmark/JMHSample_05_StateFixtures.java)。

#### @Level

+ **用途**：用于控制何时执行`@Setup`和`@TearDown`方法；
+ **作用域**：方法；
+ **执行级别类型**：

	+ **Level.Trial**

		> 默认级别，在每次启动测试类前/后执行。
	
	+ **Level.Iteration**

		> 在每次迭代前/后执行。

	+ **Level.Invocation**

		> 在每次调用测试方法前/后执行。	

+ 实例可见[JMHSample_06_FixtureLevel](src/main/java/me/hao0/benchmark/JMHSample_06_FixtureLevel.java)，[JMHSample_07_FixtureLevelInvocation](src/main/java/me/hao0/benchmark/JMHSample_07_FixtureLevelInvocation.java)。

#### @OperationsPerInvocation

+ **用途**：用于指明测试方法中的循环次数，便于测试单次循环操作的性能；
+ **作用域**：方法或类；
+ 实例可见[JMHSample_11_Loops](src/main/java/me/hao0/benchmark/JMHSample_11_Loops.java)。

#### @Fork

+ **用途**：设置测试方法的运行**Fork**环境；
+ **作用域**：方法或类；
+ 实例可见[JMHSample_12_Forking](src/main/java/me/hao0/benchmark/JMHSample_12_Forking.java)，[JMHSample_13_RunToRun](src/main/java/me/hao0/benchmark/JMHSample_13_RunToRun.java)。

#### @Group和@GroupThreads

+ **用途**：以非对称的线程数运行不同的测试方法；
+ **作用域**：方法；
+ 实例可见[JMHSample_15_Asymmetric](src/main/java/me/hao0/benchmark/JMHSample_15_Asymmetric.java)。

#### @CompilerControl

+ **用途**：用于控制测试方法的某些编译可选项；
+ **作用域**：方法，构造器或类；
+ 实例可见[JMHSample_16_CompilerControl](src/main/java/me/hao0/benchmark/JMHSample_16_CompilerControl.java)。

#### @AuxCounters

+ **用途**：用于对**状态类**中的公共字段或含返回结果的方法作分别测试；
+ **作用域**：类；
+ 实例可见[JMHSample_23_AuxCounters](src/main/java/me/hao0/benchmark/JMHSample_23_AuxCounters.java)。

#### @Param

+ **用途**：用于配置测试时，使用到的参数；
+ **作用域**：状态类中的**非final**字段；
+ 实例可见[JMHSample_27_Params](src/main/java/me/hao0/benchmark/JMHSample_27_Params.java)。

### 使用Blackhole

+ 有时候测试代码中，开发人员不希望**死代码**被JVM优化掉，这时可以使用`Blackhole`来消除死代码优化，实例可见[JMHSample_09_Blackholes](src/main/java/me/hao0/benchmark/JMHSample_09_Blackholes.java)，[JMHSample_21_ConsumeCPU](src/main/java/me/hao0/benchmark/JMHSample_21_ConsumeCPU.java)，[JMHSample_28_BlackholeHelpers](src/main/java/me/hao0/benchmark/JMHSample_28_BlackholeHelpers.java)。

### 在Spring应用中使用JMH

+ 大部分java应用会运行在**Spring容器**中，对于测试Spring应用，可以使用上述的`@Setup`和`@TearDown`注解来处理，如类似下面的代码片段：

	```java
	@State(Scope.Benchmark)
	public class MySpringBeanBenchmarkTest {
	
	    private ClassPathXmlApplicationContext springContext;
	
	    private MySpringBean mySpringBean;
	
	    @Setup
	    public void start(){
	        springContext = new ClassPathXmlApplicationContext("classpath:spring-context.xml");
	        springContext.start();
	        mySpringBean = springContext.getBean(MySpringBean.class);
	    }
	
	    @TearDown
	    public void destroy(){
	        springContext.destroy();
	    }
	
	    @Benchmark
	    @BenchmarkMode({Mode.Throughput, Mode.AverageTime, Mode.SampleTime})
	    @OutputTimeUnit(value = TimeUnit.SECONDS)
	    public void testExecute() throws InterruptedException {
	        Map<String, Object> context = new HashMap<>();
	        context.put("orderAmount", 500);
	        mySpringBean.execute("TRADE_ORDER_0001", context);
	    }
	
	    public static void main(String[] args) throws RunnerException {
	
	        Options opt = new OptionsBuilder()
	                .include(".*" + MySpringBeanBenchmarkTest.class.getSimpleName() + ".*")
	                .forks(1)
	                .threads(Runtime.getRuntime().availableProcessors())
	                .measurementIterations(5)
	                .build();
	
	        new Runner(opt).run();
	    }
	}
	```
	
	
	
	
	
	
	
	
	
	
	
