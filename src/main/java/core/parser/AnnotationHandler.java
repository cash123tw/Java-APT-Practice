package core.parser;

import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.tree.JCTree;
import core.JCMethodBoot;
import core.TreeVisitorBoot;
import jdk.jshell.execution.JdiExecutionControl;

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public abstract class AnnotationHandler<T extends Annotation> {
    public abstract void handle(TreeVisitorBoot boot);

    abstract Class<T> handleAnnotation();

    public Set<JCTree.JCMethodDecl> getAnnotationOnMethod(TreeVisitorBoot boot){
        HashSet<JCTree.JCMethodDecl> methods = new HashSet<>();

        for (JCTree.JCMethodDecl decl : boot.methodDecls) {
            T exists = isAnnotationPresent(decl.sym);
            if(Objects.nonNull(exists)){
                methods.add(decl);
            }
        }
        return methods;
    }

    public Set<JCTree.JCVariableDecl> getAnnotationOnVariable(TreeVisitorBoot boot){
        HashSet<JCTree.JCVariableDecl> vars = new HashSet<>();

        for (JCTree.JCVariableDecl decl : boot.variableDecls) {
            T exists = isAnnotationPresent(decl.sym);
            if(Objects.nonNull(exists)){
                vars.add(decl);
            }
        }
        return vars;
    }

    public boolean containsMethod(JCTree.JCMethodDecl methodDecl){
        return false;
    }

    public boolean containsVar(JCTree.JCVariableDecl variableDecl){
        return false;
    }

    public T isAnnotationOnClassType(JCTree.JCClassDecl decl){
        Symbol.ClassSymbol sym = decl.sym;
        return isAnnotationPresent(sym);
    }

    public T isAnnotationPresent(Symbol symbol){
        return symbol.getAnnotation(handleAnnotation());
    }

    public static String addPrefix(String prefix, String target) {
        StringBuffer sb = new StringBuffer();
        sb.append(prefix);
        if (target.length() > 0) {
            sb.append(target.substring(0, 1).toUpperCase());
            sb.append(target.substring(1));
        }
        return sb.toString();
    }

}
