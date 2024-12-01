package kr.merutilm.pacl.al.editor.vfx.t2manimtile.attribute;

import kr.merutilm.pacl.al.editor.vfx.Attribute;
import kr.merutilm.pacl.al.editor.vfx.AttributeBuilder;

@SuppressWarnings("UnusedReturnValue")
public record AttributeMoveTrack(int floor,
                                 double angleOffset,
                                 boolean hide) implements Attribute {
    @Override
    public AttributeBuilder edit() {
        return new Builder(floor, angleOffset, hide);
    }

    public static final class Builder implements AttributeBuilder {
        private int floor;
        private double angleOffset;
        private boolean hide;

        public Builder(int floor, double angleOffset, boolean hide) {
            this.floor = floor;
            this.angleOffset = angleOffset;
            this.hide = hide;
        }

        public Builder setFloor(int floor) {
            this.floor = floor;
            return this;
        }

        public Builder setAngleOffset(double angleOffset) {
            this.angleOffset = angleOffset;
            return this;
        }

        public Builder setHide(boolean hide) {
            this.hide = hide;
            return this;
        }

        @Override
        public AttributeMoveTrack build() {
            return new AttributeMoveTrack(floor, angleOffset, hide);
        }
    }
}
