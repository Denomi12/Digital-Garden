data class Token(val type: String, val value: String) {
    override fun toString(): String {
        return "$type(\"$value\")"
    }
}
