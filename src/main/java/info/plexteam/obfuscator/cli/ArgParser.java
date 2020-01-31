package info.plexteam.obfuscator.cli;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author jakuubkoo
 * @since 01/01/2020
 */
public class ArgParser
{
	private final Map<String, List<String>> params = new HashMap<>();
	
	/**
	 * Takes the cli arguments and parses it into a map of parameters
	 * Adapted from https://stackoverflow.com/a/26376532/11896374
	 * @param args The CLI args
	 * @return The parsed arguments
	 * @throws BadArgumentsException If the arguments supplied were not formatted properly
	 */
	public Map<String, List<String>> parse(String[] args) throws BadArgumentsException
	{
		List<String> options = null;
		for(final String arg : args)
		{
			if(arg.charAt(0) == '-')
			{
				if(arg.length() < 2)
				{
					throw new BadArgumentsException("Error at argument " + arg);
				}
				
				options = new ArrayList<>();
				params.put(arg.substring(1), options);
			}
			else if(options != null)
			{
				options.add(arg);
			}
			else
			{
				throw new BadArgumentsException("Illegal parameter usage");
			}
		}
		
		return params;
	}
}