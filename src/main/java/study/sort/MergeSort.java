package study.sort;

/**
 * Top-down mergesort theo bài giảng (Sedgewick & Wayne, Algorithms 4e, mục 2.2)
 * kèm cải tiến CUTOFF sang insertion sort cho mảng con nhỏ.
 *
 * Quy ước CUTOFF:
 *   cutoff = 0  : mergesort gốc, đệ quy tới tận đoạn 1 phần tử.
 *   cutoff > 0  : đoạn có tối đa `cutoff` phần tử được sắp bằng insertion sort.
 *   cutoff < 0  : IllegalArgumentException.
 * Mỗi lần gọi công khai trên mảng không tầm thường cấp phát đúng một mảng phụ.
 * Thuật toán ổn định (khi bằng nhau, phần tử của nửa trái được lấy trước).
 */
public final class MergeSort {
    private MergeSort() {}

    @SuppressWarnings("rawtypes")
    public static void sort(Comparable[] a, int cutoff) {
        if (cutoff < 0) throw new IllegalArgumentException("cutoff must be >= 0: " + cutoff);
        int n = a.length;
        if (n < 2) return;
        Comparable[] aux = new Comparable[n];   // một mảng phụ duy nhất
        sort(a, aux, 0, n - 1, cutoff);
    }

    @SuppressWarnings("rawtypes")
    private static void sort(Comparable[] a, Comparable[] aux, int lo, int hi, int cutoff) {
        if (hi <= lo) return;
        if (hi - lo + 1 <= cutoff) {            // đoạn có <= cutoff phần tử
            insertionSort(a, lo, hi);
            return;
        }
        int mid = lo + (hi - lo) / 2;
        sort(a, aux, lo, mid, cutoff);
        sort(a, aux, mid + 1, hi, cutoff);
        merge(a, aux, lo, mid, hi);
    }

    /** Trộn a[lo..mid] và a[mid+1..hi] (đã sắp) qua mảng phụ, giữ tính ổn định. */
    @SuppressWarnings({"rawtypes", "unchecked"})
    private static void merge(Comparable[] a, Comparable[] aux, int lo, int mid, int hi) {
        for (int k = lo; k <= hi; k++) aux[k] = a[k];
        int i = lo, j = mid + 1;
        for (int k = lo; k <= hi; k++) {
            if      (i > mid)                          a[k] = aux[j++];
            else if (j > hi)                           a[k] = aux[i++];
            else if (aux[j].compareTo(aux[i]) < 0)     a[k] = aux[j++];   // chỉ lấy bên phải khi nhỏ hơn hẳn
            else                                       a[k] = aux[i++];
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    static void insertionSort(Comparable[] a, int lo, int hi) {
        for (int i = lo + 1; i <= hi; i++)
            for (int j = i; j > lo && a[j].compareTo(a[j - 1]) < 0; j--) {
                Comparable t = a[j]; a[j] = a[j - 1]; a[j - 1] = t;
            }
    }
}
