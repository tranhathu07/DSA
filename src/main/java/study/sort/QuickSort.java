package study.sort;

import java.util.Random;

/**
 * Quicksort theo bài giảng (Sedgewick & Wayne, Algorithms 4e, mục 2.3):
 * xáo trộn ngẫu nhiên trước, partition kiểu Hoare (hai con trỏ i, j),
 * phần tử chốt là a[lo], không dùng median-of-3 hay 3-way partition
 * để cô lập ảnh hưởng của CUTOFF.
 *
 * Quy ước CUTOFF giống MergeSort: 0 = quicksort gốc; > 0 = đoạn có tối đa
 * `cutoff` phần tử dùng insertion sort; < 0 = IllegalArgumentException.
 * Quicksort KHÔNG ổn định. Seed xáo trộn cố định để lần đo lặp lại được.
 */
public final class QuickSort {
    private QuickSort() {}
    private static final long SHUFFLE_SEED = 20261106L;

    @SuppressWarnings("rawtypes")
    public static void sort(Comparable[] a, int cutoff) {
        if (cutoff < 0) throw new IllegalArgumentException("cutoff must be >= 0: " + cutoff);
        if (a.length < 2) return;
        shuffle(a, new Random(SHUFFLE_SEED));
        sort(a, 0, a.length - 1, cutoff);
    }

    @SuppressWarnings("rawtypes")
    private static void shuffle(Comparable[] a, Random rnd) {
        for (int i = a.length - 1; i > 0; i--) {          // Fisher-Yates
            int r = rnd.nextInt(i + 1);
            Comparable t = a[i]; a[i] = a[r]; a[r] = t;
        }
    }

    @SuppressWarnings("rawtypes")
    private static void sort(Comparable[] a, int lo, int hi, int cutoff) {
        if (hi <= lo) return;
        if (hi - lo + 1 <= cutoff) {
            MergeSort.insertionSort(a, lo, hi);
            return;
        }
        int j = partition(a, lo, hi);
        sort(a, lo, j - 1, cutoff);
        sort(a, j + 1, hi, cutoff);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static int partition(Comparable[] a, int lo, int hi) {
        int i = lo, j = hi + 1;
        Comparable v = a[lo];
        while (true) {
            while (a[++i].compareTo(v) < 0) if (i == hi) break;
            while (v.compareTo(a[--j]) < 0) if (j == lo) break;
            if (i >= j) break;
            Comparable t = a[i]; a[i] = a[j]; a[j] = t;
        }
        Comparable t = a[lo]; a[lo] = a[j]; a[j] = t;
        return j;
    }
}
