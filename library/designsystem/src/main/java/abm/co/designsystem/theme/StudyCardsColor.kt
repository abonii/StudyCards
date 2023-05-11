package abm.co.designsystem.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

internal val LightColors = StudyCardsColor(
    primary = Color(0xFF_00A3F6),
    textPrimary = Color(0xFF_1B1B1B),
    textSecondary = Color(0xFF_9B9B9B),
    backgroundPrimary = Color(0xFF_FFFFFF),
    backgroundSecondary = Color(0xFF_F6F7FF),
    buttonPrimary = Color(0xFF_2970E5),
    buttonDisabled = Color(0xFF_FFFFFF),
    onyx = Color(0xFF_4F4F4F),
    skyBlue = Color(0xFF_87CEEB),
    silver = Color(0xFF_E0E0E0),
    stroke = Color(0xFF_E6E6E6),
    selfish = Color(0xFF_60628B),
    grayishBlue = Color(0xFF_AFB5C1),
    blueMiddle = Color(0xFF_AFCDFB),
    middleGray = Color(0xFF_B7B7B7),
    gray = Color(0xFF_F2F2F2),
    grayishBlack = Color(0xFF_828282),
    grayishWhite = Color(0xFF_BDBDBD),
    milky = Color(0xFF_F8F8F8),
    opposition = Color(0xFF_000000),
    pressed = Color(0x33_F8F8FF),
    success = Color(0xFF_27AE60),
    error = Color(0xFF_F2376F),
    uncertain = Color(0xFF_CFB323),
    known = Color(0xFF_03DC03),
    unknown = Color(0xFF_FF0000),
    isLight = true
)

internal val DarkColors = StudyCardsColor(
    primary = Color(0xFF_002F7A),
    textPrimary = Color(0xFF_FFFFFF),
    textSecondary = Color(0xFF_98989F),
    backgroundPrimary = Color(0xFF_1D1D1D),
    backgroundSecondary = Color(0xFF_2C2C2E),
    buttonPrimary = Color(0xFF_1C3D70),
    buttonDisabled = Color(0xFF_5A5A5F),
    onyx = Color(0xFF_BBBBBB),
    skyBlue = Color(0xFF_4E7A96),
    silver = Color(0xFF_9E9E9E),
    stroke = Color(0xFF_A8A8A8),
    selfish = Color(0xFF_000000),
    grayishBlue = Color(0xFF_AFB5C1),
    middleGray = Color(0xFF_555555),
    gray = Color(0xFF_F2F2F2),
    grayishBlack = Color(0xFF_828282),
    grayishWhite = Color(0xFF_BDBDBD),
    milky = Color(0xFF_070707),
    blueMiddle = Color(0xFF_6E8CA6),
    opposition = Color(0xFF_FFFFFF),
    pressed = Color(0xFF_3A3A3C),
    success = Color(0xFF_32D74B),
    error = Color(0xFF_FF453A),
    uncertain = Color(0xFF_CFB323),
    known = Color(0xFF_03DC03),
    unknown = Color(0xFF_FF0000),
    isLight = false
)

@Immutable
class StudyCardsColor(
    primary: Color,
    textPrimary: Color,
    textSecondary: Color,
    onyx: Color,
    skyBlue: Color,
    silver: Color,
    stroke: Color,
    selfish: Color,
    grayishBlue: Color,
    middleGray: Color,
    gray: Color,
    grayishBlack: Color,
    grayishWhite: Color,
    milky: Color,
    backgroundPrimary: Color,
    backgroundSecondary: Color,
    buttonPrimary: Color,
    blueMiddle: Color,
    buttonDisabled: Color,
    opposition: Color,
    pressed: Color,
    success: Color,
    error: Color,
    uncertain: Color,
    known: Color,
    unknown: Color,
    isLight: Boolean,
) {
    var primary by mutableStateOf(primary)
        private set

    var textPrimary by mutableStateOf(textPrimary)
        private set

    var textSecondary by mutableStateOf(textSecondary)
        private set

    var onyx by mutableStateOf(onyx)
        private set

    var skyBlue by mutableStateOf(skyBlue)
        private set

    var silver by mutableStateOf(silver)
        private set

    var stroke by mutableStateOf(stroke)
        private set

    var selfish by mutableStateOf(selfish)
        private set

    var grayishBlue by mutableStateOf(grayishBlue)
        private set

    var middleGray by mutableStateOf(middleGray)
        private set

    var gray by mutableStateOf(gray)
        private set

    var grayishBlack by mutableStateOf(grayishBlack)
        private set

    var grayishWhite by mutableStateOf(grayishWhite)
        private set

    var milky by mutableStateOf(milky)
        private set

    var backgroundPrimary by mutableStateOf(backgroundPrimary)
        private set

    var backgroundSecondary by mutableStateOf(backgroundSecondary)
        private set

    var buttonPrimary by mutableStateOf(buttonPrimary)
        private set

    var blueMiddle by mutableStateOf(blueMiddle)
        private set

    var buttonDisabled by mutableStateOf(buttonDisabled)
        private set

    var opposition by mutableStateOf(opposition)
        private set

    var pressed by mutableStateOf(pressed)
        private set

    var success by mutableStateOf(success)
        private set

    var error by mutableStateOf(error)
        private set

    var uncertain by mutableStateOf(uncertain)
        private set

    var known by mutableStateOf(known)
        private set

    var unknown by mutableStateOf(unknown)
        private set

    var isLight by mutableStateOf(isLight)
        private set

    fun copy(
        primary: Color = this.primary,
        textPrimary: Color = this.textPrimary,
        textSecondary: Color = this.textSecondary,
        textOnyx: Color = this.onyx,
        textSkyBlue: Color = this.skyBlue,
        textSilver: Color = this.silver,
        stroke: Color = this.stroke,
        selfish: Color = this.selfish,
        grayishBlue: Color = this.grayishBlue,
        middleGray: Color = this.middleGray,
        gray: Color = this.gray,
        grayishBlack: Color = this.grayishBlack,
        grayishWhite: Color = this.grayishWhite,
        milky: Color = this.milky,
        backgroundPrimary: Color = this.backgroundPrimary,
        backgroundSecondary: Color = this.backgroundSecondary,
        buttonPrimary: Color = this.buttonPrimary,
        blueMiddle: Color = this.blueMiddle,
        buttonDisabled: Color = this.buttonDisabled,
        opposition: Color = this.opposition,
        pressed: Color = this.pressed,
        success: Color = this.success,
        error: Color = this.error,
        uncertain: Color = this.uncertain,
        known: Color = this.known,
        isLight: Boolean = this.isLight,
    ) = StudyCardsColor(
        primary = primary,
        textPrimary = textPrimary,
        textSecondary = textSecondary,
        onyx = textOnyx,
        skyBlue = textSkyBlue,
        silver = textSilver,
        stroke = stroke,
        selfish = selfish,
        middleGray = middleGray,
        gray = gray,
        grayishBlack = grayishBlack,
        grayishWhite = grayishWhite,
        milky = milky,
        grayishBlue = grayishBlue,
        backgroundPrimary = backgroundPrimary,
        backgroundSecondary = backgroundSecondary,
        buttonPrimary = buttonPrimary,
        blueMiddle = blueMiddle,
        buttonDisabled = buttonDisabled,
        opposition = opposition,
        pressed = pressed,
        success = success,
        error = error,
        uncertain = uncertain,
        known = known,
        unknown = unknown,
        isLight = isLight
    )

    fun updateColorsFrom(other: StudyCardsColor) {
        primary = other.primary
        textPrimary = other.textPrimary
        textSecondary = other.textSecondary
        onyx = other.onyx
        skyBlue = other.skyBlue
        silver = other.silver
        stroke = other.stroke
        selfish = other.selfish
        grayishBlue = other.grayishBlue
        middleGray = other.middleGray
        gray = other.gray
        grayishBlack = other.grayishBlack
        grayishWhite = other.grayishWhite
        milky = other.milky
        backgroundPrimary = other.backgroundPrimary
        backgroundSecondary = other.backgroundSecondary
        buttonPrimary = other.buttonPrimary
        buttonDisabled = other.buttonDisabled
        blueMiddle = other.blueMiddle
        opposition = other.opposition
        pressed = other.pressed
        success = other.success
        error = other.error
        uncertain = other.uncertain
        known = other.known
        unknown = other.unknown
        isLight = other.isLight
    }
}

val LocalColors = staticCompositionLocalOf { LightColors }