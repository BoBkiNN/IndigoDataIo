package xyz.bobkinn.indigodataio;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
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

    public static List<Byte> byteToList(byte[] v){
        if (v == null) return null;
        var ret = new Byte[v.length];
        for (int i = 0; i < v.length; i++) {
            ret[i] = v[i];
        }
        return DataHolder.toList(ret);
    }

    public static byte[] listToByte(List<Byte> v){
        if (v == null) return null;
        var a = v.toArray(Byte[]::new);
        var ret = new byte[a.length];
        for (int i = 0; i < a.length; i++) {
            ret[i] = a[i];
        }
        return ret;
    }


    public static List<Short> shortToList(short[] v){
        if (v == null) return null;
        var ret = new Short[v.length];
        for (int i = 0; i < v.length; i++) {
            ret[i] = v[i];
        }
        return DataHolder.toList(ret);
    }

    public static short[] listToShort(List<Short> v){
        if (v == null) return null;
        var a = v.toArray(Short[]::new);
        var ret = new short[a.length];
        for (int i = 0; i < a.length; i++) {
            ret[i] = a[i];
        }
        return ret;
    }


    public static List<Integer> intToList(int[] v){
        if (v == null) return null;
        var ret = new Integer[v.length];
        for (int i = 0; i < v.length; i++) {
            ret[i] = v[i];
        }
        return DataHolder.toList(ret);
    }

    public static int[] listToInt(List<Integer> v){
        if (v == null) return null;
        var a = v.toArray(Integer[]::new);
        var ret = new int[a.length];
        for (int i = 0; i < a.length; i++) {
            ret[i] = a[i];
        }
        return ret;
    }

    public static List<Long> longToList(long[] v){
        if (v == null) return null;
        var ret = new Long[v.length];
        for (int i = 0; i < v.length; i++) {
            ret[i] = v[i];
        }
        return DataHolder.toList(ret);
    }

    public static long[] listToLong(List<Long> v){
        if (v == null) return null;
        var a = v.toArray(Long[]::new);
        var ret = new long[a.length];
        for (int i = 0; i < a.length; i++) {
            ret[i] = a[i];
        }
        return ret;
    }

    public static List<Float> floatToList(float[] v){
        if (v == null) return null;
        var ret = new Float[v.length];
        for (int i = 0; i < v.length; i++) {
            ret[i] = v[i];
        }
        return DataHolder.toList(ret);
    }

    public static float[] listToFloat(List<Float> v){
        if (v == null) return null;
        var a = v.toArray(Float[]::new);
        var ret = new float[a.length];
        for (int i = 0; i < a.length; i++) {
            ret[i] = a[i];
        }
        return ret;
    }

    public static List<Double> doubleToList(double[] v){
        if (v == null) return null;
        var ret = new Double[v.length];
        for (int i = 0; i < v.length; i++) {
            ret[i] = v[i];
        }
        return DataHolder.toList(ret);
    }

    public static double[] listToDouble(List<Double> v){
        if (v == null) return null;
        var a = v.toArray(Double[]::new);
        var ret = new double[a.length];
        for (int i = 0; i < a.length; i++) {
            ret[i] = a[i];
        }
        return ret;
    }

}
