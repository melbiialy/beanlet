package org.study.beanlet.env;

public class ValueResolver{

    public static Object resolveValue(String value, Class<?> targetType) {
        return switch (targetType.getSimpleName()) {
            case "Integer", "int" -> Integer.parseInt(value);
            case "Long","long" -> Long.parseLong(value);
            case "Double","double" -> Double.parseDouble(value);
            case "Float","float" -> Float.parseFloat(value);
            case "Boolean","boolean" -> Boolean.parseBoolean(value);
            case "Character","char" -> value.charAt(0);
            case "Byte","byte" -> Byte.parseByte(value);
            case "Short", "short" -> Short.parseShort(value);
            default -> value;
        };
    }
}
