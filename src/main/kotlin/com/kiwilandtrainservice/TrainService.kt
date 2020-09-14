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

    fun findDirectRoute(source: Station, destination: Station) =
        routes
            .filter { it.source == source }
            .find { it.destination == destination }

    fun totalDistanceOfRoute(vararg stations: Station) =
        stations.toList()
            .windowed(2)
            .map {
                val source = it.first()
                val destination = it.last()
                findDirectRoute(source, destination)?.distance ?: throw IllegalStateException("NO SUCH ROUTE")
            }.sum()

    fun findRoutesWithMaxStops(source: Station, destination: Station, maxStops: Int) =
        findRoutes(
            source,
            destination,
            stopCondition = { partialRoute -> partialRoute.size > maxStops + 1 },
            validPath = { it.last() == destination && it.size > 1 && it.size <= maxStops + 1 }
        )

    fun findRoutesWithNStops(source: Station, destination: Station, stops: Int) =
        findRoutes(
            source,
            destination,
            stopCondition = { partialRoute -> partialRoute.size > stops + 1 },
            validPath = { partialRoute -> partialRoute.size == stops + 1 && partialRoute.last() == destination }
        )

    fun findShortestRoute(source: Station, destination: Station) =
        findRoutes(
            source,
            destination,
            validPath = { partialRoute -> partialRoute.last() == destination && partialRoute.size > 1 },
            stopCondition = { partialRoute -> partialRoute.drop(1).containsDuplicates() }
        ).minByOrNull { totalDistanceOfRoute(*it.toTypedArray()) }
            ?: throw IllegalStateException("NO SUCH ROUTE")

    fun routesWithMaxDistance(source: Station, destination: Station, maxDistance: Int) =
        findRoutes(
            source,
            destination,
            validPath = { partialRoute -> partialRoute.last() == destination && partialRoute.size > 1 },
            stopCondition = { partialRoute -> totalDistanceOfRoute(*partialRoute.toTypedArray()) >= maxDistance }
        )

    fun lengthShortestRoute(source: Station, destination: Station) =
        totalDistanceOfRoute(*findShortestRoute(source, destination).toTypedArray())

    private fun <E> List<E>.containsDuplicates() = this.size != this.distinct().count()

    private fun findRoutes(
        source: Station,
        destination: Station,
        stopCondition: (List<Station>) -> Boolean,
        validPath: (List<Station>) -> Boolean,
        partialRoute: List<Station> = listOf(source)
    ): List<List<Station>> {
        val allRoutes = mutableListOf<List<Station>>()
        if (stopCondition(partialRoute))
            return allRoutes
        if (validPath(partialRoute)) {
            allRoutes.add(partialRoute.toList())
        }
        adjacentStationsOf(source).forEach { station ->
            allRoutes.addAll(
                findRoutes(
                    station,
                    destination,
                    stopCondition,
                    validPath,
                    partialRoute + station
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