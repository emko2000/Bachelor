package bachelor;

import java.util.Set;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.core.network.NetworkUtils;
import org.locationtech.jts.geom.Coordinate;
import org.matsim.core.utils.geometry.geotools.MGC;
import org.matsim.core.utils.geometry.transformations.GeotoolsTransformation;
import org.matsim.core.utils.geometry.transformations.TransformationFactory;
import org.matsim.core.utils.gis.ShapeFileReader;
import org.locationtech.jts.geom.Geometry;
import org.matsim.utils.gis.shp2matsim.ShpGeometryUtils;
import org.matsim.freightDemandGeneration.FreightDemandGeneration;
import java.nio.file.Path;
import java.util.stream.Collectors;


public class NetworkModification {

	public static void main(String[] args) {

		int counter = 0;

		//read network file
		String networkLoc = "https://svn.vsp.tu-berlin.de/repos/public-svn/matsim/scenarios/countries/de/berlin/berlin-v5.5-10pct/input/berlin-v5.5-network.xml.gz";
		Network network = NetworkUtils.readNetwork(networkLoc);

		//read shapefile
		String shapefile = "scenarios/freightDemandGeneration/testShape/Bezirke_-_Berlin/Berlin_Bezirke.shp";

		//transform coordinates from the shapefile to matsim coordinates
		var transformation = TransformationFactory.getCoordinateTransformation("EPSG:31468", "EPSG:3857");
		var feature = ShapeFileReader.getAllFeatures(shapefile);



		for (var networkLink : network.getLinks().values()) {

			var freespeed = networkLink.getFreespeed();

			if (freespeed<8) {

				//Transfer the geotoolcoordinate to matsim coordinate
				for (var node : network.getNodes().values()) {

					var coord = node.getCoord();
					var transformCoord = transformation.transform(coord);
					var geotoolspoint = MGC.coord2Point(transformCoord);


					if (((Geometry) feature.iterator().next().getDefaultGeometry()).contains(geotoolspoint)) {

						counter = counter + 1;

						Link newLink = network.getFactory().createLink(Id.createLinkId("drone_" + counter), network.getNodes().get(Id.createNodeId("100027768")), node);
						Link newLinkback = network.getFactory().createLink(Id.createLinkId("drone_back_" + counter), node, network.getNodes().get(Id.createNodeId("100027768")));
						newLink.setAllowedModes(Set.of("drone"));
						newLinkback.setAllowedModes(Set.of("drone"));
						network.addLink(newLink);
						network.addLink(newLinkback);

					}


				}

			}
		}

		NetworkUtils.writeNetwork(network, "network2.xml.gz");

	}


		/*TODO:
		 *
		 *
		 * Hinweis: Iteration 300, Zeit in Sekunde, Infinite auswählen, csv nach excel kopieren und einfügen,
		 * in csv-file sind depots
		 *
		 *
		 *
		 */

}



