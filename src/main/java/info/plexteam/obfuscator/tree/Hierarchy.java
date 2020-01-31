package info.plexteam.obfuscator.tree;

import info.plexteam.obfuscator.PObf;
import org.objectweb.asm.tree.ClassNode;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author jakuubkoo
 * @since 01/01/2020
 *
 * This is all basically from ItzSomebody
 */
public class Hierarchy
{
	private final PObf pObf;
	private final Map<String, TreeBranch<ClassNode, String>> hierarchy;
	
	public Hierarchy(PObf pObf)
	{
		this.pObf = pObf;
		this.hierarchy = new HashMap<>();
	}
	
	public TreeBranch<ClassNode, String> getBranch(String name)
	{
		if(!hierarchy.containsKey(name))
		{
			if(!pObf.classPath.classPath.containsKey(name))
				throw new IllegalStateException("Class " + name + " was not found in the classpath!");

			ClassNode classNode = pObf.classPath.classPath.get(name);
			buildHierarchy(classNode, null);
		}

		return hierarchy.get(name);
	}

	public void buildHierarchy(ClassNode classNode, ClassNode subClass)
	{
		if(hierarchy.get(classNode.name) == null)
		{
			TreeBranch<ClassNode, String> treeBranch = new TreeBranch<>(classNode);

			if(classNode.superName != null)
			{
				treeBranch.getParents().add(classNode.superName);

				if(!pObf.classPath.classPath.containsKey(classNode.superName) || classNode.superName == null)
					throw new IllegalStateException("Class " + classNode.superName + " was not found in the classpath!");

				buildHierarchy(pObf.classPath.classPath.get(classNode.superName), classNode);
			}

			if(classNode.interfaces != null)
			{
				for(Object anInterface : classNode.interfaces)
				{
					treeBranch.getParents().add(String.valueOf(anInterface));

					if(!pObf.classPath.classPath.containsKey(anInterface) || anInterface == null)
						throw new IllegalStateException("Class " + anInterface + " was not found in the classpath!");

					buildHierarchy(pObf.classPath.classPath.get(anInterface), classNode);
				}
			}

			hierarchy.put(classNode.name, treeBranch);
		}

		if(subClass != null)
		{
			hierarchy.get(classNode.name).getChildren().add(subClass.name);
		}
	}
}
