/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package net.java.truecommons.io;

import edu.umd.cs.findbugs.annotations.CleanupObligation;
import edu.umd.cs.findbugs.annotations.DischargesObligation;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;
import java.util.Objects;
import javax.annotation.Nullable;
import javax.annotation.WillCloseWhenClosed;
import javax.annotation.concurrent.NotThreadSafe;

/**
 * Adapts a {@link WritableByteChannel} to an output stream.
 *
 * @see    ChannelInputStream
 * @author Christian Schlichtherle
 */
@NotThreadSafe
@CleanupObligation
public class ChannelOutputStream extends OutputStream {

    private final ByteBuffer single = ByteBuffer.allocate(1);

    /** The adapted nullable writable byte channel. */
    protected @Nullable WritableByteChannel channel;

    protected ChannelOutputStream() { }

    public ChannelOutputStream(
            final @WillCloseWhenClosed WritableByteChannel channel) {
        this.channel = Objects.requireNonNull(channel);
    }

    @Override
    public void write(int b) throws IOException {
        write((ByteBuffer) single.put(0, (byte) b).rewind());
    }

    @Override
    public final void write(byte[] b) throws IOException {
        write(ByteBuffer.wrap(b));
    }

    @Override
    public void write(final byte[] b, final int off, final int len)
    throws IOException {
        write(ByteBuffer.wrap(b, off, len));
    }

    @SuppressWarnings("SleepWhileInLoop")
    private void write(final ByteBuffer bb) throws IOException {
        while (bb.hasRemaining()) {
            if (0 == channel.write(bb)) {
                try {
                    Thread.sleep(50);
                } catch (final InterruptedException ex) {
                    Thread.currentThread().interrupt(); // restore
                }
            }
        }
    }

    @Override
    public void flush() throws IOException { }

    @Override
    @DischargesObligation
    public void close() throws IOException { channel.close(); }
}
