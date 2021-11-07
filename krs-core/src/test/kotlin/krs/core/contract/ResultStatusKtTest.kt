package krs.core.contract

import krs.core.Failed
import krs.core.Succeeded
import org.assertj.core.api.Assertions
import kotlin.test.Test

internal class ResultStatusKtTest {
    @Test
    fun `fromContract return succeeded value`() {
        val v = fromContract { 10 }
        Assertions.assertThat(v).isEqualTo(Succeeded(10))
    }

    @Test
    fun `fromContract return failed if check failed`() {
        val v = fromContract { check(false) { "check failed." } }
        Assertions.assertThat(v.isFailure()).isTrue

        v.fold(
            { Assertions.fail("") },
            { Assertions.assertThat(it).isInstanceOf(IllegalState::class.java) },
        )

        Assertions.assertThat(v.mapFailure { it.message }).isEqualTo(Failed("check failed."))
    }

    @Test
    fun `fromContract return failed if require failed`() {
        val v = fromContract { require(false) { "require failed." } }
        Assertions.assertThat(v.isFailure()).isTrue

        v.fold(
            { Assertions.fail("") },
            { Assertions.assertThat(it).isInstanceOf(IllegalArgument::class.java) },
        )
        Assertions.assertThat(v.mapFailure { it.message }).isEqualTo(Failed("require failed."))
    }

    @Test
    fun `fromContract return failed if checkNotNull failed`() {
        val value: String? = null
        val v = fromContract { checkNotNull(value) { "checkNotNull failed." } }

        Assertions.assertThat(v.isFailure()).isTrue
        v.fold(
            { Assertions.fail("") },
            { Assertions.assertThat(it).isInstanceOf(IllegalState::class.java) },
        )
        Assertions.assertThat(v.mapFailure { it.message }).isEqualTo(Failed("checkNotNull failed."))
    }

    @Test
    fun `contractMap return original when contract violated`() {
        val v = fromContract {
            check(false) { "check failed." }
            "v"
        }
        val v2 = v.contractMap { it.length }
        Assertions.assertThat(v2).isEqualTo(v)
    }

    @Test
    fun `contractMap return failed if raise in map`() {
        val v = fromContract { "" }
        val v2 = v.contractMap {
            check(it.isNotEmpty()) { "str is empty." }
            it.length
        }

        Assertions.assertThat(v2.isFailure()).isTrue
        v2.fold(
            { Assertions.fail("") },
            { Assertions.assertThat(it).isInstanceOf(IllegalState::class.java) },
        )
        Assertions.assertThat(v2.mapFailure { it.message }).isEqualTo(Failed("str is empty."))
    }

    @Test
    fun `contractMap return succeeded`() {
        val v = fromContract { "abc" }
        val v2 = v.contractMap {
            check(it.isNotEmpty()) { "str is empty." }
            it.length
        }

        Assertions.assertThat(v2).isEqualTo(Succeeded(3))
    }
}