package com.orbit.keyboard

import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.*
import android.os.Handler
import android.os.Looper
import android.view.HapticFeedbackConstants
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager

class KeyboardView(private val ime: OrbitIME) : View(ime) {
    companion object {
        const val SHIFT = -1; const val DEL = -2; const val ENTER = -3; const val SYM = -4
        const val LANG = -5; const val SPACE = -6; const val BLANK = -7
    }
    private class Cell(val key: Key, val r: RectF, val row: Int, val col: Int)

    private val prefs = ime.getSharedPreferences("orbit", Context.MODE_PRIVATE)
    private val d = resources.displayMetrics.density
    private fun dp(v: Float) = v * d
    private val toolbarH = dp(40f)
    private val fill = Paint(Paint.ANTI_ALIAS_FLAG)
    private val stroke = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.STROKE; strokeWidth = dp(1f) }
    private val text = Paint(Paint.ANTI_ALIAS_FLAG).apply { textAlign = Paint.Align.CENTER }
    private val handler = Handler(Looper.getMainLooper())
    private val pressed = HashMap<Int, Int>()
    private var cells = listOf<Cell>()
    private var rows = listOf<List<Key>>()

    private var th = Themes.all[0]
    private var style = "glass"; private var borders = true; private var radius = 15f
    private var opacity = 28; private var scale = 100; private var hints = true
    private var bangla = false; private var sym = false
    private var shifted = false; private var caps = false; private var lastShift = 0L
    private var bnFace: Typeface? = null

    init {
        setLayerType(LAYER_TYPE_SOFTWARE, null)
        bnFace = try { Typeface.createFromAsset(ime.assets, "fonts/bn.ttf") } catch (e: Exception) { null }
        reload()
    }

    fun reload() {
        th = Themes.get(prefs.getString("theme", "orbit"))
        style = prefs.getString("style", "glass")!!
        borders = prefs.getBoolean("borders", true)
        radius = prefs.getInt("radius", 15).toFloat()
        opacity = prefs.getInt("opacity", 28)
        scale = prefs.getInt("scale", 100)
        hints = prefs.getBoolean("hints", true)
        bangla = prefs.getBoolean("bangla", false)
        sym = false; shifted = false; caps = false
        rebuild()
    }

    private fun rebuild() {
        rows = Layouts.get(bangla, sym)
        if (width > 0) layout()
        invalidate()
    }

    override fun onMeasure(w: Int, h: Int) {
        setMeasuredDimension(MeasureSpec.getSize(w), (toolbarH + dp(4 * 54f + 10f)).toInt())
    }
    override fun onSizeChanged(w: Int, h: Int, ow: Int, oh: Int) { layout() }

    private fun layout() {
        val gap = dp(4f)
        val rowH = (height - toolbarH - dp(6f)) / rows.size
        val list = ArrayList<Cell>()
        rows.forEachIndexed { r, row ->
            val total = row.sumOf { it.w.toDouble() }.toFloat()
            val unit = (width - gap * (row.size + 1)) / total
            var x = gap
            row.forEachIndexed { c, k ->
                val kw = unit * k.w
                val top = toolbarH + r * rowH
                list.add(Cell(k, RectF(x, top + gap / 2, x + kw, top + rowH - gap / 2), r, c))
                x += kw + gap
            }
        }
        cells = list
    }

    private fun mix(a: Int, b: Int, t: Float): Int {
        fun m(x: Int, y: Int) = (x + (y - x) * t).toInt()
        return Color.argb(255, m(Color.red(a), Color.red(b)), m(Color.green(a), Color.green(b)), m(Color.blue(a), Color.blue(b)))
    }
    private fun lum(c: Int) = (0.299 * Color.red(c) + 0.587 * Color.green(c) + 0.114 * Color.blue(c)) / 255

    private fun label(k: Key): String = when (k.code) {
        SHIFT -> if (caps) "⇪" else "⇧"
        DEL -> "⌫"; ENTER -> "⏎"; LANG -> "🌐"
        SYM -> if (sym) (if (bangla) "ক খ" else "ABC") else (if (bangla) "?১২৩" else "?123")
        SPACE -> if (bangla) "◄ জাতীয় ক্লাসিক ►" else "◄ English ►"
        else -> if (shifted) k.s else k.n
    }

    override fun onDraw(c: Canvas) {
        c.drawColor(th.bg)
        text.typeface = Typeface.DEFAULT; text.color = th.text; text.textSize = dp(20f)
        val ty = toolbarH / 2 - (text.descent() + text.ascent()) / 2
        c.drawText("›", dp(24f), ty, text)
        listOf("⌨", "🎨", "📋").forEachIndexed { i, s -> c.drawText(s, width - dp(22f) - i * dp(44f), ty, text) }

        cells.forEachIndexed { idx, cell ->
            val k = cell.key
            if (k.code == BLANK && k.n.isEmpty() && k.w == 0.5f) return@forEachIndexed
            if (k.code == BLANK && k.w == 1.5f) return@forEachIndexed
            val special = k.code < 0 && k.code != BLANK
            var col = if (special) th.special else th.keys[(cell.row * 5 + cell.col * 3 + cell.row * cell.col) % th.keys.size]
            if (k.code == ENTER) col = mix(th.special, 0xFFD02419.toInt(), 0.35f)
            if (pressed.containsValue(idx)) col = mix(col, Color.WHITE, 0.3f)
            val rad = dp(radius)
            fill.clearShadowLayer(); fill.style = Paint.Style.FILL
            fill.color = col
            if (style == "glass") fill.alpha = (opacity * 255 / 100).coerceIn(30, 255)
            if (style == "float") fill.setShadowLayer(dp(4f), 0f, dp(2f), 0x77000000)
            c.drawRoundRect(cell.r, rad, rad, fill)
            fill.clearShadowLayer()
            if (borders) {
                stroke.color = if (style == "glass") 0x66FFFFFF else 0x33000000
                c.drawRoundRect(cell.r, rad, rad, stroke)
            }
            val dark = lum(col) > 0.6 && style != "glass"
            val fg = if (dark) 0xFF1A1A1A.toInt() else th.text
            val s = label(k)
            text.color = fg
            text.typeface = if (s.isNotEmpty() && s[0].code in 0x0980..0x09FF) (bnFace ?: Typeface.DEFAULT) else Typeface.DEFAULT
            text.textSize = dp(if (k.code == SPACE) 14f else if (s.length > 1) 15f else 22f) * scale / 100f
            if (k.code == LANG || k.code == DEL || k.code == ENTER || k.code == SHIFT) text.textSize = dp(22f)
            c.drawText(s, cell.r.centerX(), cell.r.centerY() - (text.descent() + text.ascent()) / 2, text)
            if (hints && k.h.isNotEmpty() && !shifted && k.code == 0) {
                text.textSize = dp(10f); text.color = if (dark) 0xFF444444.toInt() else th.hint
                text.textAlign = Paint.Align.RIGHT
                c.drawText(k.h, cell.r.right - dp(6f), cell.r.top + dp(13f), text)
                text.textAlign = Paint.Align.CENTER
            }
        }
    }

    private fun hit(x: Float, y: Float): Int {
        val g = dp(3f)
        return cells.indexOfFirst { !(it.key.code == BLANK) && x >= it.r.left - g && x <= it.r.right + g && y >= it.r.top - g && y <= it.r.bottom + g }
    }

    private val repeat = object : Runnable { override fun run() { ime.del(); handler.postDelayed(this, 50) } }

    override fun onTouchEvent(e: MotionEvent): Boolean {
        when (e.actionMasked) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_POINTER_DOWN -> {
                val i = e.actionIndex; val x = e.getX(i); val y = e.getY(i)
                if (y < toolbarH) { toolbar(x); return true }
                val idx = hit(x, y)
                if (idx >= 0) {
                    performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                    pressed[e.getPointerId(i)] = idx
                    if (cells[idx].key.code == DEL) { ime.del(); handler.postDelayed(repeat, 400) }
                    invalidate()
                }
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_POINTER_UP -> {
                val idx = pressed.remove(e.getPointerId(e.actionIndex))
                handler.removeCallbacks(repeat)
                if (idx != null) { if (cells[idx].key.code != DEL) fire(cells[idx].key); invalidate() }
            }
            MotionEvent.ACTION_CANCEL -> { pressed.clear(); handler.removeCallbacks(repeat); invalidate() }
        }
        return true
    }

    private fun toolbar(x: Float) {
        if (x < dp(48f)) { ime.requestHideSelf(0); return }
        when (((width - x) / dp(44f)).toInt()) {
            0 -> (ime.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).showInputMethodPicker()
            1 -> ime.startActivity(Intent(ime, MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
            2 -> {
                val cb = ime.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                cb.primaryClip?.getItemAt(0)?.coerceToText(ime)?.let { ime.commit(it.toString()) }
            }
        }
    }

    private fun fire(k: Key) {
        when (k.code) {
            0 -> { ime.commit(if (shifted) k.s else k.n); if (shifted && !caps) { shifted = false } }
            SPACE -> ime.commit(" ")
            ENTER -> ime.enter()
            SHIFT -> {
                val now = System.currentTimeMillis()
                if (now - lastShift < 350) { caps = true; shifted = true }
                else if (caps) { caps = false; shifted = false } else shifted = !shifted
                lastShift = now
            }
            SYM -> { sym = !sym; rebuild() }
            LANG -> { bangla = !bangla; prefs.edit().putBoolean("bangla", bangla).apply(); shifted = false; caps = false; sym = false; rebuild() }
        }
    }
}
