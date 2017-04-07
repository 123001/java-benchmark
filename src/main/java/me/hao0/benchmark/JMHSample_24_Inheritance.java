package me.hao0.benchmark;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import java.util.concurrent.TimeUnit;

/**
 * 使用测试父类
 */
public class JMHSample_24_Inheritance {

    @BenchmarkMode(Mode.AverageTime)
    @Fork(1)
    @State(Scope.Thread)
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    public static abstract class AbstractBenchmark {
        int x;

        @Setup
        public void setup() {
            x = 42;
        }

        @Benchmark
        @Warmup(iterations = 5, time = 100, timeUnit = TimeUnit.MILLISECONDS)
        @Measurement(iterations = 5, time = 100, timeUnit = TimeUnit.MILLISECONDS)
        public double bench() {
            return doWork() * doWork();
        }

        protected abstract double doWork();
    }

    public static class BenchmarkLog extends AbstractBenchmark {
        @Override
        protected double doWork() {
            return Math.log(x);
        }
    }

    public static class BenchmarkSin extends AbstractBenchmark {
        @Override
        protected double doWork() {
            return Math.sin(x);
        }
    }

    public static class BenchmarkCos extends AbstractBenchmark {
        @Override
        protected double doWork() {
            return Math.cos(x);
        }
    }


    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(JMHSample_24_Inheritance.class.getSimpleName())
                .build();

        new Runner(opt).run();
    }

}