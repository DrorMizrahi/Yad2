enum class Size(val productSize: String) {
    Y("Y"),
    XS("XS"),
    S("S"),
    M("M"),
    L("L"),
    XL("XL"),
    XXL("XXL");

    @Override
    override fun toString(): String {
        return productSize
    }
}
