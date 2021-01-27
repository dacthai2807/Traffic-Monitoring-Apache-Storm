package bolt;

import java.util.List;
import java.util.Map;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.IRichBolt;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;

public class MapMatchingBolt implements IRichBolt {
	private static final long serialVersionUID = -433427751113113358L;
	
	private final double LAT_MIN = 39f;
    private final double LAT_MAX = 41f;
    private final double LON_MIN = 116f;
    private final double LON_MAX = 120f;
    
    private final int LATS_SECTOR = 17;
    private final int LONS_SECTOR = 24;
	
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
		double latitude = Double.parseDouble(input.getValues().get(3).toString());
		double longitude = Double.parseDouble(input.getValues().get(4).toString());
		if (longitude > LON_MAX || longitude < LON_MIN || latitude > LAT_MAX || latitude < LAT_MIN) return;
		int latID = (int)((latitude - LAT_MIN) / ((LAT_MAX - LAT_MIN) / (LATS_SECTOR - 1)));
		int lonID = (int)((longitude - LON_MIN) / ((LON_MAX - LON_MIN) / (LONS_SECTOR - 1)));
		int roadID = latID * LONS_SECTOR + lonID;
		List<Object> values = input.getValues();
        values.add(roadID);
        collector.emit(input, values);
        collector.ack(input);
	}
	
	@Override
	public void cleanup() {
		System.out.println("-- Map matching bolt [" + taskName + " - " + taskID + "] --");

	}
	
	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields ("vehicleID", "dateTime", "occupied", "latitude", "longitude", "speed", "bearing", "roadID"));				
	}
    
	@Override
	public Map<String, Object> getComponentConfiguration() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
