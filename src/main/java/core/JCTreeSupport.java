package core;

import com.sun.tools.javac.api.JavacTrees;
import com.sun.tools.javac.code.Flags;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.Names;

public class JCTreeSupport {

    private TreeMaker treeMaker;
    private JavacTrees trees;
    private Names names;

    public JCTreeSupport(TreeMaker treeMaker, JavacTrees trees, Names names) {
        this.treeMaker = treeMaker;
        this.trees = trees;
        this.names = names;
    }

    public static String getLastWordInText(String text){
        String[] strs = text.split("\\.");
        return strs[strs.length-1];
    }
}
