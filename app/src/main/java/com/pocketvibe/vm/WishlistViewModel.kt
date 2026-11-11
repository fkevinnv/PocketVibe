package com.pocketvibe.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pocketvibe.data.TransactionRepository
import com.pocketvibe.data.local.WishItemEntity
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class WishlistViewModel(private val repo: TransactionRepository) : ViewModel() {

    val items: StateFlow<List<WishItemEntity>> = repo.wishItems
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    /** Emite el item que acaba de completarse (una sola vez por item). */
    private val _justCompleted = MutableSharedFlow<WishItemEntity>()
    val justCompleted: SharedFlow<WishItemEntity> = _justCompleted.asSharedFlow()

    private val completedIds = mutableSetOf<Long>()

    init {
        viewModelScope.launch {
            items.collect { list ->
                list.filter { it.progress >= 1f && it.id !in completedIds }
                    .forEach { item ->
                        completedIds += item.id
                        _justCompleted.emit(item)
                    }
            }
        }
    }

    fun addItem(name: String, emoji: String, target: Double) = viewModelScope.launch {
        repo.saveWish(WishItemEntity(name = name, emoji = emoji, targetAmount = target))
    }

    fun contribute(item: WishItemEntity, amount: Double) = viewModelScope.launch {
        repo.contributeToWish(item.id, amount)
    }

    fun remove(item: WishItemEntity) = viewModelScope.launch {
        completedIds.remove(item.id)
        repo.deleteWish(item)
    }
}
