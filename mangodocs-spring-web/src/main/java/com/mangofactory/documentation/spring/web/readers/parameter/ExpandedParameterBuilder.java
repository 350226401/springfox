package com.mangofactory.documentation.spring.web.readers.parameter;

import com.fasterxml.classmate.TypeResolver;
import com.google.common.base.Function;
import com.mangofactory.documentation.schema.ModelRef;
import com.mangofactory.documentation.spi.DocumentationType;
import com.mangofactory.documentation.service.AllowableListValues;
import com.mangofactory.documentation.service.AllowableValues;
import com.mangofactory.documentation.spi.service.ExpandedParameterBuilderPlugin;
import com.mangofactory.documentation.spi.service.contexts.ParameterExpansionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

import static com.google.common.base.Strings.*;
import static com.google.common.collect.Lists.*;

@Component
public class ExpandedParameterBuilder implements ExpandedParameterBuilderPlugin {
  private final TypeResolver resolver;

  @Autowired
  public ExpandedParameterBuilder(TypeResolver resolver) {
    this.resolver = resolver;
  }

  @Override
  public void apply(ParameterExpansionContext context) {
    AllowableValues allowable = allowableValues(context.getField());

    String name = isNullOrEmpty(context.getParentName())
            ? context.getField().getName()
            : String.format("%s.%s", context.getParentName(), context.getField().getName());

    context.getParameterBuilder()
            .name(name)
            .description(null).defaultValue(null)
            .required(Boolean.FALSE)
            .allowMultiple(Boolean.FALSE)
            .type(resolver.resolve(context.getField().getType()))
            .dataType(context.getDataTypeName())
            .modelRef(new ModelRef(context.getDataTypeName()))
            .allowableValues(allowable)
            .parameterType("query")
            .parameterAccess(null);
  }

  @Override
  public boolean supports(DocumentationType delimiter) {
    return true;
  }

  private AllowableValues allowableValues(final Field field) {

    AllowableValues allowable = null;
    if (field.getType().isEnum()) {
      allowable = new AllowableListValues(getEnumValues(field.getType()), "LIST");
    }

    return allowable;
  }

  private List<String> getEnumValues(final Class<?> subject) {
    return transform(Arrays.asList(subject.getEnumConstants()), new Function<Object, String>() {
      @Override
      public String apply(final Object input) {
        return input.toString();
      }
    });
  }
}