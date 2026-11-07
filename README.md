# Nghiên cứu Thực nghiệm Ngưỡng CUTOFF cho MergeSort và QuickSort (Cutoff Research Lab)

> **Môn học**: Cấu trúc Dữ liệu và Giải thuật (DSA Project) — Đại học Công nghệ (VNU-UET)  
> **Nền tảng**: OpenJDK 21 LTS, JMH 1.37, Python 3 (Matplotlib, Pandas)  
> **Báo cáo đầy đủ**: [report/BAO_CAO.md](report/BAO_CAO.md)  
> **Báo cáo đầy đủ**:https://drive.google.com/file/d/13FA73IB-Zb7_GNrLCbrWXDwBqEkM8CGA/view?usp=sharing

---

## 📌 Tổng quan Dự án

Dự án thực nghiệm nghiên cứu và xác định ngưỡng **CUTOFF** tối ưu để chuyển từ giải thuật chia để trị (**MergeSort** và **QuickSort**) sang thuật toán sắp xếp chèn sơ cấp (**Insertion Sort**) trên các đoạn con nhỏ, dựa trên lý thuyết kinh điển của Robert Sedgewick & Kevin Wayne (*Algorithms, 4th Edition*).

### Kết quả Cốt lõi
* **Dữ liệu ngẫu nhiên (Random)**: Ngưỡng tối ưu thực nghiệm tập trung tại **$\text{CUTOFF} = 4 - 10$** (chính xác nhất tại $c = 9$ cho QuickSort và $c = 5 - 10$ cho MergeSort), giúp cải thiện tốc độ từ **$+5\%$ đến $+12.9\%$** so với thuật toán gốc ($c = 0$). Vượt quá $c = 16$, chi phí $O(k^2)$ của Insertion Sort bắt đầu kéo chậm thuật toán.
* **Dữ liệu gần tăng dần (Nearly Sorted) & Tăng dần (Ascending)**: MergeSort hưởng lợi vượt trội ở cutoff lớn ($c = 32, 64$), tăng tốc lên tới **$+26.5\%$ đến $+44.6\%$** do Insertion Sort đạt tiệm cận tuyến tính $O(k)$ và triệt tiêu chi phí đệ quy / mảng phụ.
* **Dữ liệu giảm dần (Descending)**: Cảnh báo trường hợp xấu nhất, cutoff lớn ($c = 64$) làm chậm thuật toán tới **$137\%$** do số nghịch thế cực đại.
* **Khuyến nghị thực tiễn**: Đặt **$\text{CUTOFF} = 8 - 10$** khi chưa biết trước phân bố dữ liệu.

---

## 📁 Cấu trúc Thư mục

```text
sort-cutoff-research/
├── configs/
│   └── grid.properties          # Cấu hình đo lưới chính (cutoffs, warmup, forks)
├── data/
│   └── manifest.csv             # Danh mục 51 dataset nhị phân chuẩn hóa kèm SHA-256
├── report/
│   ├── BAO_CAO.md               # Bản báo cáo khoa học thực nghiệm toàn diện
│   ├── outline.md               # Khung đề cương báo cáo gốc
│   └── images/                  # 32 biểu đồ phân tích thực nghiệm Mean ± SD (PNG)
├── scripts/
│   ├── build.ps1                # Script build và kiểm thử tự động
│   ├── pilot.ps1                # Script chạy thử nghiệm Pilot chọn Warm-up
│   └── analyze.py               # Script Python tổng hợp CSV và xuất biểu đồ
├── src/
│   ├── main/java/study/sort/    # Thuật toán MergeSort & QuickSort hoàn chỉnh
│   ├── main/java/study/data/    # Generator sinh 51 bộ dữ liệu chuẩn hóa
│   └── jmh/java/study/          # SortBenchmark & GridLauncher (hỗ trợ Resume)
├── pom.xml                      # Cấu hình Maven & JMH shaded jar
└── README.md                    # Hướng dẫn dự án
```

---

## 🚀 Hướng dẫn Cài đặt và Tái lập Thí nghiệm

### 1. Chuẩn bị Môi trường
* **JDK 21 LTS** (khuyên dùng Temurin 21): `java -version`
* **Maven 3.9+**: `mvn -version`
* **Python 3.10+**: `python --version`

Cài đặt thư viện phân tích:
```powershell
python -m venv .venv
./.venv/Scripts/python.exe -m pip install -r requirements.txt
```

### 2. Build Dự án và Kiểm thử Thuật toán
Dự án yêu cầu thuật toán phải hoàn thiện và vượt qua kiểm thử tính đúng trước khi đo:
```powershell
./scripts/build.ps1 -RequireImplementation
```

### 3. Sinh Bộ Dữ liệu (51 Datasets)
Sinh dữ liệu nhị phân chuẩn hóa cho 3 kích thước ($10\text{k}, 100\text{k}, 1\text{M}$) và 5 kiểu phân bố:
```powershell
java -cp target/benchmarks.jar study.data.GenerateData
```

### 4. Chạy Thử nghiệm Pilot (Chọn Warm-up)
Thử nghiệm trên 4 dataset tiêu biểu để kiểm tra độ ổn định của JIT C2:
```powershell
./scripts/pilot.ps1 -Warmup 50 -Measurements 10 -Forks 3
```
*(Kết quả pilot cho thấy `Warmup = 50` giúp độ lệch chuẩn tương đối giảm từ $35\%$ xuống dưới $5\%$)*.

### 5. Chạy Lưới Đo Chính (Grid Launcher)
Cấu hình tham số trong `configs/grid.properties`, sau đó chạy:
```powershell
java -cp target/benchmarks.jar study.GridLauncher 2>&1 | Tee-Object "results/grid-run.log"
```

#### Tính năng Resume (Chạy tiếp khi bị gián đoạn):
Nếu quá trình đo bị dừng giữa chừng (ví dụ tắt máy hoặc bận việc), bạn có thể chạy tiếp mà không bị mất dữ liệu đã đo:
```powershell
java -cp target/benchmarks.jar study.GridLauncher results/grid-<timestamp>
```
Chương trình sẽ tự động phát hiện và bỏ qua các cấu hình đã đo xong, chỉ thực hiện các cấu hình còn thiếu.

### 6. Phân tích Dữ liệu và Xuất Biểu đồ
Tổng hợp kết quả đo thô thành các file CSV (`raw.csv`, `datasets.csv`, `summary.csv`) và vẽ biểu đồ đường $\text{Mean} \pm \text{SD}$:
```powershell
python scripts/analyze.py results/grid-<timestamp> --output results/analysis-grid
```

---

## 📊 Báo cáo và Số liệu Nghiên cứu

Chi tiết toàn bộ bảng số liệu đo đạc, phân tích độ phức tạp, biểu đồ minh họa và thảo luận chuyên sâu có tại:
👉 **[Xem Báo cáo Chi tiết (report/BAO_CAO.md)](report/BAO_CAO.md)**

---

## 👥 Tác giả & Giấy phép
* **Sinh viên thực hiện**: Hà Thu (VNU-UET)
* **Công cụ hỗ trợ**: Google Antigravity (Advanced Agentic AI)
* **Giấy phép**: Phục vụ mục đích học tập và nghiên cứu môn Cấu trúc Dữ liệu & Giải thuật.
