package com.kiwilandtrainservice

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

    fun totalDistanceOfRoute(vararg stations: Station): Int {
        return stations.toList()
            .windowed(2)
            .map {
                val source = it.first()
                val destination = it.last()
                findDirectRoute(source, destination)?.distance ?: throw IllegalStateException("NO SUCH ROUTE")
            }.sum()
    }

    fun findRoutesWithMaxStops(source: Station, destination: Station, maxStops: Int) =
        findRoutes(
            source,
            destination,
            maxStops,
            listOf(source),
            stopCondition = { partialRoute -> partialRoute.size > maxStops + 1 },
            validPath = { it.last() == destination && it.size > 1 && it.size <= maxStops + 1 }
        )

    fun findRoutesWithNStops(source: Station, destination: Station, stops: Int) =
        findRoutes(
            source,
            destination,
            stops,
            listOf(source),
            stopCondition = { partialRoute -> partialRoute.size > stops + 1 },
            validPath = { partialRoute -> partialRoute.size == stops + 1 && partialRoute.last() == destination }
        )

    private fun findRoutes(
        source: Station,
        destination: Station,
        maxStops: Int,
        partialRoute: List<Station>,
        stopCondition: (List<Station>) -> Boolean,
        validPath: (List<Station>) -> Boolean
    ): List<List<Station>> {
        val allRoutes = mutableListOf<List<Station>>()
        if (stopCondition(partialRoute))
            return allRoutes
        if (validPath(partialRoute)) {
            allRoutes.add(partialRoute.toList())
        }
        for (station in adjacentStationsOf(source)) {
            allRoutes.addAll(
                findRoutes(
                    station,
                    destination,
                    maxStops,
                    partialRoute + station,
                    stopCondition,
                    validPath
                )
            )
        }
        return allRoutes
    }

    private fun adjacentStationsOf(station: Station) =
        routes
            .filter { it.source == station }
            .map { it.destination }
}