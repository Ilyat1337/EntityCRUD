package sample.model.Entities;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)

public @interface EntityAnnotation {
    String name();
}
