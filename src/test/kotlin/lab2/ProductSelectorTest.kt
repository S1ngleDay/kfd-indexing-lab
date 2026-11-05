package lab2

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ProductSelectorTest {
    @Test
    fun returnsEmptyListWithoutReadingRecommendationsForZeroLimit() {
        val selector = ProductSelector(listOf(Product(1, "Keyboard", true)))
        val recommendations = CountingList(listOf(1L, 99L))

        val result = selector.select(recommendations, emptyList(), 0)

        assertEquals(emptyList<Product>(), result)
        assertEquals(0, recommendations.readCount)
    }

    @Test
    fun stopsReadingRecommendationsAsSoonAsLimitIsReached() {
        val keyboard = Product(1, "Keyboard", true)
        val mouse = Product(2, "Mouse", false)
        val monitor = Product(3, "Monitor", true)
        val webcam = Product(4, "Webcam", true)
        val selector = ProductSelector(listOf(webcam, keyboard, monitor, mouse))
        val recommendations = CountingList(listOf(99L, 2L, 1L, 3L, 3L, 4L, 1L))

        val result = selector.select(recommendations, listOf(1L, 1L, 100L), 2)

        assertEquals(listOf(monitor, webcam), result)
        assertEquals(6, recommendations.readCount)
    }

    @Test
    fun selectsOnlyFirstQualifyingProductForLimitOne() {
        val keyboard = Product(1, "Keyboard", true)
        val monitor = Product(3, "Monitor", true)
        val selector = ProductSelector(listOf(keyboard, monitor))
        val recommendations = CountingList(listOf(99L, 3L, 1L))

        val result = selector.select(recommendations, emptyList(), 1)

        assertEquals(listOf(monitor), result)
        assertEquals(2, recommendations.readCount)
    }

    @Test
    fun returnsAllQualifyingProductsWhenLimitIsLarger() {
        val keyboard = Product(1, "Keyboard", true)
        val mouse = Product(2, "Mouse", false)
        val monitor = Product(3, "Monitor", true)
        val webcam = Product(4, "Webcam", true)
        val selector = ProductSelector(listOf(keyboard, mouse, monitor, webcam))
        val recommendations = CountingList(listOf(3L, 2L, 3L, 99L, 1L, 4L))

        val result = selector.select(recommendations, listOf(1L), 10)

        assertEquals(listOf(monitor, webcam), result)
        assertEquals(6, recommendations.readCount)
    }

    @Test
    fun preservesInputsAndKeepsRequestsIndependent() {
        val keyboard = Product(1, "Keyboard", true)
        val monitor = Product(3, "Monitor", true)
        val products = mutableListOf(keyboard, monitor)
        val recommendations = mutableListOf(3L, 1L, 3L, 99L)
        val exclusions = mutableListOf(3L, 3L)
        val selector = ProductSelector(products)

        val first = selector.select(recommendations, exclusions, 1)
        val second = selector.select(recommendations, emptyList(), 10)
        val third = selector.select(recommendations, exclusions, 10)

        assertEquals(listOf(keyboard), first)
        assertEquals(listOf(monitor, keyboard), second)
        assertEquals(listOf(keyboard), third)
        assertEquals(listOf(keyboard, monitor), products)
        assertEquals(listOf(3L, 1L, 3L, 99L), recommendations)
        assertEquals(listOf(3L, 3L), exclusions)
    }

    @Test
    fun usesNewCatalogAfterReload() {
        val keyboard = Product(1, "Keyboard", true)
        val monitor = Product(3, "Monitor", true)
        val selector = ProductSelector(listOf(keyboard, monitor))
        val recommendations = listOf(3L, 1L, 4L)
        val beforeReload = selector.select(recommendations, emptyList(), 10)
        val unavailableMonitor = Product(3, "New Monitor", false)
        val webcam = Product(4, "Webcam", true)

        assertEquals(listOf(monitor, keyboard), beforeReload)

        selector.loadCatalog(listOf(unavailableMonitor, webcam))
        val afterReload = selector.select(recommendations, emptyList(), 10)

        assertEquals(listOf(webcam), afterReload)
        assertEquals(listOf(monitor, keyboard), beforeReload)
    }

    @Test
    fun keepsCatalogSnapshotUntilExplicitReload() {
        val keyboard = Product(1, "Keyboard", true)
        val monitor = Product(3, "Monitor", true)
        val products = mutableListOf(keyboard)
        val selector = ProductSelector(products)

        products.clear()
        products.add(monitor)
        val beforeReload = selector.select(listOf(1L, 3L), emptyList(), 10)
        selector.loadCatalog(products)
        products.clear()
        val afterReload = selector.select(listOf(1L, 3L), emptyList(), 10)

        assertEquals(listOf(keyboard), beforeReload)
        assertEquals(listOf(monitor), afterReload)
    }

    @Test
    fun handlesEmptyRecommendationsAndEmptyReloadedCatalog() {
        val keyboard = Product(1, "Keyboard", true)
        val selector = ProductSelector(listOf(keyboard))

        assertEquals(emptyList<Product>(), selector.select(emptyList(), emptyList(), 10))

        selector.loadCatalog(emptyList())

        assertEquals(emptyList<Product>(), selector.select(listOf(1L), emptyList(), 10))
    }

    private class CountingList(private val values: List<Long>) : AbstractList<Long>() {
        var readCount = 0
            private set

        override val size: Int
            get() = values.size

        override fun get(index: Int): Long {
            readCount++
            return values[index]
        }
    }
}
