/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.service.wight

import java.net.URL
import java.util
import java.util.Collections

import org.scalatest.Matchers._
import org.scalatest.WordSpec
import org.scalatest.prop.PropertyChecks._

import scala.collection.JavaConverters._

/** @author Christian Schlichtherle */
class UnifiedClassLoaderSpec extends WordSpec {

  abstract class TestClassLoader extends ClassLoader {

    final override def getResources(name: String): util.Enumeration[URL] = {
      Collections.enumeration(Collections.singleton(getResource(name)))
    }
  }

  val file: TestClassLoader = new TestClassLoader {

    override def getResource(name: String) = new URL("file:/" + name)
    override def loadClass(name: String, resolve: Boolean): Class[Integer] = classOf[java.lang.Integer]
    override def toString = "class loader for file scheme"
  }

  val http: TestClassLoader = new TestClassLoader {

    override def getResource(name: String) = new URL("http:/" + name)
    override def loadClass(name: String, resolve: Boolean): Class[String] = classOf[java.lang.String]
    override def toString = "class loader for http scheme"
  }

  "A unified class loader" when {
    "resolving two class loaders" should {
      "always return the child class loader" in {
        val parent = new ClassLoader { }
        val child = new ClassLoader(parent) { }
        UnifiedClassLoader resolve (parent, child) shouldBe theSameInstanceAs(child)
        UnifiedClassLoader resolve (child, parent) shouldBe theSameInstanceAs(child)
      }

      "otherwise return a new unified class loader upon each call" in {
        val table = Table(
          ("primary", "secondary"),
          (file, http),
          (http, file)
        )
        forAll (table) { (primary, secondary) =>
          val loader1 = UnifiedClassLoader resolve (primary, secondary)
          loader1.isInstanceOf[UnifiedClassLoader] should be (true)
          val loader2 = UnifiedClassLoader resolve (primary, secondary)
          loader2.isInstanceOf[UnifiedClassLoader] should be (true)
          loader2 should not be theSameInstanceAs (loader1)
        }
      }
    }

    "loading resources" should {
      "return the URL provided by the primary class loader" in {
        val table = Table(
          ("primary", "secondary", "resource"),
          (file, http, "foo"),
          (http, file, "bar")
        )
        forAll (table) { (primary, secondary, resource) =>
          val loader = UnifiedClassLoader resolve (primary, secondary)
          loader getResource resource should equal (primary getResource resource)
        }
      }

      "return the URLs provided by both class loaders in order" in {
        val table = Table(
          ("primary", "secondary", "resource"),
          (file, http, "foo"),
          (http, file, "bar")
        )
        forAll (table) { (primary, secondary, resource) =>
          val expected = Seq(primary.getResource(resource),
                             secondary.getResource(resource))
          val loader = UnifiedClassLoader resolve (primary, secondary)
          val result = loader.getResources(resource).asScala.toSeq
          result should equal (expected)
        }
      }

      "return any class loaded by the primary class loader" in {
        val table = Table(
          ("primary", "secondary", "clazz"),
          (file, http, classOf[java.lang.Integer]),
          (http, file, classOf[java.lang.String])
        )
        forAll (table) { (primary, secondary, clazz) =>
          val loader = UnifiedClassLoader resolve (primary, secondary)
          loader loadClass "this.is.an.unknown.ClassName" should
          be theSameInstanceAs clazz
        }
      }
    }
  }
}
