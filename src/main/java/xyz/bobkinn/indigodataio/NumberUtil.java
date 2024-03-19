package xyz.bobkinn.indigodataio;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.stream.Stream;

public class NumberUtil {
    public static @NotNull Stream<Number> floatToStream(float @NotNull [] ls){
        var ret = new ArrayList<Number>(ls.length);
        for (var f : ls) ret.add(f);
        return ret.stream();
    }

    public static @NotNull Stream<Number> shortToStream(short @NotNull [] ls){
        var ret = new ArrayList<Number>(ls.length);
        for (var f : ls) ret.add(f);
        return ret.stream();
    }
}
