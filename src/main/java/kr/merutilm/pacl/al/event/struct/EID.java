package kr.merutilm.pacl.al.event.struct;

import javax.annotation.Nonnull;

import kr.merutilm.base.struct.Struct;
import kr.merutilm.base.struct.StructBuilder;


/**
 * 고유 이벤트 아이디,
 * 임시 이벤트의 경우, address = -1입니다.
 */
public record EID(int floor, int address) implements Struct<EID> {

    public static final int TEMP = -1;


    public static EID convert(@Nonnull String value) {
        String[] arr = value.replace(" ", "").split(",");
        int x = Integer.parseInt(arr[0]);
        int y = Integer.parseInt(arr[1]);
        return new EID(x, y);
    }
    @Override
    public Builder edit() {
        return new Builder(floor, address);
    }

    public static final class Builder implements StructBuilder<EID> {
        private int floor;
        private int address;


        private Builder(int floor, int address) {
            this.floor = floor;
            this.address = address;
        }

        public Builder setFloor(int floor) {
            this.floor = floor;
            return this;
        }

        public Builder setAddress(int address) {
            this.address = address;
            return this;
        }


        @Override
        public EID build() {
            return new EID(floor, address);
        }

    }
    public boolean isTemporary(){
        return address == TEMP;
    }
    @Override
    public boolean equals(Object obj) {
        if(obj instanceof EID id){
            return id.floor == floor && id.address == address;
        }
        return false;
    }
}
