package me.hao0.benchmark;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.CompilerControl;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.WarmupMode;

import java.util.concurrent.TimeUnit;

/**
 * 使用WarmupMode，指定预热操作模式：
 * <p>
 *     <ul>
 *         <li>
 *             WarmupMode.INDI：在每个测试方法运行前作预热；
 *         </li>
 *         <li>
 *             WarmupMode.BULK：在所有测试方法运行前作统一的预热；
 *         </li>
 *         <li>
 *             WarmupMode.BULK_INDI：在所有测试方法运行前作统一的预热，且在每个测试方法运行前也作预热。
 *         </li>
 *     </ul>
 * </p>
 */
@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class JMHSample_32_BulkWarmup {

    /*
     * This is an addendum to JMHSample_12_Forking test.
     *
     * Sometimes you want an opposite configuration: instead of separating the profiles
     * for different benchmarks, you want to mix them together to test the worst-case
     * scenario.
     *
     * JMH has a bulk warmup feature for that: it does the warmups for all the tests
     * first, and then measures them. JMH still forks the JVM for each test, but once the
     * new JVM has started, all the warmups are being run there, before running the
     * measurement. This helps to dodge the type profile skews, as each test is still
     * executed in a different JVM, and we only "mix" the warmup code we want.
     */

    /*
     * These test classes are borrowed verbatim from JMHSample_12_Forking.
     */

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

    Counter c1 = new Counter1();
    Counter c2 = new Counter2();

    /*
     * And this is our test payload. Notice we have to break the inlining of the payload,
     * so that in could not be inlined in either measure_c1() or measure_c2() below, and
     * specialized for that only call.
     */

    @CompilerControl(CompilerControl.Mode.DONT_INLINE)
    public int measure(Counter c) {
        int s = 0;
        for (int i = 0; i < 10; i++) {
            s += c.inc();
        }
        return s;
    }

    @Benchmark
    public int measure_c1() {
        return measure(c1);
    }

    @Benchmark
    public int measure_c2() {
        return measure(c2);
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(JMHSample_32_BulkWarmup.class.getSimpleName())
                // .includeWarmup(...) <-- this may include other benchmarks into warmup
                .warmupMode(WarmupMode.BULK) // see other WarmupMode.* as well
                .warmupIterations(5)
                .measurementIterations(5)
                .forks(1)
                .build();

        new Runner(opt).run();
    }

}