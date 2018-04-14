package com.bitbucket.lonelydeveloper97.wifiproxysettingslibrary.proxy_change_realisation.wifi_network.reflection_realisation;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;


public abstract class ReflectionHelper {

    /**
     * Used for getting public fields with @hide annotation
     */
    public static Object getField(Object object, String name)
            throws SecurityException, NoSuchFieldException, IllegalArgumentException,
            IllegalAccessException {
        Field field = object.getClass().getField(name);
        return field.get(object);
    }

    /**
     * Used for getting private fields
     */
    public static Object getDeclaredField(Object object, String name)
            throws SecurityException, NoSuchFieldException, IllegalArgumentException,
            IllegalAccessException {
        Field declaredField = object.getClass().getDeclaredField(name);
        declaredField.setAccessible(true);
        return declaredField.get(object);
    }

    /**
     * Used for setting private fields
     */
    public static void setDeclaredField(Object object, String name, Object value)
            throws NoSuchFieldException, IllegalAccessException {
        Field declaredField = object.getClass().getDeclaredField(name);
        declaredField.setAccessible(true);
        declaredField.set(object, value);
    }

    /**
     * Used for setting Enum fields
     */
    public static void setEnumField(Object object, String value, String name)
            throws SecurityException, NoSuchFieldException, IllegalArgumentException,
            IllegalAccessException {
        Field field = object.getClass().getField(name);
        field.set(object, Enum.valueOf((Class<Enum>) field.getType(), value));
    }

    /**
     * Used for simplifying process of invoking private method
     * Automatically detects args types and founds method to get and invoke
     */
    public static Object getMethodAndInvokeIt(Object object, String methodName, Object... args)
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = object.getClass().getDeclaredMethod(methodName, parameterTypes(args));
        method.setAccessible(true);
        return method.invoke(object, args);
    }

    private static Class[] parameterTypes(Object... args) {
        ArrayList<Class> classes = new ArrayList<>();
        for (Object arg : args) {
            classes.add(arg.getClass());
        }
        return classes.toArray(new Class[args.length]);
    }

}
