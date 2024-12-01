package kr.merutilm.pacl.al.editor.vfx.t1m3ddeco.variable;

import kr.merutilm.base.struct.Point3D;

@FunctionalInterface
public interface DotBuilderCreator {
    Point3D.Builder[] create(Point3D center, Point3D scale);
}
