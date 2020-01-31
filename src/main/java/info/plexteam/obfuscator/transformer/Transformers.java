package info.plexteam.obfuscator.transformer;

import info.plexteam.obfuscator.PObf;
import info.plexteam.obfuscator.transformer.transformers.NumberObfuscation;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author cookiedragon234 31/Oct/2019
 */
public class Transformers
{
	@SuppressWarnings("unchecked")
	private static Class<? extends Transformer>[] transformerClasses =
		new Class[]
		{
				NumberObfuscation.class
		};
	
	public static List<Transformer> getTransformers(PObf pObf, Random random)
	{
		List<Transformer> transformers = new ArrayList<>();
		
		for(Class<? extends Transformer> transformer : transformerClasses)
		{
			for(Constructor<?> declaredConstructor : transformer.getDeclaredConstructors())
			{
				try
				{
					transformers.add((Transformer)declaredConstructor.newInstance(pObf, random));
				}
				catch(Exception e)
				{
					throw new IllegalStateException(e);
				}
			}
		}
		
		return transformers;
	}
}