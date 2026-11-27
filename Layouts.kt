package com.orbit.keyboard

class Key(val n: String, val s: String = n.uppercase(), val h: String = "",
          val code: Int = 0, val w: Float = 1f)

object Layouts {
    private fun en(n: String, h: String = "") = Key(n, n.uppercase(), h)
    private fun bn(n: String, s: String) = Key(n, s, s)
    private fun pad() = Key("", "", "", KeyboardView.BLANK, 0.5f)
    private fun sp(code: Int, w: Float = 1f, label: String = "") = Key(label, label, "", code, w)

    fun get(bangla: Boolean, sym: Boolean): List<List<Key>> {
        val dot = if (bangla) Key("।", "।") else Key(".", ".")
        val bottom = listOf(
            sp(KeyboardView.SYM, 1.3f), sp(KeyboardView.LANG), Key(",", ","),
            sp(KeyboardView.SPACE, 4f), dot, sp(KeyboardView.ENTER, 1.3f))
        if (sym) {
            val digits = if (bangla) "১২৩৪৫৬৭৮৯০" else "1234567890"
            return listOf(
                digits.map { Key(it.toString(), it.toString()) },
                "@#\$%&-+()".map { Key(it.toString(), it.toString()) },
                listOf(sp(KeyboardView.BLANK, 1.5f)) +
                    "*\"':;!?".map { Key(it.toString(), it.toString()) } + sp(KeyboardView.DEL, 1.5f),
                bottom)
        }
        return if (bangla) listOf(
            listOf(bn("ঙ","ং"), bn("য","য়"), bn("ড","ঢ"), bn("প","ফ"), bn("ট","ঠ"),
                   bn("চ","ছ"), bn("জ","ঝ"), bn("হ","ঞ"), bn("গ","ঘ"), bn("ড়","ঢ়")),
            listOf(bn("ৃ","ঋ"), bn("ু","ূ"), bn("ি","ী"), bn("ব","ভ"), bn("্","।"),
                   bn("া","অ"), bn("ক","খ"), bn("ত","থ"), bn("দ","ধ")),
            listOf(sp(KeyboardView.SHIFT, 1.5f), bn("ঁ","ঃ"), bn("ো","ৌ"), bn("ে","ৈ"),
                   bn("র","ল"), bn("ন","ণ"), bn("স","ষ"), bn("ম","শ"), sp(KeyboardView.DEL, 1.5f)),
            bottom)
        else listOf(
            listOf(en("q","1"), en("w","2"), en("e","3"), en("r","4"), en("t","5"),
                   en("y","6"), en("u","7"), en("i","8"), en("o","9"), en("p","0")),
            listOf(pad(), en("a","@"), en("s","#"), en("d","&"), en("f","*"), en("g","-"),
                   en("h","!"), en("j","?"), en("k","("), en("l",")"), pad()),
            listOf(sp(KeyboardView.SHIFT, 1.5f), en("z"), en("x"), en("c"), en("v"),
                   en("b"), en("n"), en("m"), sp(KeyboardView.DEL, 1.5f)),
            bottom)
    }
}
