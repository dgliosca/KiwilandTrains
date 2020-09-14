package com.kiwilandtrainservice

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.junit.jupiter.api.Test

class TrainServiceTest {

    @Test
    fun `build an empty train service`() {
        val trainService = TrainService()

        assertThat(trainService.stations(), equalTo(emptyList()))
    }

    class TrainService {
        fun stations(): List<Station> {
            return emptyList()
        }
    }
}

class Station
