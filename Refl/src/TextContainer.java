import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Objects;
@SaveTo(file = "SaveString.txt")
public class TextContainer {
    private String text = "Нихуя не понятно, но очень интересно";

    public TextContainer(String text) {
        this.text = text;
    }

    public TextContainer() {
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
@Saver
    public void save() throws IOException {
        try (BufferedWriter out = new BufferedWriter(new FileWriter(TextContainer.class.getAnnotation(SaveTo.class).file()))) {

            out.write(text);
        }

}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TextContainer that = (TextContainer) o;
        return text.equals(that.text);
    }

    @Override
    public int hashCode() {
        return Objects.hash(text);
    }

    @Override
    public String toString() {
        return "TextContainer{" +
                "text='" + text + '\'' +
                '}';
    }
}