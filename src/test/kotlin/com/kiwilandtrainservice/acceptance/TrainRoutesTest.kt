package com.kiwilandtrainservice.acceptance

import com.kiwilandtrainservice.TrainService
import com.kiwilandtrainservice.testing.*
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.natpryce.hamkrest.has
import com.natpryce.hamkrest.throws
import org.junit.jupiter.api.Test

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

    @Test
    fun `The number of trips starting at A and ending at C with exactly 4 stops`() {
        val result = trainService.findRoutesWithNStops(A, C, 4)
        assertThat(
            result, hasTheSameElementsAs(
                listOf(
                    listOf(A, B, C, D, C),
                    listOf(A, D, C, D, C),
                    listOf(A, D, E, B, C)
                )
            )
        )
    }

    @Test
    fun `The length of the shortest route (in terms of distance to travel) from A to C`() {
        val result = trainService.lengthShortestRoute(A, C)
        assertThat(result, equalTo(9))
    }

    @Test
    fun `The length of the shortest route (in terms of distance to travel) from B to B`() {
        val result = trainService.lengthShortestRoute(B, B)
        assertThat(result, equalTo(9))
    }

    @Test
    fun `The number of different routes from C to C with a distance of less than 30`() {
        val result = trainService.routesWithMaxDistance(C, C, maxDistance = 30)
        assertThat(
            result, hasTheSameElementsAs(
                listOf(
                    listOf(C, D, C),
                    listOf(C, E, B, C),
                    listOf(C, E, B, C, D, C),
                    listOf(C, D, C, E, B, C),
                    listOf(C, D, E, B, C),
                    listOf(C, E, B, C, E, B, C),
                    listOf(C, E, B, C, E, B, C, E, B, C)
                )
            )
        )
    }
}
