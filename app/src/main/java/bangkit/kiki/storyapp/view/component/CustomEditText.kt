package bangkit.kiki.storyapp.view.component

import android.content.Context
import android.graphics.Canvas
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Patterns
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import bangkit.kiki.storyapp.R

class CustomEditText @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
): AppCompatEditText(context, attrs){
    private var minLength: Int = 0
    private var customHint: String = ""
    private var inputType: String = ""

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        hint = customHint
        textAlignment = View.TEXT_ALIGNMENT_VIEW_START
    }

    init {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.CustomEditText,
            0, 0
        ).apply {
            try {
                minLength = getInt(R.styleable.CustomEditText_minLength, 0)
                customHint = getString(R.styleable.CustomEditText_customHint) ?: ""
                inputType = getString(R.styleable.CustomEditText_inputType) ?: ""
            } finally {
                recycle()
            }
        }

        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(chars: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun onTextChanged(chars: CharSequence?, start: Int, before: Int, count: Int) {
                if (inputType == "password") {
                    if (chars.toString().length < minLength) {
                        this@CustomEditText.error = context.getString(R.string.password_min_length_error, minLength)
                    } else {
                        this@CustomEditText.error = null
                    }
                }

                if (inputType == "email") {
                    if (!chars?.let { Patterns.EMAIL_ADDRESS.matcher(it).matches() }!!) {
                        this@CustomEditText.error = "Invalid email"
                    } else {
                        this@CustomEditText.error = null
                    }
                }
            }

            override fun afterTextChanged(chars: Editable?) {}
        })
    }
}