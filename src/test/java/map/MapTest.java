package map;

import org.junit.Test;
import xyz.bobkinn.indigodataio.MapBuilder;
import xyz.bobkinn.indigodataio.NestedKeyMap;

import java.util.List;
import java.util.Map;

public class MapTest {

    @Test
    public void builderTest(){
        var b = MapBuilder.newBuilder()
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
        System.out.println(b);
        var b2 = MapBuilder.newBuilder()
                .put("k1", 11)
                .down("o")
                .put("k2", 22)
                .down("o2")
                .build();
        System.out.println(b2);
    }

    @Test
    public void testSectionList(){
        var map = new NestedKeyMap();
        map.put("ls", List.of(Map.of("k", 1), Map.of("k2", 2)));
        System.out.println(map);
        var maps = map.getMapList("ls");
        var mm = maps.get(0);
        System.out.println(mm);
        var sections = map.getSections("ls");
        System.out.println(sections);
    }

    @Test
    public void testIntList(){
        var map = new NestedKeyMap();
        map.put("l", new int[]{3, 3, 3});
        System.out.println(map.getIntList("l", null));
    }

    @Test
    public void testNums(){
        var map = new NestedKeyMap();
        map.put("s", (short) 1);
        map.put("i", 2);
        map.put("f", 3f);
        map.put("d", 4d);
        map.put("l", 5L);
        var d = map.getDouble("d");
        assert d != null;
        assert d == 4.0d;
    }
}
