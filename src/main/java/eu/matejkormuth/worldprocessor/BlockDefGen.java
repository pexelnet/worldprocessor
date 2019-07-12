package eu.matejkormuth.worldprocessor;

import eu.matejkormuth.worldprocessor.blockdefs.BlockDef;
import eu.matejkormuth.worldprocessor.blockdefs.BlockDefFactory;

public final class BlockDefGen {
    private BlockDefGen() {
    }

    public static class GenState<W, T extends BlockDef<W>> {
        private final BlockDefFactory<W, T> factory;
        private final W world;

        private int maxX;
        private int maxZ;

        private int curX;
        private int curZ;

        public GenState(BlockDefFactory<W, T> factory, W world) {
            this.factory = factory;
            this.world = world;
        }
    }

    public static <W, T extends BlockDef<W>> void fill(GenState<W, T> state, BlockDef[] defs) {
        if ((defs.length % (256)) == 0) {
            throw new IllegalArgumentException("Bad defs length! Must be l % 256 == 0.");
        }

        int index = 0;
        for (; state.curX < state.maxX; state.curX++) {
            for (; state.curZ < state.maxZ; state.curZ++) {
                for (int h = 0; h < 256; h++) {
                    defs[index++] = state.factory.supply(state.curX, h, state.curZ, state.world);
                }
            }
        }
    }


    public static void preferChunks(GenState state, BlockDef[] defs) {
        if ((defs.length % (16 * 16 * 256)) == 0) {
            throw new IllegalArgumentException("Bad defs length! Must be l % 16 == 0.");
        }

        int chunks = defs / 16 * 16 * 256;

        for (int i = 0; i < chunks; i++) {
            genOneChunk(i, defs);
        }
    }

    private static void genOneChunk(int offset, BlockDef[] array) {
        offset = offset * 16 * 16 * 256;
    }
}
