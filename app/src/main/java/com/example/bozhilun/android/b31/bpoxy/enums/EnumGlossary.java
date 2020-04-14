package com.example.bozhilun.android.b31.bpoxy.enums;

/**
 * Created by Administrator on 2017/9/18.
 */

public enum EnumGlossary {
    OSHAHS(0),
    BREATHBREAK(1),
    LOWOXGEN(2),
    HEART(3),
    RATEVARABLE(4),
    SLEEP(5),
    LOWREAMIN(6),
    SLEEPBREATHBREAKTIP(7),
    BREATH(8),
    OXGEN(9);
    int value;

    EnumGlossary(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static EnumGlossary getEnum(int values) {
        switch (values) {
            case 0:
                return OSHAHS;
            case 1:
                return BREATHBREAK;
            case 2:
                return LOWOXGEN;
            case 3:
                return HEART;
            case 4:
                return RATEVARABLE;
            case 5:
                return SLEEP;
            case 6:
                return LOWREAMIN;
            case 7:
                return SLEEPBREATHBREAKTIP;
            case 8:
                return BREATH;
            case 9:
                return OXGEN;

        }
        return OSHAHS;
    }

}

