package topology;

import org.apache.log4j.BasicConfigurator;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.generated.AlreadyAliveException;
import backtype.storm.generated.InvalidTopologyException;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.tuple.Fields;
import backtype.storm.utils.Utils;
import bolt.MapMatchingBolt;
import bolt.SpeedCalculatorBolt;
import sink.ConsoleSink;
import spout.FileSpout;

public class TrafficMonitoringTopology {
    
    public static void main(String[] args) throws AlreadyAliveException, InvalidTopologyException, InterruptedException {
    	TopologyBuilder builder = new TopologyBuilder();
    	FileSpout fileSpout = new FileSpout();
    	MapMatchingBolt matchingBolt = new MapMatchingBolt();
    	SpeedCalculatorBolt speedCalculatorBolt = new SpeedCalculatorBolt();
    	ConsoleSink consoleSink = new ConsoleSink();
    	builder.setSpout("Spout", fileSpout, 1);      
    	builder.setBolt("MapMatchingBolt", matchingBolt, 1).shuffleGrouping("Spout");
    	builder.setBolt("SpeedCalculatorBolt", speedCalculatorBolt, 1).fieldsGrouping("MapMatchingBolt", new Fields("roadID"));
    	builder.setBolt("ConsoleSink", consoleSink, 1).shuffleGrouping("SpeedCalculatorBolt");
    	BasicConfigurator.configure();
    	LocalCluster cluster = new LocalCluster();
    	Config conf = new Config();
    	conf.setNumWorkers(20);
    	conf.setMaxSpoutPending(5000);
    	cluster.submitTopology("MyTopology", conf, builder.createTopology());
    	Utils.sleep(10000);
    	cluster.killTopology("MyTopology");
    	cluster.shutdown();
    }

}
