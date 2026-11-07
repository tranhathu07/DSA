# MergeSort Cutoff Lab

Dự án Java thực nghiệm tìm ngưỡng CUTOFF phù hợp để mergesort chuyển sang insertion sort trên đoạn nhỏ, sử dụng JMH để đo hiệu năng và Python để thống kê, vẽ biểu đồ.

## Lưu ý trước khi chạy

`src/main/java/study/sort/MergeSort.java` hiện còn TODO. Cần hoàn thiện thuật toán và vượt qua kiểm thử ở bước 2 trước khi chạy benchmark.

## 1. Chuẩn bị môi trường

Cần JDK 17 trở lên, Maven 3.9, PowerShell và Python 3.10 trở lên nếu dùng script phân tích. Giữ nguyên JDK trong toàn bộ đợt đo.

Mở PowerShell tại thư mục gốc dự án và kiểm tra:

```powershell
java -version
mvn -version
```

Nếu Java chưa có trên PATH, đặt `JAVA_HOME` trỏ tới thư mục JDK đã cài và thêm thư mục `bin` của JDK vào PATH. Các lệnh tiếp theo chạy tại thư mục gốc dự án.

## 2. Build và kiểm thử

```powershell
./scripts/build.ps1
```

Script tải dependency vào `.m2` trong dự án, chạy `clean package` với profile `benchmark`, tạo `target/benchmarks.jar` và kiểm tra JMH nhận diện `study.SortBenchmark.sort`.

Khi thuật toán còn TODO, test in `SCAFFOLD ONLY` và cho phép build khung. Thành công này chưa xác nhận tính đúng của thuật toán.

Hoàn thiện cài đặt trong `MergeSort.java`, giữ API:

```java
public static void sort(Comparable[] a, int cutoff)
```

Quy ước: cutoff 0 là mergesort gốc; cutoff dương tính theo số phần tử của đoạn; cutoff âm bị từ chối bằng `IllegalArgumentException`. Thuật toán hỗ trợ mảng rỗng, giữ tính ổn định và cấp phát một mảng phụ mỗi lần sort công khai trên mảng không tầm thường. Giữ các lựa chọn thuật toán khác theo bài giảng.

Trước khi đo, bắt buộc chạy:

```powershell
./scripts/build.ps1 -RequireImplementation
```

Lệnh này thất bại nếu còn TODO. Benchmark cũng từ chối chạy placeholder.

## 3. Sinh bộ dữ liệu

```powershell
java -cp target/benchmarks.jar study.data.GenerateData
```

Generator tạo 51 datasets: ba kích thước 10.000, 100.000, 1.000.000; ba phân bố ngẫu nhiên/gần tăng dần/nhiều giá trị trùng với 5 seed; hai phân bố tăng dần/giảm dần với một dataset xác định ở mỗi kích thước.

Duplicates dùng `1_000_000 + nextInt(16)` và kiểm tra offset không bị cache trên JVM đang dùng. `data/manifest.csv` lưu dataset-id, kích thước, phân bố, seed và SHA-256. File nhị phân chứa một int32 big-endian biểu diễn độ dài, tiếp theo N giá trị int32.

Chạy lại generator sẽ ghi đè file cùng tên. Lưu bộ dữ liệu và manifest của đợt nghiên cứu trước khi sửa generator.

## 4. Chạy pilot để chọn warm-up

Chỉ chạy sau khi thuật toán hoàn chỉnh và kiểm thử bắt buộc đạt:

```powershell
./scripts/pilot.ps1 -Warmup 20 -Measurements 10 -Forks 3
```

| Tham số | Ý nghĩa | Mặc định |
|---|---|---|
| Warmup | Số lần chạy làm nóng mỗi JVM, không tính vào kết quả đo | 20 |
| Measurements | Số lần đo chính mỗi JVM | 10 |
| Forks | Số JVM độc lập mỗi cấu hình | 3 |

Lệnh `./scripts/pilot.ps1 -Warmup 20` tương đương lệnh đầy đủ vì hai tham số cuối đã có mặc định.

Mỗi pilot thử 4 datasets × 5 CUTOFF × 3 forks = 60 JVM đo. Script tự build và kiểm thử trước khi chạy. Giữ chế độ nguồn cố định, cắm sạc và hạn chế tác vụ nền; không chạy nhiều benchmark cùng lúc.

Mỗi lượt tạo thư mục `results/pilot-<thời gian>`:

| File | Nội dung |
|---|---|
| console.log | Thời gian từng vòng warm-up, measurement và thông tin JMH |
| jmh.json | Số liệu measurement gốc |
| environment.txt | Phiên bản Java |
| version-stdout.txt | Stdout của lệnh lấy phiên bản; có thể rỗng |

Trong log, `ns/op` là nano giây mỗi lần sort; 1.000.000 ns/op bằng 1 ms/lần sort.

Xem các vòng cuối warm-up, xu hướng trong measurement và mean từng fork. Nếu chưa ổn định, thử thêm warm-up 50 rồi 100, chờ từng lượt kết thúc:

```powershell
./scripts/pilot.ps1 -Warmup 50 -Measurements 10 -Forks 3
./scripts/pilot.ps1 -Warmup 100 -Measurements 10 -Forks 3
```

Không mặc định 100 vòng bảo đảm JIT ổn định. Nếu các fork vẫn chênh nhiều lần, cần xem compilation, GC và tải máy trước khi chạy chính. Không chọn CUTOFF từ một phép đo nhanh nhất và không gộp pilot vào kết quả chính.

JMH dùng SingleShotTime: mỗi iteration đo một lần sort. Dataset được đọc và boxing ở trial setup, clone ở iteration setup; kiểm tra kết quả sau đo. Chi phí cấp phát mảng phụ trong hàm sort nằm trong thời gian đo.

## 5. Chạy lưới chính

Sau khi pilot đủ ổn định và có dự toán thời gian phù hợp, sửa `configs/grid.properties`. Ví dụ dưới đây chỉ minh họa, không khẳng định warm-up 50 đã đủ:

```properties
cutoffs=0,1,2,3,4,5,6,7,8,9,10,12,15,20,24,32,48,64
warmup=50
measurements=10
forks=3
orderSeed=20261103
```

Chạy:

```powershell
./scripts/build.ps1 -RequireImplementation
java -cp target/benchmarks.jar study.GridLauncher
```

Launcher đảo thứ tự cặp dataset/CUTOFF theo seed, giữ JVM fork riêng từng cấu hình và lưu lịch chạy, cấu hình, thuộc tính JVM, JSON JMH cùng CSV thô.

Ma trận đầy đủ có 918 cấu hình, tương ứng 2.754 JVM đo với 3 forks. Ước lượng thời gian từ pilot trước khi chạy. Launcher chưa hỗ trợ tự tiếp tục lượt bị gián đoạn; xác định dữ liệu thiếu trước khi phân tích.

## 6. Thống kê và vẽ biểu đồ

Tạo môi trường Python và cài thư viện:

```powershell
python -m venv .venv
./.venv/Scripts/python.exe -m pip install -r requirements.txt
```

Nếu lệnh `python` không hoạt động, dùng `py -3` hoặc đường dẫn đầy đủ tới Python đã cài. Thay `TEN-THU-MUC` bằng lượt chạy thực tế:

```powershell
./.venv/Scripts/python.exe scripts/analyze.py results/TEN-THU-MUC --output results/analysis-TEN-THU-MUC
```

Thư mục đầu vào chỉ chứa JSON JMH của đợt muốn phân tích. Script xuất `raw.csv`, `datasets.csv`, `summary.csv` và biểu đồ đường PNG với error bars Mean ± SD.

Script tổng hợp iteration trong mỗi fork, rồi fork means trong mỗi dataset. Với nhiều seed, SD biểu đồ mô tả biến thiên giữa dataset means; với dataset xác định, SD mô tả biến thiên giữa fork means. Mean ± SD không phải khoảng tin cậy hoặc bằng chứng tự động về ý nghĩa thống kê.

Bạn có thể mở CSV bằng Excel để kiểm tra và vẽ bổ sung. Giữ JSON gốc và log, không sửa tay số liệu đo.

## 7. Lưu trữ kết quả

Dữ liệu sinh nằm trong `data/`, kết quả chạy nằm trong `results/`. Các thư mục này cùng `target/`, `.m2/` và `.venv/` được loại khỏi Git. Khi chuyển dự án sang máy khác, có thể sinh lại dữ liệu và chạy lại thí nghiệm theo các bước trên.

Để lưu một đợt thí nghiệm, giữ bộ dữ liệu và manifest, cấu hình chạy, phiên bản mã nguồn, JSON gốc, log và kết quả phân tích tương ứng. Có thể dùng `report/outline.md` làm dàn ý báo cáo.

## 8. Xử lý lỗi thường gặp

| Hiện tượng | Cách xử lý |
|---|---|
| SCAFFOLD ONLY | Thuật toán còn TODO; chỉ kiểm tra được build khung |
| MergeSort is still TODO | Chế độ bắt buộc đã chặn placeholder đúng dự kiến |
| Thiếu /META-INF/BenchmarkList | Chạy build.ps1 để clean build với profile benchmark và annotation processor JMH |
| Java version ghi ra stderr | Bình thường; script hiện tại thu thông tin bằng tiến trình riêng |
| py -3 không tìm thấy Python | Dùng đường dẫn Python đầy đủ như mục 6 |
| Pilot failed | Mở console.log tương ứng và tìm lỗi gốc trước dòng tổng kết |
