package mil.nga.giat.geowave.format.tdrive;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

import mil.nga.giat.geowave.core.index.StringUtils;

import org.apache.commons.io.IOUtils;
import org.geotools.feature.AttributeTypeBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.mortbay.log.Log;
import org.opengis.feature.simple.SimpleFeatureType;

import com.vividsolutions.jts.geom.Geometry;

/**
 * This is a convenience class for performing common GPX static utility methods
 * such as schema validation, file parsing, and SimpleFeatureType definition.
 */
public class TdriveUtils
{
	public static final String TDRIVE_POINT_FEATURE = "tdrivepoint";

	private static final ThreadLocal<DateFormat> dateFormat = new ThreadLocal<DateFormat>() {
		@Override
		protected DateFormat initialValue() {
			return new SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss");
		}
	};

	public static Date parseDate(
			String source )
			throws ParseException {
		return dateFormat.get().parse(
				source);
	}

	public static SimpleFeatureType createTdrivePointDataType() {

		final SimpleFeatureTypeBuilder simpleFeatureTypeBuilder = new SimpleFeatureTypeBuilder();
		simpleFeatureTypeBuilder.setName(TDRIVE_POINT_FEATURE);

		final AttributeTypeBuilder attributeTypeBuilder = new AttributeTypeBuilder();

		simpleFeatureTypeBuilder.add(attributeTypeBuilder.binding(
				Geometry.class).nillable(
				false).buildDescriptor(
				"geometry"));
		simpleFeatureTypeBuilder.add(attributeTypeBuilder.binding(
				Double.class).nillable(
				false).buildDescriptor(
				"taxiid"));
		simpleFeatureTypeBuilder.add(attributeTypeBuilder.binding(
				Double.class).nillable(
				true).buildDescriptor(
				"pointinstance"));
		simpleFeatureTypeBuilder.add(attributeTypeBuilder.binding(
				Date.class).nillable(
				true).buildDescriptor(
				"Timestamp"));
		simpleFeatureTypeBuilder.add(attributeTypeBuilder.binding(
				Double.class).nillable(
				true).buildDescriptor(
				"Latitude"));
		simpleFeatureTypeBuilder.add(attributeTypeBuilder.binding(
				Double.class).nillable(
				true).buildDescriptor(
				"Longitude"));

		return simpleFeatureTypeBuilder.buildFeatureType();

	}

	public static boolean validate(
			final File file ) {
		Scanner scanner = null;
		try {
			scanner = new Scanner(
					file,
					StringUtils.GEOWAVE_CHAR_SET.toString());
			if (scanner.hasNextLine()) {
				final String line = scanner.nextLine();
				return line.split(",").length == 4;
			}
		}
		catch (final Exception e) {
			Log.warn(
					"Error validating file: " + file.getName(),
					e);
			return false;
		}
		finally {
			IOUtils.closeQuietly(scanner);
		}
		return false;
	}
}
