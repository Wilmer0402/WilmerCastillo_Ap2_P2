package edu.ucne.wilmercastillo_ap2_p2.presentation.repository

 sealed interface RepositoryEvent {
     data object PostRepository: RepositoryEvent
     data object GetRepositories: RepositoryEvent
     data object PutRepositories: RepositoryEvent
     data  object DeleteRepositories: RepositoryEvent
}