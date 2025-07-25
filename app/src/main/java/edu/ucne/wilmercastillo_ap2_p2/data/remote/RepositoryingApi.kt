package edu.ucne.wilmercastillo_ap2_p2.data.remote

import edu.ucne.wilmercastillo_ap2_p2.data.remote.dto.ContributorDto
import edu.ucne.wilmercastillo_ap2_p2.data.remote.dto.RepositoryDto
import retrofit2.http.GET
import retrofit2.http.Path

interface RepositoryingApi {
    @GET("users/{username}/repos")
    suspend fun listRepos(@Path("username") username: String): List<RepositoryDto>

    @GET("repos/{owner}/{repo}/contributors")
    suspend fun listContributors(
        @Path("owner") owner: String,
        @Path("repo") repo: String
    ): List<ContributorDto>
}