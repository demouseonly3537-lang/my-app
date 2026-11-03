package io.nekohasekai.sagernet.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

/**
 * First screen shown on every cold start of the app.
 *
 * The app stays locked behind an access key until the correct key is entered.
 * Once unlocked, the unlock flag is stored so the user is not asked again on
 * later launches (change [REQUIRE_KEY_EVERY_LAUNCH] to true if every launch
 * should ask again instead of only the first one).
 */
class KeyLockActivity : AppCompatActivity() {

    companion object {
        // Change this to update the required access key.
        private const val ACCESS_KEY = "TPu8BbJMyBbwFo1ncu7xuHfPREC8vRSKLS"

        private const val PREFS_NAME = "key_lock_prefs"
        private const val PREF_UNLOCKED = "unlocked"

        // Set to true if you want the key to be required every single time
        // the app is opened, not just the first time.
        private const val REQUIRE_KEY_EVERY_LAUNCH = false

        fun isUnlocked(context: Context): Boolean {
            if (REQUIRE_KEY_EVERY_LAUNCH) return false
            val prefs = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
            return prefs.getBoolean(PREF_UNLOCKED, false)
        }

        private fun setUnlocked(context: Context) {
            val prefs = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
            prefs.edit().putBoolean(PREF_UNLOCKED, true).apply()
        }
    }

    private lateinit var keyInput: EditText
    private lateinit var errorText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // If already unlocked, skip straight to the main app.
        if (isUnlocked(this)) {
            goToMain()
            return
        }

        setContentView(buildLayout())
    }

    private fun buildLayout(): View {
        val density = resources.displayMetrics.density
        fun dp(v: Int) = (v * density).toInt()

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(dp(32), dp(32), dp(32), dp(32))
        }

        val title = TextView(this).apply {
            text = "Enter Access Key"
            textSize = 20f
            gravity = Gravity.CENTER
            setPadding(0, 0, 0, dp(24))
        }
        root.addView(title)

        keyInput = EditText(this).apply {
            hint = "Access key"
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            setSingleLine(true)
        }
        root.addView(keyInput)

        errorText = TextView(this).apply {
            setTextColor(0xFFE53935.toInt())
            visibility = View.GONE
            setPadding(0, dp(8), 0, 0)
        }
        root.addView(errorText)

        val submit = Button(this).apply {
            text = "Unlock"
            setOnClickListener { attemptUnlock() }
        }
        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply { topMargin = dp(16) }
        root.addView(submit, params)

        return root
    }

    private fun attemptUnlock() {
        val entered = keyInput.text.toString().trim()
        if (entered == ACCESS_KEY) {
            setUnlocked(this)
            goToMain()
        } else {
            errorText.text = "Incorrect key. Please try again."
            errorText.visibility = View.VISIBLE
            keyInput.text?.clear()
            Toast.makeText(this, "Incorrect key", Toast.LENGTH_SHORT).show()
        }
    }

    private fun goToMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
