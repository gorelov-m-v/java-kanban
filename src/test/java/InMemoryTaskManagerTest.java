import manager.InMemoryTaskManager;

public class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager>{
    @Override
    InMemoryTaskManager createManager() {
        return new InMemoryTaskManager();
    }
}
