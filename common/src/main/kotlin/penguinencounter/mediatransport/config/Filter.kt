package penguinencounter.mediatransport.config

object Filter {
    enum class FilterMode(internal val default: Boolean) {
        Allow(false), Block(true)
    }

    fun <T> matches(mode: FilterMode, rules: Collection<T>, target: T): Boolean {
        val contain = rules.contains(target)
        // this totally works
        // read as "invert 'default' if 'contain'"
        return mode.default xor contain
    }
}