package factory.emotion.gifreader;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Created by DanLiu
 * Nov 13 2015
 */
public class ByteBufferWrapper implements InputWrapper {

    private ByteBuffer mByteBuffer;

    public ByteBufferWrapper(final byte[] bytes) {
        if (bytes == null) {
            throw new NullPointerException("bad args!");
        }
        final ByteBuffer buffer = ByteBuffer.wrap(bytes);
        mByteBuffer = buffer;
        mByteBuffer.order(ByteOrder.LITTLE_ENDIAN);
    }

    public ByteBufferWrapper(final ByteBuffer buffer) {
        if (buffer == null) {
            throw new NullPointerException("bad args!");
        }
        mByteBuffer = buffer;
        mByteBuffer.order(ByteOrder.LITTLE_ENDIAN);
    }

    @Override
    public long position() throws IOException {
        return mByteBuffer.position();
    }

    @Override
    public void position(long positon) throws IOException {
        mByteBuffer.position((int) positon);
    }

    @Override
    public void reset() throws IOException {
        mByteBuffer.position(0);
    }

    @Override
    public void get(byte[] buffer) throws IOException {
        mByteBuffer.get(buffer);
    }

    @Override
    public void get(byte[] buffer, int destOffset, int count) throws IOException {
        mByteBuffer.get(buffer, destOffset, count);
    }

    @Override
    public int get() throws IOException {
        return mByteBuffer.get();
    }


    @Override
    public int getShort() throws IOException {
        return mByteBuffer.getShort();
    }

    @Override
    public int remaining() {
        return mByteBuffer.remaining();
    }

    @Override
    public int limit() {
        return mByteBuffer.limit();
    }
}
