import com.sun.javaws.exceptions.InvalidArgumentException;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class KeyWordCounter {
    public static void main(String[] args) throws IOException, InvalidArgumentException {
        BufferedReader reader = null;
        PriorityQueue<Node> pq = new PriorityQueue<>(new Comparator<Node>() {
            @Override
            public int compare(Node o1, Node o2) {
                return Long.compare(o2.getFrequency(),o1.getFrequency());
            }
        });

        try {
            reader = new BufferedReader(new FileReader("N:\\UF Fall 2018\\AdvDS\\MaxFibonaciiHeap\\TestFiles\\input01.txt"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.out.println("Unable ro read file.");
            System.exit(-1);
        }
        FibonaciiHeap mfhp = new FibonaciiHeap();
        HashMap<String, Node> map = new HashMap<>();
        HashMap<String, Map.Entry<String,Long>> pqmap = new HashMap<>();
        String line = "";
        while ((line = reader.readLine()) != null && !line.equals("stop")) {
            String[] nodeInfo = line.split("\\s+");
            if (nodeInfo.length == 2) {
                Node n = map.getOrDefault(nodeInfo[0].substring(1), null);
                if (!map.containsKey(nodeInfo[0].substring(1))) {
                    n = mfhp.insert(nodeInfo[0].substring(1), Long.parseLong(nodeInfo[1]));
                    map.put(nodeInfo[0].substring(1), n);
                    pq.add(n);
                } else {
                    mfhp.increaseKey(n,Long.parseLong(nodeInfo[1]));
                }
            } else {
                //   mfhp.print();
                // System.out.println(mfhp.topKWords(Integer.parseInt(nodeInfo[0])));
                // mfhp.print();
                int k = Integer.parseInt(nodeInfo[0]);
                List<String> expected = new ArrayList<>();
                List<Node> entries = new ArrayList<>();
                for(int i=0;i<k;++i) {
                    entries.add(pq.poll());
                }
                for(int i=0;i<k;++i){
                    expected.add(entries.get(i).getWord());
                    pq.add(entries.get(i));
                }
                List<String> result = mfhp.topKWords(k);

                assert expected.equals(result);

            }
        }

    }
}
