package com.kiwilandtrainservice.testing

import com.natpryce.hamkrest.MatchResult
import com.natpryce.hamkrest.Matcher

fun <T> hasTheSameElementsAs(expected: List<T>): Matcher<List<T>> =
    object : Matcher<List<T>> {
        override val description: String = "has the same elements as $expected"

        override fun invoke(actual: List<T>): MatchResult {
            val remainingActual = expected.removeFrom(actual)
            val remainingExpected = actual.removeFrom(expected)

            return if (remainingActual.isEmpty() && remainingExpected.isEmpty())
                MatchResult.Match
            else {
                val mismatchReasons = listOfNotNull(
                    if (remainingActual.isNotEmpty()) "found elements that were not expected of $remainingActual" else null,
                    if (remainingExpected.isNotEmpty()) "did not find expected elements $remainingExpected" else null
                ).joinToString(" and ")

                MatchResult.Mismatch("is $actual, $mismatchReasons")
            }
        }
    }

fun <T> Collection<T>.removeFrom(other: Collection<T>): List<T> =
    fold(
        mutableListOf<T>().apply { addAll(other) }
    ) { remainder, next -> remainder.apply { remove(next) } }
        .toList()