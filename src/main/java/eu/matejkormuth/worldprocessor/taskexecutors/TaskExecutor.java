package eu.matejkormuth.worldprocessor.taskexecutors;

import eu.matejkormuth.worldprocessor.Task;

public interface TaskExecutor<T extends Task> {

    default void execute(T task) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    default void beginExecute(T task) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    default void cancelExecute(T task) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

}
