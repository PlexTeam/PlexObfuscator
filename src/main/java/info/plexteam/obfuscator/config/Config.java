package info.plexteam.obfuscator.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author jakuubkoo
 * @author cookiedragon234
 * @since 01/01/2020
 */
public class Config
{
	private JsonObject jsonObject;
	public String inputFile;
	public String outputFile;
	public List<TransformerConfig> transformerConfigs;
	Exclusion rootExclusions;

	public Config(File file) throws FileNotFoundException
	{
		this(new FileReader(file));
	}
	
	public Config(Reader reader)
	{
		jsonObject = JsonParser.parseReader(reader).getAsJsonObject();
		
		this.inputFile = jsonObject.get("input").getAsString();
		this.outputFile = jsonObject.get("output").getAsString();
		
		JsonObject transformers = jsonObject.get("transformers").getAsJsonObject();
		
		transformerConfigs = new ArrayList<>();
		for(Map.Entry<String, JsonElement> entry : transformers.entrySet())
		{
			transformerConfigs.add(new TransformerConfig(entry.getKey(), entry.getValue().getAsJsonObject()));
		}
		
		rootExclusions = new Exclusion(jsonObject.get("exclusions").getAsJsonArray());
	}

	public Config(String inputFile, String outputFile){
		this.inputFile = inputFile;
		this.outputFile = outputFile;
		transformerConfigs = new ArrayList<>();
		jsonObject = new JsonObject();
	}
	
	public TransformerConfig getTransformerConfig(String transformer)
	{
		for(TransformerConfig transformerConfig : transformerConfigs)
		{
			if(transformerConfig.getName().equals(transformer))
				return transformerConfig;
		}
		throw new BadConfigException("No config provided for " + transformer + " transformer");
	}
	
	public JsonObject getJsonObject()
	{
		return jsonObject;
	}
}