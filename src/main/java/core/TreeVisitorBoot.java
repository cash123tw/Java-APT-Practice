package core;

import com.sun.source.tree.Tree;
import com.sun.tools.javac.code.Flags;
import com.sun.tools.javac.code.Kinds;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.JCTree.JCClassDecl;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.tree.TreeTranslator;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.Name;
import com.sun.tools.javac.util.Names;

import javax.annotation.processing.Messager;
import javax.lang.model.element.ElementKind;
import javax.tools.Diagnostic;
import java.util.*;
import java.util.stream.Collectors;

import static com.sun.tools.javac.tree.JCTree.JCMethodDecl;
import static com.sun.tools.javac.tree.JCTree.JCVariableDecl;

public class TreeVisitorBoot extends TreeTranslator {


    public final int PackageLevel = 0;
    public final int ClassLevel = 1;
    public final int ParamLevel = 2;
    public final int MethodLevel = 2;
    public final String $ ="\t";

    public JCClassDecl classDecl;
    public Set<JCMethodDecl> methodDecls;
    public Set<JCVariableDecl> variableDecls;
    public final TreeMaker maker;
    public final Names names;
    public final Messager messager;


    public TreeVisitorBoot(TreeMaker maker, Names names, Messager messager) {
        this.methodDecls = new HashSet<>();
        this.variableDecls = new HashSet<>();
        this.maker = maker;
        this.names = names;
        this.messager = messager;
    }

    @Override
    public void visitPackageDef(JCTree.JCPackageDecl tree) {
        messager.printMessage(Diagnostic.Kind.NOTE, addPrefix(0, tree.getKind().name(), tree.getPackageName().toString()));
        super.visitPackageDef(tree);
    }

    @Override
    public void visitClassDef(JCClassDecl tree) {
        this.classDecl = tree;
        messager.printMessage(Diagnostic.Kind.NOTE, addPrefix(1, tree.getKind().name(), tree.getSimpleName().toString()));
        super.visitClassDef(tree);
    }

    @Override
    public void visitMethodDef(JCMethodDecl tree) {
        this.methodDecls.add(tree);

        String name = "";
        String returnType = "";

        Optional<JCTree> rt
                = Optional.ofNullable(tree.getReturnType());
        Optional<Name> n
                = Optional.ofNullable(tree.getName());

        if (n.isPresent()) {
            name = n.get().toString();
        }
        if (rt.isPresent()) {
            returnType = rt.get().toString();
        }

        messager.printMessage(Diagnostic.Kind.NOTE,
                addPrefix(2, tree.getKind().name(), returnType + ":" + name));
        super.visitMethodDef(tree);
    }

    @Override
    public void visitVarDef(JCVariableDecl tree) {
        super.visitVarDef(tree);
        if (!(tree.getModifiers().flags == Flags.PARAMETER)) {
            this.variableDecls.add(tree);
            messager.printMessage(Diagnostic.Kind.NOTE, addPrefix(2,
                    tree.getKind().name(),
                    tree.vartype.type.tsym.name + " " + tree.getName().toString()));
        }
    }

    public void addMethod(JCMethodDecl decl) {
        this.classDecl.defs = this.classDecl.defs.append(decl);
        printMethod(decl,"Add New");
    }

    public boolean containsMethod(JCMethodDecl decl) {
        return this.methodDecls.contains(decl);
    }

    public boolean containsMethod(String methodName, Class resType, Class... params) {
        java.util.List<JCVariableDecl> list = Arrays.stream(params).map(Class::getSimpleName)
                .map(name ->
                        maker.VarDef(maker.Modifiers(0), names.fromString("test"), maker.Ident(names.fromString(name)), null)
                ).collect(Collectors.toList());

        JCMethodDecl decl = maker.MethodDef(maker.Modifiers(0), names.fromString(methodName), maker.Ident(names.fromString(resType.getSimpleName())),
                List.nil(), List.from(list), List.nil(), null, null);

        return containsMethod(decl);
    }

    public void printMethod(JCMethodDecl decl,String prefix){
        String kind = getKindName(decl.getKind());
        String reyTypeName = getReyTypeName(decl.restype.type);
        String methodName = decl.getName().toString();
        String param = decl.getParameters().stream()
                .map(TreeVisitorBoot::getParamString)
                .collect(Collectors.joining(","));

        messager.printMessage(Diagnostic.Kind.NOTE,String.format(
                "%s%s\t%10s %10s %10s(%s)",prefix,$.repeat(MethodLevel),kind,reyTypeName,methodName,param
        ));
    }

    public static String getParamString(JCVariableDecl variableDecl){
        if(Objects.nonNull(variableDecl)){
            Type type = variableDecl.vartype.type;
            String name = variableDecl.getName().toString();
            return type.tsym.name.toString()+" "+name;
        }else{
            return "";
        }
    }

    public static String getReyTypeName(Type type){
        if(Objects.nonNull(type)){
            return type.toString();
        }else{
            return "";
        }
    }

    public static String getKindName(Tree.Kind  kind){
        if(Objects.nonNull(kind)){
            return kind.name();
        }else{
            return "";
        }
    }

    public static String addPrefix(int level, String prefix, String message) {
        return String.format("%s%10s\t:\t%20s", "\t".repeat(level), prefix, message);
    }
}
