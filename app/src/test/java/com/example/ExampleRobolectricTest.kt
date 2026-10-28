package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.UserRole
import com.example.data.repository.FoodCartRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Food Cart", appName)
    }

    @Test
    fun `food cart repository multi-role verification`() {
        val repo = FoodCartRepository()
        assertEquals(UserRole.CUSTOMER, repo.currentUser.value.role)

        repo.switchUserRole(UserRole.SUPER_ADMIN)
        assertEquals("Tanmoy Jana", repo.currentUser.value.name)
        assertEquals("superadmin@foodcart.com", repo.currentUser.value.email)

        // Cart and order workflow
        val item = repo.menuItems.value.first()
        repo.addToCart(item)
        assertEquals(1, repo.cartItems.value[item])

        val order = repo.placeCustomerOrder("UPI")
        assertNotNull(order)
        assertEquals(0, repo.cartItems.value.size)
    }
}
