package eu.kanade.tachiyomi.ui.browse.migration.search

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import eu.kanade.presentation.browse.MigrateSearchScreen
import eu.kanade.presentation.util.Screen
import eu.kanade.tachiyomi.ui.browse.migration.advanced.process.MigrationListScreen
import eu.kanade.tachiyomi.ui.manga.MangaScreen

class MigrateSearchScreen(private val mangaId: Long, private val validSources: List<Long>) : Screen() {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow

        val screenModel = rememberScreenModel { MigrateSearchScreenModel(mangaId = mangaId, validSources = validSources) }
        val state by screenModel.state.collectAsState()

        MigrateSearchScreen(
            navigateUp = navigator::pop,
            state = state,
            getManga = { source, manga ->
                screenModel.getManga(source = source, initialManga = manga)
            },
            onChangeSearchQuery = screenModel::updateSearchQuery,
            onSearch = screenModel::search,
            onClickSource = {
                if (!screenModel.incognitoMode.get()) {
                    screenModel.lastUsedSourceId.set(it.id)
                }
                navigator.push(SourceSearchScreen(state.manga!!, it.id, state.searchQuery))
            },
            onClickItem = {
                navigator.items
                    .filterIsInstance<MigrationListScreen>()
                    .last()
                    .newSelectedItem = mangaId to it.id
                navigator.popUntil { it is MigrationListScreen }
            },
            onLongClickItem = { navigator.push(MangaScreen(it.id, true)) },
        )
    }
}
