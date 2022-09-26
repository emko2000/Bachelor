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

		//create depot link
		//depot coordinate
		var depot_coordinates_start = new Coord(4588996.5,5828160);
		var depot_node_start = network.getFactory().createNode(Id.createNodeId("depot_node_start"), depot_coordinates_start);
		network.addNode(depot_node_start);

		var depot_coordinates_end = new Coord(4588998.5,5828160);
		var depot_node_end = network.getFactory().createNode(Id.createNodeId("depot_node_end"), depot_coordinates_end);
		network.addNode(depot_node_end);

		Link depotLink = network.getFactory().createLink(Id.createLinkId("depotLink"), depot_node_end, depot_node_start);
		depotLink.setCapacity(50);
		depotLink.setAllowedModes(Set.of("drone"));
		network.addLink(depotLink);

        //Test-Depot
        test_network.addNode(depot_node_start);
        test_network.addNode(depot_node_end);
		depotLink.setAllowedModes(Set.of("drone"));
		test_network.addLink(depotLink);

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

                        //Link newLink = network.getFactory().createLink(Id.createLinkId("drone_" + counter), depot_node_start, node);
                        //Link newLinkback = network.getFactory().createLink(Id.createLinkId("drone_back_" + counter), node, depot_node_end);
                            Link newLink = test_network.getFactory().createLink(Id.createLinkId("drone" + counter), depot_node_start, node);
                            Link newLinkback = test_network.getFactory().createLink(Id.createLinkId("drone_back_" + counter), node, depot_node_end);
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


		NetworkUtils.writeNetwork(test_network, "network2.xml.gz");

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
		 *	plausible Benennung
		 * 	shipments erstellen mit depotLink, size and capacity gleich 1, Start 8 Uhr und Ende 20 Uhr
		 *	Demandfile angucken und ändern
		 *
		 *
		 * Carrierfile anpassen (bis 20 Uhr)
		 * Zeiten dichter takten oder mehr delivery, damit die mehr Drohnen starten
		 */



	}

}

