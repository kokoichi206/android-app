package jp.mydns.kokoichi206.g.widgetprac.domain

class GetUserUsecase(
    private val githubClient: GithubClient,
) {
    suspend fun execute(): SimpleUser? {
        return githubClient
            .getView()
    }
}