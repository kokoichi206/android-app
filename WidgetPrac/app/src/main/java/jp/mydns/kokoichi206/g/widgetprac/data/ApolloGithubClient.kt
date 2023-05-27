package jp.mydns.kokoichi206.g.widgetprac.data

import com.apollographql.apollo3.ApolloClient
import jp.mydns.kokoichi206.ViewerQuery
import jp.mydns.kokoichi206.g.widgetprac.domain.GithubClient
import jp.mydns.kokoichi206.g.widgetprac.domain.SimpleUser

class ApolloGithubClient(
    private val apolloClient: ApolloClient,
) : GithubClient {
    override suspend fun getView(): SimpleUser? {
        return apolloClient
            .query(ViewerQuery())
            .execute()
            .data
            ?.viewer
            ?.toSimpleUser()
    }
}
