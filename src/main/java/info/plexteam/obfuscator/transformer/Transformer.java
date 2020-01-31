package info.plexteam.obfuscator.transformer;

import info.plexteam.obfuscator.PObf;
import org.objectweb.asm.tree.*;

import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * @author cookiedragon234 31/Oct/2019
 */
public abstract class Transformer
{
	protected final PObf pObf;
	protected final Random random;
	
	public Transformer(PObf pObf, Random random)
	{
		this.pObf = pObf;
		this.random = random;
	}
	
	public abstract void transform(Map<String, ClassNode> classMap);
	
	public abstract Priority getPriority();
	
	protected boolean hasAnnotations(ClassNode classNode)
	{
		return areAnnotationsEmpty(
			classNode.visibleAnnotations,
			classNode.invisibleAnnotations,
			classNode.visibleTypeAnnotations,
			classNode.invisibleTypeAnnotations
		);
	}
	
	protected boolean hasAnnotations(MethodNode methodNode)
	{
		return areAnnotationsEmpty(
			methodNode.visibleAnnotations,
			methodNode.invisibleAnnotations,
			methodNode.visibleTypeAnnotations,
			methodNode.invisibleTypeAnnotations
		);
	}
	
	protected boolean hasAnnotations(FieldNode fieldNode)
	{
		return areAnnotationsEmpty(
			fieldNode.visibleAnnotations,
			fieldNode.invisibleAnnotations,
			fieldNode.visibleTypeAnnotations,
			fieldNode.invisibleTypeAnnotations
		);
	}
	
	private boolean areAnnotationsEmpty(List<AnnotationNode> visibleAnnotations, List<AnnotationNode> invisibleAnnotations, List<TypeAnnotationNode> visibleTypeAnnotations, List<TypeAnnotationNode> invisibleTypeAnnotations)
	{
		if(visibleAnnotations != null && !visibleAnnotations.isEmpty())
			return true;
		
		if(invisibleAnnotations != null && !invisibleAnnotations.isEmpty())
			return true;
		
		if(visibleTypeAnnotations != null && !visibleTypeAnnotations.isEmpty())
			return true;
		
		if(invisibleTypeAnnotations != null && !invisibleTypeAnnotations.isEmpty())
			return true;
		
		return false;
	}
	
	public enum Priority
	{
		LOWEST,
		LOW,
		NORMAL,
		HIGH,
		HIGHEST
	}
}