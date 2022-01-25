/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


/**
 *
 * @author Ken Ka
 */
import java.util.Set;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;


public class XMLKeys extends DefaultHandler {


    private List keys = null;
    
    //getter method for employee list
    public List getKeys() {
        return keys;
    }

     boolean bTimeStamp = false;
     boolean bRow = false;
    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes)
            throws SAXException {
        if (keys ==null)
        {
            keys = new ArrayList();
        }
        keys.add(qName);
        StreamingIntegrator.xmlKeys.add(qName);        
    }
   public void endElement(String uri, String localName, String qName) throws MySaxTerminatorException {
        if (qName.equalsIgnoreCase("row")) {   
            throw new MySaxTerminatorException();            
        }
    }
   public class MySaxTerminatorException extends SAXException {
    public MySaxTerminatorException()
    {
        
    }

   }
   
   /* code to get keys/attributes
					SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
                    try {
                        SAXParser saxParser = saxParserFactory.newSAXParser();
                        XMLKeys keysHandler = new XMLKeys();
                        saxParser.parse(f, keysHandler);
                    } catch (ParserConfigurationException | SAXException e) {
                    }
                    //remove the headers if they are not the attributes of data
                    //xmlKeys.remove(..);
                    
                    return xmlKeys;
   */
}