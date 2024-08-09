package xyz.bobkinn.indigodataio.gson;

import com.google.gson.JsonPrimitive;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class TestGson {
    @Test
    public void builderTest(){
        var b = GsonData.newBuilder()
                .put("k1", 11)
                .down("o")
                .put("k2", 22)
                .down("o2")
                .put("k3", 33)
                .up()
                .put("k4", 23)
                .up()
                .put("k5", 12)
                .build();
        System.out.println("b = " + b);

        b.toBuilder("ib")
                .put("k6", 66)
                .down("o3")
                .put("k7", 77)
                .up()
                .put("k8", 88);
        System.out.println("b = " + b);

        var b2 = GsonData.newBuilder()
                .put("k1", 11)
                .down("o")
                .put("k2", 22)
                .down("o2")
                .build();
        System.out.println("b2 = " + b2);
    }

    @Test
    public void testSectionList(){
        var map = new GsonData();
        map.putMapList("ls", List.of(
                Map.of("k", new JsonPrimitive(1)),
                Map.of("k2", new JsonPrimitive(2))));
        map.putIntArray("ir", new int[]{1, 3, -4});
        System.out.println(map);
        var maps = map.getMapList("ls");
        var mm = maps.get(0);
        System.out.println(mm);
        var ir = map.getIntArray("ir");
        System.out.println("Arrays.toString(ir) = " + Arrays.toString(ir));
        var sections = map.getSectionList("ls");
        System.out.println(sections);
        System.out.println(map);
        var maps2 = map.getMapArray("ls");
        System.out.println(Arrays.toString(maps2));
    }

    @Test
    public void testIntList(){
        var map = new GsonData();
        map.putIntArray("l", new int[]{3, 3, 3});
        System.out.println(map.getIntList("l", null));
    }

    @Test
    public void testNums(){
        var map = new GsonData();
        map.putShort("s", (short) 1);
        map.putInt("i", 2);
        map.putFloat("f", 3f);
        map.putDouble("d", 4d);
        map.putLong("l", 5L);
        var d = map.getDouble("d");
        assert d == 4.0d;
    }
}
