public class COS226_s25_7 {
    public static int tiger(String[] lst){
        int n = lst.length;
        int lo = 0;
        int hi = n-1;
        while(hi>lo+1){
            int mid = (lo+hi)/2;
            if(lst[mid].equals("Yellow")) lo =mid;
            else hi =mid;
        }
        return lo;
    }
    public static void main(String[] args){
        String[] lst = new String[]{"Yellow","Yellow","Yellow","Black","Yellow","Yellow","Yellow","Yellow","Black","Black","Black","Black"};
        System.out.println(tiger(lst));

    }
}
