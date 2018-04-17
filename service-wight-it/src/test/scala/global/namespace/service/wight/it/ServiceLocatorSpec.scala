/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.service.wight.it

import java.util._

import global.namespace.service.wight.function.{Container, Factory}
import global.namespace.service.wight.it.ServiceLocatorSpec._
import global.namespace.service.wight.{LocatableDecorator, LocatableFactory, ServiceLocator}
import org.scalatest.Matchers._
import org.scalatest._

import scala.reflect.{ClassTag, classTag}

/** @author Christian Schlichtherle */
class ServiceLocatorSpec extends WordSpec {

  val locator = new LocatorSugar

  "A locator" when {
    "asked to create a container" should {
      "report a service configuration error if it can't locate a factory" in {
        intercept[ServiceConfigurationError] {
          locator.container[String, UnlocatableFactory]
        }
      }

      "not report a service configuration error if it can't locate a decorator" in {
        val container = locator.container[String, LocatableFactory[String], UnlocatableDecorator]
        container.get should not be null
      }
    }

    "asked to create a container" should {
      lazy val container = locator.container[String, LocatableFactory[String], LocatableDecorator[String]]

      "always reproduce the expected product" in {
        container.get shouldBe Expected
        container.get shouldBe Expected
      }

      "provide the same product" in {
        val p1 = container.get
        val p2 = container.get
        p1 shouldBe theSameInstanceAs(p2)
      }
    }

    "asked to create a factory" should {
      lazy val factory = locator.factory[String, LocatableFactory[String], LocatableDecorator[String]]

      "always reproduce the expected product" in {
        factory.get shouldBe Expected
        factory.get shouldBe Expected
      }

      "provide an equal, but not same product" in {
        val p1 = factory.get
        val p2 = factory.get
        p1 shouldBe p2
        p1 should not be theSameInstanceAs(p2)
      }
    }
  }
}

object ServiceLocatorSpec {

  val Expected  = "Hello Christian! How do you do?"

  final class LocatorSugar {

    private[this] val locator = new ServiceLocator(classOf[ServiceLocatorSpec])

    def container[P, F <: LocatableFactory[P] : ClassTag]: Container[P] =
      locator container runtimeClassOf[F]

    def container[P, F <: LocatableFactory[P] : ClassTag, D <: LocatableDecorator[P] : ClassTag]: Container[P] =
      locator container (runtimeClassOf[F], runtimeClassOf[D])

    def factory[P, F <: LocatableFactory[P] : ClassTag]: Factory[P] =
      locator factory runtimeClassOf[F]

    def factory[P, F <: LocatableFactory[P] : ClassTag, D <: LocatableDecorator[P] : ClassTag]: Factory[P] =
      locator factory (runtimeClassOf[F], runtimeClassOf[D])

    private def runtimeClassOf[A](implicit tag: ClassTag[A]): Class[A] = {
      require(tag != classTag[Nothing], "Missing type parameter.")
      tag.runtimeClass.asInstanceOf[Class[A]]
    }
  }
}