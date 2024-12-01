package kr.merutilm.pacl.al.event.struct;


import kr.merutilm.base.struct.Struct;
import kr.merutilm.base.struct.StructBuilder;
import kr.merutilm.pacl.al.event.selectable.RelativeTo;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public record RelativeTile(int offset,
                           @Nonnull RelativeTo.Tile relativeTo) implements Struct<RelativeTile> {

    @Nullable
    public static RelativeTile convert(@Nullable String value) {
        if (value == null) {
            return null;
        }
        String[] val = value.replaceAll("[ \"]", "").split(",");
        int offset = Integer.parseInt(val[0]);
        RelativeTo.Tile relativeTo = RelativeTo.Tile.typeOf(val[1]);
        return new RelativeTile(offset, relativeTo);
    }

    public int floor(int eventFloor, int length) {
        switch (relativeTo()) {
            case START -> {
                return offset;
            }
            case END -> {
                return length + offset;
            }
            default -> {
                return eventFloor + offset;
            }
        }
    }

    public Builder edit() {
        return new Builder(offset, relativeTo);
    }

    public static class Builder implements StructBuilder<RelativeTile> {

        private Integer offset;
        private RelativeTo.Tile relativeTo;


        private Builder(@Nonnull Integer offset, @Nonnull RelativeTo.Tile relativeTo) {
            this.offset = offset;
            this.relativeTo = relativeTo;
        }


        public Builder setRelativeTo(@Nonnull RelativeTo.Tile relativeTo) {
            this.relativeTo = relativeTo;
            return this;
        }


        public Builder setOffset(@Nonnull Integer offset) {
            this.offset = offset;
            return this;
        }

        @Override
        public RelativeTile build() {
            return new RelativeTile(offset, relativeTo);
        }
    }

    @Nonnull
    @Override
    public String toString() {
        return offset + ",\"" + relativeTo + "\"";
    }
}

