import java.lang.reflect.InvocationTargetException;

public class Main {
    public static void main(String[] args) throws InvocationTargetException, IllegalAccessException, InstantiationException{
        TestAnnotation.invokeTestAnnotation(Main.class);
        TextContainer txt = new TextContainer();
        AnnotationSaver.annotationSaver(TextContainer.class);




    }
    @TestAnnotations(param1 = 5, param2 = 20)
    public static void method(int param1, int param2){
        int i = param1+param2;
        System.out.println("TestAnnotation sum is" + i);
    }
}