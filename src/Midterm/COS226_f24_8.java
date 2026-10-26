import java.util.Random;
public class COS226_f24_8 {
    public static void swap(int[] a,int i,int j){
        int temp = a[i];
        a[i] = a[j];
        a[j] = temp;
    }
    public static int[] sot(int[] a){
        int n = a.length;
        int i =0;
        int j = n-1;
        int[] kq = new int[n];

        for(int N =0;N<n;N++){
            if(a[i]<=a[j]){
                kq[N] = a[i++];
            }
            else{
                kq[N] = a[j--];
            }
        }
        return kq;
    }
    public static String toString(int[] lst){
        StringBuilder s = new StringBuilder();
        for (int i = 0; i<lst.length-1;i++){
            s.append(lst[i] +", ");
        }
        s.append(lst[lst.length-1]);
        return s.toString();
    }
    public static void makeMountainLike(int[] a){

    }
    public static void main(String[] args){

        int[] lst = new int[]{1, 4, 8, 18, 31, 20, 7, 4, 2};

        int[] kq =sot(lst);
        System.out.println(toString(kq));
    }
}
