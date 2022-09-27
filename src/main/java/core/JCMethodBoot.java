package core;

import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.Name;

import java.util.Set;
import java.util.stream.Collectors;

public class JCMethodBoot extends JCTree.JCMethodDecl {

    public static JCMethodBoot getInstance(JCMethodDecl decl){
        return new JCMethodBoot(
                decl.getModifiers(),
                decl.getName(),
                decl.restype,
                decl.typarams,
                decl.recvparam,
                decl.params,
                decl.thrown,
                decl.body,
                decl.defaultValue,
                decl.sym
        );
    }

    public JCMethodBoot(JCModifiers mods,
                        Name name,
                        JCExpression restype,
                        List<JCTypeParameter> typarams,
                        JCVariableDecl recvparam,
                        List<JCVariableDecl> params,
                        List<JCExpression> thrown,
                        JCBlock body,
                        JCExpression defaultValue,
                        Symbol.MethodSymbol sym) {
        super(mods, name, restype, typarams, recvparam, params, thrown, body, defaultValue, sym);
    }




    @Override
    public boolean equals(Object obj) {
        if(obj instanceof JCMethodDecl){
            JCMethodDecl target = (JCMethodDecl) obj;

            Set<String> targetParams = target
                    .params
                    .stream()
                    .map(v->v.vartype.type.tsym.name)
                    .map(Name::toString)
                    .collect(Collectors.toSet());


            for (JCVariableDecl param : this.params) {
                if (!targetParams.contains(param.vartype.type.tsym.name.toString())) {
                    return false;
                }
            }

            return
                    this.restype.type.tsym.name.toString().equals(target.restype.type.tsym.name.toString())
                    && this.name.toString().equals(target.name.toString());
        }

        return false;
    }
}
