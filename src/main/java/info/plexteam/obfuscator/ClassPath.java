package info.plexteam.obfuscator;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;

import java.io.*;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.stream.Collectors;

/**
 * @author cookiedragon234 03/Nov/2019
 */
public class ClassPath
{
	public Map<String, ClassNode> classes = new HashMap<>();
	public Map<String, ClassNode> classPath = new HashMap<>();
	public Map<String, byte[]> files = new HashMap<>();

	private PObf cObf;
	private Random random;

	ClassPath(PObf cObf, Random random)
	{
		this.cObf = cObf;
		this.random = random;
	}

	void loadJar(File file)
	{
		loadJar(file, false);
	}

	void loadJar(File file, boolean asClassPath)
	{
		try
		{
			cObf.MANIFEST = null;

			JarFile jarFile = new JarFile(file);
			Enumeration<JarEntry> entries = jarFile.entries();

			while(entries.hasMoreElements())
			{
				JarEntry jarEntry = entries.nextElement();

				try
				{
					InputStream inputStream = jarFile.getInputStream(jarEntry);
					byte[] bytes = getBytesFromInputStream(inputStream);

					if(!jarEntry.isDirectory() && jarEntry.getName().endsWith(".class"))
					{
						ClassNode classNode = new ClassNode();
						ClassReader classReader = new ClassReader(bytes);

						classReader.accept(classNode, ClassReader.EXPAND_FRAMES);
						classPath.put(classNode.name, classNode);
						if(!asClassPath)
							classes.put(classNode.name, classNode);
					}
					else
					{
						files.put(jarEntry.getName(), bytes);

						if(jarEntry.getName().endsWith("META-INF/MANIFEST.MF"))
						{
							// Reopen the input stream, this allows us to read from the top of the file again
							inputStream.close();
							inputStream = jarFile.getInputStream(jarEntry);

							cObf.MANIFEST = new BufferedReader(new InputStreamReader(inputStream)).lines().collect(Collectors.toList());
						}
					}

					inputStream.close();
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}

			jarFile.close();
		}
		catch(Exception e)
		{
			throw new IllegalStateException(e);
		}
	}

	void saveJar(File file)
	{
		try
		{
			JarOutputStream jarOutputStream = new JarOutputStream(new FileOutputStream(file));

			// The order of the classes does not provide any logical difference, so by shuffling them
			// we can remove any potential information gathering from their order
			List<ClassNode> shuffledClasses = new ArrayList<>(classes.values());
			Collections.shuffle(shuffledClasses, random);

			for(ClassNode classNode : shuffledClasses)
			{
				JarEntry jarEntry = new JarEntry(classNode.name + ".class");
				ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS);
				jarOutputStream.putNextEntry(jarEntry);
				classNode.accept(classWriter);

				jarOutputStream.write(classWriter.toByteArray());
				jarOutputStream.closeEntry();
			}

			for(Map.Entry<String, byte[]> stringEntry : files.entrySet())
			{
				jarOutputStream.putNextEntry(new JarEntry(stringEntry.getKey()));
				jarOutputStream.write(stringEntry.getValue());
				jarOutputStream.closeEntry();
			}

			jarOutputStream.close();
		}
		catch(Exception e)
		{
			throw new IllegalStateException(e);
		}
	}

	private static byte[] getBytesFromInputStream(InputStream is) throws IOException
	{
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		byte[] buffer = new byte[0xFFFF];
		for (int len = is.read(buffer); len != -1; len = is.read(buffer))
		{
			os.write(buffer, 0, len);
		}
		return os.toByteArray();
	}
}
