package bachelor;

import java.util.ArrayList;
import java.util.Set;
import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.core.network.NetworkUtils;
import org.matsim.core.utils.geometry.geotools.MGC;
import org.matsim.core.utils.geometry.transformations.TransformationFactory;
import org.matsim.core.utils.gis.ShapeFileReader;
import org.locationtech.jts.geom.Geometry;



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


		//depot coordinate
		var depot_coordinates = new Coord(4588996.5,5828160);
		var depot_node = network.getFactory().createNode(Id.createNodeId("depot_node"), depot_coordinates);
		network.addNode(depot_node);

		var links = new ArrayList<Link>();

		//Transfer the geotoolcoordinate to matsim coordinate
		for (var Link : network.getLinks().values()) {

			//    m/s
			var freespeed = Link.getFreespeed();

			var node = Link.getFromNode();
			var coord = node.getCoord();
			var transformCoord = transformation.transform(coord);
			var geotoolspoint = MGC.coord2Point(transformCoord);


				if (((Geometry) feature.iterator().next().getDefaultGeometry()).contains(geotoolspoint)) {


					if (freespeed<=0.5) {

					counter = counter + 1;

					Link newLink = network.getFactory().createLink(Id.createLinkId("drone_" + counter), depot_node, node);
					Link newLinkback = network.getFactory().createLink(Id.createLinkId("drone_back_" + counter), node, depot_node);
					newLink.setAllowedModes(Set.of("drone"));
					newLinkback.setAllowedModes(Set.of("drone"));
					links.add(newLink);
					links.add(newLinkback);

					System.out.println("added new link");


					}

				}



		}

		for (var link : links) {
			network.addLink(link);
		}
		System.out.println(links.size());

		NetworkUtils.writeNetwork(network, "network2.xml.gz");

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

}

