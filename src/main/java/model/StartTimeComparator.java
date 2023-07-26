package model;

import java.time.Instant;
import java.util.Comparator;

public class StartTimeComparator implements Comparator<Task> {
    @Override
    public int compare(Task o1, Task o2) {
        Instant s1 = o1.getStartTime();
        Instant s2 = o2.getStartTime();
        if (s1 == null && s2 == null) {
            return o1.getId() - o2.getId();
        } else if (s1 == null) {
            return 1;
        } else if (s2 == null) {
            return -1;
        } else {
            return s1.compareTo(s2);
        }
    }
}
