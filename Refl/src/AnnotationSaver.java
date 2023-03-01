import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class AnnotationSaver {
    public static void annotationSaver (Class clazz) throws InvocationTargetException, IllegalAccessException, InstantiationException {
        Method [] methods = clazz.getDeclaredMethods();
        for (Method method : methods){
            if(method.isAnnotationPresent(Saver.class)){
                Object obj = clazz.newInstance();
                method.invoke(obj);

            }
        }
    }


}
