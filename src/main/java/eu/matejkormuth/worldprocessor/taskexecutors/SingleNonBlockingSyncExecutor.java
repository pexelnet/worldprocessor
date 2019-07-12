package eu.matejkormuth.worldprocessor.taskexecutors;

import eu.matejkormuth.worldprocessor.BlockDefGen;
import eu.matejkormuth.worldprocessor.SingleTask;
import eu.matejkormuth.worldprocessor.blockdefs.BlockDef;
import org.bukkit.Bukkit;

public class SingleNonBlockingSyncExecutor implements TaskExecutor<SingleTask> {

    /**
     * Currently executed task.
     */
    private SingleTask current;

    private BlockDefGen.GenState genState;
    private BlockDef[] defs;

    private int taskId = -1;
    private int speed = 256 * 16 * 16; // blocks per tick

    @Override
    public void beginExecute(SingleTask task) throws UnsupportedOperationException {
        if (current == null) {
            current = task;
            begin();
        } else {
            throw new IllegalStateException("Already executing a task!");
        }
    }

    @Override
    public void cancelExecute(SingleTask task) throws UnsupportedOperationException {
        if (current != null) {
            current = null;
            cancel();
        } else {
            throw new IllegalStateException("Not executing a task!");
        }
    }

    private void begin() {
        // Create gen state.
        genState = BlockDefGen.createState();
        defs = new BlockDef[speed];

        // Schedule working.
        taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, this::doWork, 1L, 0L);
    }

    private void doWork() {
        // Generate block definitions.
        BlockDefGen.preferChunks(genState, defs);

        // Run tasks on defs.
        for (int i = 0; i < speed; i++) {
            if (current.getPredicate().test(defs[i])) {
                current.getConsumer().accept(defs[i]);
            }
        }
    }

    private void cancel() {
        // Cancel working.
        Bukkit.getScheduler().cancelTask(taskId);
        taskId = -1;
    }
}
