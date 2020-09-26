// v1.0.0
(function (scope) {

var ASMAPI = Java.type("net.minecraftforge.coremod.api.ASMAPI");
var Label = Java.type("org.objectweb.asm.Label");
var Type = Java.type("org.objectweb.asm.Type");
var FieldInsnNode = Java.type("org.objectweb.asm.tree.FieldInsnNode");
var IincInsnNode = Java.type("org.objectweb.asm.tree.IincInsnNode");
var JumpInsnNode = Java.type("org.objectweb.asm.tree.JumpInsnNode");
var InsnList = Java.type("org.objectweb.asm.tree.InsnList");
var InsnNode = Java.type("org.objectweb.asm.tree.InsnNode");
var IntInsnNode = Java.type("org.objectweb.asm.tree.IntInsnNode");
var LabelNode = Java.type("org.objectweb.asm.tree.LabelNode");
var LdcInsnNode = Java.type("org.objectweb.asm.tree.LdcInsnNode");
var MethodInsnNode = Java.type("org.objectweb.asm.tree.MethodInsnNode");
var TypeInsnNode = Java.type("org.objectweb.asm.tree.TypeInsnNode");
var VarInsnNode = Java.type("org.objectweb.asm.tree.VarInsnNode");

function Primitive(value) {
    this.value = value;
}

Primitive.prototype.toString = function () {
    return this.type.getClassName() + "(" + this.value + ")";
};

Primitive.prototype.asConstant = function () {
    // Workaround for java.lang.Number types not being visible through sandbox class filter
    var arr = new (this.arrayType)(1);
    arr[0] = this.value;
    return arr[0];
};

function ClassName(name) {
    this.name = Array.isArray(name) ? name : name ? [ name ] : [];
}

ClassName.prototype.__noSuchProperty__ = function (id) {
    return new ClassName(this.name.concat(id));
};

ClassName.prototype.__noSuchMethod__ = function (id) {
    var start = 1;
    var argTypes = new (Java.type("org.objectweb.asm.Type[]"))(arguments.length - start);
    for (var i = start; i < arguments.length; i++) {
        argTypes[i - start] = arguments[i].asType();
    }
    var name = /^_c?init_$/.test(id) ? "<" + id.substring(1, id.length - 1) + ">" : id;
    return new ClassMethod(this.asType(), ASMAPI.mapMethod(name), argTypes);
};

ClassName.prototype.toString = function () {
    return this.name.join(".");
};

ClassName.prototype.asType = function () {
    return Type.getObjectType(this.name.join("/"));
};

ClassName.prototype.asField = function (field) {
    var className = this.name.concat();
    var fieldName = className.pop();
    return new ClassField(new ClassName(className).asType(), ASMAPI.mapField(fieldName));
};

ClassField.prototype.asConstant = function () {
    return this.asType();
};

function ClassField(owner, name) {
    this.owner = owner;
    this.name = name;
}

ClassField.prototype.toString = function () {
    return this.owner.toString() + " " + this.name;
};

function ClassMethod(owner, name, argumentTypes) {
    this.owner = owner;
    this.name = name;
    this.argumentTypes = argumentTypes;
}

function Opcode() {
}

Opcode.prototype.asNode = function () {
    return new InsnNode(this.opcode);
};

Opcode.prototype.asPredicate = function () {
    var opcode = this.opcode;
    return function (n) {
        return n.getOpcode() == opcode;
    };
};

function IntOpcode(operand) {
    this.operand = operand;
}

IntOpcode.prototype.asNode = function () {
    return new IntInsnNode(this.opcode, this.operand);
};

IntOpcode.prototype.asPredicate = function () {
    var superP = Opcode.prototype.asPredicate.call(this);
    var operand = this.operand;
    if (operand == undefined) {
        return superP;
    }
    return function (n) {
        return superP(n) && n.operand == operand;
    };
};

function ConstantOpcode(cst) {
    this.cst = cst ? cst.asConstant() : undefined;
}

ConstantOpcode.prototype.asNode = function () {
    return new LdcInsnNode(this.cst);
};

ConstantOpcode.prototype.asPredicate = function () {
    var superP = Opcode.prototype.asPredicate.call(this);
    var cst = this.cst;
    if (cst == undefined) {
        return superP;
    }
    return function (n) {
        return superP(n) && n.cst == cst;
    };
};

function VarOpcode(index) {
    this.index = index;
}

VarOpcode.prototype.asNode = function () {
    return new VarInsnNode(this.opcode, this.index);
};

VarOpcode.prototype.asPredicate = function () {
    var superP = Opcode.prototype.asPredicate.call(this);
    var index = this.index;
    if (index == undefined) {
        return superP;
    }
    return function (n) {
        return superP(n) && n["var"] == index;
    };
};

function IIncOpcode(index, amount) {
    this.index = index;
    this.amount = amount;
}

IIncOpcode.prototype.asNode = function () {
    return new IincInsnNode(this.index, this.amount);
};

IIncOpcode.prototype.asPredicate = function () {
    var superP = Opcode.prototype.asPredicate.call(this);
    return function (n) {
        return superP(n) && (this.index == undefined || n["var"] == this.index) && (this.incr == undefined || n.incr == this.incr);
    };
};

function JumpOpcode(label) {
    this.label = label ? label.node : undefined;
}

JumpOpcode.prototype.asNode = function () {
    return new JumpInsnNode(this.opcode, this.label);
};

JumpOpcode.prototype.asPredicate = function () {
    return Opcode.prototype.asPredicate.call(this);
};

function FieldOpcode(className, desc) {
    if (className) {
        var field = className.asField();
        this.owner = field.owner.getInternalName();
        this.name = field.name;
    } else {
        this.owner = undefined;
        this.name = undefined;
    }
    this.desc = desc ? desc.asType() : undefined;
}

FieldOpcode.prototype.asNode = function () {
    return new FieldInsnNode(this.opcode, this.owner, this.name, this.desc);
};

FieldOpcode.prototype.asPredicate = function () {
    var superP = Opcode.prototype.asPredicate.call(this);
    var owner = this.owner;
    var name = this.name;
    var desc = this.desc;
    if (owner == undefined || name == undefined) {
        return superP;
    }
    if (desc == undefined) {
        return function (n) {
            return superP(n) && n.owner == owner && n.name == name;
        };
    }
    return function (n) {
        return superP(n) && n.owner == owner && n.name == name && n.desc == desc;
    };
};

function MethodOpcode(classMethod, returnType) {
    if (classMethod) {
        this.owner = classMethod.owner.getInternalName();
        this.name = classMethod.name;
        this.desc = Type.getMethodDescriptor(returnType ? returnType.asType() : Type.VOID_TYPE, classMethod.argumentTypes);
        this.descFull = this.desc;
        if (!returnType) {
            this.desc = this.desc.substring(0, this.desc.lastIndexOf(")") + 1);
        }
    } else {
        this.owner = undefined;
        this.name = undefined;
        this.desc = undefined;
    }
}

MethodOpcode.prototype.asNode = function () {
    return new MethodInsnNode(this.opcode, this.owner, this.name, this.descFull);
};

MethodOpcode.prototype.asPredicate = function () {
    var superP = Opcode.prototype.asPredicate.call(this);
    var owner = this.owner;
    var name = this.name;
    var desc = this.desc;
    if (owner == undefined || name == undefined || desc == undefined) {
        return superP;
    }
    if (desc.endsWith(")")) {
        return function (n) {
            return superP(n) && n.owner == owner && n.name == name && n.desc.substring(0, n.desc.lastIndexOf(")") + 1) == desc;
        };
    }
    return function (n) {
        return superP(n) && n.owner == owner && n.name == name && n.desc == desc;
    };
};

function TypeOpcode(desc) {
    this.desc = desc ? desc.asType().getInternalName() : undefined;
}

TypeOpcode.prototype.asNode = function () {
    return new TypeInsnNode(this.opcode, this.desc);
};

TypeOpcode.prototype.asPredicate = function () {
    var superP = Opcode.prototype.asPredicate.call(this);
    var desc = this.desc;
    if (desc == undefined) {
        return superP;
    }
    return function (n) {
        return superP(n) && n.desc == desc;
    };
};

function NotImplementedOpcode() {
}

function LabelWrapper() {
    this.node = new LabelNode();
}

LabelWrapper.prototype.asNode = function () {
    return this.node;
};

function Builder() {
    this.functions = {};
    this.coremod = {};
}

Builder.prototype.addTransformer = function (targetClass, func) {
    var transformers = this.functions[targetClass];
    if (!transformers) {
        transformers = [];
        var transformer = {
            target: {
                type: "CLASS",
                name: targetClass
            },
            transformer: function (n) {
                transformers.forEach(function (t) {
                    t(n);
                });
                return n;
            }
        };
        this.functions[targetClass] = transformers;
        this.coremod[targetClass.substring(targetClass.lastIndexOf(".") + 1) + "Transformer"] = transformer;
    }
    transformers.push(func);
};

var builder = new Builder();

function MethodTarget(classMethod) {
    this.owner = classMethod.owner.getClassName();
    this.name = classMethod.name;
    this.desc = Type.getMethodDescriptor(Type.VOID_TYPE, classMethod.argumentTypes);
    this.desc = this.desc.substring(0, this.desc.lastIndexOf(")") + 1);
}

function getInstruction(obj) {
    return typeof obj == "function" ? obj() : obj;
}

MethodTarget.prototype.addTransformer = function (consumer) {
    var name = this.name;
    var desc = this.desc;
    if (!this.consumers) {
        this.consumers = [];
        var consumers = this.consumers;
        builder.addTransformer(this.owner, function (node) {
            for (var i = 0; i < node.methods.length; i++) {
                var method = node.methods[i];
                if (method.name == name && method.desc.substring(0, method.desc.lastIndexOf(")") + 1) == desc) {
                    consumers.forEach(function (c) {
                        c(method.instructions);
                    });
                    return;
                }
            }
            var msg = "";
            for (var i = 0; i < node.methods.length; i++) {
                var method = node.methods[i];
                if (msg) msg += ", ";
                msg += method.name + method.desc
            }
            throw "Failed to find method " + name + desc + " [ " + msg + " ]";
        });
    }
    this.consumers.push(consumer);
};

MethodTarget.prototype.atFirst = function (insn) {
    if (insn == undefined) {
        return new InstructionTarget(this, function (_, insertion) {
            return function (list) {
                insertion(list, list.getFirst());
            };
        }, undefined);
    }
    return new InstructionTarget(this, function (predicate, insertion) {
        return function (list) {
            for (var it = list.iterator(); it.hasNext(); ) {
                var node = it.next();
                if (predicate(node)) {
                    insertion(list, node);
                    return;
                }
            }
            throw "Failed to find instruction";
        };
    }, getInstruction(insn).asPredicate());
};

MethodTarget.prototype.atEach = function (insn) {
    return new InstructionTarget(this, function (predicate, insertion) {
        return function (list) {
            var count = 0;
            var node = list.getFirst();
            while (node) {
                if (predicate(node)) {
                    var next = node.getNext();
                    insertion(list, node);
                    node = next;
                    count++;
                } else {
                    node = node.getNext();
                }
            }
            if (!count) {
                throw "Failed to find instruction";
            }
        };
    }, getInstruction(insn).asPredicate());
};

MethodTarget.prototype.atLast = function (insn) {
    if (insn == undefined) {
        return new InstructionTarget(this, function (_, insertion) {
            return function (list) {
                insertion(list, list.getLast());
            };
        }, undefined);
    }
    return new InstructionTarget(this, function (predicate, insertion) {
        return function (list) {
            for (var it = list.iterator(list.size()); it.hasPrevious(); ) {
                var node = it.previous();
                if (predicate(node)) {
                    insertion(list, node);
                    return;
                }
            }
            throw "Failed to find instruction";
        };
    }, getInstruction(insn).asPredicate());
};

function InstructionTarget(target, strategy, predicate) {
    this.target = target;
    this.strategy = strategy;
    this.predicate = predicate;
}

InstructionTarget.prototype.after = function (second) {
    var first = this.predicate;
    var secondP = getInstruction(second).asPredicate();
    return new InstructionTarget(this.target, this.strategy, function (n) {
        var previous = n.getPrevious();
        return previous && first(n) && secondP(previous);
    });
};

InstructionTarget.prototype.before = function (second) {
    var first = this.predicate;
    var secondP = getInstruction(second).asPredicate();
    return new InstructionTarget(this.target, this.strategy, function (n) {
        var next = n.getNext();
        return next && first(n) && secondP(next);
    });
};

function makeInstructions(args) {
    var list = new InsnList();
    for (var i = 0; i < args.length; i++) {
        list.add(getInstruction(args[i]).asNode());
    }
    return list;
}

InstructionTarget.prototype.makeTransformer = function (insertion) {
    this.target.addTransformer(this.strategy(this.predicate, insertion));
};

InstructionTarget.prototype.prepend = function () {
    var instructions = arguments;
    this.makeTransformer(function (list, node) {
        list.insertBefore(node, makeInstructions(instructions));
    });
    return this.target;
};

InstructionTarget.prototype.replace = function () {
    var instructions = arguments;
    this.makeTransformer(function (list, node) {
        list.insert(node, makeInstructions(instructions));
        list.remove(node);
    });
    return this.target;
};

InstructionTarget.prototype.append = function () {
    var instructions = arguments;
    this.makeTransformer(function (list, node) {
        list.insert(node, makeInstructions(instructions));
    });
    return this.target;
};

scope.easycore = {
    include: function () {
        addGlobals(arguments, function (n) {
            return new ClassName(n);
        });
    },
    inMethod: function (method) {
        return new MethodTarget(method);
    },
    build: function () {
        return builder.coremod;
    }
};

function addGlobal(name, factory) {
    scope[name] = factory(name);
}

function addGlobals(elements, factory) {
    for (var i = 0; i < elements.length; i++) {
        addGlobal(elements[i], factory);
    }
}

function addPrimitiveGlobals(elements, factory) {
    for (var i = 0; i < elements.length; i++) {
        scope[elements[i][0]] = factory(elements[i]);
    }
}

addPrimitiveGlobals([ [ "boolean", Type.BOOLEAN_TYPE ], [ "byte", Type.BYTE_TYPE ], [ "char", Type.CHAR_TYPE ], [ "double", Type.DOUBLE_TYPE ], [ "float", Type.FLOAT_TYPE ], [ "int", Type.INT_TYPE ], [ "long", Type.LONG_TYPE ], [ "short", Type.SHORT_TYPE ] ], function (name) {
    var type = name[1];
    var arrayType = Java.type(name[0] + "[]");
    var factory = function (value) {
        var p = new Primitive(value);
        p.type = type;
        p.arrayType = arrayType;
        return p;
    };
    factory.asType = function () {
        return type;
    };
    return factory;
});

// Override builtin jdk.nashorn.internal.objects.Global packages and "net" for convenience
scope.easycore.include("com", "edu", "java", "javafx", "javax", "net", "org");

scope.label = function () {
    return new LabelWrapper();
};

var nextOpcode = 0;
[
    { type: Opcode, opcodes: "nop;aconst_null;iconst_m1;iconst_5;lconst_1;fconst_2;dconst_1" },
    { type: IntOpcode, opcodes: "bipush;sipush" },
    { type: ConstantOpcode, opcodes: "ldc;ldc_w;ldc2_w" },
    { type: VarOpcode, opcodes: "5load" },
    { type: Opcode, opcodes: "5load_3" },
    { type: Opcode, opcodes: "8aload" },
    { type: VarOpcode, opcodes: "5store" },
    { type: Opcode, opcodes: "5store_3" },
    { type: Opcode, opcodes: "8astore;pop;pop2;dup;dup_x1;dup_x2;dup2;dup2_x1;dup2_x2;swap;4add;4sub;4mul;4div;4rem;4neg;2shl;2shr;2ushr;2and;2or;2xor" },
    { type: IIncOpcode, opcodes: "iinc" },
    { type: Opcode, opcodes: "i2l;i2f;i2d;l2i;l2f;l2d;f2i;f2l;f2d;d2i;d2l;d2f;i2b;i2c;i2s;lcmp;fcmpl;fcmpg;dcmpl;dcmpg" },
    { type: JumpOpcode, opcodes: "ifeq;ifne;iflt;ifge;ifgt;ifle;if_icmpeq;if_icmpne;if_icmplt;if_icmpge;if_icmpgt;if_icmple;if_acmpeq;if_acmpne;goto;jsr" },
    { type: VarOpcode, opcodes: "ret" },
    { type: NotImplementedOpcode, opcodes: "tableswitch" },
    { type: NotImplementedOpcode, opcodes: "lookupswitch" },
    { type: Opcode, opcodes: "5return;_return" },
    { type: FieldOpcode, opcodes: "getstatic;putstatic;getfield;putfield" },
    { type: MethodOpcode, opcodes: "invokevirtual;invokespecial;invokestatic;invokeinterface" },
    { type: NotImplementedOpcode, opcodes: "invokedynamic" },
    { type: TypeOpcode, opcodes: "_new" },
    { type: IntOpcode, opcodes: "newarray" },
    { type: TypeOpcode, opcodes: "anewarray" },
    { type: Opcode, opcodes: "arraylength;athrow" },
    { type: TypeOpcode, opcodes: "checkcast;instanceof" },
    { type: Opcode, opcodes: "monitorenter;monitorexit" },
    { type: NotImplementedOpcode, opcodes: "wide;multianewarray" },
    { type: JumpOpcode, opcodes: "ifnull;ifnonnull" }
].forEach(function (block) {
    function createOpcode(n) {
        var type = block.type;
        var opcode = nextOpcode++;
        return function () {
            var args = Array.prototype.slice.call(arguments);
            args.unshift(undefined);
            var insn = new (Function.prototype.bind.apply(type, args));
            insn.opcode = opcode;
            return insn;
        };
    }
    block.opcodes.split(";").forEach(function (name) {
        function explodeNumber(name) {
            if (/_\d$/.test(name)) {
                for (var n = 0, end = name.length - 1; n <= name[end]; n++) {
                    addGlobal(name.substring(0, end) + n, createOpcode);
                }
            } else {
                addGlobal(name, createOpcode);
            }
        }
        if (isNaN(name[0])) {
            explodeNumber(name);
        } else {
            "ilfdabcs".split("", name[0]).forEach(function (t) {
                explodeNumber(t + name.substring(1));
            });
        }
    });
});

})(this);
