package com.webcohesion.enunciate.modules.jackson.model;

import com.google.testing.compile.Compilation;
import com.google.testing.compile.Compiler;
import com.google.testing.compile.JavaFileObjects;
import com.webcohesion.enunciate.javac.decorations.DecoratedProcessingEnvironment;
import com.webcohesion.enunciate.javac.decorations.element.DecoratedTypeElement;
import com.webcohesion.enunciate.javac.decorations.element.PropertyElement;
import org.junit.Test;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Consumer;

import static org.junit.Assert.assertEquals;

public class Jackson3AnnotationsTest {

  private static final String VALUE_TEMPLATE = String.join("\n",
    "package test;",
    "import com.fasterxml.jackson.annotation.JsonProperty;",
    "@%s(as = Value.Impl.class)",
    "public interface Value {",
    "  String id();",
    "  String displayName();",
    "  final class Impl implements Value {",
    "    @JsonProperty(\"id\") public String id() { return null; }",
    "    @JsonProperty(\"display_name\") public String displayName() { return null; }",
    "  }",
    "}");

  @Test
  public void jackson2SerializeAsRefinesAccessors() {
    assertProperties(String.format(VALUE_TEMPLATE, "com.fasterxml.jackson.databind.annotation.JsonSerialize"),
      names -> assertEquals(new TreeSet<>(Arrays.asList("id", "display_name")), names));
  }

  @Test
  public void jackson3SerializeAsRefinesAccessors() {
    assertProperties(String.format(VALUE_TEMPLATE, "tools.jackson.databind.annotation.JsonSerialize"),
      names -> assertEquals(new TreeSet<>(Arrays.asList("id", "display_name")), names));
  }

  @Test
  public void withoutSerializeAsFluentAccessorsAreNotProperties() {
    assertProperties(String.format(VALUE_TEMPLATE, "Deprecated").replace("(as = Value.Impl.class)", ""),
      names -> assertEquals(Collections.emptySet(), names));
  }

  private static void assertProperties(String source, Consumer<Set<String>> assertion) {
    class PropertiesProcessor extends AbstractProcessor {
      private boolean tested = false;

      @Override
      public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (!tested) {
          tested = true;
          DecoratedProcessingEnvironment env = new DecoratedProcessingEnvironment(processingEnv, Collections.emptyList(), Collections.emptyList(), Collections.emptyList());
          DecoratedTypeElement type = new DecoratedTypeElement(env.getElementUtils().getTypeElement("test.Value"), env);
          List<PropertyElement> properties = type.getProperties(new TypeDefinition.JacksonPropertySpec(env));
          Set<String> names = new TreeSet<>();
          for (PropertyElement property : properties) {
            names.add(property.getPropertyName());
          }
          assertion.accept(names);
        }
        return false;
      }

      @Override
      public Set<String> getSupportedAnnotationTypes() {
        return Collections.singleton("*");
      }

      @Override
      public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
      }
    }

    Compilation compilation = Compiler.javac()
      .withProcessors(new PropertiesProcessor())
      .compile(JavaFileObjects.forSourceString("test.Value", source));
    assertEquals(compilation.diagnostics().toString(), Compilation.Status.SUCCESS, compilation.status());
  }
}
