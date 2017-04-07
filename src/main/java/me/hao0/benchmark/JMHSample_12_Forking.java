package me.hao0.benchmark;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;

/**
 * 使用@Fork在相同或不同进程中进行测试
 */
@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class JMHSample_12_Forking {

    public interface Counter {
        int inc();
    }

    public class Counter1 implements Counter {
        private int x;

        @Override
        public int inc() {
            return x++;
        }
    }

    public class Counter2 implements Counter {
        private int x;

        @Override
        public int inc() {
            return x++;
        }
    }

    public int measure(Counter c) {
        int s = 0;
        for (int i = 0; i < 10; i++) {
            s += c.inc();
        }
        return s;
    }

    Counter c1 = new Counter1();
    Counter c2 = new Counter2();

    @Benchmark
    @Fork(0)
    public int measure_1_c1() {
        return measure(c1);
    }

    @Benchmark
    @Fork(0)
    public int measure_2_c2() {
        return measure(c2);
    }


    @Benchmark
    @Fork(0)
    public int measure_3_c1_again() {
        return measure(c1);
    }


    @Benchmark
    @Fork(1)
    public int measure_4_forked_c1() {
        return measure(c1);
    }

    @Benchmark
    @Fork(1)
    public int measure_5_forked_c2() {
        return measure(c2);
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(JMHSample_12_Forking.class.getSimpleName())
                .warmupIterations(5)
                .measurementIterations(5)
                .build();

        new Runner(opt).run();
    }

}