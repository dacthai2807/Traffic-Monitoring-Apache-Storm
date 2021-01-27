package spout;

import java.io.Serializable;

import backtype.storm.tuple.Fields;


public class TupleInfo implements Serializable {
	private static final long serialVersionUID = 8524559685781732841L;
	
	private String vehicleID;
	private String dateTime;
	private Boolean occupied;
	private Double latitude;
	private Double longitude;
	private Double speed;
	private Integer bearing;

	private int numMember = 7;

	public TupleInfo() {
		 vehicleID = "";
		 dateTime = "";
		 occupied = false;	
		 latitude = 0.0;
		 longitude = 0.0;
		 speed = 0.0;
		 bearing = 0;			 
	}

	public TupleInfo(String[] input) { 
		 vehicleID = input[1];	
		 dateTime = input[2];
		 occupied = true;
		 latitude = Double.parseDouble(input[3]);
		 longitude = Double.parseDouble(input[4]);
		 speed = Double.parseDouble(input[5]);
		 bearing = Integer.parseInt(input[6]);			 
	}

	public int getTupleLength() {	
		return numMember;
	}

	public Fields getFieldList() {		
		Fields fieldList = new Fields("vehicleID", "dateTime", "occupied", "latitude", "longitude", "speed", "bearing");		
		return fieldList;
	}

	public static String getDelimiter() {
		String delimiter = ",";
		return delimiter;
	}
}
