package bachelor;

import java.nio.file.Path;

import org.matsim.freightDemandGeneration.FreightDemandGeneration;

public class DemandGeneration {
	public static void main(String[] args) {

		Path output = Path.of("output/");
		Path vehicleFilePath = Path.of("scenarios/freightDemandGeneration/testVehicleTypes.xml");
		// Neues CSV erstellen!!!!
		Path carrierCSVLocation = Path.of("scenarios/freightDemandGeneration/testCarrierCSV.csv");
		Path demandCSVLocation = Path.of("scenarios/freightDemandGeneration/testDemandCSV.csv");
		//* Shape von Berlin hinzufügen!!!
		Path shapeFilePath = Path.of("scenarios/freightDemandGeneration/testShape/bezirksgrenzen.shp");
		//*Population weglassen!!
		String populationLocation = "scenarios/freightDemandGeneration/testPopulation.xml";
		String network = "https://svn.vsp.tu-berlin.de/repos/public-svn/matsim/scenarios/countries/de/berlin/berlin-v5.5-10pct/input/berlin-v5.5-network.xml.gz";
//		String network = "https://raw.githubusercontent.com/matsim-org/matsim-libs/master/examples/scenarios/freight-chessboard-9x9/grid9x9.xml";
		new FreightDemandGeneration().execute(
				"--output", output.toString(),
				"--carrierOption", "createCarriersFromCSV",
				"--demandOption", "createDemandFromCSVAndUsePopulation",
				"--populationOption", "usePopulationInShape",
				"--populationSamplingOption", "createMoreLocations",
				"--VRPSolutionsOption", "runJspritAndMATSim",
				"--combineSimilarJobs", "false",
				"--carrierFileLocation", "",
				"--carrierVehicleFileLocation", vehicleFilePath.toString(),
				"--shapeFileLocation", shapeFilePath.toString(),
				"--shapeCRS", "WGS84",
				"--populationFileLocation", populationLocation.toString(),
				"--populationCRS", "WGS84",
				"--network", network,
				"--networkCRS", "WGS84",
				"--networkChangeEvents", "",
				"--inputCarrierCSV", carrierCSVLocation.toString(),
				"--inputDemandCSV", demandCSVLocation.toString(),
				"--populationSample", "0.5",
				"--populationSamplingTo", "1.0",
				"--defaultJspriIterations", "3");
	}
}
