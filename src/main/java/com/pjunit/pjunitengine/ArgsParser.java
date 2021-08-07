package com.pjunit.pjunitengine;

import static java.util.Optional.ofNullable;

import java.util.Map;
import java.util.function.Function;

final class ArgsParser {

    private ArgsParser() {}

    static Map<Class<?>, Function<String, Object>> parsers =
            Map.ofEntries(
                    Map.entry(Boolean.class, Boolean::parseBoolean),
                    Map.entry(boolean.class, Boolean::parseBoolean),
                    Map.entry(Byte.class, Byte::parseByte),
                    Map.entry(byte.class, Byte::parseByte),
                    Map.entry(Short.class, Short::parseShort),
                    Map.entry(short.class, Short::parseShort),
                    Map.entry(Integer.class, Integer::parseInt),
                    Map.entry(int.class, Integer::parseInt),
                    Map.entry(Long.class, Long::parseLong),
                    Map.entry(long.class, Long::parseLong),
                    Map.entry(Float.class, Float::parseFloat),
                    Map.entry(float.class, Float::parseFloat),
                    Map.entry(Double.class, Double::parseDouble),
                    Map.entry(double.class, Double::parseDouble),
                    Map.entry(Character.class, s -> s.charAt(0)),
                    Map.entry(String.class, s -> s));

    static Object parseParam(Class<?> clazz, String param) {
        if (param.isEmpty())
            throw new IllegalArgumentException("Passed param is empty!");
        return ofNullable(parsers.get(clazz))
            .orElseThrow(() -> new IllegalArgumentException("Wrong argument type: " + param))
            .apply(param);
    }
}
