import org.assertj.core.api.Assertions
import kotlin.test.Test

internal class SampleTest {

    @Test
    fun changeName() {
        val old = Sample("abc")
        val new = old.changeName("def")

        Assertions.assertThat(old).isEqualTo(Sample("abc"))
        Assertions.assertThat(new).isEqualTo(Sample("def"))
    }
}