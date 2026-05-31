package mod.tjt01.lapislib.energy;

import net.minecraft.nbt.IntTag;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.energy.IEnergyStorage;

public class SimpleEnergyStorage implements IEnergyStorage, INBTSerializable<IntTag> {
    protected final int capacity;
    protected final int maxReceive;
    protected final int maxExtract;

    protected int energy;

    public SimpleEnergyStorage(int capacity) {
        this(capacity, capacity, capacity, 0);
    }

    public SimpleEnergyStorage(int capacity, int maxTransfer) {
        this(capacity, maxTransfer, maxTransfer, 0);
    }

    public SimpleEnergyStorage(int capacity, int maxReceive, int maxExtract) {
        this(capacity, maxReceive, maxExtract, 0);
    }

    public SimpleEnergyStorage(int capacity, int maxReceive, int maxExtract, int energy) {
        this.capacity = capacity;
        this.maxReceive = maxReceive;
        this.maxExtract = maxExtract;
        this.energy = energy;
    }

    public int getMaxReceive() {
        return maxReceive;
    }

    public int getMaxExtract() {
        return maxExtract;
    }

    public int getRemainingCapacity() {
        return this.getMaxEnergyStored() - this.getEnergyStored();
    }

    public void setEnergy(int energy) {
        this.energy = energy;
        this.onEnergyChanged();
    }

    public void onEnergyChanged() {

    }

    @Override
    public int receiveEnergy(int receive, boolean simulate) {
        if (!canReceive()) return 0;
        int rec = Math.min(receive, getRemainingCapacity());
        if (rec <= 0) return 0;
        if (!simulate) setEnergy(getEnergyStored() + rec);
        return rec;
    }

    @Override
    public int extractEnergy(int extract, boolean simulate) {
        if (!canExtract() || getEnergyStored() <= 0) return 0;
        int ext = Math.min(extract, this.getEnergyStored());
        if (!simulate) setEnergy(getEnergyStored() - ext);
        return ext;
    }

    @Override
    public int getEnergyStored() {
        return energy;
    }

    @Override
    public int getMaxEnergyStored() {
        return capacity;
    }

    @Override
    public boolean canExtract() {
        return getMaxExtract() > 0;
    }

    @Override
    public boolean canReceive() {
        return getMaxReceive() > 0;
    }

    @Override
    public IntTag serializeNBT() {
        return IntTag.valueOf(this.energy);
    }

    @Override
    public void deserializeNBT(IntTag nbt) {
        this.energy = nbt.getAsInt();
    }
}
