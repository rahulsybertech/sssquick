package com.ssspvtltd.quick.widgets

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.ssspvtltd.quick.R
import com.ssspvtltd.quick.databinding.IncludeToolbarBinding
import com.ssspvtltd.quick.utils.extension.dp

class AppToolbar : ConstraintLayout {
    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initView(attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(
        context, attrs, defStyle
    ) {
        initView(attrs)
    }

    private val binding = IncludeToolbarBinding.inflate(LayoutInflater.from(context), this)
    init {
        //val view = View.inflate(context, R.layout.include_toolbar, this)
        this.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, 56.dp)
        this.minHeight = 56.dp
        this.setBackgroundColor(context.getColor(R.color.deep_orange_800))
    }

    private fun initView(attrs: AttributeSet?) {
        if (attrs != null) {
            val ta: TypedArray = context.obtainStyledAttributes(attrs, R.styleable.AppToolbar)
            try {
                val hideNavigation = ta.getBoolean(R.styleable.AppToolbar_hideNavigation, false)
                val navigationType = ta.getInt(R.styleable.AppToolbar_navigationType, 1)

                val titleType = ta.getInt(R.styleable.AppToolbar_titleType, 0)
                val title = ta.getString(R.styleable.AppToolbar_title)
                    ?: resources.getString(R.string.app_name)

                val isNotificationVisible =
                    ta.getBoolean(R.styleable.AppToolbar_isNotificationVisible, false)
                val isLocationVisible =
                    ta.getBoolean(R.styleable.AppToolbar_isLocationVisible, false)
                val isLogoutVisible =
                    ta.getBoolean(R.styleable.AppToolbar_isLogoutVisible, false)

                val isCartVisible = ta.getBoolean(R.styleable.AppToolbar_isCartVisible, false)
                val cartCount = ta.getInt(R.styleable.AppToolbar_cartCount, 0)

                //Update UI
                binding.btnNavigation.visibility = if (hideNavigation) GONE else VISIBLE
                binding.btnNavigation.setImageResource(if (navigationType == 0) R.drawable.ic_menu else R.drawable.ic_baseline_keyboard_backspace_24)
                binding.logo.visibility = if (titleType == 0) GONE else VISIBLE
                binding.tvTitle.visibility = if (titleType == 1) GONE else VISIBLE
                binding.btnNotification.visibility = if (isNotificationVisible) VISIBLE else GONE
                binding.btnLocation.visibility = if (isLocationVisible) VISIBLE else GONE
                binding.btnLogout.visibility = if (isLogoutVisible) VISIBLE else GONE
                setIsCartVisible(isCartVisible)
                setTitle(title)
                setCartCount(cartCount)
                if (!isInEditMode && navigationType == 1) {
                    setUpDefaultListeners()
                }
            } finally {
                ta.recycle()
            }
        }
    }
    /*
     * Handle default Back & Cart Buttons
     * This functions can be removed depend upon project
     * */
    fun setUpDefaultListeners() {
        binding.btnNavigation.setOnClickListener {
            (context as? AppCompatActivity)?.onBackPressed()
        }
    }

    fun setNavigationClickListener(listener: OnClickListener) {
        binding.btnNavigation.setOnClickListener(listener)
    }

    fun setNotificationClickListener(listener: OnClickListener) {
        binding.btnNotification.setOnClickListener(listener)
    }

    fun setLocationClickListener(listener: OnClickListener) {
        binding.btnLocation.setOnClickListener(listener)
    }

    fun setLogoutClickListener(listener: OnClickListener) {
        binding.btnLogout.setOnClickListener(listener)
    }

    fun setCartClickListener(listener: OnClickListener) {
        binding.btnCart.setOnClickListener(listener)
    }

    fun setTitle(title: String) {
        binding.tvTitle.text = title
    }

    fun setTitle(@StringRes titleRes: Int) {
        binding.tvTitle.setText(titleRes)
    }

    fun setIsCartVisible(isVisible: Boolean) {
        binding.btnCart.visibility = if (isVisible) VISIBLE else GONE
    }

    fun setCartCount(count: Int) {
        binding.btnCart.setImageResource(
            if (count > 0) R.drawable.ic_baseline_cart_with_brick_24
            else R.drawable.ic_baseline_cart_24
        )
    }
}