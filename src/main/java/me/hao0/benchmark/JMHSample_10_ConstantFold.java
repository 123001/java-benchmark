package me.hao0.benchmark;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;

/**
 * JVM自动优化可预测的计算结果
 */
@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class JMHSample_10_ConstantFold {

    // 普通变量，值可能发生变化，不可预知
    private double x = Math.PI;

    // final变量，值不会发生变化，即可预知
    private final double wrongX = Math.PI;

    @Benchmark
    public double baseline() {
        return Math.PI;
    }

    @Benchmark
    public double measureWrong_1() {
        // Math.PI是常量，值可预知，计算会被合并
        return Math.log(Math.PI);
    }

    @Benchmark
    public double measureWrong_2() {
        // wrongX是常量，值可预知，计算会被合并
        return Math.log(wrongX);
    }

    @Benchmark
    public double measureRight() {
        // x是普通变量，值不可预知，计算不会被合并
        return Math.log(x);
    }

    public static void main(String[] args) throws RunnerException {

        Options opt = new OptionsBuilder()
                .include(JMHSample_10_ConstantFold.class.getSimpleName())
                .warmupIterations(5)
                .measurementIterations(5)
                .forks(1)
                .build();

        new Runner(opt).run();
    }

}