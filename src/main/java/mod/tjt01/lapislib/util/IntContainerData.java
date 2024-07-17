package mod.tjt01.lapislib.util;

import net.minecraft.world.inventory.ContainerData;

/**
 * {@link ContainerData} implementation that fully supports integers.
 */
public interface IntContainerData extends ContainerData {
    int UPPER = 0xFFFF0000;
    int LOWER = 0x0000FFFF;

    int getInt(int index);

    void setInt(int index, int value);

    int getIntCount();

    default short getLower(int index) {
        return (short) getInt(index);
    }

    default short getUpper(int index) {
        return (short) (getInt(index) >> 16);
    }

    default void setLower(int index, short value) {
        this.setInt(index, (this.getInt(index) & UPPER) | (value & LOWER));
    }

    default void setUpper(int index, short value) {
        this.setInt(index, (this.getInt(index) & LOWER) | (value << 16 & UPPER));
    }

    @Override
    default int get(int index) {
        return index%2 == 0 ? getLower(index / 2) : getUpper(index / 2);
    }

    @Override
    default void set(int index, int value) {
        if (index%2 == 0) {
            setLower(index / 2, (short) value);
        } else {
            setUpper(index / 2, (short) value);
        }
    };

    @Override
    default int getCount() {
        return getIntCount() * 2;
    }
}
