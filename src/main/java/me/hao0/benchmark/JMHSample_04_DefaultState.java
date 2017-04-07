package me.hao0.benchmark;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

/**
 * 默认状态使用
 * <p>大部分情况下，我们会使用单例的状态对象，
 * 这时，可以标记当前类为 @State(Scope.Thread)，
 * 并在属性中使用状态变量
 * </p>
 */
@State(Scope.Thread)
public class JMHSample_04_DefaultState {

    double x = Math.PI;

    @Benchmark
    public void measure() {
        x++;
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(JMHSample_04_DefaultState.class.getSimpleName())
                .warmupIterations(5)
                .measurementIterations(5)
                .forks(1)
                .build();

        new Runner(opt).run();
    }

}