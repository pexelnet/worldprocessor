package eu.matejkormuth.worldprocessor;

import eu.matejkormuth.worldprocessor.blockdefs.BlockDef;

import java.util.function.Consumer;

public class TaskPipeline implements Task {

    private SingleTask[] tasks;
    private int modCount;
    private int size;

    public TaskPipeline(int initialCapacity) {
        tasks = new SingleTask[initialCapacity];
        size = initialCapacity;
        modCount = 0;
    }

    public TaskPipeline() {
        this(16);
    }

    public void addLast(SingleTask task) {
        if (modCount + 1 > size) {
            doubleCapacity();
        }
        tasks[modCount++] = task;
    }

    private void doubleCapacity() {
        SingleTask[] newArray = new SingleTask[size * 2];
        System.arraycopy(tasks, 0, newArray, 1, modCount);
        tasks = newArray;
        size = size * 2;
    }

    @Override
    public Consumer<BlockDef> getConsumer() {
        return this::executePipeline;
    }

    private void executePipeline(BlockDef def) {
        for (int i = 0; i < modCount; i++) {
            if (tasks[i].getPredicate().test(def)) {
                tasks[i].getConsumer().accept(def);
            }
        }
    }
}
