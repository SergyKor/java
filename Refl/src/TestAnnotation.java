import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class TestAnnotation {


    public static void invokeTestAnnotation (Class clazz) throws InvocationTargetException, IllegalAccessException {
        Method [] methods = clazz.getDeclaredMethods();
        for (Method method : methods){
            if(method.isAnnotationPresent(TestAnnotations.class)){
                Annotation annotation = method.getAnnotation(TestAnnotations.class);
                method.invoke(null, ((TestAnnotations) annotation).param1(), ((TestAnnotations) annotation).param2());
            }
        }
    }
}
