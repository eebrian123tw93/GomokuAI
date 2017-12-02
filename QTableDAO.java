package com.JavaBeginnerToPro.GomoWhiz;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class QTableDAO {

    public QTableDAO() {

    }

    public static Map<String, HashMap<Integer, Double>> load(String path) {
        Map<String, HashMap<Integer, Double>> qTable=null;
        Path path1 = Paths.get(path).toAbsolutePath();
        if(Files.notExists(path1)){
            return qTable;
        }
        try (Stream<String> lines = Files.lines (path1, StandardCharsets.UTF_8))
        {

            qTable=new HashMap<>();
            for (String line : (Iterable<String>) lines::iterator)
            {
                String row[]=line.split(":");
                Map<Integer,Double> action=new HashMap<>();
                for (int i=1;i<row.length;i++) {
                    String a[]=row[i].split(",");
                    action.put(Integer.parseInt(a[0]),Double.parseDouble(a[1]));
                }
                qTable.put(row[0], (HashMap<Integer, Double>) action);
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return qTable;
    }

    public static void save(String path, Map<String, HashMap<Integer, Double>> qTable) {
        Path path1 = Paths.get(path).toAbsolutePath();
        try {
            Files.deleteIfExists(path1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(Files.notExists(path1)){
            try {
                Files.createFile(path1);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try(BufferedWriter writer=Files.newBufferedWriter(path1, Charset.defaultCharset())) {
            StringBuilder stringBuilder=new StringBuilder();
            for (Map.Entry<String,HashMap<Integer,Double>> key:qTable.entrySet()) {
                stringBuilder.append(key.getKey());
//                stringBuilder.append(":");

                HashMap<Integer,Double>actions=key.getValue();
                for (Map.Entry<Integer,Double>action:actions.entrySet()) {
                    stringBuilder.append(":");
                    stringBuilder.append(action.getKey());
                    stringBuilder.append(",");
                    stringBuilder.append(action.getValue());

                }

                writer.write(stringBuilder.toString(),0,stringBuilder.length());
                writer.newLine();
                stringBuilder.delete(0, stringBuilder.length());
            }



        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String [] args)throws  Exception{

        //INDArray input = Nd4j.zeros(4, 2);


        //INDArray labels = Nd4j.zeros(4, 2);

        //System.out.println(labels.getDouble(2,1));


//       MDP
    }
}
