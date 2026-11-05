package lab2

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ProductSelectionTest {
    @Test
    fun selectsAvailableProductsInRecommendationOrder() {
        val keyboard = Product(1, "Keyboard", true)
        val mouse = Product(2, "Mouse", false)
        val monitor = Product(3, "Monitor", true)
        val webcam = Product(4, "Webcam", true)
        val products = listOf(keyboard, mouse, monitor, webcam)

        val result = selectProducts(
            products,
            listOf(3L, 2L, 3L, 99L, 1L, 4L),
            listOf(1L)
        )

        assertEquals(listOf(monitor, webcam), result)
    }

    @Test
    fun returnsEmptyListWhenThereAreNoRecommendations() {
        val products = listOf(Product(1, "Keyboard", true))

        val result = selectProducts(products, emptyList(), emptyList())

        assertEquals(emptyList<Product>(), result)
    }

    @Test
    fun preservesRecommendationOrderWithReorderedCatalogAndDuplicates() {
        val keyboard = Product(1, "Keyboard", true)
        val mouse = Product(2, "Mouse", false)
        val monitor = Product(3, "Monitor", true)
        val webcam = Product(4, "Webcam", true)

        val result = selectProducts(
            listOf(webcam, keyboard, monitor, mouse),
            listOf(3L, 2L, 3L, 99L, 1L, 4L, 4L),
            listOf(1L, 1L, 100L)
        )

        assertEquals(listOf(monitor, webcam), result)
    }

    @Test
    fun returnsEmptyListForEmptyCatalog() {
        val result = selectProducts(emptyList(), listOf(1L, 1L, 99L), listOf(2L))

        assertEquals(emptyList<Product>(), result)
    }

    @Test
    fun selectsProductsWithEmptyExclusions() {
        val keyboard = Product(1, "Keyboard", true)
        val monitor = Product(3, "Monitor", true)

        val result = selectProducts(
            listOf(keyboard, monitor),
            listOf(3L, 1L, 3L),
            emptyList()
        )

        assertEquals(listOf(monitor, keyboard), result)
    }

    @Test
    fun returnsEmptyListWhenNoProductQualifies() {
        val result = selectProducts(
            listOf(Product(1, "Keyboard", true), Product(2, "Mouse", false)),
            listOf(1L, 2L, 99L, 1L),
            listOf(1L, 1L)
        )

        assertEquals(emptyList<Product>(), result)
    }

    @Test
    fun preservesInputsAndKeepsCallsIndependent() {
        val keyboard = Product(1, "Keyboard", true)
        val monitor = Product(3, "Monitor", true)
        val products = mutableListOf(keyboard, monitor)
        val recommendedIds = mutableListOf(3L, 1L, 3L, 99L)
        val excludedIds = mutableListOf(1L, 1L)

        val first = selectProducts(products, recommendedIds, excludedIds)
        val second = selectProducts(products, recommendedIds, emptyList())
        val third = selectProducts(products, recommendedIds, excludedIds)

        assertEquals(listOf(monitor), first)
        assertEquals(listOf(monitor, keyboard), second)
        assertEquals(listOf(monitor), third)
        assertEquals(listOf(keyboard, monitor), products)
        assertEquals(listOf(3L, 1L, 3L, 99L), recommendedIds)
        assertEquals(listOf(1L, 1L), excludedIds)
    }
}
