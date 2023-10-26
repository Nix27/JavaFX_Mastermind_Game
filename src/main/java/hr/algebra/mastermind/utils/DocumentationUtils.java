package hr.algebra.mastermind.utils;

import java.io.IOException;
import java.lang.reflect.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class DocumentationUtils {
    private DocumentationUtils(){}

    private static final Path targetPath = Path.of("target");

    private final static StringBuilder htmlContent = new StringBuilder("""
                    <!DOCTYPE html>
                    <html>
                    <head>
                    <title>HTML Tutorial</title>
                    </head>
                    <body>""");

    public static void createHtmlDocumentation(){
        try (Stream<Path> paths = Files.walk(targetPath)) {
            List<String> classFiles = paths
                    .map(Path::toString)
                    .filter(file -> file.endsWith(".class"))
                    .toList();

            for(var classFile : classFiles){
                String fullyQualifiedName = getFullyQualifiedName(classFile);

                htmlContent.append("<h1>").append(fullyQualifiedName).append("</h1>");

                Class<?> deserializedClass = Class.forName(fullyQualifiedName);

                addFieldsToHtmlContent(deserializedClass);
                addConstructorsToHtmlContent(deserializedClass);
                addMethodsToHtmlContent(deserializedClass);
            }

            htmlContent.append("""    
                    </body>
                    </html>
                    """);

            Path documentationPath = Path.of("files/documentation.html");

            Files.write(documentationPath, htmlContent.toString().getBytes());
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private static String getFullyQualifiedName(String classFile){
        String[] classFileTokens = classFile.split("classes");
        String classFilePath = classFileTokens[1];
        String reducedClassPath = classFilePath.substring(1, classFilePath.lastIndexOf('.'));
        return reducedClassPath.replace('\\', '.');
    }

    private static void addFieldsToHtmlContent(Class<?> deserializedClass){
        htmlContent.append("<h2>Fields</h2>");
        Field[] classFields = deserializedClass.getDeclaredFields();

        for (var classField : classFields){
            htmlContent.append("<h3>");

            int modifiers = classField.getModifiers();

            if(Modifier.isPublic(modifiers)){
                htmlContent.append("public ");
            }else if(Modifier.isPrivate(modifiers)){
                htmlContent.append("private ");
            }else if(Modifier.isProtected(modifiers)){
                htmlContent.append("protected ");
            }

            if(Modifier.isStatic(modifiers)){
                htmlContent.append("static ");
            }

            if(Modifier.isFinal(modifiers)){
                htmlContent.append("final ");
            }

            htmlContent.append(classField.getType().getTypeName()).append(' ');
            htmlContent.append(classField.getName());

            htmlContent.append("</h3>");
        }
    }

    private static void addConstructorsToHtmlContent(Class<?> deserializedClass){
        htmlContent.append("<h2>Constructor</h2>");
        Constructor<?>[] classConstructors = deserializedClass.getDeclaredConstructors();

        for(var classConstructor : classConstructors){
            htmlContent.append("<h3>");

            int modifiers = classConstructor.getModifiers();

            if(Modifier.isPublic(modifiers)){
                htmlContent.append("public ");
            }else if(Modifier.isPrivate(modifiers)){
                htmlContent.append("private ");
            }else if(Modifier.isProtected(modifiers)){
                htmlContent.append("protected ");
            }

            htmlContent.append(classConstructor.getName());

            addParametersToHtmlContent(classConstructor);

            htmlContent.append("</h3>");
        }
    }

    private static void addMethodsToHtmlContent(Class<?> deserializedClass){
        htmlContent.append("<h2>Methods</h2>");
        Method[] classMethods = deserializedClass.getDeclaredMethods();

        for(var classMethod : classMethods){
            htmlContent.append("<h3>");

            int modifiers = classMethod.getModifiers();

            if(Modifier.isPublic(modifiers)){
                htmlContent.append("public ");
            }else if(Modifier.isPrivate(modifiers)){
                htmlContent.append("private ");
            }else if(Modifier.isProtected(modifiers)){
                htmlContent.append("protected ");
            }

            if(Modifier.isStatic(modifiers)){
                htmlContent.append("static ");
            }

            if(Modifier.isFinal(modifiers)){
                htmlContent.append("final ");
            }

            htmlContent.append(classMethod.getName());

            addParametersToHtmlContent(classMethod);

            htmlContent.append(" ");

            addExceptionsToHtmlContent(classMethod);

            htmlContent.append("</h3>");
        }
    }

    private static void addExceptionsToHtmlContent(Executable executable) {
        if(executable.getExceptionTypes().length > 0){
            htmlContent.append(Stream.of(executable.getExceptionTypes())
                    .map(Class::getSimpleName)
                    .collect(Collectors.joining(", ", "throws ", "")));
        }
    }

    private static void addParametersToHtmlContent(Executable executable){
        htmlContent.append("(");

        Parameter[] parameters = executable.getParameters();

        for(int i = 0; i < parameters.length; i++){
            if(i < parameters.length - 1){
                htmlContent.append(parameters[i].getType().getTypeName())
                        .append(" ")
                        .append(parameters[i].getName())
                        .append(", ");
            }else {
                htmlContent.append(parameters[i].getType().getTypeName())
                        .append(" ")
                        .append(parameters[i].getName());
            }
        }

        htmlContent.append(")");
    }
}
