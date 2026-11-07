# Hướng dẫn chạy (Windows, PowerShell)

## 0. Cài công cụ (một lần)
- JDK 17+ (khuyên dùng Temurin 21): `winget install EclipseAdoptium.Temurin.21.JDK`
- Maven 3.9: tải bản binary zip tại https://maven.apache.org/download.cgi, giải nén, thêm thư mục `bin` vào PATH
- Python 3.10+: `winget install Python.Python.3.12`
- JMH KHÔNG cần cài riêng: Maven tự tải jmh-core 1.37 theo pom.xml.
Mở PowerShell MỚI rồi kiểm tra: `java -version`, `mvn -version`, `python --version`.

## 1. Build + kiểm thử (tại thư mục gốc dự án)
    ./scripts/build.ps1 -RequireImplementation
Thấy BUILD SUCCESS và dòng study.SortBenchmark.sort là đạt.

## 2. Sinh dữ liệu
    java -cp target/benchmarks.jar study.data.GenerateData

## 3. Pilot chọn warm-up
    ./scripts/pilot.ps1 -Warmup 20 -Measurements 10 -Forks 3
Xem results/pilot-*/console.log; nếu chưa ổn định thử -Warmup 50, 100.

## 4. Lưới chính
Sửa configs/grid.properties (algos=merge,quick; warmup theo pilot). Lưới đầy đủ rất dài
(51 dataset x 18 cutoff x 2 thuật toán x 3 fork = 5508 JVM). Nên thu gọn: sao lưu data/manifest.csv
rồi chỉ giữ dòng tiêu đề + các dataset muốn đo (vd random_*_1..3, duplicates_*_1, ascending/descending).
    java -cp target/benchmarks.jar study.GridLauncher

## 5. Thống kê + biểu đồ
    python -m venv .venv
    ./.venv/Scripts/python.exe -m pip install -r requirements.txt
    ./.venv/Scripts/python.exe scripts/analyze.py results/grid-<số> --output results/analysis

## Phương án không cần Maven/JMH (chỉ cần JDK)
    mkdir target/classes
    javac -d target/classes (Get-ChildItem src/main -Recurse -Filter *.java).FullName
    java -cp target/classes study.harness.SelfTest
    java -cp target/classes study.data.GenerateData
    java -cp target/classes study.harness.Grid configs/run-100k.properties results/grid-100k
    python scripts/analyze_harness.py results/grid-100k --output results/analysis   (cần: pip install scipy pandas matplotlib)
