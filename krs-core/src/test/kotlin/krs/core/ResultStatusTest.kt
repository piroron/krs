package krs.core

import org.assertj.core.api.Assertions
import kotlin.test.Test

internal class ResultStatusTest {

    @Test
    fun `map not effected if failed`() {
        val v: ResultStatus<Int, String> = Failed("fail")
        Assertions.assertThat(v.map { it + 5 }).isEqualTo(Failed("fail"))
    }

    @Test
    fun `map effected if succeeded`() {
        val v: ResultStatus<Int, String> = Succeeded(10)
        Assertions.assertThat(v.map { it  + 5 }).isEqualTo(Succeeded(15))
    }

    @Test
    fun `mapFailure effected if failed`() {
        val v: ResultStatus<Int, String> = Failed("fail")
        Assertions.assertThat(v.mapFailure { it.uppercase() }).isEqualTo(Failed("FAIL"))
    }

    @Test
    fun `mapFailure not effected if succeeded`() {
        val v: ResultStatus<Int, String> = Succeeded(10)
        Assertions.assertThat(v.mapFailure { it.uppercase() }).isEqualTo(Succeeded(10))
    }

    @Test
    fun `fold if succeeded`() {
        val v: ResultStatus<String, String> = Succeeded("Fold")
        val actual = v.fold(
            success = { it.lowercase() },
            failure = { it.uppercase() },
        )
        Assertions.assertThat(actual).isEqualTo("fold")
    }

    @Test
    fun `fold if failed`() {
        val v: ResultStatus<String, String> = Failed("Fold")
        val actual = v.fold(
            success = { it.lowercase() },
            failure = { it.uppercase() },
        )
        Assertions.assertThat(actual).isEqualTo("FOLD")
    }

    @Test
    fun `toNullable make null if failed`() {
        val v: ResultStatus<String, String> = Failed("Fold")
        Assertions.assertThat(v.toNullable()).isNull()
    }

    @Test
    fun `toNullable make notNull if succeeded`() {
        val v: ResultStatus<String, String> = Succeeded("Fold")
        Assertions.assertThat(v.toNullable()).isNotNull
    }

    @Test
    fun `forEach run if succeeded`() {
        var run = false
        val v: ResultStatus<String, String> = Succeeded("Fold")
        v.forEach { run = true }
        Assertions.assertThat(run).isTrue
    }

    @Test
    fun `forEach not run if failed`() {
        var run = false
        val v: ResultStatus<String, String> = Failed("Fold")
        v.forEach { run = true }
        Assertions.assertThat(run).isFalse
    }

    @Test
    fun `fromNullable make Failed instance from null`() {
        val v: String? = null
        val actual = ResultStatus.fromNullable(v)
        Assertions.assertThat(actual.isFailure()).isTrue
        Assertions.assertThat(actual).isInstanceOf(Failed::class.java)
    }

    @Test
    fun `fromNullable make Succeeded instance from not null`() {
        val v = "abc"
        val actual = ResultStatus.fromNullable(v)
        Assertions.assertThat(actual.isSuccess()).isTrue
        Assertions.assertThat(actual).isInstanceOf(Succeeded::class.java)
    }
}