package jp.mydns.kokoichi206.g.widgetprac.data

import jp.mydns.kokoichi206.ViewerQuery
import jp.mydns.kokoichi206.g.widgetprac.domain.SimpleUser

fun ViewerQuery.Viewer.toSimpleUser(): SimpleUser {
    return SimpleUser(
        login = login,
        location = location ?: "",
    )
}
