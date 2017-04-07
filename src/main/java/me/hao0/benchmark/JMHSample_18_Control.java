package me.hao0.benchmark;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Group;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Control;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 通过Control对象获取测试状态
 */
@State(Scope.Group)
public class JMHSample_18_Control {

    public final AtomicBoolean flag = new AtomicBoolean();

    @Benchmark
    @Group("pingpong")
    public void ping(Control cnt) {
        while (!cnt.stopMeasurement && !flag.compareAndSet(false, true)) {
            // this body is intentionally left blank
            // System.err.println("ping stop false");
        }
        // System.err.println("ping stop true");
    }

    @Benchmark
    @Group("pingpong")
    public void pong(Control cnt) {
        cnt.stopMeasurement = false;
        while (!cnt.stopMeasurement && !flag.compareAndSet(true, false)) {
            // this body is intentionally left blank
            // System.err.println("pong stop false");
        }
        // System.err.println("pong stop true");
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(JMHSample_18_Control.class.getSimpleName())
                .warmupIterations(1)
                .measurementIterations(5)
                .threads(2)
                .forks(1)
                .build();

        new Runner(opt).run();
    }

}