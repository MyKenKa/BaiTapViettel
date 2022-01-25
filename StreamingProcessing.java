/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package StreamingProcessing;

import au.com.bytecode.opencsv.CSVReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.xml.sax.SAXException;

import spark.api.java.JavaPairRDD;
import spark.api.java.JavaRDD;
import spark.api.java.function.Function2;
import scala.Tuple2;

/**
 *
 * @author Ken Ka
 */
public class StreamingProcessing {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {

        // Initialize and create SourcesManagers from sources/files
        List<SourcesManager> sourcesManagers = new ArrayList<SourcesManager>();

        String path = System.getProperty("user.dir");
        File f = new File(path + "/src"); // current directory
        File[] files = f.listFiles();
        for (File file : files) {
            if (file.isFile()) {
                //System.out.println(file.getName());
                SourcesManager sM = getSourcesManager(file);
                sourcesManagers.add(sM);
            }
        }
        
        List<String> commonKeys = Arrays.asList();
        long windowSize = 1000 * 60 * 60 * ; // s: 1 hour size
      
		
		UserDefinedFunctuon func = new UserDefinedFunction();
        List<Window> iWindows = Integrator(commonKeys, sourcesManagers, windowSize, func);
        

    }

   
  

    public static List<Window> Integrator(List<String> commonKeys, List<SourcesManager> sourcesManagers, long windowSize, Function function) { //for compression
        long totalTime = 0;

        List<Window> integratedWindows = new ArrayList<Window>();
        DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
        // set up starting time to create windows
        LocalDateTime startTime = formatter.parseLocalDateTime("/"); 
        LocalDateTime endTime = LocalDateTime.now();

        int count = 0;
        // set lastRecordProcessed to Null to run the loop for experiment
        for (SourcesManager sM : sourcesManagers) {
            sM.setNullLastRecordProcessed();
        }
        while (startTime.isBefore(endTime)) {
            long startTimeS = System.currentTimeMillis();

            List<Record> iRecords = new ArrayList<Record>();
            for (SourcesManager sM : sourcesManagers) {
                List<Record> recordsList = sM.getRecords(startTime, windowSize);
                //System.out.println(sM.getLastRecordProcessed());
                //iRecords.addAll(sM.getRecords(startTime, windowSize));
                for (Record r : recordsList) {
                    Map<String, String> v = new HashMap<>();
                    for (String keys : commonKeys) {
                        v.put(keys, r.get(keys));
                    }
                    Record newR = new Record(v);
                    iRecords.add(newR);
                }

            }
            long endTimeS = System.currentTimeMillis();
            long elapsedTime = endTimeS - startTimeS;
            if (!iRecords.isEmpty()) {
                totalTime += elapsedTime;
                count += 1;
                //System.out.println("Running time to process a window with window size " + windowSize + ": " + elapsedTime);
            }

            Window window = new Window(iRecords, startTime, windowSize);
            integratedWindows.add(window);
            //window.info();
            startTime = startTime.plusMillis((int) windowSize);
        }
        System.out.println("The AVG of running time to process with window size " + windowSize + ": " + ((double) totalTime) / 10);
        return function.funtion(integratedWindows);
       
    }

   
 //Apache Spark processing   

//     public JavaRDD<Record> TaskProcessing(JavaRDD<Record> leftUserJavaRDD, JavaRDD<Record> rightUserJavaRDD) {
//
//        JavaPairRDD<String, Record> leftUserJavaPairRDD =
//                   leftUserJavaRDD.mapToPair(r -> new Tuple2<>(r.get("timestamp"), r));
//
//        JavaPairRDD<String, Record> rightUserJavaPairRDD =
//                   rightUserJavaRDD.mapToPair(r -> new Tuple2<>(r.get("timestamp"), r));
//
//        return leftUserJavaPairRDD
//                   .union(rightUserJavaPairRDD)
//                   .reduceByKey(merge).values();
//   }
//    private static Function2<Record, Record, Record> merge =
//            getProcessingFunction (NV1...NV6)

    public static SourcesManager getSourcesManager(File file) throws IOException {
        SourcesManager sourcesManager = null;
        if (file.isFile()) {
            switch (getExtentionOfFile(file.getName())) {
                case "txt":
                case "csv":
                    //System.out.println(file.getName()); 
                    List<Record> records = new ArrayList<Record>();
                    try {
                        CSVReader csvReader = new CSVReader(new FileReader(file));
                        List keys = keysExtractionOfAFile(file);
                        String[] nextLine = csvReader.readNext(); //skip header
                        //Read the file to retrive records 

                        while ((nextLine = csvReader.readNext()) != null) {
                            Map<String, String> v = new HashMap<>();
                            for (int i = 0; i < nextLine.length; i++) {
                                v.put(keys.get(i).toString(), nextLine[i]);
                                //add more timestamp attrribute taken from real time attrubute of source
                                // and change the format of time to and standard format: yyyy-MM-ddTHH:mm:ss
                                if (keys.get(i).toString().equalsIgnoreCase("date")) {
                                    String sTime = nextLine[i];
                                    DateTimeFormatter formatter1;
                                    //DateTimeFormatter formatter1 =  DateTimeFormat.forPattern("dd/MM/yyyy hh:mm:ss a");
                                    //DateTimeFormatter formatter1 =  DateTimeFormat.forPattern("dd/MM/yyyy HH:mm");// formatter is up to file
                                    if (sTime.length() < 17) {
                                        formatter1 = DateTimeFormat.forPattern("dd/MM/yyyy HH:mm");
                                    } else {
                                        formatter1 =  DateTimeFormat.forPattern("dd/MM/yyyy hh:mm:ss a");
                                        //formatter1 = DateTimeFormat.forPattern("MM/dd/yyyy hh:mm:ss a");
                                        //System.out.println(sTime);
                                    }
                                    DateTimeFormatter formatter2 = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
                                    //System.out.println(sTime);
                                    sTime = sTime.replaceAll("\'", "");
                                    //System.out.println(sTime);
                                    LocalDateTime formattedDate = formatter1.parseLocalDateTime(sTime);
                                    String formattedString = formatter2.print(formattedDate);
                                    v.put("timestamp", formattedString);
                                }
                            }
                            Record r = new Record(v);
                            records.add(r);
                        }
                        sourcesManager = new SourcesManager(records);
                        return sourcesManager;
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                    return null;
                case "xml":
                    SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
                    try {
                        SAXParser saxParser = saxParserFactory.newSAXParser();
                        XMLHandlerSAX xmlHandel = new XMLHandlerSAX();
                        saxParser.parse(file, xmlHandel);
                        sourcesManager = new SourcesManager(xmlHandel.getRecords());
                        return sourcesManager;
                    } catch (ParserConfigurationException | SAXException e) {
                    }
            }

        }
        return sourcesManager;
    }
    public static List xmlKeys = new ArrayList();

    public static List keysExtractionOfAFile(File f) throws IOException {
        //Set[] keys = new TreeSet[files.length];       
        if (f.isFile()) {
            switch (getExtentionOfFile(f.getName())) {
                case "txt":
                //break;
                case "csv":
                    try {

                        CSVReader csvReader;
                        if (f.getName().equalsIgnoreCase("data2016320165.txt"))// this file have no header
                        {
                            String path = System.getProperty("user.dir");
                            csvReader = new CSVReader(new FileReader(new File(path + "/header.txt")));
                        } else {
                            csvReader = new CSVReader(new FileReader(f));
                        }
                        String[] headers = csvReader.readNext();
                        csvReader.close();
                        List headerList = new ArrayList();
                        if (headers != null && headers.length > 0) {
                            for (int i = 0; i < headers.length; i++) {
                                headerList.add(headers[i]);
                            }
                        }
                        return headerList;
                    } catch (FileNotFoundException ex) {

                    }
                    break;
                case "xml":
                    SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
                    try {
                        SAXParser saxParser = saxParserFactory.newSAXParser();
                        XMLKeys keysHandler = new XMLKeys();
                        saxParser.parse(f, keysHandler);
                    } catch (ParserConfigurationException | SAXException e) {
                    }
                    //fixed code: remove the two first headers that are not the attributes of data
                    xmlKeys.remove(0);
                    xmlKeys.remove(1);
                    return xmlKeys;
                //System.out.println(xmlKeys);

                default:
                    System.out.println("add more function for this type to extract keys!");
                    break;
            }
        }

        return null;
    }

    
}
