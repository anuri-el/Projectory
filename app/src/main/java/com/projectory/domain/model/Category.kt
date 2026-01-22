package com.projectory.domain.model

enum class Category(val displayName: String, val icon: String) {
    PERSONAL("Personal", "👤"),
    WORK("Work", "💼"),
    HEALTH("Health", "❤️"),
    EDUCATION("Education", "📚"),
    CREATIVE("Creative", "🎨"),
    HOME("Home", "🏠"),
    FINANCE("Finance", "💰"),
    SOCIAL("Social", "👥"),
    HOBBY("Hobby", "🎮"),
    OTHER("Other", "📌");

    companion object {
        fun fromString(value: String): Category {
            return entries.find { it.name.equals(value, ignoreCase = true) } ?: OTHER
        }
    }
}