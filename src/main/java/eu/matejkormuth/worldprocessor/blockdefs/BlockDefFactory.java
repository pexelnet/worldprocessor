package eu.matejkormuth.worldprocessor.blockdefs;

@FunctionalInterface
public interface BlockDefFactory<W, T extends BlockDef<W>> {
    T supply(int x, int y, int z, W world);
}
