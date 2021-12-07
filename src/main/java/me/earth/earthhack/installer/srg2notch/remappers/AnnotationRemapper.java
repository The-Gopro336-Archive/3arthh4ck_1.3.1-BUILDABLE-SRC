package me.earth.earthhack.installer.srg2notch.remappers;

import java.util.ArrayList;
import java.util.List;
import me.earth.earthhack.installer.srg2notch.Mapping;
import me.earth.earthhack.installer.srg2notch.MappingUtil;
import me.earth.earthhack.installer.srg2notch.remappers.Remapper;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;

public class AnnotationRemapper
implements Remapper {
    @Override
    public void remap(ClassNode cn, Mapping mapping) {
        if (cn.invisibleAnnotations != null) {
            for (AnnotationNode node : cn.invisibleAnnotations) {
                this.remapAnnotation(node, mapping);
            }
        }
        if (cn.visibleAnnotations != null) {
            for (AnnotationNode node : cn.visibleAnnotations) {
                this.remapAnnotation(node, mapping);
            }
        }
    }

    private void remapAnnotation(AnnotationNode node, Mapping mapping) {
        if (node.values == null || node.values.isEmpty()) {
            return;
        }
        ArrayList<Object> values = new ArrayList<Object>(node.values.size());
        this.remapList(node.values, values, mapping);
        node.values = values;
    }

    /*
     * WARNING - void declaration
     */
    private void remapList(List<Object> objects, List<Object> collecting, Mapping mapping) {
        for (Object object : objects) {
            void var5_5;
            if (object instanceof Type) {
                Type type = MappingUtil.map((Type)object, mapping);
            } else if (object instanceof List) {
                List list = (List)object;
                ArrayList arrayList = new ArrayList(list.size());
                this.remapList(list, arrayList, mapping);
            }
            collecting.add(var5_5);
        }
    }
}
