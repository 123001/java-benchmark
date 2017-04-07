package me.hao0.benchmark;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;

/**
 * Blackhole使用例子
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Thread)
public class JMHSample_09_Blackholes {

    double x1 = Math.PI;
    double x2 = Math.PI * 2;

    /*
     * Baseline measurement: how much single Math.log costs.
     */

    @Benchmark
    public double baseline() {
        return Math.log(x1);
    }

    @Benchmark
    public double measureWrong() {
        // 该行代码会被优化掉
        Math.log(x1);
        return Math.log(x2);
    }

    /*
     * This demonstrates Option A:
     *
     * Merge multiple results into one and return it.
     * This is OK when is computation is relatively heavyweight, and merging
     * the results does not offset the results much.
     */

    @Benchmark
    public double measureRight_1() {
        return Math.log(x1) + Math.log(x2);
    }


    /**
     * 使用Blackhole消除死代码优化
     */
    @Benchmark
    public void measureRight_2(Blackhole bh) {
        bh.consume(Math.log(x1));
        bh.consume(Math.log(x2));
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(JMHSample_09_Blackholes.class.getSimpleName())
                .warmupIterations(5)
                .measurementIterations(5)
                .forks(1)
                .build();

        new Runner(opt).run();
    }
}