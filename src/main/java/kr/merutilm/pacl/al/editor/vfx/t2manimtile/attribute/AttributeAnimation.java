package kr.merutilm.pacl.al.editor.vfx.t2manimtile.attribute;

import kr.merutilm.base.selectable.Ease;
import kr.merutilm.base.struct.Point2D;
import kr.merutilm.pacl.al.editor.vfx.Attribute;
import kr.merutilm.pacl.al.editor.vfx.AttributeBuilder;

/**
 * 애니메이션 속성, randomP는 곱연산(%), random 은 합연산(+)입니다.
 * @param animationStartBeats 타일 애니메이션 시작 시각
 * @param duration            타일 애니메이션 기간. 이 기간이 시작되기 직전에 등장합니다.
 * @param position            타일 위치 오프셋
 * @param rotation            타일 각도 오프셋
 * @param scale               타일 크기
 * @param ease                가감속
 */
public record AttributeAnimation(double animationStartBeats, double duration,
                                 Point2D position, double rotation, double scale, Ease ease,
                                 double randomDurationP, double randomRadius, double randomRotation,
                                 double randomScaleP) implements Attribute {

    @Override
    public AttributeBuilder edit() {
        return new Builder(animationStartBeats, duration, position, rotation, scale, ease, randomDurationP, randomRadius, randomRotation, randomScaleP);
    }

    public static final class Builder implements AttributeBuilder {
        private double animationStartBeats;
        private double duration;
        private Point2D position;
        private double rotation;
        private double scale;
        private Ease ease;
        private double randomDuration;
        private double randomRadius;
        private double randomRotation;
        private double randomScale;

        public Builder(double animationStartBeats, double duration, Point2D position, double rotation, double scale, Ease ease, double randomDuration, double randomRadius, double randomRotation, double randomScale) {
            this.animationStartBeats = animationStartBeats;
            this.duration = duration;
            this.position = position;
            this.rotation = rotation;
            this.scale = scale;
            this.ease = ease;
            this.randomDuration = randomDuration;
            this.randomRadius = randomRadius;
            this.randomRotation = randomRotation;
            this.randomScale = randomScale;
        }

        public Builder setAnimationStartBeats(double animationStartBeats) {
            this.animationStartBeats = animationStartBeats;
            return this;
        }

        public Builder setDuration(double duration) {
            this.duration = duration;
            return this;
        }

        public Builder setPosition(Point2D position) {
            this.position = position;
            return this;
        }

        public Builder setRotation(double rotation) {
            this.rotation = rotation;
            return this;
        }

        public Builder setScale(double scale) {
            this.scale = scale;
            return this;
        }

        public Builder setEase(Ease ease) {
            this.ease = ease;
            return this;
        }

        public Builder setRandomDuration(double randomDuration) {
            this.randomDuration = randomDuration;
            return this;
        }

        public Builder setRandomRadius(double randomRadius) {
            this.randomRadius = randomRadius;
            return this;
        }

        public Builder setRandomRotation(double randomRotation) {
            this.randomRotation = randomRotation;
            return this;
        }

        public Builder setRandomScale(double randomScale) {
            this.randomScale = randomScale;
            return this;
        }

        @Override
        public AttributeAnimation build() {
            return new AttributeAnimation(animationStartBeats, duration, position, rotation, scale, ease, randomDuration, randomRadius, randomRotation, randomScale);
        }
    }
}
