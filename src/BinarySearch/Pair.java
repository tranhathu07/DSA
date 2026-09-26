class Pair {
    public static int fPair(int[] lst){
        int n = lst.length;
//        int min = 0;
//        int max = 1;
        int left = lst[0];
        int right = lst[1];
        if (left>right){
            int temp = left+right;
            left = temp-left;
            right =temp-left;

        }
        int maxDist;
        for (int i =0;i<n;i++){
            if(lst[i]<left){
                left = lst[i];
//                min =i;
            }
            else if(lst[i]>right){
                right = lst[i];
//                max = i;
            }

        }
        maxDist = right - left;
        return maxDist;
    }
    public static void main(String[] args){
        int[] lst = new int[]{3,-5,1,8,10,-2};
        System.out.println(fPair(lst));
    }
}
