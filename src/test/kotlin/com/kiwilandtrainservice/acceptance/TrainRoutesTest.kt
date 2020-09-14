package com.kiwilandtrainservice.acceptance

import com.kiwilandtrainservice.TrainService
import com.kiwilandtrainservice.testing.*
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.natpryce.hamkrest.has
import com.natpryce.hamkrest.throws
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import kotlin.test.junit5.JUnit5Asserter.fail

class TrainRoutesTest {
    private val trainService = TrainService("AB5, BC4, CD8, DC8, DE6, AD5, CE2, EB3, AE7")

    @Test
    fun `The distance of the route A-B-C`() {
        val result = trainService.totalDistanceOfRoute(A, B, C)
        assertThat(result, equalTo(9))
    }

    @Test
    fun `The distance of the route A-D`() {
        val result = trainService.totalDistanceOfRoute(A, D)
        assertThat(result, equalTo(5))
    }

    @Test
    fun `The distance of the route A-D-C`() {
        val result = trainService.totalDistanceOfRoute(A, D, C)
        assertThat(result, equalTo(13))
    }

    @Test
    fun `The distance of the route A-E-B-C-D`() {
        val result = trainService.totalDistanceOfRoute(A, E, B, C, D)
        assertThat(result, equalTo(22))
    }

    @Test
    fun `The distance of the route A-E-D`() {
        assertThat(
            { trainService.totalDistanceOfRoute(A, E, D) },
            throws(has(IllegalStateException::message, equalTo("NO SUCH ROUTE")))
        )
    }

    @Test
    fun `The number of trips starting at C and ending at C with a maximum of 3 stops`() {
        val result = trainService.findRoutesWithMaxStops(C, C, maxStops = 3)
        assertThat(
            result, hasTheSameElementsAs(
                listOf(
                    listOf(C, D, C),
                    listOf(C, E, B, C)
                )
            )
        )
    }

    @Disabled
    @Test
    fun `The number of trips starting at A and ending at C with exactly 4 stops`() {
        fail("Non implemented yet")
    }

    @Disabled
    @Test
    fun `The length of the shortest route (in terms of distance to travel) from A to C`() {
        fail("Non implemented yet")
    }

    @Disabled
    @Test
    fun `The length of the shortest route (in terms of distance to travel) from B to B`() {
        fail("Non implemented yet")
    }

    @Disabled
    @Test
    fun `The number of different routes from C to C with a distance of less than 30`() {
        fail("Non implemented yet")
    }
}
