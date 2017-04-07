package me.hao0.benchmark;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

/**
 * 通过@Setup和@TearDown注解，
 * 对状态对象作初始化和销毁
 */
@State(Scope.Thread)
public class JMHSample_05_StateFixtures {

    double x;

    /**
     * 初始化操作，默认在所有测试方法执行之前
     */
    @Setup
    public void prepare() {
        x = Math.PI;
    }

    /**
     * 销毁操作，默认在所有测试方法执行之后
     */
    @TearDown
    public void check() {
        assert x > Math.PI : "Nothing changed?";
    }

    /**
     * check成功
     */
    @Benchmark
    public void measureRight() {
        x++;
    }

    /**
     * check失败
     */
    @Benchmark
    public void measureWrong() {
        double x = 0;
        x++;
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(JMHSample_05_StateFixtures.class.getSimpleName())
                .warmupIterations(5)
                .measurementIterations(5)
                .forks(1)
                .jvmArgs("-ea")
                .build();

        new Runner(opt).run();
    }

}