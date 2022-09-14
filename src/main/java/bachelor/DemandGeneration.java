package bachelor;

import java.nio.file.Path;

import org.matsim.freightDemandGeneration.FreightDemandGeneration;

public class DemandGeneration {
	public static void main(String[] args) {
//freightDemandGeneration ändern
		Path output = Path.of("output/");
		//layout ist nicht wichtig, capacity lassen, kosten pro meter und pro sekunde, networkmode, fixkosten
		Path vehicleFilePath = Path.of("scenarios/freightDemandGeneration/testVehicleTypes.xml");
		//csv-dateien ändern
		Path carrierCSVLocation = Path.of("scenarios/freightDemandGeneration/testCarrierCSV.csv");
		Path demandCSVLocation = Path.of("scenarios/freightDemandGeneration/testDemandCSV.csv");
		//shapefile von charlottenburg/wilmersdorf übertragen
		Path shapeFilePath = Path.of("scenarios/freightDemandGeneration/testShape/Bezirke_-_Berlin/Berlin_Bezirke.shp");
		//brauche ich nicht!!
		//String populationLocation = "scenarios/freightDemandGeneration/testPopulation.xml";
		//das neue netzwerk einfügen (mit drohnen)
		String network = "C:\\Users\\Asus\\Documents\\Bachelor3\\network2.xml.gz";
//		String network = "https://svn.vsp.tu-berlin.de/repos/public-svn/matsim/scenarios/countries/de/berlin/berlin-v5.5-10pct/input/berlin-v5.5-network.xml.gz";
//		String network = "https://raw.githubusercontent.com/matsim-org/matsim-libs/master/examples/scenarios/freight-chessboard-9x9/grid9x9.xml";
		new FreightDemandGeneration().execute(
				"--output", output.toString(),
				"--carrierOption", "createCarriersFromCSV",
				"--demandOption", "createDemandFromCSV",
				"--populationOption", "useNoPopulation",
				"--populationSamplingOption", "createMoreLocations",
				"--VRPSolutionsOption", "runJspritAndMATSim",
				"--combineSimilarJobs", "false",
				"--carrierFileLocation", "",
				"--carrierVehicleFileLocation", vehicleFilePath.toString(),
				"--shapeFileLocation", shapeFilePath.toString(),
				"--shapeCRS", "EPSG:3857",
				"--populationFileLocation", "",
				"--populationCRS", "WGS84",
				"--network", network,
				"--networkCRS", "EPSG:31468",
				"--networkChangeEvents", "",
				"--inputCarrierCSV", carrierCSVLocation.toString(),
				"--inputDemandCSV", demandCSVLocation.toString(),
				"--populationSample", "0.5",
				"--populationSamplingTo", "1.0",
				"--defaultJspriIterations", "3");
	}
}
