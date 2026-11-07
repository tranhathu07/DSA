package study;
import org.openjdk.jmh.runner.*;
import org.openjdk.jmh.runner.options.*;
import org.openjdk.jmh.results.format.ResultFormatType;
import java.nio.file.*;
import java.util.*;
import java.io.*;
import study.sort.MergeSort;

/** One parent JVM, isolated measurement forks; deterministic shuffled schedule. */
public class GridLauncher {
 public static void main(String[] args) throws Exception {
  MergeSort.sort(new Integer[0],0);study.sort.QuickSort.sort(new Integer[0],0);
  Properties config=new Properties();
  try(var in=Files.newInputStream(Path.of("configs/grid.properties"))){config.load(in);}
  Path out;
  boolean resuming = false;
  if (args.length > 0 && !args[0].isBlank()) {
   out = Path.of(args[0].trim());
   resuming = Files.isDirectory(out);
  } else {
   out = Path.of("results/grid-" + System.currentTimeMillis());
  }
  if (!resuming) {
   Files.createDirectories(out);
   Files.copy(Path.of("configs/grid.properties"), out.resolve("grid.properties"), StandardCopyOption.REPLACE_EXISTING);
   Files.writeString(out.resolve("environment.txt"), System.getProperties().toString());
  }
  List<String[]> jobs = new ArrayList<>();
  for (String line : Files.readAllLines(Path.of("data/manifest.csv")).subList(1, Files.readAllLines(Path.of("data/manifest.csv")).size()))
   for (String c : config.getProperty("cutoffs").split(",")) for (String algo : config.getProperty("algos", "merge,quick").split(",")) jobs.add(new String[]{line.split(",")[0], c, algo.trim()});
  Collections.shuffle(jobs, new Random(Long.parseLong(config.getProperty("orderSeed"))));
  if (!resuming) {
   try (var schedule = Files.newBufferedWriter(out.resolve("schedule.csv"))) {
    schedule.write("dataset,cutoff,algo\n");
    for (var job : jobs) schedule.write(job[0] + "," + job[1] + "," + job[2] + "\n");
   }
  }
  boolean rawExists = Files.exists(out.resolve("raw.csv")) && Files.size(out.resolve("raw.csv")) > 0;
  try (var csv = Files.newBufferedWriter(out.resolve("raw.csv"), StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
   if (!rawExists) csv.write("algo,dataset,cutoff,fork,iteration,time_ns\n");
   for (var job : jobs) {
    Path jsonFile = out.resolve(job[2] + "-" + job[0] + "-" + job[1] + ".json");
    if (Files.exists(jsonFile) && Files.size(jsonFile) > 0) {
     System.out.println("SKIP (already done): " + jsonFile.getFileName());
     continue;
    }
    Files.deleteIfExists(jsonFile);
    var options = new OptionsBuilder().include("study.SortBenchmark.sort").param("dataset", job[0]).param("cutoff", job[1]).param("algo", job[2])
     .warmupIterations(Integer.parseInt(config.getProperty("warmup"))).measurementIterations(Integer.parseInt(config.getProperty("measurements")))
     .forks(Integer.parseInt(config.getProperty("forks"))).shouldFailOnError(true)
     .resultFormat(ResultFormatType.JSON).result(jsonFile.toString()).build();
    for (var run : new Runner(options).run()) {
     int fork = 0;
     for (var bench : run.getBenchmarkResults()) {
      int iteration = 0;
      for (var result : bench.getIterationResults())
       csv.write(job[2] + "," + job[0] + "," + job[1] + "," + fork + "," + (iteration++) + "," + result.getPrimaryResult().getScore() + "\n");
      fork++;
     }
    }
    csv.flush();
   }
  }
 }
}
