package spout;

import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.IRichSpout;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;

public class FileSpout implements IRichSpout {
	private static final long serialVersionUID = 1L;
	
	private SpoutOutputCollector collector;
	private BufferedReader fileReader;
	private TupleInfo tupleInfo = new TupleInfo();

	public void open(Map conf, TopologyContext context, SpoutOutputCollector collector) {
		this.collector = collector;		    
		String file = new String();
		if (file.equals("")) {
			file = "input/data.csv";
		} 
		try {	
			fileReader = new BufferedReader(new FileReader(new File(file))); 
		} catch (FileNotFoundException e) {
			throw new RuntimeException ("Error reading file [" + file + "]");
		}
	}	

	@SuppressWarnings("unused")
	public void nextTuple() {   
		String line = null;  
  		BufferedReader access = new BufferedReader(fileReader);
  		try {  		   
  			while ((line = access.readLine()) != null) { 
  				if (line != null) {
  					String[] record = line.split(tupleInfo.getDelimiter());
                    if (tupleInfo.getFieldList().size() == record.length) {
                    	collector.emit(new Values(record));       
                    }                          
  				}          
  			} 
        } catch (IOException ex) { System.out.println(ex); } 
	}        
	
	public void ack(Object id) {
		System.out.println("OK: " + id);
	}
  
	public void fail(Object id) {
		System.out.println("Fail: " + id);
	}    

	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		TupleInfo tuple = new TupleInfo();
		Fields fieldsArr;
		try {
			fieldsArr = tuple.getFieldList(); 
			declarer.declare(fieldsArr);
		} catch (Exception e) {
			// TODO: handle exception
			throw new RuntimeException("Error: fail to new Tuple object in declareOutputFields, tuple is null", e);  
		}    	  		
	}
	
	public void close() {
		// TODO Auto-generated method stub
	}

	public void activate() {
		// TODO Auto-generated method stub
	}

	public void deactivate() {
		// TODO Auto-generated method stub
	}

	public Map<String, Object> getComponentConfiguration() {
		// TODO Auto-generated method stub
		return null;
	}
}