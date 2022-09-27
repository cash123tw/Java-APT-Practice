package core;

import com.sun.tools.javac.api.JavacTrees;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.Names;
import core.parser.AnnotationHandler;
import core.parser.DataParser;
import core.parser.GetterParser;
import core.parser.SetterParser;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@SupportedAnnotationTypes({"annotation.CreateMethod"})
@SupportedSourceVersion(SourceVersion.RELEASE_11)
public class ProjectProcessor extends AbstractProcessor {

    private Context context;
    private JavacTrees trees;
    private TreeMaker treeMaker;
    private Names names;
    private Messager messager;
    private java.util.List<AnnotationHandler> annotationHandlers;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        JavacProcessingEnvironment env = (JavacProcessingEnvironment) processingEnv;
        this.context = env.getContext();
        this.messager = env.getMessager();
        this.trees = JavacTrees.instance(context);
        this.treeMaker = TreeMaker.instance(context);
        this.names = Names.instance(context);
        this.annotationHandlers = new ArrayList<>();
        registerAnnotationHandler();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment re) {
        try {
            messager.printMessage(Diagnostic.Kind.NOTE, "Inside Processor!!!");
            Set<? extends Element> relativeClass
                    = re.getElementsAnnotatedWithAny(annotations.toArray(new TypeElement[0]));
            resolveClass(relativeClass.stream().collect(Collectors.toList()));
        } catch (Exception e) {
            messager.printMessage(Diagnostic.Kind.ERROR, e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    protected void registerAnnotationHandler() {
        GetterParser getter = new GetterParser();
        SetterParser setter = new SetterParser();
        DataParser dataParser = new DataParser(getter, setter);

        addAnnotationHandler(getter,setter,dataParser);
    }

    protected void addAnnotationHandler(AnnotationHandler ...handlers) {
        for (AnnotationHandler handler : handlers) {
            this.annotationHandlers.add(handler);
        }
    }

    public void resolveClass(Collection<Element> elements) {
        for (Element element : elements) {
            JCTree tree = trees.getTree(element);
            TreeVisitorBoot boot = new TreeVisitorBoot(treeMaker, names, messager);
            tree.accept(boot);
            handle(boot);
        }
    }

    private void handle(TreeVisitorBoot boot) {
        for (AnnotationHandler handler : this.annotationHandlers) {
            handler.handle(boot);
        }
    }
}
