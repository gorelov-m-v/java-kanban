import manager.FileBackedTasksManager;

import java.io.File;
import java.nio.file.Path;

public class FileBackedTasksManagerTest extends TaskManagerTest<FileBackedTasksManager> {
    @Override
    FileBackedTasksManager createManager() {
       final Path PATH = Path.of(
                "src" + File.separator +
                        "main" + File.separator +
                        "resources" + File.separator +
                        "test.csv");
       File file = new File(String.valueOf(PATH));
       return new FileBackedTasksManager(file);
    }
}
