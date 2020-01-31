package info.plexteam.obfuscator.tree;

import java.util.HashSet;
import java.util.Set;

/**
 * @author jakuubkoo
 * @since 01/01/2020
 */
public class TreeBranch<T, V>
{
	private final T value;
	private final Set<V> parents;
	private final Set<V> children;
	
	public TreeBranch(T value)
	{
		this.value = value;
		this.parents = new HashSet<>();
		this.children = new HashSet<>();
	}
	
	public T getValue()
	{
		return value;
	}
	
	public Set<V> getParents()
	{
		return parents;
	}
	
	public Set<V> getChildren()
	{
		return children;
	}
}