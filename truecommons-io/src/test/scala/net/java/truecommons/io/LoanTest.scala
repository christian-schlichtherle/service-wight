/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package net.java.truecommons.io

import java.io._
import org.junit.runner.RunWith
import org.scalatest.WordSpec
import org.scalatest.junit.JUnitRunner
import org.scalatest.Matchers._
import org.scalatest.mock.MockitoSugar.mock
import org.mockito.Mockito._

/** @author Christian Schlichtherle */
@RunWith(classOf[JUnitRunner])
class LoanTest extends WordSpec {
  import Loan._

  "A Loan" should {
    "call AutoCloseable.close()" when {
      "returning" in {
        val resource = mock[AutoCloseable]
        Loan(resource) { _ => }
        verify(resource) close ()
      }

      "throwing a Throwable" in {
        val ex = new Exception
        val resource = mock[AutoCloseable]
        intercept[Exception] (loan (resource) to { _ => throw ex }) should be theSameInstanceAs ex
        verify(resource) close ()
      }
    }

    "suppress any Throwable thrown by AutoCloseable.close()" in {
      val ex = new Exception
      val resource = mock[AutoCloseable]
      doThrow(ex) when resource close ()
      intercept[Exception] (loan (resource) to { _ => throw new Exception }).getSuppressed should equal (Array(ex))
    }

    "support the documented concise use case" in {
      val out = new ByteArrayOutputStream
      Loan(new PrintWriter(out)) { w: PrintWriter => w.println("Hello world!") }
    }

    "support the documented readable use case" in {
      import Loan._
      val out = new ByteArrayOutputStream
      loan (new PrintWriter(out)) to { w: PrintWriter => w.println("Hello world!") }
    }
  }
}
