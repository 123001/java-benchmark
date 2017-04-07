package me.hao0.benchmark;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;

/**
 * 死代码消除的例子
 * <p>
 *     baseline和measureWrong的测试结果相近，
 *     因为，measureWrong的代码会被优化掉；
 *     但由于measureRight返回了结果(该结果会被Blackholes隐性使用)，因此代码不会被优化掉。
 * </p>
 */
@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class JMHSample_08_DeadCode {

    private double x = Math.PI;

    @Benchmark
    public void baseline() {
        // 什么不做，与measureWrong对比
    }

    @Benchmark
    public void measureWrong() {
        // 由于方法没有使用到返回值，因此该代码会被优化掉
        Math.log(x);
    }

    @Benchmark
    public double measureRight() {
        // Blackholes会使用到返回值，因此该代码不会被优化掉
        return Math.log(x);
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(JMHSample_08_DeadCode.class.getSimpleName())
                .warmupIterations(5)
                .measurementIterations(5)
                .forks(1)
                .build();

        new Runner(opt).run();
    }

}