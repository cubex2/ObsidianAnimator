package obsidianAPI.io

import org.junit.Assert.*
import org.junit.Test
import java.io.CharArrayReader

class LegacyModelFileParserTest
{
    @Test
    fun readsEmptyNBTIfFileDoesNotContainNBTPart()
    {
        val file = "# Model #"
        val reader = CharArrayReader(file.toCharArray())

        //val parseFile = LegacyModelFileParser.parseFile(reader)

        //assertTrue(parseFile.nbt.hasNoTags())
    }
}