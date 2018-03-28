package com.github.s4ke.cassandra.playground.client;

import java.util.List;
import java.util.Random;


import com.datastax.driver.core.Session;
import com.datastax.driver.core.schemabuilder.SchemaBuilder;
import com.datastax.driver.extras.codecs.date.SimpleTimestampCodec;
import com.datastax.driver.extras.codecs.joda.LocalDateCodec;
import com.datastax.driver.mapping.Mapper;
import com.datastax.driver.mapping.MappingManager;

import com.github.s4ke.cassandra.playground.sensordata.model.SensorData;
import com.github.s4ke.cassandra.playground.sensordata.repository.SensorDataAccessor;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CassandraClient {

	private static final Logger LOG = LoggerFactory.getLogger( CassandraClient.class );

	public static void main(String args[]) {
		CassandraConnector connector = new CassandraConnector();
		connector.connect( "192.168.83.131", null );
		Session session = connector.getSession();

		session.getCluster().getConfiguration().getCodecRegistry().register( SimpleTimestampCodec.instance );
		session.getCluster().getConfiguration().getCodecRegistry().register( LocalDateCodec.instance );

		session.execute( log( SchemaBuilder.createKeyspace( "weather" )
									  .ifNotExists()
									  .with()
									  .replication( ImmutableMap.of(
											  "class",
											  "SimpleStrategy",
											  "replication_factor",
											  1
									  ) ) ) );

		session.execute( "USE weather" );

		maybeRecreate( session, true );

		{
			MappingManager manager = new MappingManager( session );
			//Mapper<SensorData> mapper = manager.mapper( SensorData.class );
			SensorDataAccessor accessor = manager.createAccessor( SensorDataAccessor.class );
			for ( SensorData sensorData : accessor.getAll() ) {
				System.out.println( sensorData );
			}
		}

		connector.close();
	}

	private static <T> T log(T t) {
		System.out.println( t.toString() );
		return t;
	}

	private static void maybeRecreate(Session session, boolean recreate) {
		if ( recreate ) {
			//hacky, drop existing table
			session.execute( SchemaBuilder.dropTable( "sensorData" ).ifExists() );

			//define SensorData Table
			session.execute( log( "\tCREATE TABLE IF NOT EXISTS SensorData(\n" +
										  "\t\tcityName text,\n" +
										  "\t\tdate text,\n" +
										  "\t\ttype text,\n" +
										  "\t\ttimestamp timestamp,\n" +
										  "\t\tlat double,\n" +
										  "\t\tlon double,\n" +
										  "\t\tvalue double,\n" +
										  "\t\tPRIMARY KEY((cityName, type, date), timestamp))" )
			);

			MappingManager manager = new MappingManager( session );
			Mapper<SensorData> mapper = manager.mapper( SensorData.class );
			SensorDataAccessor accessor = manager.createAccessor( SensorDataAccessor.class );

			populate( mapper, accessor );

		}
	}

	private static void populate(Mapper<SensorData> mapper, SensorDataAccessor accessor) {
		List<String> cities = ImmutableList.of( "Bayreuth", "Bamberg", "Hof" );
		Random random = new Random();
		for ( int i = 0; i < 10000; ++i ) {
			SensorData sensorData = SensorData.builder()
					.cityName( cities.get( i % cities.size() ) )
					.lat( 10 )
					.lon( 10 )
					.date( "2018-03-28" )
					.timestamp( System.currentTimeMillis() )
					.type( "temp" )
					.value( random.nextInt( 100 ) ).build();
			mapper.save( sensorData );
		}
	}

}
