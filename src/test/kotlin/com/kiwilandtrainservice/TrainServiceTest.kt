package com.kiwilandtrainservice

import com.natpryce.hamkrest.absent
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.natpryce.hamkrest.present
import org.junit.jupiter.api.Test
import java.util.stream.DoubleStream

class TrainServiceTest {

    @Test
    fun `build an empty train service`() {
        val trainService = TrainService("")

        assertThat(trainService.stations(), equalTo(emptySet()))
    }

    @Test
    fun `build a train service with two stations`() {
        val trainService = TrainService("AB1")

        assertThat(trainService.stations(), equalTo(setOf(Station("A"), Station("B"))))
    }

    @Test
    fun `build a train service with three stations`() {
        val trainService = TrainService("AB1, BC2")

        assertThat(trainService.stations(), equalTo(setOf(Station("A"), Station("B"), Station("C"))))
    }

    @Test
    fun `find direct route between two stations`() {
        val trainService = TrainService("AB1, BC2")

        assertThat(trainService.findDirectRoute(Station("A"), Station("B")), present())
    }

    @Test
    fun `cannot find direct route between two stations`() {
        val trainService = TrainService("AB1, BC2")

        assertThat(trainService.findDirectRoute(Station("A"), Station("C")), absent())
    }

    @Test
    fun `distance between two connected stations`() {
        val trainService = TrainService("AB1, BC2")

        assertThat(trainService.findDirectRoute(Station("A"), Station("B"))?.distance, equalTo(1))
    }

    @Test
    fun `total distance of a route`() {
        val trainService = TrainService("AB1, BC2")

        assertThat(trainService.totalDistanceOfARoute(Station("A"), Station("B"), Station("C")), equalTo(3))
    }

    class TrainService(routes: String) {
        private val routes: Set<Route>

        init {
            this.routes = if (routes.isEmpty()) {
                emptySet()
            } else {
                routes.split(",")
                    .map { it.trim() }
                    .map { rawRoute ->
                        val source = rawRoute[0].toString()
                        val destination = rawRoute[1].toString()
                        val distance = rawRoute.drop(2).toInt()
                        Route(Station(source), Station(destination), distance)
                    }.toSet()
            }
        }

        fun stations() = routes.flatMap { route -> listOf(route.source, route.destination) }.toSet()

        fun findDirectRoute(source: Station, destination: Station): Route? {
            return routes
                .filter { it.source == source }
                .find { it.destination == destination }
        }

        fun totalDistanceOfARoute(vararg stations: Station): Int {
            return stations.toList()
                .windowed(2)
                .map {
                    val source = it.first()
                    val destination = it.last()
                    findDirectRoute(source, destination)?.distance ?: throw IllegalStateException("NO SUCH ROUTE")
                }.sum()
        }
    }
}

data class Route(val source: Station, val destination: Station, val distance: Int)

data class Station(val name: String)