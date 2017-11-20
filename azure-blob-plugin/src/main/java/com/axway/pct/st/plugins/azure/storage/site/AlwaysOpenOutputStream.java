package com.axway.pct.st.plugins.azure.storage.site;
/**
 * 
 */


import java.io.IOException;
import java.io.InputStream;

/**
 * @author cmanda
 *
 */
public class AlwaysOpenOutputStream extends InputStream {

	private final InputStream stream;

    public AlwaysOpenOutputStream(InputStream stream) {
        this.stream = stream;
    }
    
    @Override
    public void close() {
        //No closing for this stream - to avoid Streaming errors
    }

    @Override
    public int read() throws IOException {
        return stream.read();
    }

    @Override
    public int read(byte[] bytes) throws IOException {
        return stream.read(bytes);
    }

    @Override
    public int read(byte[] bytes, int start, int end) throws IOException {
        return stream.read(bytes, start, end);
    }

    @Override
    public long skip(long l) throws IOException {
        return stream.skip(l);
    }

    @Override
    public int available() throws IOException {
        return stream.available();
    }

    @Override
    public synchronized void mark(int i) {
        stream.mark(i);
    }

    @Override
    public synchronized void reset() throws IOException {
        stream.reset();
    }

    @Override
    public boolean markSupported() {
        return stream.markSupported();
    }

}
