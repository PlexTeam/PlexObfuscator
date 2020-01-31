package info.plexteam.obfuscator.utils;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;

/**
 * @author jakuubkoo
 * @since 14/01/2020
 */
public class NumberUtils {

    public static boolean isIntegerNumber(AbstractInsnNode node){
        return node.getOpcode() == Opcodes.SIPUSH || node.getOpcode() == Opcodes.BIPUSH;
    }

    public static int getIntegerValue(AbstractInsnNode node){
        if (node.getOpcode() == Opcodes.SIPUSH || node.getOpcode() == Opcodes.BIPUSH) {
            return ((IntInsnNode) node).operand;
        }

        throw new IllegalArgumentException("This is not integer");
    }

    public static AbstractInsnNode generateIntPush(int i) {
        if (i >= -128 && i <= 127) {
            return new IntInsnNode(Opcodes.BIPUSH, i);
        }
        if (i >= -32768 && i <= 32767) {
            return new IntInsnNode(Opcodes.SIPUSH, i);
        }
        return new LdcInsnNode(i);
    }

    public static AbstractInsnNode generateXorPush(int i, int random) {
        if (i >= -128 && i <= 127) {
            return new IntInsnNode(Opcodes.BIPUSH,i ^ random);
        }
        if (i >= -32768 && i <= 32767) {
            return new IntInsnNode(Opcodes.SIPUSH,i ^ random);
        }
        return new LdcInsnNode(i);
    }


}
