package org.study.beanlet.value;

public class ValueResolverImpl implements ValueResolver{
    @Override
    public Object resolveValue(String value, Class<?> targetType) {
        return switch (targetType.getSimpleName()) {
            case "Integer" -> Integer.parseInt(value);
            case "Long" -> Long.parseLong(value);
            case "Double" -> Double.parseDouble(value);
            case "Float" -> Float.parseFloat(value);
            case "Boolean" -> Boolean.parseBoolean(value);
            case "Character" -> value.charAt(0);
            case "Byte" -> Byte.parseByte(value);
            case "Short" -> Short.parseShort(value);
            default -> value;
        };
    }
}
