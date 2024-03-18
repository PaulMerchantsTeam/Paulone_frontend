package com.paulmerchants.gold.pagingdata

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.bumptech.glide.load.HttpException
import com.paulmerchants.gold.model.newmodel.PmlBranch
import com.paulmerchants.gold.model.newmodel.Transactions
import com.paulmerchants.gold.remote.ApiParams
import java.io.IOException
import javax.inject.Inject

private const val BRANCH_STARTING_PAGE_INDEX = 0


class TxnPagingSource @Inject constructor(
    private val status: Int,
    private val apiParams: ApiParams,
    private val token: String,
    private val custId: String,
) : PagingSource<Int, Transactions>() {


    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Transactions> {
        val position = params.key ?: BRANCH_STARTING_PAGE_INDEX
        return try {

            val response = when (status) {
                0 -> {
                    apiParams.txnHistorySearch(
                        token, custId, "CREATED", position,
                        10
                    )
                }

                1 -> {
                    apiParams.txnHistorySearch(
                        token, custId, "PAID", position,
                        10
                    )
                }

                else -> {
                    apiParams.txnHistory(
                        token, custId, position,
                        10
                    )
                }
            }
            val repos = response.body()?.data ?: emptyList<Transactions>()


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

    override fun getRefreshKey(state: PagingState<Int, Transactions>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }


}