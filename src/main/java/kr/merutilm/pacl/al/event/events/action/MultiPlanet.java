package kr.merutilm.pacl.al.event.events.action;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import kr.merutilm.pacl.al.event.selectable.Planets;


public record MultiPlanet(Boolean active,
                          Planets planets) implements Actions {


    @Nonnull
    @Override
    public Builder edit() {
        return new Builder()
                .setPlanets(planets);
    }


    public static final class Builder implements ActionsBuilder {
        private Boolean active;
        private Planets planets = Planets.TWO_PLANETS;
        @Override
        public Builder setActive(@Nullable Boolean active) {
            this.active = active;
            return this;
        }
        public Builder setPlanets(@Nullable Planets planets) {
            this.planets = planets;
            return this;
        }


        @Override
        public MultiPlanet build() {
            return new MultiPlanet(active, planets);
        }
    }
}
