package cazcade.io;

import cazcade.fountain.datastore.impl.Constants;
import cazcade.fountain.datastore.impl.io.FountainNeoExporter;
import cazcade.fountain.datastore.impl.io.FountainNeoImporter;
import org.apache.commons.cli.*;
import org.neo4j.kernel.EmbeddedGraphDatabase;

import javax.xml.stream.XMLStreamException;
import java.io.File;
import java.io.IOException;

/**
 * @author neilellis@cazcade.com
 */
public class Main {
    public static void main(String[] args) throws ParseException, IOException, XMLStreamException {
        CommandLineParser parser = new PosixParser();
        Options options = new Options();
        options.addOption("n", "neo", true, "The neo4j directory to use.");
        options.addOption("j", "json", true, "The json file to use.");

        final CommandLine line = parser.parse(options, args);

        String neo;
        if (line.hasOption("neo")) {
            neo = line.getOptionValue("neo");
        } else {
            neo = Constants.FOUNTAIN_NEO_STORE_DIR;
        }
        String json;
        if (line.hasOption("json")) {
            json = line.getOptionValue("json");
        } else {
            json = Constants.FOUNTAIN_NEO_STORE_DIR + "/export/";
        }
        new File(json).mkdirs();

        if (line.getArgs().length > 0 && line.getArgs()[0].equals("export")) {
            EmbeddedGraphDatabase graphDatabase = new EmbeddedGraphDatabase(neo);
            FountainNeoExporter fountainNeoExporter = new FountainNeoExporter(graphDatabase);
            fountainNeoExporter.export(json);
            System.out.println("Exported to " + json);
            graphDatabase.shutdown();

//            Graph graph = new Neo4jGraph(neo);
//            OutputStream out = new FileOutputStream(json);
//            GraphMLWriter writer = new GraphMLWriter(graph);
//            writer.outputGraph(out);
        } else {
//
////            Graph graph = new Neo4jBatchGraph(neo);
//            Graph graph = new Neo4jGraph(neo);
//            InputStream in = new FileInputStream(json);
//            GraphMLReader reader = new GraphMLReader(graph);
//            reader.inputGraph(in);
            new File(neo + ".imported").mkdirs();
            FountainNeoImporter importer = new FountainNeoImporter(neo + ".imported");
            importer.importJson(json);
            System.out.println("Imported to " + neo + ".imported");
        }
    }
}
