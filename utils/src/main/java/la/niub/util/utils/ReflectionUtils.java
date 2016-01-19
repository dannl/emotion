
package la.niub.util.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ReflectionUtils {

    /**
     * @param className 类名
     * @param params 实际参数列表数据
     * @return 返回Object引用的对象，实际实际创建出来的对象，如果要使用可以强制转换为自己想要的对象 带参数的反射创建对象
     */
    public static Object newInstance(String className, Object[] params) {
        try {
            Class<?> cls = Class.forName(className);
            return newInstance(cls, params);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Object newInstance(Class<?> cls, Object[] params) {
        try {
            if (params == null || params.length == 0) {
                return cls.newInstance();
            }
            int len = params.length;
            Class<?>[] parameterTypes = new Class[len];
            for (int i = 0; i < len; ++i) {
                parameterTypes[i] = params[i].getClass();
            }
            Constructor<?> con = cls.getConstructor(parameterTypes);
            return con.newInstance(params);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Object invokeStaticMethod(String className, String methodName,
            Class<?>[] parameterTypes, Object[] parameters) {
        try {
            Class<?> c = Class.forName(className);
            Method method = c.getMethod(methodName, parameterTypes);
            if (method != null) {
                method.setAccessible(true);
                return method.invoke(null, parameters);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Object getStaticFieldValue(String className, String fieldName) {
        try {
            Class<?> cls = Class.forName(className);
            Field field = cls.getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void setStaticFieldValue(String className, String fieldName, Object value) {
        try {
            Class<?> cls = Class.forName(className);
            Field field = cls.getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(null, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 直接调用对象方法, 而忽略修饰符(private, protected, default)
     * 
     * @param object : 子类对象
     * @param methodName : 父类中的方法名
     * @param parameterTypes : 父类中的方法参数类型
     * @param parameters : 父类中的方法参数
     * @return 父类中方法的执行结果
     */

    public static Object invokeMethod(Object object, String methodName, Class<?>[] parameterTypes,
            Object[] parameters) {
        Method method = getDeclaredMethod(object, methodName, parameterTypes);
        if (method != null) {
            method.setAccessible(true);
            try {
                return method.invoke(object, parameters);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static Object invokeMethod(String className, String methodName, Object object,
            Class<?>[] parameterTypes, Object[] parameters) {
        try {
            Class<?> cls = Class.forName(className);
            Method method = cls.getMethod(methodName, parameterTypes);
            return method.invoke(object, parameters);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }

    /**
     * 直接设置对象属性值, 忽略 private/protected 修饰符, 也不经过 setter
     * 
     * @param object : 子类对象
     * @param fieldName : 父类中的属性名
     * @param value : 将要设置的值
     */

    public static void setFieldValue(Object object, String fieldName, Object value) {
        Field field = getDeclaredField(object, fieldName);
        if (field != null) {
            field.setAccessible(true);
            try {
                field.set(object, value);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void setFieldValue(String className, String fieldName, Object object,
            Object value) {
        try {
            Class<?> cls = Class.forName(className);
            Field field = cls.getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(object, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 直接读取对象的属性值, 忽略 private/protected 修饰符, 也不经过 getter
     * 
     * @param object : 子类对象
     * @param fieldName : 父类中的属性名
     * @return : 父类中的属性值
     */

    public static Object getFieldValue(Object object, String fieldName) {
        Field field = getDeclaredField(object, fieldName);
        if (field != null) {
            field.setAccessible(true);
            try {
                return field.get(object);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static Object getFieldValue(String className, Object object, String fieldName) {
        try {
            Class<?> cls = Class.forName(className);
            Field field = cls.getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(object);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 循环向上转型, 获取对象的 DeclaredMethod 根据 对象、方法名和对应的方法参数 通过反射 调用上面的方法获取 Method 对象
     * 
     * @param object : 子类对象
     * @param methodName : 父类中的方法名
     * @param parameterTypes : 父类中的方法参数类型
     * @return 父类中的方法对象
     */

    private static Method getDeclaredMethod(Object object, String methodName,
            Class<?>... parameterTypes) {
        for (Class<?> clazz = object.getClass(); clazz != Object.class; clazz = clazz
                .getSuperclass()) {
            try {
                return clazz.getDeclaredMethod(methodName, parameterTypes);
            } catch (Exception e) {
                // 这里甚么都不要做！并且这里的异常必须这样写，不能抛出去。
                // 如果这里的异常打印或者往外抛，则就不会执行clazz =
                // clazz.getSuperclass(),最后就不会进入到父类中了
            }
        }
        return null;
    }

    /**
     * 循环向上转型, 获取对象的 DeclaredField
     * 
     * @param object : 子类对象
     * @param fieldName : 父类中的属性名
     * @return 父类中的属性对象
     */

    private static Field getDeclaredField(Object object, String fieldName) {
        for (Class<?> clazz = object.getClass(); clazz != Object.class; clazz = clazz
                .getSuperclass()) {
            try {
                return clazz.getDeclaredField(fieldName);
            } catch (Exception e) {
                // 这里甚么都不要做！并且这里的异常必须这样写，不能抛出去。
                // 如果这里的异常打印或者往外抛，则就不会执行clazz =
                // clazz.getSuperclass(),最后就不会进入到父类中了
            }
        }
        return null;
    }

}
