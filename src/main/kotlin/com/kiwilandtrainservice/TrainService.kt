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

    fun findRoutesWithMaxStops(source: Station, destination: Station, maxStops: Int): List<List<Station>> {
        return findRoutes(source, destination, maxStops, listOf(source))
    }

    fun findRoutesWithNStops(source: Station, destination: Station, stops: Int) =
        findRoutesForExactlyNStops(
            source,
            destination,
            stops,
            listOf(source),
            stopCondition = { partialRoute -> stopConditionFoExactlyNStops(partialRoute, stops) },
            validPath = { partialRoute -> validPathForExactlyNStops(partialRoute, stops, destination) }
        )

    private fun findRoutes(
        source: Station,
        destination: Station,
        maxStops: Int,
        partialRoute: List<Station>
    ): List<List<Station>> {
        val allRoutes = mutableListOf<List<Station>>()
        if (partialRoute.size > maxStops + 1)
            return allRoutes
        if (partialRoute.last() == destination && partialRoute.size > 1 && partialRoute.size <= maxStops + 1) {
            allRoutes.add(partialRoute.toList())
        }
        for (station in adjacentStationsOf(source)) {
            allRoutes.addAll(findRoutes(station, destination, maxStops, partialRoute + station))
        }
        return allRoutes
    }

    private fun findRoutesForExactlyNStops(
        source: Station,
        destination: Station,
        stops: Int,
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
                findRoutesForExactlyNStops(
                    station,
                    destination,
                    stops,
                    partialRoute + station,
                    stopCondition,
                    validPath
                )
            )
        }
        return allRoutes
    }

    private fun stopConditionFoExactlyNStops(
        partialRoute: List<Station>,
        stops: Int
    ) = partialRoute.size > stops + 1

    private fun validPathForExactlyNStops(
        partialRoute: List<Station>,
        stops: Int,
        destination: Station
    ) = partialRoute.size == stops + 1 && partialRoute.last() == destination

    private fun adjacentStationsOf(station: Station) =
        routes
            .filter { it.source == station }
            .map { it.destination }
}