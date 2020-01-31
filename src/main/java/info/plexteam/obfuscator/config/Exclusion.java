package info.plexteam.obfuscator.config;

import com.google.gson.JsonArray;
import jdk.internal.org.objectweb.asm.tree.ClassNode;
import jdk.internal.org.objectweb.asm.tree.FieldNode;
import jdk.internal.org.objectweb.asm.tree.MethodNode;

import java.util.regex.Pattern;

/**
 * @author jakuubkoo
 * @since 01/01/2020
 */
public class Exclusion
{
	private final Pattern[] exclusions;
	
	public Exclusion(JsonArray jsonArray)
	{
		this(jsonArrayToStrArray(jsonArray));
	}
	
	public Exclusion(String[] exclusions)
	{
		Pattern[] patterns = new Pattern[exclusions.length];
		
		for(int i = 0; i < exclusions.length; i++)
		{
			patterns[i] = Pattern.compile(exclusions[i]);
		}
		
		this.exclusions = patterns;
	}
	
	public boolean isExcluded(ClassNode classNode, Config config)
	{
		return isExcluded(classNode.name, config);
	}
	
	public boolean isExcluded(MethodNode methodNode, Config config)
	{
		return isExcluded(methodNode.name, config);
	}
	
	public boolean isExcluded(FieldNode fieldNode, Config config)
	{
		return isExcluded(fieldNode.name, config);
	}
	
	public boolean isExcluded(String name, Config config)
	{
		for(Pattern exclusion : exclusions)
		{
			if(exclusion.matcher(name).matches())
			{
				return true;
			}
		}
		
		if(this != config.rootExclusions)
		{
			return config.rootExclusions.isExcluded(name, config);
		}
		
		return false;
	}
	
	private static String[] jsonArrayToStrArray(JsonArray jsonArray)
	{
		String[] strArr = new String[jsonArray.size()];
		for(int i = 0; i < jsonArray.size(); i++)
		{
			strArr[i] = jsonArray.get(i).getAsString();
		}
		return strArr;
	}
}