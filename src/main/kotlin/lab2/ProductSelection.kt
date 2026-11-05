package lab2

fun selectProducts(
    products: List<Product>,
    recommendedIds: List<Long>,
    excludedIds: List<Long>
): List<Product> {
    return ProductSelector(products).select(recommendedIds, excludedIds, recommendedIds.size)
}
