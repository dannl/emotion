package factory.emotion.gifreader;

import java.io.IOException;

/**
 * Created by DanLiu
 * Nov 13 2015
 */
public interface InputWrapper {

    long position() throws IOException;

    void position(final long positon) throws IOException;

    void reset() throws IOException;

    void get(final byte[] buffer) throws  IOException;

    void get(byte[] buffer, int position, int count) throws IOException;

    int get() throws  IOException;

    int getShort() throws  IOException;

    int remaining();

    int limit();
}
