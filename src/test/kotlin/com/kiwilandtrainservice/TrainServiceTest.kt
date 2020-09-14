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
                        Route(Station(source), Station(destination))
                    }.toSet()
            }
        }

        fun stations() = routes.flatMap { route -> listOf(route.source, route.destination) }.toSet()

        fun findDirectRoute(source: Station, destination: Station): Route? {
            return routes
                .filter { it.source == source }
                .find { it.destination == destination }
        }
    }
}

data class Route(val source: Station, val destination: Station)

data class Station(val name: String)