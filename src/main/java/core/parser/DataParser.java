package core.parser;

import annotation.Data;
import annotation.Setter;
import com.sun.tools.javac.tree.JCTree;
import core.TreeVisitorBoot;

import java.util.Objects;

public class DataParser extends AnnotationHandler<Data>{

    private GetterParser getterParser;
    private SetterParser setterParser;

    public DataParser(GetterParser getterParser, SetterParser setterParser) {
        this.getterParser = getterParser;
        this.setterParser = setterParser;
    }

    @Override
    public void handle(TreeVisitorBoot boot) {
        Data exits = isAnnotationOnClassType(boot.classDecl);

        if(Objects.nonNull(exits)){
            boot.variableDecls.forEach(
                    var->{
                        getterParser.createGetterMethodAndSave(boot,var);
                        setterParser.createSetMethodAndSave(boot,var);
                    }
            );
        }else{
            for (JCTree.JCVariableDecl var : boot.variableDecls) {
                exits = isAnnotationPresent(var.sym);

                if(Objects.nonNull(exits)){
                    getterParser.createGetterMethodAndSave(boot,var);
                    setterParser.createSetMethodAndSave(boot,var);
                }
            }
        }

    }

    @Override
    Class<Data> handleAnnotation() {
        return Data.class;
    }

}
