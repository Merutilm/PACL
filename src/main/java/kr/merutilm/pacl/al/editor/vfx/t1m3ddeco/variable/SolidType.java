package kr.merutilm.pacl.al.editor.vfx.t1m3ddeco.variable;

import kr.merutilm.base.selectable.Selectable;
import kr.merutilm.base.struct.Point3D;
import kr.merutilm.pacl.al.editor.vfx.t1m3ddeco.VFXGenerator3DDecoration;

import java.util.Arrays;

public enum SolidType implements Selectable {
    CUBE("cube", (center, scale) -> {
        Point3D.Builder[] db = new Point3D.Builder[8];
        double div = 200;
        db[0] = center.edit().add(new Point3D(-scale.x() / div, scale.y() / div, -scale.z() / div));
        db[1] = center.edit().add(new Point3D(-scale.x() / div, -scale.y() / div, -scale.z() / div));
        db[2] = center.edit().add(new Point3D(scale.x() / div, scale.y() / div, -scale.z() / div));
        db[3] = center.edit().add(new Point3D(scale.x() / div, -scale.y() / div, -scale.z() / div));
        db[4] = center.edit().add(new Point3D(-scale.x() / div, scale.y() / div, scale.z() / div));
        db[5] = center.edit().add(new Point3D(-scale.x() / div, -scale.y() / div, scale.z() / div));
        db[6] = center.edit().add(new Point3D(scale.x() / div, scale.y() / div, scale.z() / div));
        db[7] = center.edit().add(new Point3D(scale.x() / div, -scale.y() / div, scale.z() / div));
        return db;
    }),
    TETRAHEDRON("tetrahedron", (center, scale) -> {
        Point3D.Builder[] db = new Point3D.Builder[4];
        double div = 200 * VFXGenerator3DDecoration.SQRT_2;
        db[0] = center.edit().add(new Point3D(-scale.x() / div, -scale.y() / div, -scale.z() / div));
        db[1] = center.edit().add(new Point3D(-scale.x() / div, scale.y() / div, scale.z() / div));
        db[2] = center.edit().add(new Point3D(scale.x() / div, scale.y() / div, -scale.z() / div));
        db[3] = center.edit().add(new Point3D(scale.x() / div, -scale.y() / div, scale.z() / div));
        return Arrays.stream(db).map(v -> v.rotate(center.x(), center.y(), center.z(), 45, -45, 45)).toArray(Point3D.Builder[]::new);
    }),
    OCTAHEDRON("octahedron", (center, scale) -> {
        Point3D.Builder[] db = new Point3D.Builder[6];
        double div = 200 / VFXGenerator3DDecoration.SQRT_2;
        db[0] = center.edit().add(new Point3D(scale.x() / div, 0, 0));
        db[1] = center.edit().add(new Point3D(-scale.x() / div, 0, 0));
        db[2] = center.edit().add(new Point3D(0, scale.y() / div, 0));
        db[3] = center.edit().add(new Point3D(0, -scale.y() / div, 0));
        db[4] = center.edit().add(new Point3D(0, 0, scale.z() / div));
        db[5] = center.edit().add(new Point3D(0, 0, -scale.z() / div));
        return db;
    });

    private final String name;
    private final DotBuilderCreator creator;

    @Override
    public String toString() {
        return name;
    }

    public DotBuilderCreator getCreator() {
        return creator;
    }

    SolidType(String name, DotBuilderCreator creator) {
        this.name = name;
        this.creator = creator;
    }

    public static SolidType typeOf(String name) {
        return Arrays.stream(values())
                .filter(value -> value.name.equals(name))
                .findAny()
                .orElse(null);
    }

    // center, rotation, scale


}

