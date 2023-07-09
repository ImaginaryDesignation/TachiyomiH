package eu.kanade.tachiyomi.ui.browse.migration.advanced.design

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.coroutineScope
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.kanade.domain.UnsortedPreferences
import eu.kanade.domain.source.service.SourcePreferences
import eu.kanade.tachiyomi.databinding.PreMigrationListBinding
import eu.kanade.tachiyomi.source.online.HttpSource
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import tachiyomi.core.util.lang.launchIO
import tachiyomi.domain.source.service.SourceManager
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get

class PreMigrationScreenModel(
    private val sourceManager: SourceManager = Injekt.get(),
    private val prefs: UnsortedPreferences = Injekt.get(),
    private val sourcePreferences: SourcePreferences = Injekt.get(),
) : ScreenModel {

    private val _state = MutableStateFlow(emptyList<MigrationSourceItem>())
    val state = _state.asStateFlow()

    lateinit var controllerBinding: PreMigrationListBinding
    var adapter: MigrationSourceAdapter? = null

    val startMigration = MutableSharedFlow<String?>()

    val listener = object : StartMigrationListener {
        override fun startMigration(extraParam: String?) {
            val listOfSources = adapter?.currentItems
                ?.filterIsInstance<MigrationSourceItem>()
                ?.filter {
                    it.sourceEnabled
                }
                ?.joinToString("/") { it.source.id.toString() }
                .orEmpty()

            prefs.migrationSources().set(listOfSources)

            coroutineScope.launch {
                startMigration.emit(extraParam)
            }
        }
    }
    val clickListener = FlexibleAdapter.OnItemClickListener { _, position ->
        val adapter = adapter ?: return@OnItemClickListener false
        adapter.getItem(position)?.let {
            it.sourceEnabled = !it.sourceEnabled
        }
        adapter.notifyItemChanged(position)
        false
    }

    lateinit var dialog: MigrationBottomSheetDialog

    init {
        coroutineScope.launchIO {
            val enabledSources = getEnabledSources()
            _state.update { enabledSources }
        }
    }

    /**
     * Returns a list of enabled sources ordered by language and name.
     *
     * @return list containing enabled sources.
     */
    private fun getEnabledSources(): List<MigrationSourceItem> {
        val languages = sourcePreferences.enabledLanguages().get()
        val sourcesSaved = prefs.migrationSources().get().split("/")
            .mapNotNull { it.toLongOrNull() }
        val disabledSources = sourcePreferences.disabledSources().get()
            .mapNotNull { it.toLongOrNull() }
        val sources = sourceManager.getCatalogueSources()
            .asSequence()
            .filterIsInstance<HttpSource>()
            .filter { it.lang in languages }
            .sortedBy { "(${it.lang}) ${it.name}" }
            .map {
                MigrationSourceItem(
                    it,
                    isEnabled(
                        sourcesSaved,
                        disabledSources,
                        it.id,
                    ),
                )
            }
            .toList()

        return sources
            .filter { it.sourceEnabled }
            .sortedBy { sourcesSaved.indexOf(it.source.id) }
            .plus(
                sources.filterNot { it.sourceEnabled },
            )
    }

    fun isEnabled(
        sourcesSaved: List<Long>,
        disabledSources: List<Long>,
        id: Long,
    ): Boolean {
        return if (sourcesSaved.isEmpty()) {
            id !in disabledSources
        } else {
            id in sourcesSaved
        }
    }

    fun massSelect(selectAll: Boolean) {
        val adapter = adapter ?: return
        adapter.currentItems.forEach {
            it.sourceEnabled = selectAll
        }
        adapter.notifyDataSetChanged()
    }

    fun matchSelection(matchEnabled: Boolean) {
        val adapter = adapter ?: return
        val enabledSources = if (matchEnabled) {
            sourcePreferences.disabledSources().get().mapNotNull { it.toLongOrNull() }
        } else {
            sourcePreferences.pinnedSources().get().mapNotNull { it.toLongOrNull() }
        }
        val items = adapter.currentItems.toList()
        items.forEach {
            it.sourceEnabled = if (matchEnabled) {
                it.source.id !in enabledSources
            } else {
                it.source.id in enabledSources
            }
        }
        val sortedItems = items.sortedBy { it.source.name }.sortedBy { !it.sourceEnabled }
        adapter.updateDataSet(sortedItems)
    }
}
