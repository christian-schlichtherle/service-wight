/*
 * Copyright (C) 2012-2013 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package net.java.truecommons.io

import java.nio._
import java.nio.file._

/**
  * @author Christian Schlichtherle
  */
class ByteBufferReadOnlyChannelIT extends ReadOnlyChannelITSuite {
  override def newChannel(path: Path) =
    new ReadOnlyChannel(new ByteBufferChannel(ByteBuffer.wrap(Files.readAllBytes(path))))
}
