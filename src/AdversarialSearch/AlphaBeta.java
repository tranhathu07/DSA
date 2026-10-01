import java.lang.*;
import java.util.List;

public class AlphaBeta {
    private static class Node{
        private String name;
        private int value;
        private Node[] childs =new Node[10];
        int N = 0;
        public void resize(int n){
            Node[] ne = new Node[n];
            int len = Math.min(n, childs.length);
            for(int i =0;i<len;i++){
                ne[i] = childs[i];
            }
            childs = ne;
        }
        public Node(String name){
            this.name =name;
            this.childs = new Node[10];

        }
        public void addChild(Node child){
            if(N == childs.length){
                resize(childs.length * 2);
            }
            childs[N++] = child;
        }
        public Node(String name,int value){
            this.name =name;
            this.value = value;
            this.childs = null;

        }

    }

    Node state;
    public static int maxValue(Node state, int alpha, int beta){
        int v = -9999;
        v = Math.max(v, value(state.childs,alpha,beta,2));
        return v;

    }

    public static int value(Node[] lst,int alpha, int beta,int level){
        boolean isMax = (level % 2 == 0);

//        // Khởi tạo giá trị tốt nhất tùy thuộc vào MAX hay MIN
        int bestValue = isMax ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        for(Node child: lst){
            if (child == null) {
                break;
            }
            int v;
            if(child.childs == null){
                 v =  child.value;

            }
            else{
                v = value(child.childs, alpha,beta,level+1);

            }
            if (!isMax){

                bestValue = Math.min(bestValue,v);
                beta = Math.min(beta,bestValue);
                System.out.println(beta);

            }
            else{
                System.out.println(level);
                bestValue = Math.max(bestValue,v);
                alpha = Math.max(alpha,bestValue);
                System.out.println(alpha);

            }
            if(alpha>=beta){
                break;
            }
        }
        return bestValue;
    }

    public static void main(String[] args) {
        // 1. Tạo các nút lá theo đúng thứ tự trong ảnh (từ trái qua phải)
        Node nodeB = new Node("b", 10);
        Node nodeC = new Node("c", 8);
        Node nodeE = new Node("e", 4);
        Node nodeF = new Node("f", 50);

        // 2. Tạo 2 nút nhánh MIN (màu hồng)
        // Nhánh 'a' chứa 'b' và 'c'
        Node NodeA = new Node("Nhánh a");
        NodeA.addChild(nodeB);
        NodeA.addChild(nodeC);

        // Nhánh 'd' chứa 'e' và 'f'
        Node NodeD = new Node("Nhánh d");
        NodeD.addChild(nodeE);
        NodeD.addChild(nodeF);

        // 3. Khai báo danh sách con của nút MAX gốc (màu xanh dương trên cùng)
//        Node[] rootChildren = new Node[] { minNodeA, minNodeD };
        Node target = new Node("target");
        target.addChild(NodeA);
        target.addChild(NodeD);

        System.out.println("Bắt đầu duyệt cây theo sơ đồ ảnh...\n");

        // 4. Gọi hàm chạy thuật toán (Root ở level 1 là MAX)
        int result = maxValue(target, Integer.MIN_VALUE, Integer.MAX_VALUE);

        System.out.println("\n=> Giá trị của nút trên cùng là: " + result);
    }

}

//public class AlphaBeta {
//
//    private static class Node {
//        private String name;
//        private int value;
//        // Khởi tạo mảng trực tiếp, không cần lặp lại trong constructor
//        private Node[] childs = new Node[10];
//        int N = 0;
//
//        public void resize(int n) {
//            // Sửa lỗi ép kiểu: Khởi tạo thẳng mảng Node
//            Node[] ne = new Node[n];
//            int len = Math.min(n, childs.length);
//            for (int i = 0; i < len; i++) {
//                ne[i] = childs[i];
//            }
//            childs = ne;
//        }
//
//        public Node(String name) {
//            this.name = name;
//        }
//
//        public Node(String name, int value) {
//            this.name = name;
//            this.value = value;
//            this.childs = null;
//        }
//
//        public void addChild(Node child) {
//            if (N == childs.length) {
//                resize(childs.length * 2);
//            }
//            childs[N++] = child;
//        }
    //}
//
//    // Hàm gọi đầu tiên ở nút gốc
//    public static int maxValue(Node state, int alpha, int beta) {
//        // Gọi xuống các nhánh con của gốc, các nhánh con này nằm ở level 2
//        return value(state.childs, alpha, beta, 2);
//    }
//
//    public static int value(Node[] lst, int alpha, int beta, int level) {
//        // Level lẻ (1, 3) là MAX. Level chẵn (2, 4) là MIN.
//        boolean isMax = (level % 2 == 0);
//        int bestValue = isMax ? Integer.MIN_VALUE : Integer.MAX_VALUE;
//
//        for (Node child : lst) {
//            // BỎ QUA các ô trống (null) trong mảng 10 phần tử để tránh crash
//            if (child == null) {
//                break;
//            }
//
//            System.out.println("Đang xét [" + child.name + "] - Level: " + level);
//
//            int v;
//            if (child.childs == null) {
//                v = child.value;
//            } else {
//                v = value(child.childs, alpha, beta, level + 1);
//            }
//
//            // Cập nhật giá trị tốt nhất và Alpha/Beta
//            if (!isMax) {
//                bestValue = Math.min(bestValue, v);
//                beta = Math.min(beta, bestValue);
//            } else {
//                bestValue = Math.max(bestValue, v);
//                alpha = Math.max(alpha, bestValue);
//            }
//
//            // Cắt tỉa
//            if (alpha >= beta) {
//                System.out.println(" => CẮT TỈA TẠI NHÁNH: " + child.name);
//                break;
//            }
//        }
//        return bestValue;
//    }
//
//    public static void main(String[] args) {
//        // 1. Tạo các nút lá
//        Node nodeB = new Node("b", 10);
//        Node nodeC = new Node("c", 8);
//        Node nodeE = new Node("e", 4);
//        Node nodeF = new Node("f", 50);
//
//        // 2. Tạo 2 nút nhánh MIN (màu hồng)
//        Node NodeA = new Node("Nhánh a");
//        NodeA.addChild(nodeB);
//        NodeA.addChild(nodeC);
//
//        Node NodeD = new Node("Nhánh d");
//        NodeD.addChild(nodeE);
//        NodeD.addChild(nodeF);
//
//        // 3. Tạo nút MAX gốc (màu xanh dương trên cùng)
//        Node target = new Node("Nút Root");
//        target.addChild(NodeA);
//        target.addChild(NodeD);
//
//        System.out.println("Bắt đầu duyệt cây theo sơ đồ ảnh...\n");
//
//        // 4. SỬA LỖI: Gọi hàm với Beta là Integer.MAX_VALUE
//        int result = maxValue(target, Integer.MIN_VALUE, Integer.MAX_VALUE);
//
//        System.out.println("\n=> Giá trị của nút trên cùng là: " + result);
//    }
//}
