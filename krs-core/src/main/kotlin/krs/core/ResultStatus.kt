package krs.core

/**
 * Represent success or failure state.
 * @param S the type when succeeded
 * @param F the type when failed
 */
sealed class ResultStatus<out S, out F> {
    protected abstract val succeeded: Boolean
    protected abstract val failed: Boolean

    fun isSuccess(): Boolean = succeeded
    fun isFailure(): Boolean = failed

    companion object {
        fun <S> fromNullable(v: S?): ResultStatus<S, Unit> = v?.let(::Succeeded) ?: Failed(Unit)
    }

    fun <S1> map(f: (S) -> S1): ResultStatus<S1, F> {
        return when(this) {
            is Succeeded -> Succeeded(f(value))
            is Failed -> this
        }
    }

    fun <F1> mapFailure(f: (F) -> F1): ResultStatus<S, F1> {
        return when(this) {
            is Succeeded -> this
            is Failed -> Failed(f(value))
        }
    }

    fun <A> fold(success: (S) -> A, failure: (F) -> A): A {
        return when(this) {
            is Succeeded -> success(value)
            is Failed -> failure(value)
        }
    }

    fun <S1, F1> bimap(success: (S) -> S1, failure: (F) -> F1): ResultStatus<S1, F1> {
        return fold(
            { Succeeded(success(it)) },
            { Failed(failure(it)) },
        )
    }

    fun toNullable(): S? {
        return when(this) {
            is Succeeded -> value
            is Failed -> null
        }
    }

    fun forEach(f: (S) -> Unit): ResultStatus<S, F> {
        if (this is Succeeded) f(value)
        return this
    }

}

data class Succeeded<out S>(val value: S): ResultStatus<S, Nothing>() {
    override val succeeded: Boolean = true
    override val failed: Boolean = false
}

data class Failed<out F>(val value: F): ResultStatus<Nothing, F>() {
    override val succeeded: Boolean = false
    override val failed: Boolean = true
}


fun <S1, S, F> ResultStatus<S, F>.flatMap(f: (S) -> ResultStatus<S1, F>): ResultStatus<S1, F> {
    return when(this) {
        is Succeeded -> f(value)
        is Failed -> this
    }
}

fun <S> ResultStatus<S, *>.getOrElse(default: () -> S): S {
    return when(this) {
        is Succeeded -> value
        is Failed -> default()
    }
}
