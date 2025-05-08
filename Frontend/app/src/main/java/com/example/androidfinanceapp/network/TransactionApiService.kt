package com.example.androidfinanceapp.network
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface TransactionApiService {
    //get all transactions
    @GET("/transactions/{token}")
    suspend fun getTransactions(
        @Path("token", encoded = true) token: String,
    ): Response<TransactionsResponse>

    //add transactions to db
    @POST("/transactions/{token}")
    suspend fun addTransaction(
        @Path("token", encoded = true) token: String,
        @Body request: AddTransactionRequest
    ):Response<Unit>
}

@Serializable
data class TransactionsResponse(
    @SerialName("transactions") val transactions: List<Transaction>
)


//getting the transaction and its category
//notice that local_amount should be returned for calculate percentage
@Serializable
data class Transaction(
    @SerialName("type") val type: String,
    @SerialName("category_type") val categoryType: String,
    @SerialName("currency_type") val currencyType: String,
    @SerialName("amount") val amount: Double,
    @SerialName("converted_amount") val convertedAmount: Double,
    @SerialName("date") val date: String,
)


//adding the transaction and its category
@Serializable
data class AddTransactionRequest(
    @SerialName("type") val type: String,
    @SerialName("category_type") val categoryType: String,
    @SerialName("currency_type") val currencyType: String,
    @SerialName("amount") val amount: Double,
    @SerialName("date") val date: String,
)
