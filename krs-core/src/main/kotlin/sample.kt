data class Sample(
    val name: String
) {
    fun changeName(newName: String): Sample = Sample(newName)
}
