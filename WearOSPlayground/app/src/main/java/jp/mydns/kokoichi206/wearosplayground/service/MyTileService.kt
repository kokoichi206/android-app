package jp.mydns.kokoichi206.wearosplayground.service


import androidx.wear.protolayout.ColorBuilders.argb
import androidx.wear.protolayout.DimensionBuilders
import androidx.wear.protolayout.LayoutElementBuilders
import androidx.wear.protolayout.ResourceBuilders
import androidx.wear.protolayout.TimelineBuilders
import androidx.wear.protolayout.TimelineBuilders.Timeline
import androidx.wear.protolayout.material.Text
import androidx.wear.tiles.RequestBuilders
import androidx.wear.tiles.TileBuilders
import androidx.wear.tiles.TileBuilders.Tile
import androidx.wear.tiles.TileService
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.tiles.SuspendingTileService
import jp.mydns.kokoichi206.wearosplayground.R

private const val RESOURCES_VERSION = "0"

@OptIn(ExperimentalHorologistApi::class)
class MyTileService : SuspendingTileService() {

    override suspend fun resourcesRequest(
        requestParams: RequestBuilders.ResourcesRequest
    ): ResourceBuilders.Resources {
        return ResourceBuilders.Resources.Builder()
            .setVersion(RESOURCES_VERSION)
            .build()
    }

    override suspend fun tileRequest(
        requestParams: RequestBuilders.TileRequest
    ): TileBuilders.Tile {
        val singleTileTimeline = TimelineBuilders.Timeline.Builder()
            .addTimelineEntry(
                TimelineBuilders.TimelineEntry.Builder()
                    .setLayout(
                        LayoutElementBuilders.Layout.Builder()
                            .setRoot(tileLayout())
                            .build()
                    )
                    .build()
            )
            .build()

        return TileBuilders.Tile.Builder()
            .setResourcesVersion(RESOURCES_VERSION)
            .setTileTimeline(singleTileTimeline)
            .build()
    }

    private fun tileLayout(): LayoutElementBuilders.LayoutElement {
//        val text = getString(R.string.hello_tile_body)
        val text = getString(R.string.hello_world)
        return LayoutElementBuilders.Box.Builder()
            .setVerticalAlignment(LayoutElementBuilders.VERTICAL_ALIGN_CENTER)
            .setWidth(DimensionBuilders.expand())
            .setHeight(DimensionBuilders.expand())
            .addContent(
                LayoutElementBuilders.Text.Builder()
                    .setText("text: $text")
                    .build()
            )
            .build()
    }
}