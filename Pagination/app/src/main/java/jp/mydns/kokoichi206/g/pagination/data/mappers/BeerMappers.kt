package jp.mydns.kokoichi206.g.pagination.data.mappers

import jp.mydns.kokoichi206.g.pagination.data.local.BeerEntity
import jp.mydns.kokoichi206.g.pagination.data.remote.BeerDto
import jp.mydns.kokoichi206.g.pagination.domain.Beer

fun BeerDto.toBeerEntity(): BeerEntity {
    return BeerEntity(
        id = id,
        name = name,
        tagline = tagline,
        description = description,
        firstBrewed = first_brewed,
        imageUrl = image_url,
    )
}

fun BeerEntity.toBeer(): Beer {
    return Beer(
        id = id,
        name = name,
        tagline = tagline,
        description = description,
        firstBrewed = firstBrewed,
        imageUrl = imageUrl,
    )
}
