public class MergeSort {
    private int[] lst;
    public static void merge_sort(int[] lst) {
        int n = lst.length;
        if(n<= 1) return;
        int lo = 0;
        int hi = n - 1;
        int[] a = new int[n];
        sort(lst, a, lo, hi);
    }
    public static void sort(int[] lst, int[] a, int lo, int hi){
        int mid = (lo+hi)/2;

        if (lo >=hi){
            return;
        }
        else{
            sort(lst,a,lo,mid);
            sort(lst,a,mid+1,hi);
            merge(lst,a,lo,hi);

        }
    }
    public static String toString(int[] lst) {
        StringBuilder s = new StringBuilder();
        if(lst.length ==0) return "";
        for (int i = 0; i<lst.length-1;i++){
            s.append(lst[i] +", ");
        }
        s.append(lst[lst.length-1]);
        return s.toString();
    }
    public static void merge(int[] lst, int[] a, int lo, int hi) {
        int i = lo;
        int mid = (lo + hi) / 2;
        int j = mid + 1;
        for (int m = lo; m <= hi; m++) {
            a[m] = lst[m];
        }
        //System.out.println(toString(a));
        for (int k = lo; k <= hi; k++) {
            if (i > mid) {
                lst[k] = a[j];
                j++;

            } else if (j > hi) {
                lst[k] = a[i];
                i++;
            } else if (a[i] <= a[j]) {
                lst[k] = a[i];
                i++;

            }
            //(a[i]>a[j])
            else {
                lst[k] = a[j];
                j++;
            }
        }
    }

    public static void main(String[] args){
        int[] lst = new int[] {};
        merge_sort(lst);
        System.out.println(toString(lst));
    }

}

