package core.parser;

import annotation.Setter;
import com.sun.tools.javac.code.Flags;
import com.sun.tools.javac.code.TypeTag;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.ListBuffer;
import com.sun.tools.javac.util.Name;
import com.sun.tools.javac.util.Names;
import core.TreeVisitorBoot;

import java.util.Iterator;
import java.util.Objects;

import static com.sun.tools.javac.tree.JCTree.*;

public class SetterParser extends AnnotationHandler<Setter> {

    public final String SET = "set";

    @Override
    public void handle(TreeVisitorBoot boot) {
        Setter exists = isAnnotationOnClassType(boot.classDecl);

        if (Objects.nonNull(exists)) {
            Iterator<JCVariableDecl> it = boot.variableDecls.iterator();
            while (it.hasNext()) {
                JCVariableDecl decl = it.next();
                createSetMethodAndSave(boot,decl);
            }
        }else{
            boot
                    .variableDecls
                    .stream()
                    .filter(f->Objects.nonNull(isAnnotationPresent(f.sym)))
                    .forEach(v->createSetMethodAndSave(boot,v));
            ;
        }

    }

    @Override
    Class<Setter> handleAnnotation() {
        return Setter.class;
    }

    protected void createSetMethodAndSave(TreeVisitorBoot boot, JCVariableDecl decl){
        JCMethodDecl method = createSetMethod(boot, decl);
        boot.addMethod(method);
    }

    protected JCMethodDecl createSetMethod(TreeVisitorBoot boot, JCVariableDecl decl) {
        TreeMaker maker = boot.maker;
        Names names = boot.names;
        ListBuffer<JCStatement> stats = new ListBuffer<>();

        Name name = decl.name;
        String methodName = addPrefix(SET,name.toString());
        JCVariableDecl param = createMethodParam(boot, decl);

        JCAssign assign = maker.Assign(
                maker.Select(maker.Ident(names.fromString("this")), name),
                maker.Ident(name)
        );

        stats.append(maker.Exec(assign));

        return maker.MethodDef(
                maker.Modifiers(Flags.PUBLIC),
                names.fromString(methodName),
                maker.TypeIdent(TypeTag.VOID),
                List.nil(),
                List.of(param),
                List.nil(),
                maker.Block(0,stats.toList()),
                null
                );
    }

    protected JCVariableDecl createMethodParam(TreeVisitorBoot boot, JCVariableDecl decl) {
        TreeMaker maker = boot.maker;
        Names names = boot.names;

        Name name = decl.getName();
        JCExpression vartype = decl.vartype;

        return maker.VarDef(
                maker.Modifiers(Flags.PARAMETER),
                name,
                vartype,
                null
        );
    }

}
