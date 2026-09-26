class OnlyAddnSub {
    public static boolean addnSub(int[] lst, int target){
        int n = lst.length;
        if(target>lst[0]){
            return false;
        }
        if(target<lst[n-1]){
            return false;
        }
        if(target==lst[0] ){
            return true;
        }
        int fk2 = 0;
        int fk1 =1;
        int f =fk1+fk2;
        int i =0;
        while(f<n){
            fk2 = fk1;
            fk1 = f;
            f = fk1 + fk2;

        }

        while(f>=1){
            if(i+fk2>=n){
                i=n-1-fk2;
            }
            if(lst[i + fk2] == target){
                return true;
            }

            if(lst[i + fk2] > target){
                i = i+fk2;
                f=fk1;
                fk1=fk2;
                fk2=f-fk1;


            }
            else{
                f=fk2;
                fk1=fk1-fk2;
                fk2 = f-fk1;
            }


        }
        if (fk1 == 1 &&
                i+fk2 + 1 < n &&
                lst[i+fk2 + 1] == target) {
            return true;
        }

        return false;

    }
    public static void main(String[] args) {
        // LƯU Ý: Các mảng đầu vào đều được sắp xếp GIẢM DẦN

        // Test Case 1: Target nằm ở nửa đầu mảng
        runTest("Test 1: Target ở nửa đầu", new int[]{50, 40, 30, 20, 10, 5, 0}, 40);

        // Test Case 2: Target nằm ở nửa sau mảng
        runTest("Test 2: Target ở nửa sau", new int[]{50, 40, 30, 20, 10, 5, 0}, 5);

        // Test Case 3: Target là phần tử đầu tiên (Max)
        runTest("Test 3: Target ở đầu mảng (Max)", new int[]{100, 80, 60, 40, 20}, 100);

        // Test Case 4: Target là phần tử cuối cùng (Min)
        runTest("Test 4: Target ở cuối mảng (Min)", new int[]{100, 80, 60, 40, 20}, 20);

        // Test Case 5: Target không tồn tại (nằm xen kẽ giữa mảng)
        runTest("Test 5: Target không tồn tại", new int[]{50, 40, 30, 20, 10}, 25);

        // Test Case 6: Target nằm ngoài phạm vi mảng
        runTest("Test 6a: Target lớn hơn phần tử Max", new int[]{30, 20, 10}, 50);
        runTest("Test 6b: Target nhỏ hơn phần tử Min", new int[]{30, 20, 10}, 0);

        // Test Case 7: Mảng kích thước nhỏ (1 và 2 phần tử)
        runTest("Test 7a: Mảng 1 phần tử (Tìm thấy)", new int[]{15}, 15);
        runTest("Test 7b: Mảng 1 phần tử (Không thấy)", new int[]{15}, 10);
        runTest("Test 7c: Mảng 2 phần tử(thay)", new int[]{50, 40, 30, 20, 10, 5, 0}, 10);
    }

    private static void runTest(String label, int[] lst, int target) {
        System.out.println("=== " + label + " ===");
        boolean result = addnSub(lst, target);
        System.out.println("Target: " + target + " -> Found: " + result);
        System.out.println();
    }
}
