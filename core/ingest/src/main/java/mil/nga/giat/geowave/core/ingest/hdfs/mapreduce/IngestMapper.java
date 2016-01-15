package mil.nga.giat.geowave.core.ingest.hdfs.mapreduce;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import mil.nga.giat.geowave.core.index.ByteArrayId;
import mil.nga.giat.geowave.core.index.ByteArrayUtils;
import mil.nga.giat.geowave.core.index.PersistenceUtils;
import mil.nga.giat.geowave.core.ingest.GeoWaveData;
import mil.nga.giat.geowave.core.store.CloseableIterator;
import mil.nga.giat.geowave.mapreduce.output.GeoWaveOutputKey;

import org.apache.avro.mapred.AvroKey;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.mapreduce.Mapper;

/**
 * This class is the map-reduce mapper for ingestion with the mapper only.
 */
public class IngestMapper extends
		Mapper<AvroKey, NullWritable, GeoWaveOutputKey, Object>
{
	private IngestWithMapper ingestWithMapper;
	private String globalVisibility;
	private List<ByteArrayId> primaryIndexIds;

	@Override
	protected void map(
			final AvroKey key,
			final NullWritable value,
			final org.apache.hadoop.mapreduce.Mapper.Context context )
			throws IOException,
			InterruptedException {
		try (CloseableIterator<GeoWaveData> data = ingestWithMapper.toGeoWaveData(
				key.datum(),
				primaryIndexIds,
				globalVisibility)) {
			while (data.hasNext()) {
				final GeoWaveData d = data.next();
				context.write(
						d.getKey(),
						d.getValue());
			}
		}
	}

	@Override
	protected void setup(
			final org.apache.hadoop.mapreduce.Mapper.Context context )
			throws IOException,
			InterruptedException {
		super.setup(context);
		try {
			final String ingestWithMapperStr = context.getConfiguration().get(
					AbstractMapReduceIngest.INGEST_PLUGIN_KEY);
			final byte[] ingestWithMapperBytes = ByteArrayUtils.byteArrayFromString(ingestWithMapperStr);
			ingestWithMapper = PersistenceUtils.fromBinary(
					ingestWithMapperBytes,
					IngestWithMapper.class);
			globalVisibility = context.getConfiguration().get(
					AbstractMapReduceIngest.GLOBAL_VISIBILITY_KEY);
			primaryIndexIds = AbstractMapReduceIngest.getPrimaryIndexIds(context.getConfiguration());
		}
		catch (final Exception e) {
			throw new IllegalArgumentException(
					e);
		}
	}
}
