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

        //drone-network
        Network drone_network = NetworkUtils.createNetwork();


		//read shapefile
		String shapefile = "scenarios/freightDemandGeneration/testShape/Bezirke_-_Berlin/Berlin_Bezirke.shp";

		//transform coordinates from the shapefile to matsim coordinates
		var transformation = TransformationFactory.getCoordinateTransformation("EPSG:31468", "EPSG:3857");
		var feature = ShapeFileReader.getAllFeatures(shapefile);

		//create depot link
		//depot coordinate
		var depot_coordinates_start = new Coord(4588956.5,5828160);
		var depot_node_start = network.getFactory().createNode(Id.createNodeId("depot_node_start"), depot_coordinates_start);

		var depot_coordinates_end = new Coord(4589143.5,5828153);
		var depot_node_end = network.getFactory().createNode(Id.createNodeId("depot_node_end"), depot_coordinates_end);
        //Add depot to drone-network
		Link depotLink = network.getFactory().createLink(Id.createLinkId("depotLink"), depot_node_end, depot_node_start);
		depotLink.setCapacity(20000);
		depotLink.setFreespeed(30);
		depotLink.setAllowedModes(Set.of("drone"));

		//Add depot to dhl-network
		var depottest = network.getNodes().get(Id.createNodeId(27177359));
		var depottest2 = network.getNodes().get(Id.createNodeId(27177345));
		Link depotLink_dhl = network.getFactory().createLink(Id.createLinkId("depotLink_dhl"),depottest, depottest2);
		depotLink_dhl.setCapacity(20000);
		depotLink_dhl.setFreespeed(30);
		network.addLink(depotLink_dhl);

        //Add Drone-Depot to drone_network
        drone_network.addNode(depot_node_start);
        drone_network.addNode(depot_node_end);
		depotLink.setAllowedModes(Set.of("drone"));
		drone_network.addLink(depotLink);

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


                } else {

                        counter = counter + 1;

                        Link newLink = drone_network.getFactory().createLink(Id.createLinkId("drone" + counter), depot_node_start, node);
						Link newLinkback = drone_network.getFactory().createLink(Id.createLinkId("drone_back_" + counter), node, depot_node_end);

						newLink.setAllowedModes(Set.of("drone"));
                        newLinkback.setAllowedModes(Set.of("drone"));
						newLink.setFreespeed(90);
						newLinkback.setFreespeed(90);
						newLink.setCapacity(20000);
						newLinkback.setCapacity(20000);

						drone_network.addNode(node);
						drone_network.addLink(newLink);
						drone_network.addLink(newLinkback);

                        System.out.println("added new link");

						}

			}


		}


		NetworkUtils.writeNetwork(drone_network, "network_drone.xml.gz");
		///NetworkUtils.writeNetwork(network, "network_DHL.xml.gz");

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

