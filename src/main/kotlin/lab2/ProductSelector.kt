package lab2

class ProductSelector(products: List<Product>) {
    private var productsById = products.associateBy { it.id }

    fun loadCatalog(products: List<Product>) {
        productsById = products.associateBy { it.id }
    }

    fun select(
        recommendedIds: List<Long>,
        excludedIds: List<Long>,
        limit: Int
    ): List<Product> {
        if (limit == 0) return emptyList()

        val excluded = excludedIds.toHashSet()
        val selectedIds = HashSet<Long>()

        return buildList {
            for (id in recommendedIds) {
                if (id in excluded) continue

                val product = productsById[id] ?: continue
                if (product.available && selectedIds.add(id)) {
                    add(product)
                    if (size == limit) break
                }
            }
        }
    }
}
