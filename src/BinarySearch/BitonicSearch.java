public class BitonicSearch {
    public static boolean bitonic(int[] lst, int target){
        int n = lst.length;
        int left = 0;
        int right = n;
        int med = 0;
        int index = -1;
        while(left<right){
            med = (left+right)/2;
            if(med+1<n &&lst[med]< lst[med+1]){
                left = med+1;


            }
            else if(med-1>=0 &&lst[med]<lst[med-1]){
                right=med;


            }

            else{
                break;
            }
        }


        if(target==lst[med]){
            index=med;
        }
        else{
            right = med;
            left = 0;
            while(left<right){
                int mid = (left+right)/2;
                if(lst[mid]>target){
                    right = mid;
                }
                else if(lst[mid]<target){
                    left = mid+1;
                }
                else{
                    index = mid;
                    break;
                }
            }
        }

        if(index == -1){
            left = med;
            right = n;
            while(left<right){
                int mid = (left+right)/2;

                if(lst[mid]>target){
                    left = mid+1;
                }
                else if(lst[mid]<target){
                    right = mid;
                }
                else{
                    index = mid;

                    break;
                }
            }
        }

        return index != -1;
    }


    public static void main(String[] args) {
        // Test Case 1: Target nằm ở chuỗi tăng
        runTest("Test 1: Target ở nửa tăng", new int[]{1, 3, 8, 12, 20, 15, 9, 4}, 8);

        // Test Case 2: Target nằm ở chuỗi giảm
        runTest("Test 2: Target ở nửa giảm", new int[]{1, 3, 8, 12, 20, 15, 9, 4}, 9);

        // Test Case 3: Target chính là đỉnh (Peak)
        runTest("Test 3: Target là đỉnh", new int[]{1, 3, 8, 12, 20, 15, 9, 4}, 20);

        // Test Case 4: Target không tồn tại trong mảng
        runTest("Test 4: Target không tồn tại", new int[]{1, 3, 8, 12, 20, 15, 9, 4}, 10);

        // Test Case 5: Target nhỏ hơn mọi phần tử
        runTest("Test 5: Target nhỏ hơn min", new int[]{5, 10, 25, 18, 12, 7}, 2);

        // Test Case 6: Target ở phần tử đầu hoặc cuối mảng
        runTest("Test 6: Target ở biên", new int[]{-5, 2, 9, 14, 11, 3, -10}, -10);

        // Test Case 7: Mảng kích thước tối thiểu (N = 3)
        runTest("Test 7: Mảng 3 phần tử", new int[]{2, 10, 5}, 5);
    }

    private static void runTest(String label, int[] lst, int target) {
        System.out.println("=== " + label + " ===");
        boolean result = bitonic(lst, target);
        System.out.println("Target: " + target + " -> Found: " + result);
        System.out.println();
    }
}
