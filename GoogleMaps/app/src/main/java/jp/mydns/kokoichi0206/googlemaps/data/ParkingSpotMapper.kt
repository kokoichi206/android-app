package jp.mydns.kokoichi0206.googlemaps.data

import jp.mydns.kokoichi0206.googlemaps.domain.model.ParkingSpot

fun ParkingSpotEntity.toParkingSpot(): ParkingSpot {
    return ParkingSpot(
        lat = lat,
        lng = lng,
        id = id,
    )
}

fun ParkingSpot.toParkingSpot(): ParkingSpotEntity {
    return ParkingSpotEntity(
        lat = lat,
        lng = lng,
        id = id,
    )
}