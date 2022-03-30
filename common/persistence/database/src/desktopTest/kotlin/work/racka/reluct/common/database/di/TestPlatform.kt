package work.racka.reluct.common.database.di

import com.squareup.sqldelight.sqlite.driver.JdbcSqliteDriver
import org.koin.dsl.module
import work.racka.reluct.common.database.dao.DatabaseWrapper
import work.racka.reluct.common.database.db.ReluctDatabase

internal actual object TestPlatform {
    actual fun platformDatabaseModule() = module {
        single {
            val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
                .also { ReluctDatabase.Schema.create(it) }
            DatabaseWrapper(ReluctDatabase(driver))
        }
    }
}
