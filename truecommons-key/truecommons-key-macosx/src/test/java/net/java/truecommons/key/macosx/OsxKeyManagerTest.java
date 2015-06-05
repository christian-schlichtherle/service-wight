/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package net.java.truecommons.key.macosx;

import net.java.truecommons.key.spec.common.AesKeyStrength;
import net.java.truecommons.key.spec.common.AesPbeParameters;
import net.java.truecommons.shed.Option;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;

import static net.java.truecommons.key.macosx.OsxKeyManager.deserialize;
import static net.java.truecommons.key.macosx.OsxKeyManager.serialize;
import static net.java.truecommons.shed.Buffers.string;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * @author Christian Schlichtherle
 */
public class OsxKeyManagerTest {

    private static final Logger
            logger = LoggerFactory.getLogger(OsxKeyManagerTest.class);

    @Test
    public void testXmlSerialization() {
        final AesPbeParameters original = new AesPbeParameters();
        original.setChangeRequested(true);
        original.setKeyStrength(AesKeyStrength.BITS_256);
        original.setPassword("föo".toCharArray());
        final ByteBuffer xml = serialize(Option.apply(original)).get(); // must not serialize password!

        logger.trace("Serialized object to {} bytes.", xml.remaining());
        logger.trace("Serialized form:\n{}", string(xml));

        final AesPbeParameters clone = (AesPbeParameters) deserialize(Option.apply(xml)).get();
        assertNull(clone.getPassword());
        original.setPassword(null);
        assertEquals(original, clone);
    }
}
