/*******************************************************************************
 *
 *    Copyright (c) Niub Info Tech Co. Ltd
 *
 *    Snappy
 *
 *    BitUtil
 *
 *    @author: chzhong
 *    @since:  2010-6-9
 *    @version: 1.0
 *
 ******************************************************************************/

package la.niub.util.utils;

import java.util.Arrays;

/**
 * 表示操作字节的工具类。
 * @author chzhong
 *
 */
public final class BitUtil {

    /**
     * The Byte Ordering Mark of Unicode encoding for Windows-based files.
     */
    private static final byte[] UNICODE_BOE = new byte[] { (byte) 0xFF, (byte) 0xFE };
    /**
     * The Byte Ordering Mark of UTF-8 encoding for Windows-based files.
     */
    private static final byte[] UTF8_BOE = new byte[] { (byte) 0xEF, (byte) 0xBB, (byte) 0xBF };

    private static int[] byte_masks = new int[] { 0xFF, 0x7F, 0x3F, 0x1F, 0x0F, 0x07, 0x03, 0x01};
    private static int[] short_masks = new int[]{ 0xFFFF, 0x7FFF, 0x3FFF, 0x1FFF, 0x0FFF, 0x07FF, 0x03FF, 0x01FF,
        0x00FF, 0x007F, 0x003F, 0x001F, 0x000F, 0x0007, 0x0003, 0x0001 };


    private BitUtil() {}

    /**
     * 将给定的值转换为字节数组。
     * @param value 要转换的值。
     * @return value 在内存中的字节数组表示。
     */
    public static final byte[] getBytes(boolean value) {
        return new byte[]{ (byte) (value ? 1 : 0) };
    }

    /**
     * 将给定的值转换为字节数组。
     * @param value 要转换的值。
     * @return value 在内存中的字节数组表示。
     */
    public static final byte[] getBytes(byte value) {
        return new byte[]{ value };
    }


    /**
     * 将给定的值转换为字节数组。
     * @param value 要转换的值。
     * @return value 在内存中的字节数组表示。
     */
    public static final byte[] getBytes(char value) {
        return new byte[]{ (byte) (value & 0xFF), (byte) (value >>> 8) };
    }


    /**
     * 将给定的值转换为字节数组。
     * @param value 要转换的值。
     * @return value 在内存中的字节数组表示。
     */
    public static final byte[] getBytes(short value) {
        return new byte[]{ (byte) (value & 0xFF), (byte) (value >>> 8) };
    }

    /**
     * 将给定的值转换为字节数组。
     * @param value 要转换的值。
     * @return value 在内存中的字节数组表示。
     */
    public final static byte[] getBytes(int value) {
        return new byte[]{ (byte) (value & 0xFF), (byte) (value >>> 8), (byte) (value >>> 16), (byte) (value >>> 24) };
    }

    /**
     * 将给定的值转换为字节数组。
     * @param value 要转换的值。
     * @return value 在内存中的字节数组表示。
     */
    public static final byte[] getBytes(long value) {
        return new byte[]{ (byte) (value & 0xFF), (byte) (value >>> 8), (byte) (value >>> 16), (byte) (value >>> 24),
                (byte) (value >>> 32), (byte) (value >>> 40), (byte) (value >>> 48), (byte) (value >>> 56)};
    }


    /**
     * 将给定的字节数组转换为布尔值。
     * @param data 要转换的数据。
     * @param index 要开始转换的索引。
     * @return data 在 index 处所表示的布尔值。
     */
    public static final boolean toBoolean(byte[] data, int index) {
        return data[index] != 0;
    }

    /**
     * 将给定的字节数组转换为布尔值。
     * @param data 要转换的数据。
     * @return data 在 0 索引处所表示的布尔值。
     */
    public static final boolean toBoolean(byte[] data) {
        return toBoolean(data, 0);
    }

    /**
     * 将给定的字节数组转换为 byte。
     * @param data 要转换的数据。
     * @param index 要开始转换的索引。
     * @return data 在 index 处所表示的 byte。
     */
    public static final byte toByte(byte[] data, int index) {
        return data[index];
    }

    /**
     * 将给定的字节数组转换为 byte。
     * @param data 要转换的数据。
     * @return data 在 0 索引处所表示的 byte。
     */
    public static final byte toByte(byte[] data) {
        return toByte(data, 0);
    }

    /**
     * 将给定的字节数组转换为 char。
     * @param data 要转换的数据。
     * @param index 要开始转换的索引。
     * @return data 在 index 处所表示的 char。
     */
    public static final char toChar(byte[] data, int index) {
        return (char)(data[index] | data[index + 1]);
    }

    /**
     * 将给定的字节数组转换为 char。
     * @param data 要转换的数据。
     * @return data 在 0 索引处所表示的 char。
     */
    public static final char toChar(byte[] data) {
        return toChar(data, 0);
    }

    /**
     * 将给定的字节数组转换为 short。
     * @param data 要转换的数据。
     * @param index 要开始转换的索引。
     * @return data 在 index 处所表示的 short。
     */
    public static final short toShort(byte[] data, int index) {
        return (short)(data[index] | data[index + 1]);
    }

    /**
     * 将给定的字节数组转换为 short。
     * @param data 要转换的数据。
     * @return data 在 0 索引处所表示的 short。
     */
    public static final short toShort(byte[] data) {
        return toShort(data, 0);
    }


    /**
     * 将给定的字节数组转换为 int。
     * @param data 要转换的数据。
     * @param index 要开始转换的索引。
     * @return data 在 index 处所表示的 int。
     */
    public static final int toInt(byte[] data, int index) {
        return (data[index] | data[index + 1] | data[index + 2] | data[index + 3]);
    }

    /**
     * 将给定的字节数组转换为 int。
     * @param data 要转换的数据。
     * @return data 在 0 索引处所表示的 int。
     */
    public static final int toInt(byte[] data) {
        return toInt(data, 0);
    }


    /**
     * 将给定的字节数组转换为 long。
     * @param data 要转换的数据。
     * @param index 要开始转换的索引。
     * @return data 在 index 处所表示的 long。
     */
    public static final long toLong(byte[] data, int index) {
        return ((long)data[index] | data[index + 1] | data[index + 2] | data[index + 3]);
    }

    /**
     * 将给定的字节数组转换为 long。
     * @param data 要转换的数据。
     * @return data 在 0 索引处所表示的 long。
     */
    public static final long toLong(byte[] data) {
        return toLong(data, 0);
    }

    /**
     * 判断一个 BOE 头是否为 UTF-8 的头。
     * @param boe Bytes of Encoding 文件头。
     * @return 如果 BOE 的内容是表示 UTF-8（前 3 个字节为 EF BB BF），则返回 true；否则返回 false。
     */
    public static boolean boeIsUtf8(byte[] boe) {
        if (null == boe || boe.length < 3) {
            return false;
        }
        return Arrays.equals(UTF8_BOE, boe);
    }

    /**
     * 判断一个 BOE 头是否为 Unicode 的头。
     * @param boe Bytes of Encoding 文件头。
     * @return 如果 BOE 的内容是表示 Unicode（前 2 个字节为 FF FE），则返回 true；否则返回 false。
     */
    public static boolean boeIsUnicode(byte[] boe) {
        if (null == boe || boe.length < 2) {
            return false;
        }
        return Arrays.equals(UNICODE_BOE, boe);
    }

    /**
     * Perform a true unsigned right shift on a {@link byte}.
     * @param value The value to shift.
     * @param offset The number of bits to shift.
     * @return Unsigned right shift <i>offset</i> bits on the given <i>value</i>.
     * <p>If offset < 0, perform left shift of <i>-offset</i> bits; if offset >=8, return 0.</p>
     */
    public static final byte unsignedRightShift(final byte value, final int offset) {
        if (offset < 0) {
            return (byte) (value << -offset);
        } else if(offset >= 8) {
            return 0;
        }
        byte result;
        if (value > 0) {
            result = (byte) (value >> offset);
        } else {
            result = (byte) ((value >> offset) & byte_masks [offset]);
        }
        return result;
    }

    /**
     * Perform a true unsigned right shift on a {@link short}.
     * @param value The value to shift.
     * @param offset The number of bits to shift.
     * @return Unsigned right shift <i>offset</i> bits on the given <i>value</i>.
     * <p>If offset < 0, perform left shift of <i>-offset</i> bits; if offset >=16, return 0.</p>
     */
    public static final short unsignedRightShift(final short value, final int offset) {
        if (offset < 0) {
            return (short) (value << -offset);
        } else if(offset >= 16) {
            return 0;
        }
        short result;
        if (value > 0) {
            result = (short) (value >> offset);
        } else {
            result = (short) ((value >> offset) & short_masks [offset]);
        }
        return result;
    }

    private static final int BYTES_PER_LINE = 16;

    public static String debug(byte[] data) {
        final int size = data.length;
        final int rows = (int) Math.ceil((double) size / BYTES_PER_LINE);
        final StringBuffer sb = new StringBuffer(rows * 80);
        int pos = 0;
        for (int i = 0; i < rows; i++) {
            sb.append(String.format("%08x", pos));
            sb.append("h: ");
            for (int j = 0; j < BYTES_PER_LINE; j++) {
                if (j + pos < data.length) {
                    sb.append(String.format("%02X", data[pos + j]));
                } else {
                    sb.append("  ");
                }
                sb.append(" ");
            }
            sb.append("; ");
            String content = StringUtil.newUtf8String(data, pos, BYTES_PER_LINE);

            final char[] c = content.toCharArray();
            for (final char aC : c) {
                if (Character.isISOControl(aC)) {
                    sb.append('.');
                } else {
                    sb.append(aC);
                }
            }
            sb.append("\n");
            pos += 16;
        }
        return sb.toString();
    }

}
