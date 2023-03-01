package academy.prog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class JsonMessages {
    private final List<Message> list = new ArrayList<>();

    public JsonMessages(List<Message> sourceList, int fromIndex, String toStr) {
        for (int i = fromIndex; i < sourceList.size(); i++)
            if (toStr.equals(sourceList.get(i).getTo()) || sourceList.get(i).getTo() == null)
            list.add(sourceList.get(i));
    }

    public List<Message> getList() {
        return Collections.unmodifiableList(list);
    }
}
