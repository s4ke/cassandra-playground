package com.github.s4ke.cassandra.playground.sensordata.model;

import com.datastax.driver.mapping.annotations.Column;
import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author Martin Braun
 * @version 1.1.0
 * @since 1.1.0
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table( name = "SensorData"	)
@ToString
public class SensorData {

	@Column(name = "cityName")
	@PartitionKey(0)
	private String cityName;

	@Column
	@PartitionKey(1)
	private String date;

	@Column
	private Long timestamp;

	@Column
	private double lon;

	@Column
	private double lat;

	@Column
	private String type;

	@Column
	private double value;

}
