package me.hao0.benchmark;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Group;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;

/**
 * 由于CPU缓存行(Cache Line)失效导致的伪共享问题，及几种解决方案
 */
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(5)
public class JMHSample_22_FalseSharing {

    @State(Scope.Group)
    public static class StateBaseline {
        int readOnly;
        int writeOnly;
    }

    @Benchmark
    @Group("baseline")
    public int reader(StateBaseline s) {
        return s.readOnly;
    }

    @Benchmark
    @Group("baseline")
    public void writer(StateBaseline s) {
        s.writeOnly++;
    }

    /**
     * 1. 通过填充来解决伪共享问题，使得读写操作针对不同的缓存行(64byte为一个缓存行时)
     */
    @State(Scope.Group)
    public static class StatePadded {
        int readOnly;
        int p01, p02, p03, p04, p05, p06, p07, p08;
        int p11, p12, p13, p14, p15, p16, p17, p18;
        int writeOnly;
        int q01, q02, q03, q04, q05, q06, q07, q08;
        int q11, q12, q13, q14, q15, q16, q17, q18;
    }

    @Benchmark
    @Group("padded")
    public int reader(StatePadded s) {
        return s.readOnly;
    }

    @Benchmark
    @Group("padded")
    public void writer(StatePadded s) {
        s.writeOnly++;
    }

    /**
     * 2. 通过父类字段继承解决
     */
    public static class StateHierarchy_1 {
        int readOnly;
    }

    public static class StateHierarchy_2 extends StateHierarchy_1 {
        int p01, p02, p03, p04, p05, p06, p07, p08;
        int p11, p12, p13, p14, p15, p16, p17, p18;
    }

    public static class StateHierarchy_3 extends StateHierarchy_2 {
        int writeOnly;
    }

    public static class StateHierarchy_4 extends StateHierarchy_3 {
        int q01, q02, q03, q04, q05, q06, q07, q08;
        int q11, q12, q13, q14, q15, q16, q17, q18;
    }

    @State(Scope.Group)
    public static class StateHierarchy extends StateHierarchy_4 {
    }

    @Benchmark
    @Group("hierarchy")
    public int reader(StateHierarchy s) {
        return s.readOnly;
    }

    @Benchmark
    @Group("hierarchy")
    public void writer(StateHierarchy s) {
        s.writeOnly++;
    }

    /**
     * 3. 使用数组变量解决
     */
    @State(Scope.Group)
    public static class StateArray {
        int[] arr = new int[128];
    }

    @Benchmark
    @Group("sparse")
    public int reader(StateArray s) {
        return s.arr[0];
    }

    @Benchmark
    @Group("sparse")
    public void writer(StateArray s) {
        s.arr[64]++;
    }

    /**
     * 4. 通过JDK8中的@sun.misc.Contended注解，并开启-XX:-RestrictContended
     */
    @State(Scope.Group)
    public static class StateContended {
        int readOnly;

        // @sun.misc.Contended
        int writeOnly;
    }

    @Benchmark
    @Group("contended")
    public int reader(StateContended s) {
        return s.readOnly;
    }

    @Benchmark
    @Group("contended")
    public void writer(StateContended s) {
        s.writeOnly++;
    }


    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(JMHSample_22_FalseSharing.class.getSimpleName())
                .threads(Runtime.getRuntime().availableProcessors() * 2)
                .build();

        new Runner(opt).run();
    }

}