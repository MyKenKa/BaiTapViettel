/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


/**
 *
 * @author Ken Ka :-)
 */
import java.util.List;
import org.joda.time.LocalDateTime;

/**
 *
 * @author Ken Ka
 */
public class Window {
    private long windowSize; //diration
    private Record reference; // ?? previous record to refer to current window
    private LocalDateTime timeStart;
    private List<Record> listOfRecord;
    private double summarizedValue; 
    public Window (List<Record> listRecord, LocalDateTime timeStart, long windowSize) {
        this.listOfRecord = listRecord;
        this.timeStart = timeStart;
        this.windowSize = windowSize;
        this.summarizedValue = 0;
    }
    public void setValue(double summarizedValue) {
        this.summarizedValue = summarizedValue;
    }
    public double getValue() {
        return this.summarizedValue;
    }
    public List<Record> getListOfRecords() {
        return listOfRecord;
    }
    public LocalDateTime getTimeStart() {
        return timeStart;        
    }
    public long getWindowSize()
    {
        return windowSize;
    }
    public void setTimeStart(LocalDateTime timeStart) {
        this.timeStart = timeStart;
    }
    public void info() {
        System.out.println("Starting Time: " + timeStart + ", list of records: " + listOfRecord.toString());
    }    
}
