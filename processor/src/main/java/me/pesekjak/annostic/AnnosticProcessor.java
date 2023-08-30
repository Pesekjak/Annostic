package me.pesekjak.annostic;

import com.google.auto.service.AutoService;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.type.MirroredTypeException;
import javax.tools.Diagnostic;
import javax.tools.FileObject;
import javax.tools.StandardLocation;
import java.io.BufferedWriter;
import java.io.Writer;
import java.util.*;

@AutoService(Processor.class)
@SupportedSourceVersion(SourceVersion.RELEASE_17)
public class AnnosticProcessor extends AbstractProcessor {

    private FileObject fileObject;
    private final JsonObject entries = new JsonObject();

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (TypeElement annotation : annotations)
            roundEnv.getElementsAnnotatedWith(annotation).forEach(element -> {
                if (!(element instanceof TypeElement typeElement)) {
                    printIllegalElementWarning(element);
                    return;
                }
                handleElement(typeElement);
            });

        if (fileObject == null) {
            try {
                fileObject = processingEnv.getFiler().createResource(
                        StandardLocation.CLASS_OUTPUT,
                        "",
                        "annostic.json"
                );

                try (Writer writer = new BufferedWriter(fileObject.openWriter())) {
                    new Gson().toJson(entries, writer);
                }

            } catch (Exception exception) {
                printException(exception);
                return true;
            }
        }

        return true;
    }

    void handleElement(TypeElement element) {
        if (element.getKind() != ElementKind.INTERFACE) {
            printIllegalElementWarning(element);
            return;
        }

        String source = getInternalName(element);
        String target;
        try {
            target = element.getAnnotation(StaticProvider.class).target().getName().replace('.', '/');
        } catch (MirroredTypeException mirroredTypeException) {
            Element targetElement = processingEnv.getTypeUtils().asElement(mirroredTypeException.getTypeMirror());
            target = getInternalName((TypeElement) targetElement);
        }

        entries.addProperty(target, source);
    }

    String getInternalName(TypeElement element) {
        int innerCount = 0;
        Element outer = element;
        while (true) {
            outer = outer.getEnclosingElement();
            if (!(outer instanceof TypeElement)) break;
            innerCount++;
        }
        StringBuilder name = new StringBuilder(element.getQualifiedName().toString());
        for (int i = 0; i < innerCount; i++)
            name.setCharAt(name.lastIndexOf("."), '$');
        return name.toString().replace('.', '/');
    }

    void printIllegalElementWarning(Element element) {
        Name name = element instanceof TypeElement typeElement ? typeElement.getQualifiedName() : element.getSimpleName();
        processingEnv.getMessager().printMessage(
                Diagnostic.Kind.MANDATORY_WARNING,
                "'" + name + "' is not an interface, only interfaces can be annotated with @StaticProvider"
        );
    }

    void printException(Throwable exception) {
        String stacktrace = String.join("\n", Arrays.stream(exception.getStackTrace()).map(Object::toString).toArray(String[]::new));
        processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR,
                "AnnosticProcessor generated " + exception + "\n" + stacktrace
        );
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Collections.singleton(StaticProvider.class.getName());
    }

}
