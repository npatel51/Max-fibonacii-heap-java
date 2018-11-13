import java.io.*;
import java.util.*;

public class KeyWordCounter {
    public static void main(String[] args) throws IOException {
        if( args.length == 0 ) return ;
        BufferedReader reader = null;

        PriorityQueue<Node> pq = new PriorityQueue<>((o1, o2) -> Long.compare(o2.getFrequency(),o1.getFrequency()));

        try {
            reader = new BufferedReader(new FileReader(args[0])); // read the file
            System.out.println("Reading the file.........");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.out.println("Unable ro read file.");
            System.exit(-1);
        }

        FibonaciiHeap mfhp = new FibonaciiHeap();
        HashMap<String, Node> map = new HashMap<>();

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
                    pq.remove(n);
                    mfhp.increaseKey(n,Long.parseLong(nodeInfo[1]));
                    pq.add(n);
                }
            } else {
                int k = Integer.parseInt(nodeInfo[0]);
                List<String> expected = new ArrayList<>();
                List<Node> entries = new ArrayList<>();
                Set<Node> set = new HashSet<>();
                List<String> topKWords = new ArrayList<>();

                for(int i=0;i<k;++i) {
                    Node n = pq.poll();
                    entries.add(n);
                }
                for(int i=0;i<k;++i){
                    expected.add(entries.get(i).getWord());
                    pq.add(entries.get(i));
                }

                for(String key : map.keySet()){
                 //   System.out.println(map.get(key));
                }
                List<Node> result = mfhp.topKWords(k);
                for( Node n : result ){
                    topKWords.add(n.getWord());
                }
                System.out.println(topKWords);
               // System.out.println("Expected = > "+expected);
               // System.out.println("Result = > "+result);
                for(int i=0;i<k;++i){
                    if( entries.get(i).getFrequency() != result.get(i).getFrequency()){
                        System.out.println("Expected = > "+entries.get(i));
                        System.out.println("Result = > "+result.get(i));
                    }
                }
            }
        }
    }

}
