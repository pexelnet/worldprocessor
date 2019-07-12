package eu.matejkormuth.worldprocessor;

import eu.matejkormuth.worldprocessor.blockdefs.BlockDef;

import java.util.function.Consumer;

public interface Task {
    Consumer<BlockDef> getConsumer();
}
