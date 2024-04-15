package com.daduck.lazycolumnsoflazyrows

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.focus.FocusRequester

data class ContainerFocusState(
    private var lastInternalFocusKey: String,
    private val internalItemsFocusRequester: MutableMap<String, FocusRequester> = mutableMapOf(),
) {
    fun lastInternalFocusRequester(): FocusRequester =
        internalItemsFocusRequester[lastInternalFocusKey]
            ?: FocusRequester.Default

    fun addInternalItemFocusRequester(key: String, focusRequester: FocusRequester) {
        internalItemsFocusRequester[key] = focusRequester
    }

    fun updateLastInternalFocusKey(key: String) {
        lastInternalFocusKey = key
    }

    companion object {
        /**
         * The default [Saver] implementation for [ParentFocusState].
         */
        val Saver: Saver<ContainerFocusState, Any> = listSaver(
            save = { listOf(it.lastInternalFocusKey) },
            restore = {
                ContainerFocusState(
                    lastInternalFocusKey = it[0],
                )
            }
        )
    }
}

@Composable
fun rememberContainerFocusState(
    lastSelectedKey: String = ""
): ContainerFocusState {
    return rememberSaveable(saver = ContainerFocusState.Saver) {
        ContainerFocusState(
            lastInternalFocusKey = lastSelectedKey
        )
    }
}


@Composable
inline fun initChildFocusOttProperties(
    childKey: String,
    containerFocusState: ContainerFocusState
): FocusOttProperties {
    val fr = remember {
        FocusRequester()
    }
    containerFocusState.addInternalItemFocusRequester(childKey, fr)
    return FocusOttProperties(
        focusRequester = fr,
        onFocused = {
            containerFocusState.updateLastInternalFocusKey(childKey)
        })
}



data class FocusOttProperties(
    val focusRequester: FocusRequester = FocusRequester(),
    val onFocused: (() -> Unit)? = null,
)