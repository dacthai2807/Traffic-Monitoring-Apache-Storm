package sink;

import java.util.Map;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.IRichBolt;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;

public class ConsoleSink implements IRichBolt {
	private static final long serialVersionUID = 1L;
	
	private OutputCollector collector;	
	
	Integer taskID;
	String taskName;
	
	@Override
	public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
		this.collector = collector;	
		this.taskID = context.getThisTaskId();
		this.taskName = context.getThisComponentId();
	}
	
	@Override
	public void execute(Tuple input) {
		int roadID = Integer.parseInt(input.getValue(0).toString());
		String startDate = input.getValue(1).toString();
		String finishDate = input.getValue(2).toString();
		double averageSpeed = Double.parseDouble(input.getValue(3).toString());
		
		System.out.println(startDate + " -> " + finishDate + ": road " + roadID + " has average speed: " + averageSpeed);
		collector.ack(input);
	}
	
	@Override
	public void cleanup() {
		System.out.println("-- Console sink [" + taskName + " - " + taskID + "] --");
	}
	
	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		// TODO Auto-generated method stub			
	}
    
	@Override
	public Map<String, Object> getComponentConfiguration() {
		// TODO Auto-generated method stub
		return null;
	}
}
