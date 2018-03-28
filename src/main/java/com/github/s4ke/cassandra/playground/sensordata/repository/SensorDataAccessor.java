package com.github.s4ke.cassandra.playground.sensordata.repository;

import com.datastax.driver.mapping.Result;
import com.datastax.driver.mapping.annotations.Accessor;
import com.datastax.driver.mapping.annotations.Query;
import com.github.s4ke.cassandra.playground.sensordata.model.SensorData;

/**
 * @author Martin Braun
 * @version 1.1.0
 * @since 1.1.0
 */
@Accessor
public interface SensorDataAccessor {

	@Query("SELECT * FROM SensorData")
	Result<SensorData> getAll();

}
