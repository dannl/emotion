/*******************************************************************************
 *
 *    Copyright (c) Niub Info Tech Co. Ltd
 *
 *    NiubCoreLibrary
 *
 *    ArrayUtils
 *    TODO File description or class description.
 *
 *    @author: dhu
 *    @since:  May 20, 2011
 *    @version: 1.0
 *
 ******************************************************************************/

package la.niub.util.utils;

import android.text.TextUtils;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public final class ArrayUtils {
    private static Object[] EMPTY = new Object[0];
    private static final int CACHE_SIZE = 73;
    private static Object[] sCache = new Object[CACHE_SIZE];

    private ArrayUtils() { /* cannot be instantiated */
    }

    public static int idealByteArraySize(int need) {
        for (int i = 4; i < 32; i++)
            if (need <= (1 << i) - 12)
                return (1 << i) - 12;

        return need;
    }

    public static int idealBooleanArraySize(int need) {
        return idealByteArraySize(need);
    }

    public static int idealShortArraySize(int need) {
        return idealByteArraySize(need * 2) / 2;
    }

    public static int idealCharArraySize(int need) {
        return idealByteArraySize(need * 2) / 2;
    }

    public static int idealIntArraySize(int need) {
        return idealByteArraySize(need * 4) / 4;
    }

    public static int idealFloatArraySize(int need) {
        return idealByteArraySize(need * 4) / 4;
    }

    public static int idealObjectArraySize(int need) {
        return idealByteArraySize(need * 4) / 4;
    }

    public static int idealLongArraySize(int need) {
        return idealByteArraySize(need * 8) / 8;
    }

    /**
     * Checks if the beginnings of two byte arrays are equal.
     * 
     * @param array1 the first byte array
     * @param array2 the second byte array
     * @param length the number of bytes to check
     * @return true if they're equal, false otherwise
     */
    public static boolean equals(byte[] array1, byte[] array2, int length) {
        if (array1 == array2) {
            return true;
        }
        if (array1 == null || array2 == null || array1.length < length || array2.length < length) {
            return false;
        }
        for (int i = 0; i < length; i++) {
            if (array1[i] != array2[i]) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns an empty array of the specified type. The intent is that it will
     * return the same empty array every time to avoid reallocation, although
     * this is not guaranteed.
     */
    @SuppressWarnings("unchecked")
    public static <T> T[] emptyArray(Class<T> kind) {
        if (kind == Object.class) {
            return (T[]) EMPTY;
        }

        int bucket = ((System.identityHashCode(kind) / 8) & 0x7FFFFFFF) % CACHE_SIZE;
        Object cache = sCache[bucket];

        if (cache == null || cache.getClass().getComponentType() != kind) {
            cache = Array.newInstance(kind, 0);
            sCache[bucket] = cache;

            // Log.e("cache", "new empty " + kind.getName() + " at " +
            // bucket);
        }

        return (T[]) cache;
    }

    /**
     * Checks that value is present as at least one of the elements of the
     * array.
     * 
     * @param array the array to check in
     * @param value the value to check for
     * @return true if the value is present in the array
     */
    public static <T> boolean contains(T[] array, T value) {
        if (array != null) {
            for (T element : array) {
                if (element == null) {
                    if (value == null)
                        return true;
                } else {
                    if (value != null && element.equals(value))
                        return true;
                }
            }
        }
        return false;
    }

    /**
     * Determine whether specified array contains a certain integer.
     * 
     * @param array the array to check.
     * @param value the value to compare.
     * @return true if {@code array} contains {@code value}, false otherwise.
     */
    public static final boolean contains(int[] array, int value) {
        if (null == array) {
            return false;
        }
        final int length = array.length;
        for (int i = 0; i < length; ++i) {
            final int element = array[i];
            if (value == element) {
                return true;
            }
        }
        return false;
    }

    public static <E> boolean isCollectionEquals(Collection<E> collection1,
            Collection<E> collection2) {
        boolean equals = true;
        if (collection1 != null && null == collection2) {
            equals = false;
        } else if (collection2 != null && null == collection1) {
            equals = false;
        } else if (collection1 != null && collection2 != null) {
            int size1 = collection1.size();
            int size2 = collection2.size();
            if (size1 != size2) {
                equals = false;
            } else {
                Iterator<E> iterator1 = collection1.iterator();
                Iterator<E> iterator2 = collection2.iterator();
                while (iterator1.hasNext() && iterator2.hasNext()) {
                    if (!iterator1.next().equals(iterator2.next())) {
                        equals = false;
                        break;
                    }
                }
            }
        }
        return equals;
    }

    /**
     * Remove all null or empty strings in an array.
     * 
     * @param array the array to normalize. If you pass {@code null}, an empty
     *            array will be returned.
     */
    public static final String[] normalize(String[] array) {
        if (null == array) {
            return new String[0];
        }
        final int length = array.length;
        final ArrayList<String> list = new ArrayList<String>();
        for (int i = 0; i < length; i++) {
            final String item = array[i];
            if (!TextUtils.isEmpty(item) && !TextUtils.isEmpty(item.trim())) {
                list.add(item);
            }
        }
        String[] normalizedItems = new String[list.size()];
        normalizedItems = list.toArray(normalizedItems);
        return normalizedItems;
    }

    /**
     * Determine the position of an element in an array.
     * 
     * @param <T> the type of the element.
     * @param array the array.
     * @param s the element.
     * @return the index of {@code s} in {@code array}, or -1 if not found.
     */
    public static <T> int indexOf(T[] array, T s) {
        if (null == array) {
            return -1;
        }
        for (int i = 0; i < array.length; i++) {
            if (array[i].equals(s)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Determine the position of an element in an array.
     * 
     * @param <T> the type of the element.
     * @param array the array.
     * @param s the element.
     * @return the index of {@code s} in {@code array}, or -1 if not found.
     */
    public static int indexOf(int[] array, int s) {
        if (null == array) {
            return -1;
        }
        for (int i = 0; i < array.length; i++) {
            if (array[i] == s) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Determine the position of an element in an array.
     * 
     * @param <T> the type of the element.
     * @param array the array.
     * @param s the element.
     * @return the index of {@code s} in {@code array}, or -1 if not found.
     */
    public static long indexOf(long[] array, long s) {
        if (null == array) {
            return -1;
        }
        for (int i = 0; i < array.length; i++) {
            if (array[i] == s) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Returns a {@code List} of the objects in the specified array.
     * 
     * @param array the array.
     * @return a {@code List} of the elements of the specified array.
     */
    public static <T> List<T> asList(T[] array) {
        List<T> list = new ArrayList<T>();
        for (T object : array) {
            list.add(object);
        }
        return list;
    }

}
