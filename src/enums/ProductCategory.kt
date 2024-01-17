import java.util.ArrayList
import java.util.List

enum class ProductCategory(private val productCategory: String) {
    PANTS("Pants"),
    SHIRTS("Shirts"),
    SKIRTS("Skirts"),
    DRESSES("Dresses"),
    JUMPERS("Jumpers"),
    ACCESSORIES("Accessories"),
    OTHER("Other");

    @Override
    override fun toString(): String {
        return productCategory
    }
}
