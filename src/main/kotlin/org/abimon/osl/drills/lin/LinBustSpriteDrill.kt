package org.abimon.osl.drills.lin

import org.abimon.osl.AllButMatcher
import org.abimon.osl.OpenSpiralLanguageParser
import org.abimon.osl.drills.DrillHead
import org.abimon.spiral.core.objects.game.hpa.HopesPeakDRGame
import org.abimon.spiral.core.objects.game.hpa.UnknownHopesPeakGame
import org.abimon.spiral.core.objects.scripting.lin.LinScript
import org.abimon.spiral.core.objects.scripting.lin.SpriteEntry
import org.parboiled.Action
import org.parboiled.Rule
import kotlin.reflect.KClass

object LinBustSpriteDrill : DrillHead<LinScript> {
    val cmd: String = "LIN-BUST-SPRITE"
    val NAME = AllButMatcher(charArrayOf(':', '\n'))
    val NUMERAL_REGEX = "\\d+".toRegex()

    override val klass: KClass<LinScript> = LinScript::class

    override fun OpenSpiralLanguageParser.syntax(): Rule =
            FirstOf(
                    Sequence(
                            clearTmpStack(cmd),
                            "Display sprite for ",
                            pushTmpAction(cmd, this@LinBustSpriteDrill),
                            FirstOf(
                                    Parameter(cmd),
                                    Sequence(
                                            OneOrMore(Digit()),
                                            pushTmpAction(cmd)
                                    )
                            ),
                            Action<Any> {
                                val name = peekTmpAction(cmd)?.toString() ?: ""
                                return@Action name in customIdentifiers || name in (game as? HopesPeakDRGame ?: UnknownHopesPeakGame).characterIdentifiers || name.matches(NUMERAL_REGEX)
                            },
                            " with ID ",
                            OneOrMore(Digit()),
                            pushTmpAction(cmd),
                            pushTmpStack(cmd)
                    ),
                    Sequence(
                            clearTmpStack(cmd),
                            "Display",
                            pushTmpAction(cmd, this@LinBustSpriteDrill),
                            Whitespace(),
                            FirstOf(
                                    Parameter(cmd),
                                    Sequence(
                                            OneOrMore(Digit()),
                                            pushTmpAction(cmd)
                                    )
                            ),
                            Action<Any> {
                                val name = peekTmpAction(cmd)?.toString() ?: ""
                                return@Action name in customIdentifiers || name in (game as? HopesPeakDRGame ?: UnknownHopesPeakGame).characterIdentifiers || name.matches(NUMERAL_REGEX)
                            },
                            Whitespace(),
                            "pose",
                            Whitespace(),
                            OneOrMore(Digit()),
                            pushTmpAction(cmd),
                            pushTmpStack(cmd)
                    )
            )

    override fun operate(parser: OpenSpiralLanguageParser, rawParams: Array<Any>): LinScript {
        val characterStr = rawParams[0].toString()
        val character = parser.customIdentifiers[characterStr] ?: (parser.game as? HopesPeakDRGame ?: UnknownHopesPeakGame).characterIdentifiers[characterStr] ?: characterStr.toIntOrNull() ?: 0
        val sprite = rawParams[1].toString().toIntOrNull() ?: 0

        return SpriteEntry(0, character, sprite, 1, 2)
    }
}