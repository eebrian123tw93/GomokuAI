package com.JavaBeginnerToPro.GomoWhiz.ConwayAI_V2;
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

public class QMapIO {

    public static Map<String, Double> load(String path) {
        Map<String, Double> qTable=null;
        Path path1 = Paths.get(path).toAbsolutePath();
        if(Files.notExists(path1)){
            qTable = new HashMap<>();
            return qTable;
        }
        try (Stream<String> lines = Files.lines (path1, StandardCharsets.UTF_8))
        {
            System.out.println("Started loading brain");
            qTable=new HashMap<>();
            for (String line : (Iterable<String>) lines::iterator)
            {
                String row[] = line.split(":");
                qTable.put(row[0], Double.parseDouble(row[1]));
            }
            System.out.println("Done loading brain");

        }catch (Exception e){
            e.printStackTrace();
        }

        return qTable;
    }

    public static void save(String path, Map<String, Double> qTable) {
        Path path1 = Paths.get(path).toAbsolutePath();
        double qValue;

        try {
            Files.deleteIfExists(path1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(Files.notExists(path1)){
            try {
                Files.createFile(path1);
            }

            catch (IOException e) {
                e.printStackTrace();
            }
        }

        try(BufferedWriter writer=Files.newBufferedWriter(path1, Charset.defaultCharset())) {
            StringBuilder stringBuilder=new StringBuilder();

            for (Map.Entry<String, Double> key:qTable.entrySet()) {
                stringBuilder.append(key.getKey());
                stringBuilder.append(":");
                qValue = key.getValue();
                stringBuilder.append(qValue);

                writer.write(stringBuilder.toString(),0,stringBuilder.length());
                writer.newLine();
                stringBuilder.delete(0, stringBuilder.length());
            }
        }

        catch (IOException e) {
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
