import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

public class KeyWordCounter {
    public static void main(String[] args) throws IOException {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader("N:\\UF Fall 2018\\AdvDS\\MaxFibonaciiHeap\\TestFiles\\input01.txt"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.out.println("Unable ro read file.");
            System.exit(-1);
        }
        MaxFibonaciiHeap mfhp = new MaxFibonaciiHeap();
        HashMap<String, Node> map = new HashMap<>();

        String line = "";
        while ((line = reader.readLine()) != null) {
            String[] nodeInfo = line.split("\\s+");
            if (nodeInfo.length == 2) {
                mfhp.insert(nodeInfo[0].substring(1), Long.parseLong(nodeInfo[1]));
            }
        }
        System.out.println(mfhp.getMaxPointer());
    }
}
