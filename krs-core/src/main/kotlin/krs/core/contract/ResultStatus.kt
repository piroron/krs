package krs.core.contract

import krs.core.Failed
import krs.core.ResultStatus
import krs.core.Succeeded

sealed class ContractViolation {
    abstract val ex: Throwable
    val message by lazy { ex.message }
}

data class IllegalState(override val ex: Throwable): ContractViolation()
data class IllegalArgument(override val ex: Throwable): ContractViolation()

typealias ContractStatus<S> = ResultStatus<S, ContractViolation>

fun <S> fromContract(block: () -> S): ContractStatus<S> {
    return tryContract(block)
}

fun <S, S1> ResultStatus<S, ContractViolation>.contractMap(f: (S) -> S1): ContractStatus<S1> {
    return when (this) {
        is Succeeded -> tryContract { f(value) }
        is Failed -> this
    }
}

private fun <S> tryContract(f: () -> S): ContractStatus<S> {
    return runCatching(f).fold(
        onSuccess = { Succeeded(it) },
        onFailure = { when(it) {
            is IllegalArgumentException -> Failed(IllegalArgument(it))
            is IllegalStateException -> Failed(IllegalState(it))
            else -> throw it
        }}
    )
}