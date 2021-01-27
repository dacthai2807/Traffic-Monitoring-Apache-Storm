package bolt;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.IRichBolt;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;

public class SpeedCalculatorBolt implements IRichBolt {
	private static final long serialVersionUID = 1L;
	
	private final int WINDOW_SIZE = 5;
	private final int DELTA = 4;
	
	private OutputCollector collector;	
	
	Integer taskID;
	String taskName;
	
	private Map<Integer, Queue<Values>> slidingWindowMap;
	private Map<Integer, Double> sumSpeeds;
	private Map<Integer, Integer> countVehicles;
	
	public void initialize() {
		slidingWindowMap = new HashMap<>();
		sumSpeeds = new HashMap<>();
		countVehicles = new HashMap<>();
	}
	
	@Override
	public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
		initialize();
		this.collector = collector;	
		this.taskID = context.getThisTaskId();
		this.taskName = context.getThisComponentId();
	}
	
	@Override
	public void execute(Tuple input) {
		String dateTime = input.getValues().get(2).toString(); 
		double speed = Double.parseDouble(input.getValues().get(5).toString());
		int roadID = Integer.parseInt(input.getValues().get(7).toString());
		
		Queue<Values> slidingWindow;
		double curSumSpeeds = 0;
		int curCountVehicles = 0; 
		
        if (slidingWindowMap.containsKey(roadID)) {
            slidingWindow = slidingWindowMap.get(roadID);
            curSumSpeeds = sumSpeeds.get(roadID);
            curCountVehicles = countVehicles.get(roadID);
        } else {
        	slidingWindow = new LinkedList<>();
        }
        
        // update sliding window
        slidingWindow.add(new Values(speed, dateTime));
        curSumSpeeds += speed;
        ++curCountVehicles;
        
        if (slidingWindow.size() == WINDOW_SIZE + DELTA) {
            for (int i = 0; i < DELTA; ++i) {
            	Values temp = slidingWindow.poll();
            	curSumSpeeds -= Double.parseDouble(temp.get(0).toString());
                --curCountVehicles;
            }
        }
               
        if (slidingWindow.size() == WINDOW_SIZE) {
        	double averageSpeed = curSumSpeeds / (double)curCountVehicles;
        	System.out.println(averageSpeed);
        	collector.emit(new Values(roadID, slidingWindow.peek().get(1).toString(), dateTime, averageSpeed));
        }
        
        slidingWindowMap.put(roadID, slidingWindow);
        sumSpeeds.put(roadID, curSumSpeeds);
        countVehicles.put(roadID, curCountVehicles);
 
        collector.ack(input);
	}
	
	@Override
	public void cleanup() {
		System.out.println("-- Speed calculator bolt [" + taskName + " - " + taskID + "] --");

	}
	
	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields ("roadID", "startTime", "finishTime", "averageSpeed"));				
	}
    
	@Override
	public Map<String, Object> getComponentConfiguration() {
		// TODO Auto-generated method stub
		return null;
	}
}