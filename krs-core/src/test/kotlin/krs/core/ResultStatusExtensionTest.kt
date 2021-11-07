package krs.core

import org.assertj.core.api.Assertions
import kotlin.test.Test

class ResultStatusExtensionTest {

    @Test
    fun `flatMap effected if succeeded`() {
        val v: ResultStatus<Int, String> = Succeeded(10)
        val v2 = v.flatMap { Succeeded(it + 5) }
        val v3 = v2.flatMap { Succeeded(it + 5) }
        Assertions.assertThat(v3).isEqualTo(Succeeded(20))
    }

    @Test
    fun `flatMap return Failure if failed`() {
        val v: ResultStatus<Int, String> = Succeeded(10)
        val v2: ResultStatus<Int, String> = v.flatMap { Failed("failed") }
        val v3 = v2.flatMap { Succeeded(it + 5) }
        Assertions.assertThat(v3).isEqualTo(Failed("failed"))
    }

    @Test
    fun `flatMap return Failure if failed when first`() {
        val v: ResultStatus<Int, String> = Failed("first failed")
        val v2 = v.flatMap { Succeeded(it + 5) }
        val v3 = v2.flatMap { Succeeded(it + 5) }
        Assertions.assertThat(v3).isEqualTo(Failed("first failed"))
    }

    @Test
    fun `getOrElse return original if succeeded`() {
        val v: ResultStatus<Int, String> = Succeeded(10)
        Assertions.assertThat(v.getOrElse { 1 }).isEqualTo(10)
    }

    @Test
    fun `getOrElse return default value if failed`() {
        val v: ResultStatus<Int, String> = Failed("failed")
        Assertions.assertThat(v.getOrElse { 1 }).isEqualTo(1)
    }
}