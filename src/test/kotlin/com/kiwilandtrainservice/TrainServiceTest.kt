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

    class TrainService(route: String) {
        private val stations: Set<Station>

        init {
            stations = if (route.isEmpty()) {
                emptySet()
            } else {
                val source = route[0].toString()
                val destination = route[1].toString()
                setOf(Station(source), Station(destination))
            }
        }

        fun stations() = stations
    }
}

data class Station(val name: String)