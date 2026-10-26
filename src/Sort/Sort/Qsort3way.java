public class Qsort3way {
    public static void qsort3(int[] lst){
        int n = lst.length;
        int lo = 0;
        int hi =n-1;
        sort3(lst,lo,hi);
    }
    public static void swap(int[] lst, int i,int j){
        int temp = lst[i];
        lst[i] = lst[j];
        lst[j] = temp;
    }
    public static void sort3(int[] lst,int lo, int hi){
        if(lo>=hi) return;
        int lt = lo;
        int gt = hi;
        int pivot = lst[lo];
        int i = lt+1;
        while(i<=gt){
            if ( lst[i]<pivot){
                swap(lst,i++,lt++);
            }
            else if(lst[i]>pivot){
                swap(lst,i,gt--);
            }
            else{
                i++;
            }
        }
        sort3(lst,lo,lt-1);
        sort3(lst,gt+1,hi);
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
        qsort3(lst);
        System.out.println(toString(lst));
    }
}