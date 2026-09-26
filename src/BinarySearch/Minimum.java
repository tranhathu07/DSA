class Minimum {
    public static int localMin(int[] lst){
        int n = lst.length;
        int left = 0;
        int right = n-1;
        int local=lst[0];
        while (left<right){
            int mid = (left+right)/2;
            if (mid == 0){
                local = lst[mid];
                break;
            }
            else if(lst[mid]<lst[mid+1] && lst[mid]<lst[mid-1]){
                local = lst[mid];
                break;
            }

            else if(lst[mid]>lst[mid+1]){
                left = mid+1;
            }
            else {
                right = mid;

            }
        }
        if(left==right){
            local = lst[right];
        }
        return local;
    }
    public static int matrixMin(int[][] matrix){
        int n = matrix.length;
        int left = 0;
        int right = n;
        int local = matrix[0][0];
        while(left<right){
            int mid = (left+right)/2;
            int r = 0;
            for (int row = 0; row<n;row++) {
                if (matrix[row][mid] < matrix[r][mid]) {
                    r = row;
                }
            }
            if(mid-1>=0 && matrix[r][mid]>matrix[r][mid-1]){
                right = mid;
            }

            else if(mid+1<n && matrix[r][mid]>matrix[r][mid+1]){
                left = mid +1;
            }
            else{
                local = matrix[r][mid];
                break;
            }

        }
        return local;
    }
    public static void main(String[] args){

            // Test Case 1: Local min nằm ở giữa ma trận
            int[][] test1 = {
                    {9, 8, 7},
                    {6, 1, 5},
                    {4, 3, 2}
            };
            runTestCase("Test 1: Local min ở giữa", test1);

            // Test Case 2: Local min nằm ở góc trên-trái (0, 0)
            int[][] test2 = {
                    {1, 5, 9},
                    {6, 7, 8},
                    {2, 3, 4}
            };
            runTestCase("Test 2: Local min ở góc (Corner)", test2);

            // Test Case 3: Local min nằm ở rìa/mép ma trận
            int[][] test3 = {
                    {12, 15, 18},
                    { 3, 10, 14},
                    {20, 25, 30}
            };
            runTestCase("Test 3: Local min ở mép (Edge)", test3);

            // Test Case 4: Ma trận nhỏ nhất 2x2
            int[][] test4 = {
                    {5, 2},
                    {8, 4}
            };
            runTestCase("Test 4: Ma trận 2x2", test4);

            // Test Case 5: Ma trận có nhiều local min (Đường chéo chính)
            int[][] test5 = {
                    {10, 20, 30},
                    { 9, 15, 40},
                    { 8,  5, 50}
            };
            runTestCase("Test 5: Nhiều Local Min", test5);
        }

        // Hàm phụ trợ in ma trận và chạy test
        private static void runTestCase(String testName, int[][] matrix) {
            System.out.println("=== " + testName + " ===");
            int res = matrixMin(matrix);

            System.out.println("Gia tri Local Minimum: " + res);
//            } else {
//                System.out.println("Khong tim thấy!");
//            }
//            System.out.println();
        }
}

