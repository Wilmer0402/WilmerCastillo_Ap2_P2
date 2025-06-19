package edu.ucne.wilmercastillo_ap2_p2.data.repository

import edu.ucne.wilmercastillo_ap2_p2.data.remote.RemoteDataSource
import edu.ucne.wilmercastillo_ap2_p2.data.remote.Resource
import edu.ucne.wilmercastillo_ap2_p2.data.remote.dto.RepositoryDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class RepositoryRepository @Inject constructor(
    private val remoteDataSource: RemoteDataSource
) {
    fun getRepositories(username: String): Flow<Resource<List<RepositoryDto>>> = flow {
        emit(Resource.Loading())
        try {
            val repositories = remoteDataSource.getRepository(username)
            emit(Resource.Success(repositories))
        } catch (e: Exception) {
            emit(Resource.Error("Error al obtener repositorios: ${e.message}"))
            }
        }

}