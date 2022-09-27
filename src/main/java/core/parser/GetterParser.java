package core.parser;

import annotation.Getter;
import com.sun.tools.javac.code.Flags;
import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.Name;
import com.sun.tools.javac.util.Names;
import core.TreeVisitorBoot;

import java.util.Iterator;
import java.util.Objects;

import static com.sun.tools.javac.tree.JCTree.*;

public class GetterParser extends AnnotationHandler<Getter> {

    protected String GET = "get";

    @Override
    public void handle(TreeVisitorBoot boot) {
        Getter exists = isAnnotationOnClassType(boot.classDecl);

        if(Objects.nonNull(exists)){
            for (JCVariableDecl var : boot.variableDecls) {
                createGetterMethodAndSave(boot,var);
            }
        }else{
            for (JCVariableDecl var : boot.variableDecls) {
                Getter exists2 = isAnnotationPresent(var.sym);
                if(Objects.nonNull(exists2)){
                    createGetterMethodAndSave(boot,var);
                }
            }
        }

    }

    @Override
    Class<Getter> handleAnnotation() {
        return Getter.class;
    }

    public void createGetterMethodAndSave(TreeVisitorBoot boot,JCVariableDecl decl){
        JCMethodDecl method = createGetterMethod(boot, decl);
        boot.addMethod(method);
    }

    public JCMethodDecl createGetterMethod(TreeVisitorBoot boot, JCVariableDecl decl) {
        TreeMaker maker = boot.maker;
        Names names = boot.names;

        Name name = decl.getName();
        String methodName = addPrefix(GET, name.toString());

        JCReturn retValue = maker.Return(
                maker.Select(maker.Ident(names.fromString("this")), name)
        );

        return maker.MethodDef(
                maker.Modifiers(Flags.PUBLIC),
                names.fromString(methodName),
                decl.vartype,
                List.nil(),
                List.nil(),
                List.nil(),
                maker.Block(0, List.of(retValue)),
                null
        );
    }
}
