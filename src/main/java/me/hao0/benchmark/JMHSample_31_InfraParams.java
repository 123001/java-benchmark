package me.hao0.benchmark;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.BenchmarkParams;
import org.openjdk.jmh.infra.IterationParams;
import org.openjdk.jmh.infra.ThreadParams;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * 在测试中使用上下文参数
 * <p>
 *     <ul>
 *         <li>
 *              BenchmarkParams：当前测试的全局上下文
 *         </li>
 *         <li>
 *              IterationParams：当前迭代的上下文
 *         </li>
 *         <li>
 *              ThreadParams：当前线程的上下文
 *         </li>
 *     </ul>
 * </p>
 */
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.SECONDS)
@State(Scope.Benchmark)
public class JMHSample_31_InfraParams {

    static final int THREAD_SLICE = 1000;

    private ConcurrentHashMap<String, String> mapSingle;
    private ConcurrentHashMap<String, String> mapFollowThreads;

    @Setup
    public void setup(BenchmarkParams params) {
        int capacity = 16 * THREAD_SLICE * params.getThreads();
        mapSingle        = new ConcurrentHashMap<>(capacity, 0.75f, 1);
        mapFollowThreads = new ConcurrentHashMap<>(capacity, 0.75f, params.getThreads());
    }

    /*
     * Here is another neat trick. Generate the distinct set of keys for all threads:
     */

    @State(Scope.Thread)
    public static class Ids {
        private List<String> ids;

        @Setup
        public void setup(ThreadParams threads) {
            ids = new ArrayList<>();
            for (int c = 0; c < THREAD_SLICE; c++) {
                ids.add("ID" + (THREAD_SLICE * threads.getThreadIndex() + c));
            }
        }
    }

    @Benchmark
    public void measureDefault(Ids ids) {
        for (String s : ids.ids) {
            mapSingle.remove(s);
            mapSingle.put(s, s);
        }
    }

    @Benchmark
    public void measureFollowThreads(Ids ids, IterationParams iterationParams) {
        for (String s : ids.ids) {
            mapFollowThreads.remove(s);
            mapFollowThreads.put(s, s);
        }
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(JMHSample_31_InfraParams.class.getSimpleName())
                .warmupIterations(5)
                .measurementIterations(5)
                .threads(4)
                .forks(5)
                .build();

        new Runner(opt).run();
    }

}