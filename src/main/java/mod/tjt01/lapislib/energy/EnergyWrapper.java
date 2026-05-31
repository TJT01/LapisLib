package mod.tjt01.lapislib.energy;

import net.minecraftforge.energy.IEnergyStorage;

public abstract class EnergyWrapper implements IEnergyStorage {
    protected final IEnergyStorage wrapped;

    public EnergyWrapper(IEnergyStorage wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        return wrapped.receiveEnergy(maxReceive, simulate);
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        return wrapped.extractEnergy(maxExtract, simulate);
    }

    @Override
    public int getEnergyStored() {
        return wrapped.getEnergyStored();
    }

    @Override
    public int getMaxEnergyStored() {
        return wrapped.getMaxEnergyStored();
    }

    @Override
    public boolean canExtract() {
        return wrapped.canExtract();
    }

    @Override
    public boolean canReceive() {
        return wrapped.canReceive();
    }

    public static class ReadOnlyEnergy extends EnergyWrapper {
        public ReadOnlyEnergy(IEnergyStorage wrapped) {
            super(wrapped);
        }

        @Override
        public int receiveEnergy(int maxReceive, boolean simulate) {
            return 0;
        }

        @Override
        public int extractEnergy(int maxExtract, boolean simulate) {
            return 0;
        }

        @Override
        public boolean canExtract() {
            return false;
        }

        @Override
        public boolean canReceive() {
            return false;
        }
    }

    public static class LimitedEnergy extends EnergyWrapper {
        protected final int maxReceive;
        protected final int maxExtract;

        public LimitedEnergy(IEnergyStorage wrapped, int maxReceive, int maxExtract) {
            super(wrapped);
            this.maxReceive = maxReceive;
            this.maxExtract = maxExtract;
        }

        @Override
        public boolean canExtract() {
            return maxExtract > 0 && super.canExtract();
        }

        @Override
        public boolean canReceive() {
            return maxReceive > 0 && super.canReceive();
        }

        @Override
        public int extractEnergy(int extract, boolean simulate) {
            if (maxExtract <= 0) return 0;
            return super.extractEnergy(Math.min(maxExtract, extract), simulate);
        }

        @Override
        public int receiveEnergy(int receive, boolean simulate) {
            if (maxReceive <= 0) return 0;
            return super.receiveEnergy(Math.min(maxReceive, receive), simulate);
        }
    }
}
