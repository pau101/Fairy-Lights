package com.pau101.fairylights.core;

import java.util.List;
import java.util.logging.Logger;

import net.minecraft.launchwrapper.IClassTransformer;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

public class FairyLightsClassTransformer implements IClassTransformer {
	private Logger logger = Logger.getLogger("FairyLightsClassTransformer");

	private String fieldNameAddition = "currentFrustum";

	@Override
	public byte[] transform(String name, String transformedName, byte[] basicClass) {
		boolean obf = false;
		if ((obf = "blt".equals(name)) || "net.minecraft.client.renderer.EntityRenderer".equals(name)) {
			ClassNode classNode = new ClassNode();
			ClassReader classReader = new ClassReader(basicClass);
			classReader.accept(classNode, 0);

			String entityRendererOwner = obf ? "blt" : "net/minecraft/client/renderer/EntityRenderer";
			String frustumDesc = obf ? "Lbmx;" : "Lnet/minecraft/client/renderer/culling/Frustrum;";

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

	private int getLocalVarIndex(List<LocalVariableNode> list, String name, String name2) {
		// Returns the index of a specific local variable name from the method. This is required for compatibility with any Optifine version.
		// It uses a list which you can create in the specific method using method.localVariables, and two names since for some dumb reasons,
		// The game can access varX fine with the latest Optifine but not the internal name, and the opposite otherwise.

		for (int i = 0; i < list.size(); i += 1) {
			LocalVariableNode object = list.get(i);
			if (object.name.equals(name) || object.name.equals(name2)) return object.index;
		}
		return -1;

	}

	private boolean transformFrustumStore(boolean obf, List<MethodNode> methods, String entityRendererOwner, String frustumDesc) {
		boolean replacedASTORE8 = false;
		for (MethodNode method : methods) {
			List localVarList = method.localVariables;
			int var14index = getLocalVarIndex(localVarList, "var14", "frustrum");

			if ("(FJ)V".equals(method.desc)) {
				InsnList instructions = method.instructions;
				for (int i = 0; i < instructions.size(); i++) {
					AbstractInsnNode instruction = instructions.get(i);
					if (replacedASTORE8) {
						// replace all things trying to get the frustum from the
						// stack frame to getting the frustum from the class
						// field
						if (instruction.getOpcode() == Opcodes.ALOAD && ((VarInsnNode) instruction).var == var14index) {
							((VarInsnNode) instruction).var = 0;
							instructions.insert(instruction, new FieldInsnNode(Opcodes.GETFIELD, entityRendererOwner, fieldNameAddition, frustumDesc));
						}

					} else {
						// instead of storing the newly created frustum into
						// slot 14 of the method stack frame, put in the added
						// class field for external access
						if (instruction.getOpcode() == Opcodes.ASTORE && ((VarInsnNode) instruction).var == var14index) {
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
