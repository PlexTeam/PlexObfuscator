package info.plexteam.obfuscator.transformer.transformers;

import info.plexteam.obfuscator.PObf;
import info.plexteam.obfuscator.config.TransformerConfig;
import info.plexteam.obfuscator.transformer.Transformer;
import info.plexteam.obfuscator.utils.NumberUtils;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.Map;
import java.util.Random;

/**
 * @author jakuubkoo
 * @since 14/01/2020
 */
public class NumberObfuscation extends Transformer {

    public NumberObfuscation(PObf pObf, Random random){
        super(pObf, random);
    }

    @Override
    public void transform(Map<String, ClassNode> classMap) {
        TransformerConfig transformerConfig = pObf.config.getTransformerConfig("NumberObfuscation");

        //Useless? but certainty..
        if (!transformerConfig.isEnabled()) return;

        for(ClassNode classNode : classMap.values()) {
            for(MethodNode methodNode : classNode.methods){
                if (methodNode != null){
                    for (AbstractInsnNode node : methodNode.instructions.toArray()){
                        if (NumberUtils.isIntegerNumber(node)) {

                            int value = NumberUtils.getIntegerValue(node);
                            int idc = random.nextInt(200);

                            value = value ^ idc;

                            methodNode.instructions.insertBefore(node, hovno(node, value, idc));
                            methodNode.instructions.remove(node);


                        }
                    }
                }
            }
        }

    }

    public InsnList hovno(AbstractInsnNode node, int value, int idc){
        InsnList methodInstructions = new InsnList();
        methodInstructions.insertBefore(node, NumberUtils.generateIntPush(value));
        methodInstructions.add(NumberUtils.generateIntPush(idc));
        methodInstructions.add(new InsnNode(Opcodes.IXOR));
        return methodInstructions;
    }

    @Override
    public Priority getPriority() {
        return Priority.NORMAL;
    }
}
