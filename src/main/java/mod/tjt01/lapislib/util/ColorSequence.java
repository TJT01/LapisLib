package mod.tjt01.lapislib.util;

import net.minecraft.core.NonNullList;

import java.util.LinkedList;
import java.util.List;

public class ColorSequence {
    private final Keypoint[] sequence;

    private ColorSequence(List<Keypoint> keypoints) {
        this.sequence = keypoints.toArray(new Keypoint[0]);
    }

    public static Builder builder(int color) {
        return new Builder(color);
    }

    public int getLength() {
        return sequence.length;
    }

    public Keypoint get(int index) {
        return sequence[index];
    }

    public static class Builder {
        private final LinkedList<Keypoint> sequence;
        private double lastTime = 0;

        private Builder(int color) {
            sequence = new LinkedList<>();
            sequence.add(new Keypoint(color, 0));
        }

        public Builder add(Keypoint keypoint) {
            if (keypoint.time < lastTime) throw new IllegalArgumentException("Keypoints received out of order");
            this.sequence.add(keypoint);
            this.lastTime = keypoint.time;
            return this;
        }

        public Builder add(int color, double time) {
            return this.add(new Keypoint(color, time));
        }

        public ColorSequence end(int color) {
            this.sequence.add(new Keypoint(color, 1.0D));
            return new ColorSequence(this.sequence);
        }
    }

    public record Keypoint(int color, double time) {
        public Keypoint {
            if (time < 0 || time > 1)
                throw new IllegalArgumentException("Argument 2 must be within [0, 1], got " + time);
        }
    }
}
