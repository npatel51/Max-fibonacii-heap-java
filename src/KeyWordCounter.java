import java.io.*;
import java.util.*;

public class keywordcounter {
    public static void main(String[] args) throws IOException {
        if( args.length == 0 ) return ;
        BufferedReader reader = null;
        //creates an output file named output
        BufferedWriter writer = new BufferedWriter(new FileWriter("output_file.txt"));
        StringBuffer sb = new StringBuffer();

        try {
            reader = new BufferedReader(new FileReader(args[0])); // read the input file
            System.out.println("Reading the file.........");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.out.println("Unable ro read file.");
            System.exit(-1);
        }

        FibonaciiHeap mfhp = new FibonaciiHeap();
        // stores the node associated with the word
        HashMap<String, Node> map = new HashMap<>();
        String line = "";
        while ((line = reader.readLine()) != null && !line.equalsIgnoreCase("stop")) {
            String[] nodeInfo = line.split("\\s+");
            if(nodeInfo.length == 2) {
                Node n = map.getOrDefault(nodeInfo[0].substring(1), null);
                if ( n == null) {
                    n = mfhp.insert(nodeInfo[0].substring(1), Long.parseLong(nodeInfo[1]));
                    map.put(nodeInfo[0].substring(1), n);
                } else {
                    mfhp.increaseKey(n,Long.parseLong(nodeInfo[1]));
                }
            } else {
                int k = Integer.parseInt(nodeInfo[0]);
                if( k > 0 ) {
                    List<String> topKWords = mfhp.topKWords(k);
                    for (int i = 0; i < topKWords.size() - 1; ++i) {
                        sb.append(topKWords.get(i)).append(',');
                    }
                    sb.append(topKWords.get(topKWords.size() - 1)).append('\n');
                }
            }
        }
        reader.close();              // close read
        writer.write(sb.toString()); // write to the file
        writer.close();              // close write
    }
}
