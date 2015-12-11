package com.pau101.fairylights.core;

import java.util.List;
import java.util.logging.Logger;

import net.minecraft.launchwrapper.IClassTransformer;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

public class FairyLightsClassTransformer implements IClassTransformer {
	private Logger logger = Logger.getLogger("FairyLightsClassTransformer");

	private String fieldNameAddition = "currentFrustum";

	@Override
	public byte[] transform(String name, String transformedName, byte[] basicClass) {
		boolean obf = false;
		if ((obf = "bll".equals(name)) || "net.minecraft.client.renderer.EntityRenderer".equals(name)) {
			ClassNode classNode = new ClassNode();
			ClassReader classReader = new ClassReader(basicClass);
			classReader.accept(classNode, 0);

			String entityRendererOwner = obf ? "bll" : "net/minecraft/client/renderer/EntityRenderer";
			String frustumDesc = obf ? "Lbmp;" : "Lnet/minecraft/client/renderer/culling/Frustrum;";

			if (transformFrustumStore(obf, classNode.methods, entityRendererOwner, frustumDesc)) {
				classNode.fields.add(new FieldNode(Opcodes.ACC_PUBLIC, fieldNameAddition, frustumDesc, null, null));
			} else {
				logger.severe("Failed to transform the EntityRenderer!");
				return basicClass;
			}

			ClassWriter classWriter = new ClassWriter(0);
			classNode.accept(classWriter);
			return classWriter.toByteArray();
		}
		return basicClass;
	}

	private boolean transformFrustumStore(boolean obf, List<MethodNode> methods, String entityRendererOwner, String frustumDesc) {
		boolean replacedASTORE8 = false;
		for (MethodNode method : methods) {
			if ("(FJ)V".equals(method.desc)) {
				InsnList instructions = method.instructions;
				for (int i = 0; i < instructions.size(); i++) {
					AbstractInsnNode instruction = instructions.get(i);
					if (replacedASTORE8) {
						// replace all things trying to get the frustum from the
						// stack frame to getting the frustum from the class
						// field
						if (instruction.getOpcode() == Opcodes.ALOAD && ((VarInsnNode) instruction).var == 14) {
							((VarInsnNode) instruction).var = 0;
							instructions.insert(instruction, new FieldInsnNode(Opcodes.GETFIELD, entityRendererOwner, fieldNameAddition, frustumDesc));
						}
					} else {
						// instead of storing the newly created frustum into
						// slot 14 of the method stack frame, put in the added
						// class field for external access
						if (instruction.getOpcode() == Opcodes.ASTORE && ((VarInsnNode) instruction).var == 14) {
							InsnList newInsn = new InsnList();
							instructions.insertBefore(instructions.get(i - 3), new VarInsnNode(Opcodes.ALOAD, 0));
							instructions.set(instruction, new FieldInsnNode(Opcodes.PUTFIELD, entityRendererOwner, fieldNameAddition, frustumDesc));
							replacedASTORE8 = true;
						}
					}
				}
				break;
			}
		}
		return replacedASTORE8;
	}
}
