package com.cebolao.app.core

/**
 * Eventos de UI one-shot (ex: Snackbar, navegação).
 * Estes eventos são emitidos uma única vez e não são reemitidos após rotação.
 */
sealed class UiEvent {
    data class ShowSnackbar(val message: String) : UiEvent()
    data class ShowSuccess(val message: String) : UiEvent()
}
