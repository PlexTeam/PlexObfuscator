package info.plexteam.obfuscator.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * @author jakuubkoo
 * @since 01/01/2020
 */
public class TransformerConfig
{
	protected final String name;
	protected final JsonObject jsonObject;
	
	public TransformerConfig(String name, JsonObject jsonObject)
	{
		this.name = name;
		this.jsonObject = jsonObject;
	}
	
	public String getName()
	{
		return name;
	}
	
	public boolean isEnabled()
	{
		return jsonObject.get("enabled").getAsBoolean();
	}
	
	public JsonElement get(String memberName)
	{
		return jsonObject.get(memberName);
	}
	
	public JsonObject getJsonObject()
	{
		return jsonObject;
	}
	
	public Exclusion getExclusions()
	{
		return new Exclusion(jsonObject.get("exclusions").getAsJsonArray());
	}
}