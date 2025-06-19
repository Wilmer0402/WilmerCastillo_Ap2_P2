package edu.ucne.wilmercastillo_ap2_p2.data.remote

import edu.ucne.wilmercastillo_ap2_p2.data.remote.dto.RepositoryDto
import javax.inject.Inject

class RemoteDataSource @Inject constructor(
    private val repositoryingApi: RepositoryingApi
) {
    suspend fun getRepository(username: String) = repositoryingApi.listRepos(username)
}
