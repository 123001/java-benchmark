package me.hao0.benchmark;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * 分支预测的性能测试，将提升执行性能
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(5)
@State(Scope.Benchmark)
public class JMHSample_36_BranchPrediction {

    private static final int COUNT = 1024 * 1024;

    private byte[] sorted;
    private byte[] unsorted;

    @Setup
    public void setup() {
        sorted = new byte[COUNT];
        unsorted = new byte[COUNT];
        Random random = new Random(1234);
        random.nextBytes(sorted);
        random.nextBytes(unsorted);
        Arrays.sort(sorted);
    }

    @Benchmark
    @OperationsPerInvocation(COUNT)
    public void sorted(Blackhole bh1, Blackhole bh2) {
        for (byte v : sorted) {
            if (v > 0) {
                bh1.consume(v);
            } else {
                bh2.consume(v);
            }
        }
    }

    @Benchmark
    @OperationsPerInvocation(COUNT)
    public void unsorted(Blackhole bh1, Blackhole bh2) {
        for (byte v : unsorted) {
            if (v > 0) {
                bh1.consume(v);
            } else {
                bh2.consume(v);
            }
        }
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(".*" + JMHSample_36_BranchPrediction.class.getSimpleName() + ".*")
                .build();

        new Runner(opt).run();
    }

}