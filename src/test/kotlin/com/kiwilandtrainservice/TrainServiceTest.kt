package com.kiwilandtrainservice

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
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

        assertThat(trainService.stations(), equalTo(setOf(Station("A"), Station("B"))))
    }

    @Test
    fun `build a train service with three stations`() {
        val trainService = TrainService("AB1, BC2")

        assertThat(trainService.stations(), equalTo(setOf(Station("A"), Station("B"), Station("C"))))
    }

    class TrainService(routes: String) {
        private val stations: Set<Station>

        init {
            stations = if (routes.isEmpty()) {
                emptySet()
            } else {
                routes.split(",")
                    .map { it.trim() }
                    .flatMap { rawRoute ->
                        val source = rawRoute[0].toString()
                        val destination = rawRoute[1].toString()
                        setOf(Station(source), Station(destination))
                    }.toSet()
            }
        }

        fun stations() = stations
    }
}

data class Station(val name: String)