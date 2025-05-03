package com.example.androidfinanceapp.data
import com.example.androidfinanceapp.network.AddTransactionRequest
import com.example.androidfinanceapp.network.TransactionApiService
import com.example.androidfinanceapp.network.TransactionsResponse
import retrofit2.Response

interface TransactionRepository {
    suspend fun getTransactions(
        token: String,
        startDate: String,
        endDate: String
    ): Response<TransactionsResponse>

    suspend fun addTransaction(
        token: String,
        type: String,
        categoryType: String,
        currencyType: String,
        amount: Double,
        date: String,
        remark: String,
        createAt: String
    ): Response<Unit>
}

// For DI injection
class NetworkTransactionRepository(
    private val transactionApiService: TransactionApiService
) : TransactionRepository {

    override suspend fun getTransactions(
        token: String,
        startDate: String,
        endDate: String
    ): Response<TransactionsResponse> =
        transactionApiService.getTransactions(
            token = token,
            startDate = startDate,
            endDate = endDate
        )

    override suspend fun addTransaction(
        token: String,
        type: String,
        categoryType: String,
        currencyType: String,
        amount: Double,
        date: String,
        remark: String,
        createAt: String
    ): Response<Unit> =
        transactionApiService.addTransaction(
            request = AddTransactionRequest(
                token = token,
                type = type,
                categoryType = categoryType,
                currencyType = currencyType,
                amount = amount,
                date = date,
                remark = remark,
                createdAt = createAt
            )
        )
}