package jp.mydns.kokoichi0206.kmmnote.android.di

import android.app.Application
import com.squareup.sqldelight.db.SqlDriver
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jp.mydns.kokoichi0206.kmmnote.data.local.DatabaseDriverFactory
import jp.mydns.kokoichi0206.kmmnote.data.note.SqlDelightNoteDataSource
import jp.mydns.kokoichi0206.kmmnote.database.NoteDatabase
import jp.mydns.kokoichi0206.kmmnote.domain.note.NoteDatasource
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideSqlDriver(app: Application): SqlDriver {
        return DatabaseDriverFactory(app).createDriver()
    }

    @Provides
    @Singleton
    fun provideNoteDataSource(driver: SqlDriver): NoteDatasource {
        return SqlDelightNoteDataSource(NoteDatabase(driver))
    }
}