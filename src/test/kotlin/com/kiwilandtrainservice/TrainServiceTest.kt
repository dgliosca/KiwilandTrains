package com.kiwilandtrainservice

import com.kiwilandtrainservice.testing.*
import com.natpryce.hamkrest.*
import com.natpryce.hamkrest.assertion.assertThat
import org.junit.jupiter.api.Test

class TrainServiceTest {

    @Test
    fun `build an empty train service`() {
        val trainService = TrainService("")
        assertThat(trainService.stations(), equalTo(emptySet()))
    }

    @Test
    fun `build a train service with two stations`() {
        val trainService = TrainService("AB1")
        assertThat(trainService.stations(), equalTo(setOf(A, B)))
    }

    @Test
    fun `build a train service with three stations`() {
        val trainService = TrainService("AB1, BC2")
        assertThat(trainService.stations(), equalTo(setOf(A, B, C)))
    }

    @Test
    fun `find direct route between two stations`() {
        val trainService = TrainService("AB1, BC2")
        assertThat(trainService.findDirectRoute(A, B), present())
    }

    @Test
    fun `cannot find direct route between two stations`() {
        val trainService = TrainService("AB1, BC2")
        assertThat(trainService.findDirectRoute(A, C), absent())
    }

    @Test
    fun `distance between two connected stations`() {
        val trainService = TrainService("AB1, BC2")
        assertThat(trainService.findDirectRoute(A, B)?.distance, equalTo(1))
    }

    @Test
    fun `total distance of a route`() {
        val trainService = TrainService("AB1, BC2")
        assertThat(trainService.totalDistanceOfRoute(A, B, C), equalTo(3))
    }

    @Test
    fun `cannot calculate total distance of a route that doesn't exist`() {
        val trainService = TrainService("AB1, BC2")
        assertThat(
            { trainService.totalDistanceOfRoute(A, C) },
            throws(has(IllegalStateException::message, equalTo("NO SUCH ROUTE")))
        )
    }

    @Test
    fun `find routes between two stations with maximum N stops`() {
        val trainService = TrainService("AB1, BC2")
        assertThat(
            trainService.findRoutesWithMaxStops(source = A, destination = C, maxStops = 2),
            equalTo(listOf(listOf(A, B, C)))
        )
    }

    @Test
    fun `find routes when source stations is the same as the destination`() {
        val trainService = TrainService("AB1, BC1, CA1")
        assertThat(
            trainService.findRoutesWithMaxStops(source = A, destination = A, maxStops = 3),
            equalTo(listOf(listOf(A, B, C, A)))
        )
    }

    @Test
    fun `cannot find route with max N stops`() {
        val trainService = TrainService("AB1, BC1, CA1")
        assertThat(
            trainService.findRoutesWithMaxStops(source = A, destination = C, maxStops = 1),
            equalTo(listOf())
        )
    }

    @Test
    fun `cannot find route with max N stops when towns are disconnected`() {
        val trainService = TrainService("AB1, CD1")
        assertThat(
            trainService.findRoutesWithMaxStops(source = A, destination = D, maxStops = 1),
            equalTo(listOf())
        )
    }

    @Test
    fun `find route with exactly N stops`() {
        val trainService = TrainService("AB1, BC1, CA1")
        assertThat(
            trainService.findRoutesWithNStops(source = A, destination = C, stops = 2),
            hasTheSameElementsAs(listOf(listOf(A, B, C)))
        )
    }

    @Test
    fun `cannot find routes with exactly N stops`() {
        val trainService = TrainService("AB1, BC1")
        assertThat(
            trainService.findRoutesWithNStops(source = A, destination = C, stops = 1),
            hasTheSameElementsAs(emptyList())
        )
    }

    @Test
    fun `cannot find routes with exactly N stops when source same as destination`() {
        val trainService = TrainService("AB1, BC1, CA1")
        assertThat(
            trainService.findRoutesWithNStops(source = A, destination = A, stops = 2),
            hasTheSameElementsAs(emptyList())
        )
    }

    @Test
    fun `find shortest route`() {
        val trainService = TrainService("AB1, BC1, CD1, DA1, CA1")
        val expected = listOf(A, B, C, A)
        assertThat(trainService.findShortestRoute(source = A, destination = A), equalTo(expected))
        assertThat(trainService.totalDistanceOfRoute(*expected.toTypedArray()), equalTo(3))
    }

    @Test
    fun `different routes with with max distance between two stations`() {
        val trainService = TrainService("AB1, BC1, CD1, DA1, CA1")
        val result = trainService.routesWithMaxDistance(source = A, destination = A, maxDistance = 6)
        assertThat(result, hasTheSameElementsAs(listOf(listOf(A, B, C, D, A), listOf(A, B, C, A))))
    }

    @Test
    fun `different routes with with max distance when there are multiple routes with same length`() {
        val trainService = TrainService("AB1, BC1, CD1, DA1, CA2")
        val result = trainService.routesWithMaxDistance(source = A, destination = A, maxDistance = 5)
        assertThat(result, hasTheSameElementsAs(listOf(listOf(A, B, C, A), listOf(A, B, C, D, A))))
    }
}