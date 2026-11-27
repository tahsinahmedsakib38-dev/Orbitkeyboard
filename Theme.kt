package com.orbit.keyboard

class Theme(val id: String, val name: String, val bg: Int, val keys: IntArray,
            val special: Int, val text: Int, val hint: Int)

object Themes {
    val all = listOf(
        Theme("orbit", "Orbit", 0xFF1A1B1E.toInt(),
            intArrayOf(0xFF6E7075.toInt(), 0xFFA3A5A9.toInt(), 0xFF4B4D52.toInt(), 0xFFD02419.toInt(), 0xFF8A8C90.toInt(), 0xFF35373B.toInt()),
            0xFF3A3C41.toInt(), 0xFFF2F2F2.toInt(), 0xFFC9CACD.toInt()),
        Theme("slate", "Slate", 0xFF0F1013.toInt(), intArrayOf(0xFF2A2C31.toInt()),
            0xFF1E2024.toInt(), 0xFFFFFFFF.toInt(), 0xFF9A9CA3.toInt()),
        Theme("amoled", "AMOLED purple", 0xFF000000.toInt(), intArrayOf(0xFF241A3D.toInt(), 0xFF2E2150.toInt()),
            0xFF150F26.toInt(), 0xFFE4D7FF.toInt(), 0xFFA18CD6.toInt()),
        Theme("light", "Jatiya Light", 0xFFA9ABA9.toInt(), intArrayOf(0xFFC4C6C4.toInt()),
            0xFFB3B5B3.toInt(), 0xFF1E1E1E.toInt(), 0xFF555555.toInt())
    )
    fun get(id: String?) = all.firstOrNull { it.id == id } ?: all[0]
}
