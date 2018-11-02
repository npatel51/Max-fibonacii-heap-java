import com.sun.javaws.exceptions.InvalidArgumentException;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class KeyWordCounter {
    public static void main(String[] args) throws IOException, InvalidArgumentException {
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
        while ((line = reader.readLine()) != null && !line.equals("stop")) {
            String[] nodeInfo = line.split("\\s+");
            if (nodeInfo.length == 2) {
                Node n = map.getOrDefault(nodeInfo[0].substring(1),null);
                    if(!map.containsKey(nodeInfo[0].substring(1))) {
                        n = mfhp.insert(nodeInfo[0].substring(1), Integer.parseInt(nodeInfo[1]));
                        map.put(nodeInfo[0].substring(1),n);
                    }else{
                        mfhp.increaseKey(n,Integer.parseInt(nodeInfo[1]));
                    }
            }else {

            }
            System.out.println(map.size());
            Node temp = mfhp.getMaxPointer();
            Set<Node> set = new HashSet<>();
            while(set.add(temp) && temp!=null){
                System.out.println(temp);
                temp = temp.getRight();
            }
            System.out.println();
        }
    }
}
