package io.github.thebesteric.framework.versioner.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Field;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
public class FieldDefinition {
    public enum FieldType {
        BASIC, POJO, COLLECTION, MAP
    }
    private String[] versions;
    private Field field;
    private FieldType fieldType;
    private Set<FieldDefinition> subFieldDefinitions;
    private boolean showBeRemove;
}
