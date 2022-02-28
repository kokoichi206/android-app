package jp.mydns.kokoichi0206.googlemaps.presentation

import com.google.android.gms.maps.model.LatLng
import jp.mydns.kokoichi0206.googlemaps.domain.model.ParkingSpot

sealed class MapEvent {
    object ToggleFalloutMap : MapEvent()
    data class OnMapLongClick(val latLng: LatLng) : MapEvent()
    data class OnINfoWindowLongClick(val spot: ParkingSpot) : MapEvent()
}
