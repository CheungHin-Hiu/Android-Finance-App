package com.example.androidfinanceapp

import com.example.androidfinanceapp.data.DataStoreManager
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

@OptIn(ExperimentalCoroutinesApi::class)
class DataStoreManagerTest {
    private lateinit var dataStoreManager: DataStoreManager
    private lateinit var testDataStore: DataStore<Preferences>
    private val testScope = TestScope(UnconfinedTestDispatcher() + Job())

    @get:Rule
    val tempFolder: TemporaryFolder = TemporaryFolder()

    @Before
    fun setup() {
        val testContext = ApplicationProvider.getApplicationContext<Context>()
        val testFile = File(tempFolder.newFolder(), "test_datastore")

        testDataStore = PreferenceDataStoreFactory.create(
            scope = testScope,
            produceFile = { testFile }
        )

        dataStoreManager = DataStoreManager(testContext)
    }

    @Test
    fun testSaveAndRetrieveLoginData() = runTest {
        val testUserId = "test123"
        val testUserName = "John Doe"
        val testToken = "jwt_token_123"

        dataStoreManager.saveLoginData(testUserId, testUserName, testToken)

        val savedUserId = dataStoreManager.userIdFlow.first()
        val savedUserName = dataStoreManager.usernameFlow.first()
        val savedToken = dataStoreManager.tokenFlow.first()

        assertEquals(testUserId, savedUserId)
        assertEquals(testUserName, savedUserName)
        assertEquals(testToken, savedToken)
    }


    @Test
    fun testUpdateLoginData() = runTest {
        // Given
        val initialUserId = "initial123"
        val initialUserName = "Initial User"
        val initialToken = "initial_token"

        dataStoreManager.saveLoginData(initialUserId, initialUserName, initialToken)
        val updatedUserId = "updated123"
        val updatedUserName = "Updated User"
        val updatedToken = "updated_token"

        dataStoreManager.saveLoginData(updatedUserId, updatedUserName, updatedToken)

        val savedUserId = dataStoreManager.userIdFlow.first()
        val savedUserName = dataStoreManager.usernameFlow.first()
        val savedToken = dataStoreManager.tokenFlow.first()

        assertEquals(updatedUserId, savedUserId)
        assertEquals(updatedUserName, savedUserName)
        assertEquals(updatedToken, savedToken)
    }

    @Test
    fun testSaveEmptyValues() = runTest {
        val emptyUserId = ""
        val emptyUserName = ""
        val emptyToken = ""

        dataStoreManager.saveLoginData(emptyUserId, emptyUserName, emptyToken)

        val savedUserId = dataStoreManager.userIdFlow.first()
        val savedUserName = dataStoreManager.usernameFlow.first()
        val savedToken = dataStoreManager.tokenFlow.first()

        assertEquals(emptyUserId, savedUserId)
        assertEquals(emptyUserName, savedUserName)
        assertEquals(emptyToken, savedToken)
    }
}