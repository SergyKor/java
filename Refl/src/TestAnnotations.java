import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(value = RetentionPolicy.RUNTIME)
public @interface TestAnnotations {
    int param1();
    int param2();
}
