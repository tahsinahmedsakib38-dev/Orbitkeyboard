package com.orbit.keyboard

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.provider.Settings
import android.view.inputmethod.InputMethodManager
import android.widget.*

class MainActivity : Activity() {
    override fun onCreate(b: Bundle?) {
        super.onCreate(b)
        val p = getSharedPreferences("orbit", Context.MODE_PRIVATE)
        val d = resources.displayMetrics.density
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding((20 * d).toInt(), (24 * d).toInt(), (20 * d).toInt(), (40 * d).toInt())
            setBackgroundColor(0xFF0B0B0D.toInt())
        }
        setContentView(ScrollView(this).apply { addView(root); setBackgroundColor(0xFF0B0B0D.toInt()) })
        fun title(t: String) = root.addView(TextView(this).apply {
            text = t; setTextColor(Color.WHITE); textSize = 17f; setPadding(0, (18 * d).toInt(), 0, (6 * d).toInt()) })
        fun btn(t: String, a: () -> Unit) = root.addView(Button(this).apply { text = t; setOnClickListener { a() } })
        fun radio(t: String, opts: List<Pair<String, String>>, key: String, def: String) {
            title(t)
            val g = RadioGroup(this)
            opts.forEach { (id, name) ->
                g.addView(RadioButton(this).apply {
                    text = name; setTextColor(Color.WHITE); tag = id
                    isChecked = p.getString(key, def) == id
                    setOnClickListener { p.edit().putString(key, id).apply() }
                })
            }
            root.addView(g)
        }
        fun toggle(t: String, key: String, def: Boolean) = root.addView(Switch(this).apply {
            text = t; setTextColor(Color.WHITE); isChecked = p.getBoolean(key, def)
            setOnCheckedChangeListener { _, v -> p.edit().putBoolean(key, v).apply() } })
        fun seek(t: String, key: String, min: Int, max: Int, def: Int) {
            val lbl = TextView(this).apply { setTextColor(Color.WHITE); setPadding(0, (12 * d).toInt(), 0, 0) }
            val cur = p.getInt(key, def)
            lbl.text = "$t: $cur"
            root.addView(lbl)
            root.addView(SeekBar(this).apply {
                this.max = max - min; progress = cur - min
                setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                    override fun onProgressChanged(s: SeekBar?, v: Int, u: Boolean) {
                        p.edit().putInt(key, v + min).apply(); lbl.text = "$t: ${v + min}" }
                    override fun onStartTrackingTouch(s: SeekBar?) {}
                    override fun onStopTrackingTouch(s: SeekBar?) {}
                })
            })
        }

        title("Orbit বাংলা কিবোর্ড")
        btn("১) কিবোর্ড চালু করুন") { startActivity(Intent(Settings.ACTION_INPUT_METHOD_SETTINGS)) }
        btn("২) কিবোর্ড সিলেক্ট করুন") {
            (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).showInputMethodPicker() }
        root.addView(EditText(this).apply {
            hint = "এখানে টাইপ করে টেস্ট করুন"; setTextColor(Color.WHITE); setHintTextColor(Color.GRAY) })
        radio("Theme", Themes.all.map { it.id to it.name }, "theme", "orbit")
        radio("Key Caps", listOf("solid" to "Solid", "glass" to "Glass", "float" to "Float"), "style", "glass")
        toggle("Key Borders", "borders", true)
        seek("Key Corner Radius (dp)", "radius", 0, 28, 15)
        seek("Key Fill Opacity (%)", "opacity", 5, 100, 28)
        toggle("Show Key Hints", "hints", true)
        seek("Key Label Scale (%)", "scale", 70, 140, 100)
    }
}
