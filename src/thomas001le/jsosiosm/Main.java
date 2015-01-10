package thomas001le.jsosiosm;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.zip.GZIPInputStream;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;

import thomas001le.jsosiosm.plugins.AdministrativeOmråderPlugin;
import thomas001le.jsosiosm.plugins.ArealdekkePlugin;
import thomas001le.jsosiosm.plugins.BygningerPlugin;
import thomas001le.jsosiosm.plugins.GeneralPlugin;
import thomas001le.jsosiosm.plugins.HoydePlugin;
import thomas001le.jsosiosm.plugins.RestriksjonsområderPlugin;
import thomas001le.jsosiosm.plugins.SamferdselPlugin;
import thomas001le.jsosiosm.plugins.SkrivemåtePlugin;

public class Main {
	
	private static ParserPlugin loadScript(String filename) throws FileNotFoundException, ScriptException, UnsupportedEncodingException {
		String ext = filename.substring(filename.lastIndexOf('.')+1);
		ScriptEngineManager manager = new ScriptEngineManager();
		ScriptEngine engine = manager.getEngineByExtension(ext);
		InputStream file = new FileInputStream(filename);
		engine.eval(new InputStreamReader(file,"UTF-8"));
		ParserPlugin plugin = ( (Invocable) engine ).getInterface(ParserPlugin.class);
		if(plugin == null)
			throw new ScriptException("ParserPlugin interface not found in file " + filename);
		return plugin;
	}

	public static void main(String[] args) throws Exception {
		
		Options options = new Options();
		options.addOption("o",true,"output file (.osm or .osm.pbf)");
		options.addOption("s",true,"load script");
		CommandLineParser clp = new PosixParser();
		CommandLine cmdline = clp.parse(options, args);
		String outname = cmdline.getOptionValue("o", "out.osm.pbf");
		
		SOSIToOSMParser parser = new SOSIToOSMParser(outname);
		
		if(cmdline.hasOption("s")) {
			for(String filename : cmdline.getOptionValues("s")) {
				ParserPlugin plugin = loadScript(filename);
				parser.registerPlugin(plugin);
			}
		}
		parser.registerPlugin(new ArealdekkePlugin());
		parser.registerPlugin(new GeneralPlugin());
		parser.registerPlugin(new BygningerPlugin());
		parser.registerPlugin(new HoydePlugin());
		parser.registerPlugin(new SamferdselPlugin());
		parser.registerPlugin(new SkrivemåtePlugin());
		parser.registerPlugin(new RestriksjonsområderPlugin());
		parser.registerPlugin(new AdministrativeOmråderPlugin());
		
		for(String arg: cmdline.getArgs()) {
			System.out.println(arg);
			InputStream in = new FileInputStream(arg);
			if( arg.endsWith(".gz"))
				in = new GZIPInputStream(in);
			parser.reset(in);
			parser.parse();
		}
		parser.close();

	}

}
