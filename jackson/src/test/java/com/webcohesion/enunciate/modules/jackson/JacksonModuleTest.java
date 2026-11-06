package com.webcohesion.enunciate.modules.jackson;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class JacksonModuleTest {

  private final JacksonModule module = new JacksonModule();

  @Test
  public void serializeAnnotationsOfBothVersionsDetectTypes() {
    assertTrue(module.isJacksonSerializationAnnotation("com.fasterxml.jackson.databind.annotation.JsonSerialize"));
    assertTrue(module.isJacksonSerializationAnnotation("tools.jackson.databind.annotation.JsonSerialize"));
    assertTrue(module.isJacksonSerializationAnnotation("com.fasterxml.jackson.annotation.JsonProperty"));
  }

  @Test
  public void otherDatabindAnnotationsDoNotDetectTypes() {
    assertFalse(module.isJacksonSerializationAnnotation("com.fasterxml.jackson.databind.annotation.JsonDeserialize"));
    assertFalse(module.isJacksonSerializationAnnotation("tools.jackson.databind.annotation.JsonDeserialize"));
    assertFalse(module.isJacksonSerializationAnnotation("com.fasterxml.jackson.annotation.JacksonAnnotation"));
  }
}
