/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.service.wight.it

import global.namespace.service.wight.core.{CompositeProvider, ServiceLocator}
import global.namespace.service.wight.it.ServiceLocatorSpec._
import global.namespace.service.wight.it.case1.{UnlocatableServiceFilter, UnlocatableServiceProvider}
import global.namespace.service.wight.it.case2.{Salutation, Subject}
import org.scalatest.matchers.should.Matchers._
import org.scalatest.wordspec.AnyWordSpec

import java.util.Collections.reverse
import java.util.ServiceConfigurationError
import java.util.function.{Supplier, UnaryOperator}
import scala.reflect.{ClassTag, classTag}

/** @author Christian Schlichtherle */
class ServiceLocatorSpec extends AnyWordSpec {

  val locator = new LocatorSugar

  "A locator" when {
    "told to create a composite provider" should {
      "throw a service configuration error if it can't locate a provider" in {
        intercept[ServiceConfigurationError] {
          locator.provider[String, UnlocatableServiceProvider]
        }
      }

      "not throw a service configuration error if it can't locate a mapping" in {
        locator.provider[String, Subject, UnlocatableServiceFilter].get should not be null
      }

      "consistently reproduce the expected service" in {
        val provider = locator.provider[String, Subject, Salutation]
        provider.get shouldBe Expected
        provider.get shouldBe Expected
      }

      "support reversing its findings" in {
        val provider = locator.provider[String, Subject, Salutation]
        val subjects = provider.providers
        reverse(subjects)
        val salutations = provider.filters
        reverse(salutations)
        val updated = new CompositeProvider[String, Subject, Salutation](subjects, salutations)
        updated.get shouldBe ReversedExpected
      }
    }
  }
}

object ServiceLocatorSpec {

  val Expected  = "Hello Christian! How do you do?"
  val ReversedExpected = "Hello World How do you do?!"

  final class LocatorSugar {

    private val locator = new ServiceLocator

    def provider[S, SP <: Supplier[S] : ClassTag]: CompositeProvider[S, SP, _ <: UnaryOperator[S]] =
      locator.provider[S, SP](runtimeClassOf[SP])

    def provider[S, SP <: Supplier[S] : ClassTag, SF <: UnaryOperator[S] : ClassTag]: CompositeProvider[S, SP, SF] =
      locator.provider(runtimeClassOf[SP], runtimeClassOf[SF])

    private def runtimeClassOf[A](implicit tag: ClassTag[A]): Class[A] = {
      require(tag != classTag[Nothing], "Missing type parameter.")
      tag.runtimeClass.asInstanceOf[Class[A]]
    }
  }
}
