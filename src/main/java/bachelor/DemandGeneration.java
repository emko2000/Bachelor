package bachelor;

import java.nio.file.Path;
import java.util.concurrent.Callable;

import org.matsim.freightDemandGeneration.FreightDemandGeneration;

import picocli.CommandLine;

@CommandLine.Command(name = "generate-scenario-BA-Emre", showDefaultValues = true)
public class DemandGeneration implements Callable<Integer>{
	
	@CommandLine.Option(names = "--output", description = "Path to output folder", defaultValue = "output/")
	private Path output;
	
	@CommandLine.Option(names = "--carrierVehicleFileLocation", description = "Path to carrierVehicleFileLocation")
	private Path vehicleFilePath;
	
	@CommandLine.Option(names = "--inputCarrierCSV", description = "Path to inputCarrierCSV")
	private Path carrierCSVLocation;
	
	@CommandLine.Option(names = "--inputDemandCSV", description = "Path to inputDemandCSV")
	private Path demandCSVLocation;
	
	@CommandLine.Option(names = "--shapeFileLocation", description = "Path to shapeFileLocation")
	private Path shapeFilePath;
	
	@CommandLine.Option(names = "--network", description = "Path to network.")
	private String network;
	
	@CommandLine.Option(names = "--combineSimilarJobs", defaultValue = "false", description = "Select the option if created jobs of the same carrier with same location and time will be combined. Options: true, false", required = true)
	private String combineSimilarJobs;
	
	public static void main(String[] args) {
		System.exit(new CommandLine(new DemandGeneration()).execute(args));
	}

	@Override
	public Integer call() throws Exception {
		
		if (carrierCSVLocation == null) {
				//layout ist nicht wichtig, capacity lassen, kosten pro meter und pro sekunde, networkmode, fixkosten
				vehicleFilePath = Path.of("scenarios/freightDemandGeneration/testVehicleTypes.xml");
				//csv-dateien ändern
				carrierCSVLocation = Path.of("scenarios/freightDemandGeneration/testCarrierCSV.csv");
				demandCSVLocation = Path.of("scenarios/freightDemandGeneration/testDemandCSV.csv");
				//shapefile von charlottenburg/wilmersdorf übertragen
				shapeFilePath = Path.of("scenarios/freightDemandGeneration/testShape/Bezirke_-_Berlin/Berlin_Bezirke.shp");
				//brauche ich nicht!!
				//String populationLocation = "scenarios/freightDemandGeneration/testPopulation.xml";
				//das neue netzwerk einfügen (mit drohnen)
				//String network = "C:\\Users\\Asus\\Documents\\Bachelor3\\network_drone.xml.gz";
				network = "C:\\Users\\Asus\\Documents\\Bachelor3\\network_DHL.xml.gz";
		//		String network = "https://svn.vsp.tu-berlin.de/repos/public-svn/matsim/scenarios/countries/de/berlin/berlin-v5.5-10pct/input/berlin-v5.5-network.xml.gz";
		//		String network = "https://raw.githubusercontent.com/matsim-org/matsim-libs/master/examples/scenarios/freight-chessboard-9x9/grid9x9.xml";
				combineSimilarJobs = "false";
		}
		new FreightDemandGeneration().execute(
						"--output", output.toString(),
						"--carrierOption", "createCarriersFromCSV",
						"--demandOption", "createDemandFromCSV",
						"--populationOption", "useNoPopulation",
						"--populationSamplingOption", "createMoreLocations",
						"--VRPSolutionsOption", "runJspritAndMATSim",
						"--combineSimilarJobs", combineSimilarJobs,
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
				return 0;
			}
}
