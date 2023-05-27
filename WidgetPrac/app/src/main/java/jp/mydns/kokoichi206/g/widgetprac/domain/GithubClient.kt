package jp.mydns.kokoichi206.g.widgetprac.domain

interface GithubClient {
    suspend fun getView(): SimpleUser?
}
