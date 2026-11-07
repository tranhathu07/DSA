package study;
import org.openjdk.jmh.annotations.*;
import java.util.concurrent.TimeUnit;
import java.nio.file.*;
import java.io.*;
import java.util.*;
import study.sort.MergeSort;
import study.sort.QuickSort;

@State(Scope.Thread) @BenchmarkMode(Mode.SingleShotTime) @OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations=20) @Measurement(iterations=10) @Fork(3)
public class SortBenchmark {
 @Param({"merge","quick"}) public String algo;
 @Param({"random_10000_1"}) public String dataset;
 @Param({"0","1","2","3","4","5","6","7","8","9","10","12","15","20","24","32","48","64"}) public int cutoff;
 private Integer[] original, expected, working;
 @Setup(Level.Trial) public void load() throws Exception {
  sortWith(new Integer[0]); // Fail immediately if the implementation is missing.
  try(var in=new DataInputStream(Files.newInputStream(Path.of("data/inputs",dataset+".bin")))) {
   int n=in.readInt(); original=new Integer[n];for(int i=0;i<n;i++)original[i]=in.readInt();
  }
  expected=original.clone();Arrays.sort(expected);
 }
 @Setup(Level.Iteration) public void reset(){working=original.clone();}
 private void sortWith(Integer[] a){if(algo.equals("quick"))QuickSort.sort(a,cutoff);else MergeSort.sort(a,cutoff);}
 @Benchmark public Integer[] sort(){sortWith(working);return working;}
 @TearDown(Level.Iteration) public void verify(){if(!Arrays.equals(expected,working))throw new IllegalStateException("Incorrect sorting");}
}
