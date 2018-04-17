/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.service.wight

import global.namespace.service.wight.LocatableComparatorSpec._
import org.scalacheck.Gen
import org.scalatest.Matchers._
import org.scalatest._
import org.scalatest.prop.PropertyChecks._

import scala.util._

class LocatableComparatorSpec extends WordSpec {

  "A service comparator" when {
    "comparing locatable objects" should {
      "produce ordered results" in {
        val n = 256
        val locatables = Seq.fill(n)(new Test).sorted(LocatableOrdering)
        forAll ((Gen.choose(0, n - 2), "i")) { i: Int =>
          whenever (0 <= i && i < n - 1) {
            locatables(i).getPriority should be <= locatables(i + 1).getPriority
          }
        }
      }
    }
  }
}

private object LocatableComparatorSpec {

  private class Test extends LocatableService {

    override val getPriority: Int = Random.nextInt
  }

  private object LocatableOrdering extends LocatableComparator with Ordering[LocatableService]
}
