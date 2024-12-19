package com.paulmerchants.gold.pagingdata

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.bumptech.glide.load.HttpException
import com.google.gson.Gson
import com.paulmerchants.gold.BuildConfig
import com.paulmerchants.gold.model.newmodel.RespTxnHistory
import com.paulmerchants.gold.model.newmodel.Transactions
import com.paulmerchants.gold.remote.ApiParams
import com.paulmerchants.gold.utility.decryptKey
import com.paulmerchants.gold.utility.encryptKey
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
        val gson = Gson()
        val position = params.key ?: BRANCH_STARTING_PAGE_INDEX
        return try {
            val encryptedCustId = encryptKey(BuildConfig.SECRET_KEY_UAT, custId)
            val encryptedCREATED = encryptKey(BuildConfig.SECRET_KEY_UAT, "CREATED")
            val encryptedPAID = encryptKey(BuildConfig.SECRET_KEY_UAT, "PAID")
            val response = when (status) {
                0 -> {
                    apiParams.txnHistorySearch(
                        token, cust_id = encryptedCustId, status = encryptedCREATED, position,
                        10
                    )
                }

                1 -> {
                    apiParams.txnHistorySearch(
                        token, cust_id = encryptedCustId, status = encryptedPAID, position,
                        10
                    )
                }

                else -> {
                    apiParams.txnHistory(
                        token, encryptedCustId, position,
                        10
                    )
                }
            }
            val plainTextResponse = response.string()

            // Do something with the plain text response
            Log.d("Response", plainTextResponse.toString())

            val decryptData = decryptKey(
                BuildConfig.SECRET_KEY_UAT,
                plainTextResponse
            )
            println("decrypt-----$decryptData")
            val respPending =
                gson.fromJson(decryptData.toString(), RespTxnHistory ::class.java)

            val repos = respPending?.data?.data ?: emptyList<Transactions>()


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
/* override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Transactions> {
        val position = params.key ?: BRANCH_STARTING_PAGE_INDEX
       val data = encryptKey(BuildConfig.SECRET_KEY_UAT,custId)

//       val data = URLEncoder.encode(encryptedData, "UTF-8")
        return try {

            val response = when (status) {
                0 -> {
                    apiParams.txnHistorySearch1(
                        token, data.toString(), "CREATED", position,
                        10
                    )
                }

                1 -> {
                    apiParams.txnHistorySearch1(
                        token, data.toString(), "PAID", position,
                        10
                    )
                }

                else -> {

                    apiParams.txnHistory1(
                        token,  position,
                        10,data
                    )
                }
            }
            val plainTextResp = response.string()
            val decryptData = decryptKey(
                BuildConfig.SECRET_KEY_UAT,
                plainTextResp
            )
            val gson = Gson()
            val repos1 =   gson.fromJson(decryptData.toString(), Transactions::class.java)
            val repos = listOf(repos1)  ?: emptyList<Transactions>()


//            Log.d("PAGGGIIINNNGGG", "load: ............${repos.size}")
            val nextKey = if (repos.toString().isEmpty()) {
                null
            } else {
                // initial load size = 3 * NETWORK_PAGE_SIZE
                // ensure we're not requesting duplicating items, at the 2nd request
                position + 1
            }
            LoadResult.Page(
                data = repos ?: emptyList(),
                prevKey = if (position <= BRANCH_STARTING_PAGE_INDEX) null else position - 1,
                nextKey = nextKey
            )
        } catch (exception: IOException) {
            return LoadResult.Error(exception)
        } catch (exception: HttpException) {
            return LoadResult.Error(exception)
        }
    }*/

    override fun getRefreshKey(state: PagingState<Int, Transactions>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }


}