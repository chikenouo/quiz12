package com.example.quiz12.constants;

public enum QuesType {
    SINGLE("Single"),//
    MULTI("Multi"),//
    TEXT("Text");//

    private String type;

    private QuesType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    // 因為ENUM不能new 所以要用static 讓其他地方可以用
    public static boolean checkType(String type) {
        for (QuesType item : QuesType.values()) {
            if (type.equalsIgnoreCase(item.getType())) {
                return true;
            }
        }
        return false;
    }
}
