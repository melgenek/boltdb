sbt jmh:run ".*FreeListBenchmark.*"
sbt  jmh:run ".*FreeListBenchmark.*"  -prof 

sbt 'jmh:run -prof "async:dir=target/async-reports;interval=1000000;output=flamegraph;libPath=/Users/ymelnyk/Downloads/async-profiler-2.8.3-macos/build/libasyncProfiler.so" ".*FreeListBenchmark.*"'

sbt 'jmh:run -prof async:help'