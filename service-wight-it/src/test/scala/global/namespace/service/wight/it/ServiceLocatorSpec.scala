/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.service.wight.it

import java.util.Collections.reverse
import java.util.ServiceConfigurationError
import java.util.function.{Supplier, UnaryOperator}

import global.namespace.service.wight.core.{CompositeProvider, ServiceLocator}
import global.namespace.service.wight.it.ServiceLocatorSpec._
import global.namespace.service.wight.it.case1.{UnlocatableProvider, UnlocatableTransformation}
import global.namespace.service.wight.it.case2.{Salutation, Subject}
import org.scalatest.Matchers._
import org.scalatest._

import scala.reflect.{ClassTag, classTag}

/** @author Christian Schlichtherle */
class ServiceLocatorSpec extends WordSpec {

  val locator = new LocatorSugar

  "A locator" when {
    "told to create a composite provider" should {
      "throw a service configuration error if it can't locate a provider" in {
        intercept[ServiceConfigurationError] {
          locator.provider[String, UnlocatableProvider]
        }
      }

      "not throw a service configuration error if it can't locate a mapping" in {
        locator.provider[String, Subject, UnlocatableTransformation].get should not be null
      }

      "consistently reproduce the expected product" in {
        val provider = locator.provider[String, Subject, Salutation]
        provider.get shouldBe Expected
        provider.get shouldBe Expected
      }

      "support reversing its findings" in {
        val provider = locator.provider[String, Subject, Salutation]
        val subjects = provider.providers
        reverse(subjects)
        val salutations = provider.transformations
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

    def provider[P, PP <: Supplier[P] : ClassTag]: CompositeProvider[P, PP, _ <: UnaryOperator[P]] =
      locator.provider(runtimeClassOf[PP])

    def provider[P, PP <: Supplier[P] : ClassTag, MP <: UnaryOperator[P] : ClassTag]: CompositeProvider[P, PP, MP] =
      locator.provider(runtimeClassOf[PP], runtimeClassOf[MP])

    private def runtimeClassOf[A](implicit tag: ClassTag[A]): Class[A] = {
      require(tag != classTag[Nothing], "Missing type parameter.")
      tag.runtimeClass.asInstanceOf[Class[A]]
    }
  }
}
