package bachelor;

import java.util.Set;

import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.core.network.NetworkUtils;

public class NetworkModification {

	public static void main(String[] args) {
		
		String networkLoc = "https://svn.vsp.tu-berlin.de/repos/public-svn/matsim/scenarios/countries/de/berlin/berlin-v5.5-10pct/input/berlin-v5.5-network.xml.gz";
		
		Network network = NetworkUtils.readNetwork(networkLoc);
		/*TODO: 
		 * shape file Berlin Bezirke einlesen
		 * Link sfiltern nach Bezirk Charlottenburg-Wilmersdorf
		 * neue Links für Drohne erzeugen (Hin-und Rückweg)
		 * 
		 */
		Link newLink= network.getFactory().createLink(null, null, null);
		newLink.setAllowedModes(Set.of("drone"));
		
		network.addLink(newLink);

	}

}
