package bachelor;

import java.util.Set;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
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

        //test-network
        Network test_network = NetworkUtils.createNetwork();

		//read shapefile
		String shapefile = "scenarios/freightDemandGeneration/testShape/Bezirke_-_Berlin/Berlin_Bezirke.shp";

		//transform coordinates from the shapefile to matsim coordinates
		var transformation = TransformationFactory.getCoordinateTransformation("EPSG:31468", "EPSG:3857");
		var feature = ShapeFileReader.getAllFeatures(shapefile);


		//depot coordinate
		var depot_coordinates = new Coord(4588996.5,5828160);
		var depot_node = network.getFactory().createNode(Id.createNodeId("depot_node"), depot_coordinates);
		network.addNode(depot_node);

        //Test-Depot
        test_network.addNode(depot_node);


		//is giving the values of the link
        for (var node : network.getNodes().values()) {

            //filter the Links to public transport out
            String regex = "pt_\\d\\d\\d\\d\\d\\d\\d\\d\\d\\d\\d\\d";
            var id = node.getId();

            Pattern pt = Pattern.compile(regex);
            Matcher mt = pt.matcher(String.valueOf(id));

            //Transfer the geotoolcoordinate to matsim coordinate
            var coord = node.getCoord();
            var transformCoord = transformation.transform(coord);
            var geotoolspoint = MGC.coord2Point(transformCoord);


            if (((Geometry) feature.iterator().next().getDefaultGeometry()).contains(geotoolspoint)) {

                if (mt.matches()) {

                    System.out.println("failed");

                } else {

                        counter = counter + 1;

                        Link newLink = network.getFactory().createLink(Id.createLinkId("drone_" + counter), depot_node, node);
                        Link newLinkback = network.getFactory().createLink(Id.createLinkId("drone_back_" + counter), node, depot_node);
                            //Link newLink = test_network.getFactory().createLink(Id.createLinkId("drone" + counter), depot_node, node);
                            //Link newLinkback = test_network.getFactory().createLink(Id.createLinkId("drone_back_" + counter), node, depot_node);
                        newLink.setAllowedModes(Set.of("drone"));
                        newLinkback.setAllowedModes(Set.of("drone"));
						newLink.setFreespeed(25);
						newLinkback.setFreespeed(25);
							test_network.addNode(node);
							test_network.addLink(newLink);
							test_network.addLink(newLinkback);
							network.addLink(newLink);
							network.addLink(newLinkback);

                        System.out.println("added new link");

						}

			}



		}


		NetworkUtils.writeNetwork(network, "network2.xml.gz");

		/*TODO:
		 *
		 *
		 * Hinweis: Iteration 300, Zeit in Sekunde, Infinite auswählen, csv nach excel kopieren und einfügen,
		 * in csv-file sind depots
		 *
		 * kein pt Links
		 * shapefile area1 id-name einfügen
		 *
		 * Depot-Link erstellen und Depot-Link in testCarrierCSV einfügen
		 * Depot-Link: Ein Startpunkt und Endpunkt für Drohnen erstellen
		 *
		 *
		 *
		 */



	}

}

