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
}