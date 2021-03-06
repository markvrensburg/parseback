/*
 * Copyright 2017 Daniel Spiewak
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package parseback

import org.specs2.matcher.Matcher
import org.specs2.mutable._
import org.specs2.specification.SpecificationFeatures

trait ParsebackSpec extends Spec with SpecificationFeatures {

  final def parseOk[A](input: String)(results: A*): Matcher[Parser[A]] = { p: Parser[A] =>
    val maybeResults = p(LineStream(input)).value

    maybeResults match {
      case \/-(actual) =>
        val permuted = actual flatMap { a => results map { b => (a, b) } }

        val compared = permuted filter {
          case (a, b) => a == b
        }

        (results.length == actual.length && compared.length == results.length,
          s"accepted '$input' with results $actual",
          s"accepted '$input' but produced results $actual, expected $results")

      case -\/(err) =>
        (false, "", s"failed to parse '$input' with error $err")
    }
  }

  final def failToParse(input: String)(errors: ParseError*): Matcher[Parser[_]] = { p: Parser[_] =>
    val maybeResults = p(LineStream(input)).value

    maybeResults match {
      case \/-(results) =>
        (false, "", s"failed to reject '$input' with results $results")

      case -\/(actual) =>
        val permuted = actual flatMap { a => errors map { b => (a, b) } }

        val compared = permuted filter {
          case (a, b) => a == b
        }

        (errors.length == actual.length && compared.length == errors.length,
          s"rejected '$input' with errors $actual",
          s"rejected '$input' with errors $actual, expected $errors")
    }
  }
}
