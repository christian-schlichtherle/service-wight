/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.service.wight.it

import java.util._

import global.namespace.service.wight.function._
import global.namespace.service.wight.it.ServiceLocatorSpec._
import global.namespace.service.wight.{CompositeProvider, ServiceLocator}
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
          locator.provider[String, UnlocatableProvider]
        }
      }

      "not report a service configuration error if it can't locate a decorator" in {
        val provider = locator.provider[String, Provider[String], UnlocatableMapping]
        provider.get should not be null
      }
    }

    "asked to create a provider" should {
      lazy val provider = locator.provider[String, Provider[String], Mapping[String]]

      "consistently reproduce the expected product" in {
        provider.get shouldBe Expected
        provider.get shouldBe Expected
      }
    }
  }
}

object ServiceLocatorSpec {

  val Expected  = "Hello Christian! How do you do?"

  final class LocatorSugar {

    private[this] val locator = new ServiceLocator()

    def provider[P, PP <: Provider[P] : ClassTag]: CompositeProvider[P, PP, _ <: Mapping[P]] =
      locator.provider(runtimeClassOf[PP])

    def provider[P, PP <: Provider[P] : ClassTag, MP <: Mapping[P] : ClassTag]: CompositeProvider[P, PP, MP] =
      locator.provider(runtimeClassOf[PP], runtimeClassOf[MP])

    private def runtimeClassOf[A](implicit tag: ClassTag[A]): Class[A] = {
      require(tag != classTag[Nothing], "Missing type parameter.")
      tag.runtimeClass.asInstanceOf[Class[A]]
    }
  }
}
