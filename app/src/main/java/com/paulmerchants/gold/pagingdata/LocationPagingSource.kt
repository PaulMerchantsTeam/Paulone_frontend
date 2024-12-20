package com.paulmerchants.gold.pagingdata

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.bumptech.glide.load.HttpException
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.paulmerchants.gold.BuildConfig
import com.paulmerchants.gold.model.responsemodels.PmlBranch
import com.paulmerchants.gold.model.responsemodels.BaseResponse
import com.paulmerchants.gold.model.responsemodels.PagingRespAllBranches
import com.paulmerchants.gold.remote.ApiParams
import com.paulmerchants.gold.utility.decryptKey
import java.io.IOException
import javax.inject.Inject


private const val BRANCH_STARTING_PAGE_INDEX = 0


class LocationPagingSource @Inject constructor(
    private val apiParams: ApiParams,
    private val token: String,
) : PagingSource<Int, PmlBranch>() {


    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, PmlBranch> {
        val gson = Gson()
        val position = params.key ?: BRANCH_STARTING_PAGE_INDEX
        return try {
            val response = apiParams.fetchAllBranch(
                token, position,
                10
            )
            val plainTextResponse = response.string()

            // Do something with the plain text response
            Log.d("Response", plainTextResponse.toString())

            val decryptData = decryptKey(
                BuildConfig.SECRET_KEY_UAT,
                plainTextResponse
            )
            println("decrypt-----$decryptData")

            val typeToken = object : TypeToken<BaseResponse<PagingRespAllBranches>>() {}
            val respPending: BaseResponse<PagingRespAllBranches> = gson.fromJson(decryptData, typeToken.type)
            val repos = respPending?.data?.data ?: emptyList()
            Log.d("PAGGGIIINNNGGG", "load: ............${repos.size}")
            val nextKey = if (repos.isEmpty()) {
                null
            } else {
                // initial load size = 3 * NETWORK_PAGE_SIZE
                // ensure we're not requesting duplicating items, at the 2nd request
                position + 1
            }
            LoadResult.Page(
                data = repos,
                prevKey = if (position <= BRANCH_STARTING_PAGE_INDEX) null else position - 1,
                nextKey = nextKey
            )
        } catch (exception: IOException) {
            return LoadResult.Error(exception)
        } catch (exception: HttpException) {
            return LoadResult.Error(exception)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, PmlBranch>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }


}