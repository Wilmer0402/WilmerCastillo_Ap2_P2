package edu.ucne.wilmercastillo_ap2_p2.presentation.contributors

sealed interface ContributorEvent {
    data class GetContributors(val repoPath: String) : ContributorEvent
}