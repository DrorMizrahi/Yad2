enum class Gender(private val genderString: String) {
    FEMALE("Female"),
    MALE("Male"),
    OTHER("Other");

    @Override
    override fun toString(): String {
        return genderString
    }
}