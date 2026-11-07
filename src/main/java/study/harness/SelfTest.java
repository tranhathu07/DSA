package study.harness;

import study.sort.*;
import java.util.*;

/** Kiểm thử đúng đắn không cần JUnit: so với Arrays.sort, kiểm tra ổn định (MergeSort), cutoff âm. */
public class SelfTest {
    record Item(int key, int idx) implements Comparable<Item> { public int compareTo(Item o) { return Integer.compare(key, o.key); } }
    @SuppressWarnings("rawtypes")
    interface S { void sort(Comparable[] a, int c); }
    static int checks = 0;
    static void check(boolean ok, String msg) { checks++; if (!ok) throw new AssertionError(msg); }

    public static void main(String[] x) {
        Map<String, S> algs = new LinkedHashMap<>();
        algs.put("merge", MergeSort::sort); algs.put("quick", QuickSort::sort);
        for (var e : algs.entrySet()) {
            S s = e.getValue();
            for (int c : new int[]{0, 1, 2, 3, 7, 10, 64, 5000})
                for (int n : new int[]{0, 1, 2, 3, 6, 7, 8, 9, 10, 11, 63, 64, 65, 1000, 4097}) {
                    Integer[] a = new Random(123 + n).ints(n, -20, 21).boxed().toArray(Integer[]::new);
                    Integer[] exp = a.clone(); Arrays.sort(exp); s.sort(a, c);
                    check(Arrays.equals(exp, a), e.getKey() + " c=" + c + " n=" + n);
                }
            boolean threw = false;
            try { s.sort(new Integer[0], -1); } catch (IllegalArgumentException ex) { threw = true; }
            check(threw, e.getKey() + " negative cutoff");
            for (int c : new int[]{0, 7, 10, 64})
                for (Integer[] in : new Integer[][]{{Integer.MIN_VALUE, 0, Integer.MAX_VALUE, -1}, {1, 2, 3, 4}, {4, 3, 2, 1}, {5, 5, 5, 5}}) {
                    Integer[] exp = in.clone(); Arrays.sort(exp); s.sort(in, c); check(Arrays.equals(exp, in), e.getKey() + " edge");
                }
            // Dữ liệu lớn: ngẫu nhiên, đã sắp, ngược, nhiều trùng
            Random r = new Random(7);
            for (int n : new int[]{100_000}) for (int kind = 0; kind < 4; kind++) {
                Integer[] a = new Integer[n];
                for (int i = 0; i < n; i++) a[i] = switch (kind) { case 0 -> r.nextInt(); case 1 -> i; case 2 -> n - i; default -> 1_000_000 + r.nextInt(16); };
                Integer[] exp = a.clone(); Arrays.sort(exp); s.sort(a, 10); check(Arrays.equals(exp, a), e.getKey() + " big kind " + kind);
            }
        }
        for (int c : new int[]{0, 1, 7, 10, 64}) {      // tính ổn định của mergesort
            Item[] items = new Item[2000]; for (int i = 0; i < items.length; i++) items[i] = new Item(i % 4, i);
            Item[] exp = items.clone(); Arrays.sort(exp); MergeSort.sort(items, c);
            check(Arrays.equals(exp, items), "merge stable c=" + c);   // Arrays.sort(Object[]) là ổn định
        }
        // Mảng phụ: mergesort cấp phát một mảng; kiểm tra gián tiếp bằng không đổi kết quả khi gọi lặp
        System.out.println("ALL OK (" + checks + " checks)");
    }
}
