enum class ProductCondition(private val productCondition: String) {
    GOOD_AS_NEW("Good as new"),
    GOOD("Good"),
    OK("Ok");

    @Override
    override fun toString(): String {
        return productCondition
    }
}
