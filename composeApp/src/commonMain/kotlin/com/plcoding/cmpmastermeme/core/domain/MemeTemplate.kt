package com.plcoding.cmpmastermeme.core.domain

import cmpmastermeme.composeapp.generated.resources.Res
import cmpmastermeme.composeapp.generated.resources.meme_template_01
import cmpmastermeme.composeapp.generated.resources.meme_template_02
import cmpmastermeme.composeapp.generated.resources.meme_template_03
import cmpmastermeme.composeapp.generated.resources.meme_template_04
import cmpmastermeme.composeapp.generated.resources.meme_template_05
import cmpmastermeme.composeapp.generated.resources.meme_template_06
import cmpmastermeme.composeapp.generated.resources.meme_template_07
import cmpmastermeme.composeapp.generated.resources.meme_template_08
import cmpmastermeme.composeapp.generated.resources.meme_template_09
import cmpmastermeme.composeapp.generated.resources.meme_template_10
import cmpmastermeme.composeapp.generated.resources.meme_template_11
import cmpmastermeme.composeapp.generated.resources.meme_template_12
import cmpmastermeme.composeapp.generated.resources.meme_template_13
import cmpmastermeme.composeapp.generated.resources.meme_template_14
import cmpmastermeme.composeapp.generated.resources.meme_template_15
import cmpmastermeme.composeapp.generated.resources.meme_template_16
import cmpmastermeme.composeapp.generated.resources.meme_template_17
import cmpmastermeme.composeapp.generated.resources.meme_template_18
import cmpmastermeme.composeapp.generated.resources.meme_template_19
import cmpmastermeme.composeapp.generated.resources.meme_template_20
import cmpmastermeme.composeapp.generated.resources.meme_template_21
import cmpmastermeme.composeapp.generated.resources.meme_template_22
import cmpmastermeme.composeapp.generated.resources.meme_template_23
import cmpmastermeme.composeapp.generated.resources.meme_template_24
import cmpmastermeme.composeapp.generated.resources.meme_template_25
import cmpmastermeme.composeapp.generated.resources.meme_template_26
import cmpmastermeme.composeapp.generated.resources.meme_template_27
import cmpmastermeme.composeapp.generated.resources.meme_template_28
import cmpmastermeme.composeapp.generated.resources.meme_template_29
import cmpmastermeme.composeapp.generated.resources.meme_template_30
import cmpmastermeme.composeapp.generated.resources.meme_template_31
import cmpmastermeme.composeapp.generated.resources.meme_template_32
import cmpmastermeme.composeapp.generated.resources.meme_template_33
import cmpmastermeme.composeapp.generated.resources.meme_template_34
import cmpmastermeme.composeapp.generated.resources.meme_template_35
import cmpmastermeme.composeapp.generated.resources.meme_template_36
import cmpmastermeme.composeapp.generated.resources.meme_template_37
import cmpmastermeme.composeapp.generated.resources.meme_template_38
import cmpmastermeme.composeapp.generated.resources.meme_template_39
import cmpmastermeme.composeapp.generated.resources.meme_template_40
import cmpmastermeme.composeapp.generated.resources.meme_template_41
import cmpmastermeme.composeapp.generated.resources.meme_template_42
import cmpmastermeme.composeapp.generated.resources.meme_template_43
import cmpmastermeme.composeapp.generated.resources.meme_template_44
import cmpmastermeme.composeapp.generated.resources.meme_template_45
import cmpmastermeme.composeapp.generated.resources.meme_template_46
import cmpmastermeme.composeapp.generated.resources.meme_template_47
import cmpmastermeme.composeapp.generated.resources.meme_template_48
import cmpmastermeme.composeapp.generated.resources.meme_template_49
import cmpmastermeme.composeapp.generated.resources.meme_template_50
import cmpmastermeme.composeapp.generated.resources.meme_template_51
import cmpmastermeme.composeapp.generated.resources.meme_template_52
import org.jetbrains.compose.resources.DrawableResource

enum class MemeTemplate(val id: String) {
    TEMPLATE_01("meme_template_01"),
    TEMPLATE_02("meme_template_02"),
    TEMPLATE_03("meme_template_03"),
    TEMPLATE_04("meme_template_04"),
    TEMPLATE_05("meme_template_05"),
    TEMPLATE_06("meme_template_06"),
    TEMPLATE_07("meme_template_07"),
    TEMPLATE_08("meme_template_08"),
    TEMPLATE_09("meme_template_09"),
    TEMPLATE_10("meme_template_10"),
    TEMPLATE_11("meme_template_11"),
    TEMPLATE_12("meme_template_12"),
    TEMPLATE_13("meme_template_13"),
    TEMPLATE_14("meme_template_14"),
    TEMPLATE_15("meme_template_15"),
    TEMPLATE_16("meme_template_16"),
    TEMPLATE_17("meme_template_17"),
    TEMPLATE_18("meme_template_18"),
    TEMPLATE_19("meme_template_19"),
    TEMPLATE_20("meme_template_20"),
    TEMPLATE_21("meme_template_21"),
    TEMPLATE_22("meme_template_22"),
    TEMPLATE_23("meme_template_23"),
    TEMPLATE_24("meme_template_24"),
    TEMPLATE_25("meme_template_25"),
    TEMPLATE_26("meme_template_26"),
    TEMPLATE_27("meme_template_27"),
    TEMPLATE_28("meme_template_28"),
    TEMPLATE_29("meme_template_29"),
    TEMPLATE_30("meme_template_30"),
    TEMPLATE_31("meme_template_31"),
    TEMPLATE_32("meme_template_32"),
    TEMPLATE_33("meme_template_33"),
    TEMPLATE_34("meme_template_34"),
    TEMPLATE_35("meme_template_35"),
    TEMPLATE_36("meme_template_36"),
    TEMPLATE_37("meme_template_37"),
    TEMPLATE_38("meme_template_38"),
    TEMPLATE_39("meme_template_39"),
    TEMPLATE_40("meme_template_40"),
    TEMPLATE_41("meme_template_41"),
    TEMPLATE_42("meme_template_42"),
    TEMPLATE_43("meme_template_43"),
    TEMPLATE_44("meme_template_44"),
    TEMPLATE_45("meme_template_45"),
    TEMPLATE_46("meme_template_46"),
    TEMPLATE_47("meme_template_47"),
    TEMPLATE_48("meme_template_48"),
    TEMPLATE_49("meme_template_49"),
    TEMPLATE_50("meme_template_50"),
    TEMPLATE_51("meme_template_51"),
    TEMPLATE_52("meme_template_52");

    val drawableResource: DrawableResource
        get() = when (this) {
            TEMPLATE_01 -> Res.drawable.meme_template_01
            TEMPLATE_02 -> Res.drawable.meme_template_02
            TEMPLATE_03 -> Res.drawable.meme_template_03
            TEMPLATE_04 -> Res.drawable.meme_template_04
            TEMPLATE_05 -> Res.drawable.meme_template_05
            TEMPLATE_06 -> Res.drawable.meme_template_06
            TEMPLATE_07 -> Res.drawable.meme_template_07
            TEMPLATE_08 -> Res.drawable.meme_template_08
            TEMPLATE_09 -> Res.drawable.meme_template_09
            TEMPLATE_10 -> Res.drawable.meme_template_10
            TEMPLATE_11 -> Res.drawable.meme_template_11
            TEMPLATE_12 -> Res.drawable.meme_template_12
            TEMPLATE_13 -> Res.drawable.meme_template_13
            TEMPLATE_14 -> Res.drawable.meme_template_14
            TEMPLATE_15 -> Res.drawable.meme_template_15
            TEMPLATE_16 -> Res.drawable.meme_template_16
            TEMPLATE_17 -> Res.drawable.meme_template_17
            TEMPLATE_18 -> Res.drawable.meme_template_18
            TEMPLATE_19 -> Res.drawable.meme_template_19
            TEMPLATE_20 -> Res.drawable.meme_template_20
            TEMPLATE_21 -> Res.drawable.meme_template_21
            TEMPLATE_22 -> Res.drawable.meme_template_22
            TEMPLATE_23 -> Res.drawable.meme_template_23
            TEMPLATE_24 -> Res.drawable.meme_template_24
            TEMPLATE_25 -> Res.drawable.meme_template_25
            TEMPLATE_26 -> Res.drawable.meme_template_26
            TEMPLATE_27 -> Res.drawable.meme_template_27
            TEMPLATE_28 -> Res.drawable.meme_template_28
            TEMPLATE_29 -> Res.drawable.meme_template_29
            TEMPLATE_30 -> Res.drawable.meme_template_30
            TEMPLATE_31 -> Res.drawable.meme_template_31
            TEMPLATE_32 -> Res.drawable.meme_template_32
            TEMPLATE_33 -> Res.drawable.meme_template_33
            TEMPLATE_34 -> Res.drawable.meme_template_34
            TEMPLATE_35 -> Res.drawable.meme_template_35
            TEMPLATE_36 -> Res.drawable.meme_template_36
            TEMPLATE_37 -> Res.drawable.meme_template_37
            TEMPLATE_38 -> Res.drawable.meme_template_38
            TEMPLATE_39 -> Res.drawable.meme_template_39
            TEMPLATE_40 -> Res.drawable.meme_template_40
            TEMPLATE_41 -> Res.drawable.meme_template_41
            TEMPLATE_42 -> Res.drawable.meme_template_42
            TEMPLATE_43 -> Res.drawable.meme_template_43
            TEMPLATE_44 -> Res.drawable.meme_template_44
            TEMPLATE_45 -> Res.drawable.meme_template_45
            TEMPLATE_46 -> Res.drawable.meme_template_46
            TEMPLATE_47 -> Res.drawable.meme_template_47
            TEMPLATE_48 -> Res.drawable.meme_template_48
            TEMPLATE_49 -> Res.drawable.meme_template_49
            TEMPLATE_50 -> Res.drawable.meme_template_50
            TEMPLATE_51 -> Res.drawable.meme_template_51
            TEMPLATE_52 -> Res.drawable.meme_template_52
        }

    companion object {
        fun fromId(id: String): MemeTemplate? = entries.find { it.id == id }
        
        fun getAllTemplates(): List<MemeTemplate> = entries.toList()
    }
}