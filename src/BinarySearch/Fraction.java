public class Fraction {
    public static int thaTrung(int N, int F){
        return 1;
    }
    public static void main(String[] args) {
        int F;
        // Test 1 - KQ: 1
        F = 1;
        System.out.println("Test 1 - KQ: " + thaTrung(10));

        // Test 2 - KQ: 5
        F = 5;
        System.out.println("Test 2 - KQ: " + thaTrung(10));

        // Test 3 - KQ: 37
        F = 37;
        System.out.println("Test 3 - KQ: " + thaTrung(100));

        // Test 4 - KQ: 50
        F = 50;
        System.out.println("Test 4 - KQ: " + thaTrung(100));

        // Test 5 - KQ: 100
        F = 100;
        System.out.println("Test 5 - KQ: " + thaTrung(100));

        // Test 6 - KQ: 127
        F = 127;
        System.out.println("Test 6 - KQ: " + thaTrung(1000));

        // Test 7 - KQ: 2
        F = 2;
        System.out.println("Test 7 - KQ: " + thaTrung(1000000));

        // Test 8 - KQ: 20
        F = 20;
        System.out.println("Test 8 - KQ: " + thaTrung(1000000));
    }
}
