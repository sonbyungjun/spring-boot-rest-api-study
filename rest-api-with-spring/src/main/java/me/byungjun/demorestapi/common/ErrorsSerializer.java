package me.byungjun.demorestapi.common;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;
import org.springframework.boot.jackson.JsonComponent;
import org.springframework.validation.Errors;

@JsonComponent
public class ErrorsSerializer extends JsonSerializer<Errors> {


  @Override
  public void serialize(Errors errors, JsonGenerator jsonGenerator,
      SerializerProvider serializerProvider) throws IOException {

    jsonGenerator.writeStartArray();
    errors.getFieldErrors().stream().forEach(e -> {
      try {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeStringField("field", e.getField());
        jsonGenerator.writeStringField("objectName", e.getObjectName());
        jsonGenerator.writeStringField("code", e.getCode());
        jsonGenerator.writeStringField("defaultMessage", e.getDefaultMessage());
        Object rejectedValue = e.getRejectedValue();
        if (rejectedValue != null) {
          jsonGenerator.writeStringField("rejectedValue", rejectedValue.toString());
        }
        jsonGenerator.writeEndObject();
      } catch (IOException ex) {
        ex.printStackTrace();
      }
    });

    errors.getGlobalErrors().stream().forEach(e -> {
      try {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeStringField("objectName", e.getObjectName());
        jsonGenerator.writeStringField("code", e.getCode());
        jsonGenerator.writeStringField("DefaultMessage", e.getDefaultMessage());
        jsonGenerator.writeEndObject();
      } catch (IOException ex) {
        ex.printStackTrace();
      }
    });
    jsonGenerator.writeEndArray();

  }
}
