package edu.ucne.wilmercastillo_ap2_p2.presentation

sealed class UiEvent {
    object NavigateUp : UiEvent()
    data class ShowSnackbar(val message: String):UiEvent()
}