/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


/**
 *
 * @author Ken Ka
 */
import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import java.io.*;


public class XMLHandlerSAX extends DefaultHandler {
    private List<Record> records = null;
    private Record rec = null;
    //getter method for records list
	private XMLFileName ="";
    public List<Record> getRecords() {
        return records;
    }
    boolean bTimeStamp = false;
    boolean bRow = false; 
      
    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes)
            throws SAXException {
        
        if (qName.equalsIgnoreCase("row") && attributes.getValue("_id")!=null) {
            //create a new Record and put it in Map
            String id = attributes.getValue("_id");
            //initialize Employee object and set id attribute
            rec = new Record(id);
            if (records == null)
                records = new ArrayList<>();
            bRow = true;          
        } else if (qName.equalsIgnoreCase("timestamp")) {
            //set boolean values for fields, will be used in setting Record variables (put(key,value))
            bTimeStamp = true;       
        } // check more attributes here
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (qName.equalsIgnoreCase(XMLFileName) ) {
            
            records.add(rec);  
            //throw new MySaxTerminatorException();
        }
    }

    @Override
    public void characters(char ch[], int start, int length) throws SAXException {

        if (bTimeStamp) {
            //timestamp element, set Record timeStamp
            rec.put("timestamp", new String(ch, start, length));
            bTimeStamp = false;
        } 
        //put more attributes here
        if (bRow)
        {
            bRow = false;
        }
    }
}