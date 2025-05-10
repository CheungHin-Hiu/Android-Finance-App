package com.example.androidfinanceapp.data
import com.example.androidfinanceapp.network.AddTransactionRequest
import com.example.androidfinanceapp.network.TransactionApiService
import com.example.androidfinanceapp.network.TransactionItemData
import com.example.androidfinanceapp.network.TransactionsResponse
import com.example.androidfinanceapp.ui.overview.TransactionItem
import retrofit2.Response

interface TransactionRepository {
    suspend fun getTransactions(
        token: String
    ): Response<TransactionsResponse>

    suspend fun addTransaction(
        token: String,
        type: String,
        categoryType: String,
        currencyType: String,
        amount: Double,
        date: String,
    ): Response<Unit>
}

// For DI injection
class NetworkTransactionRepository(
    private val transactionApiService: TransactionApiService
) : TransactionRepository {

    override suspend fun getTransactions(
        token: String,
    ): Response<TransactionsResponse> =
        transactionApiService.getTransactions(
            token = token,
        )

    override suspend fun addTransaction(
        token: String,
        type: String,
        categoryType: String,
        currencyType: String,
        amount: Double,
        date: String,
    ): Response<Unit> =
        transactionApiService.addTransaction(
            token=token,
            request = AddTransactionRequest(
                TransactionItemData(type = type,
                    categoryType = categoryType,
                    currencyType = currencyType,
                    amount = amount,
                    date = date,)

            )
        )
}