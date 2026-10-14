import java.util.Random;
public class QuickSort {
    public static void randomShuffle(int[] lst){
        Random rnd = new Random();
        for(int i = 1; i<lst.length;i++){
            int j = rnd.nextInt(0,i);
            int temp = lst[i];
            lst[i] = lst[j];
            lst[j] = temp;
        }
    }
    public static void qsort(int[] lst){
        int n = lst.length;
        int lo = 0;
        int hi = n-1;
        randomShuffle(lst);
        sort(lst,lo,hi);

    }
    public static void sort(int[] lst, int lo,int hi){
        if (lo>=hi) return;
        int j = partition(lst,lo,hi);
        sort(lst,lo,j-1);
        sort(lst,j+1,hi);

    }
    private static int partition(int[] lst,int lo, int hi){
        int i = lo;
        int j = hi+1;
        while (true) {
            while (lst[lo] > lst[++i]) {
                if (i >= hi) {
                    break;
                }
            }
            while (lst[lo] < lst[--j]) {
                if (j <= lo) {
                    break;
                }
            }
            if(i>=j){
                break;
            }
            int temp = lst[j];
            lst[j] = lst[i];
            lst[i] = temp;

        }
        int temp = lst[j];
        lst[j] = lst[lo];
        lst[lo] = temp;
        return j;
    }
    public static String toString(int[] lst){
        StringBuilder s = new StringBuilder();
        for (int i = 0; i<lst.length-1;i++){
            s.append(lst[i] +", ");
        }
        s.append(lst[lst.length-1]);
        return s.toString();
    }
    public static void main(String[] args){
        int[] lst = new int[] {4,3,5,7,5,3,4,2,1,7};
        qsort(lst);
        System.out.println(toString(lst));
    }
}
