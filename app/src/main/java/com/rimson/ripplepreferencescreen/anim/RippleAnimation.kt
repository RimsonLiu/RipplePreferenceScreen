package com.rimson.ripplepreferencescreen.anim

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.graphics.*
import android.os.Build
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup

@SuppressLint("ViewConstructor")
class RippleAnimation private constructor(context: Context, startX: Float, startY: Float, radius: Int) : View(context) {

	private lateinit var mBackground: Bitmap
	private var mRootView: ViewGroup
	private var mStartX = startX
	private var mStartY = startY
	private var mMaxRadius = 0f
	private var mStartRadius = radius
	private var mCurrentRadius = 0f
	private var mDuration = 0L
	private lateinit var mAnimationListener: Animator.AnimatorListener
	private lateinit var mOnAnimationEndListener: OnAnimationEndListener
	private lateinit var mAnimatorUpdateListener: ValueAnimator.AnimatorUpdateListener
	private var isStarted: Boolean = false
	private var mPaint: Paint

	init {
		mRootView = getActivityFromContext(context).window.decorView as ViewGroup
		mPaint = Paint()
		mPaint.isAntiAlias = true
		// 擦除
		mPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
		updateMaxRadius()
		initListener()
	}

	companion object {
		fun create(onClickView: View): RippleAnimation {
			val context = onClickView.context
			val newWidth = onClickView.width / 2
			val newHeight = onClickView.height / 2
			// 计算起点位置
			val startX = getAbsoluteX(onClickView) + newWidth
			val startY = getAbsoluteY(onClickView) + newHeight
			// 计算半径
			val radius = Math.max(newWidth, newHeight)
			return RippleAnimation(context, startX, startY, radius)
		}

		private fun getAbsoluteX(view: View): Float {
			var x = view.x
			val parent = view.parent
			if (parent != null && parent is View) {
				x += getAbsoluteX(parent)
			}
			return x
		}

		private fun getAbsoluteY(view: View): Float {
			var y = view.y
			val parent = view.parent
			if (parent != null && parent is View) {
				y += getAbsoluteY(parent)
			}
			return y
		}
	}

	private fun getActivityFromContext(context: Context): Activity {
		var mContext = context
		while (mContext is ContextWrapper) {
			if (mContext is Activity) {
				return mContext
			}
			mContext = mContext.baseContext
		}
		throw RuntimeException("Activity not found")
	}

	private fun updateMaxRadius() {
		// 将屏幕分成四个矩形
		val leftTop = RectF(0f, 0f, mStartX + mStartRadius, mStartY + mStartRadius)
		val rightTop = RectF(leftTop.right, 0f, mRootView.right.toFloat(), leftTop.bottom)
		val leftBottom = RectF(0f, leftTop.bottom, leftTop.right, mRootView.bottom.toFloat())
		val rightBottom = RectF(leftTop.right, leftTop.bottom, rightTop.right, leftBottom.bottom)
		// 计算对角线长度
		val leftTopDiagonal = Math.sqrt(
			Math.pow(leftTop.width().toDouble(), 2.0) +
					Math.pow(leftTop.height().toDouble(), 2.0)
		)
		val rightTopDiagonal = Math.sqrt(
			Math.pow(rightTop.width().toDouble(), 2.0) +
					Math.pow(rightTop.height().toDouble(), 2.0)
		)
		val leftBottomDiagonal = Math.sqrt(
			Math.pow(leftBottom.width().toDouble(), 2.0) +
					Math.pow(leftBottom.height().toDouble(), 2.0)
		)
		val rightBottomDiagonal = Math.sqrt(
			Math.pow(rightBottom.width().toDouble(), 2.0) +
					Math.pow(rightBottom.height().toDouble(), 2.0)
		)
		mMaxRadius = Math.max(
			Math.max(leftTopDiagonal, rightTopDiagonal), Math.max(leftBottomDiagonal, rightBottomDiagonal)
		).toFloat()
	}

	private fun initListener() {
		mAnimationListener = object : AnimatorListenerAdapter() {
			override fun onAnimationEnd(animation: Animator?) {
				mOnAnimationEndListener.onAnimationEnd()
				isStarted = false
				detachFromRootView()
			}
		}
		mAnimatorUpdateListener = ValueAnimator.AnimatorUpdateListener { animation ->
			mCurrentRadius = ((animation?.animatedValue as Float) + mStartRadius)
			postInvalidate()
		}
		mOnAnimationEndListener = object : OnAnimationEndListener {
			override fun onAnimationEnd() {
			}
		}
	}

	private fun detachFromRootView() {
		mRootView.removeView(this)
		if (!mBackground.isRecycled) {
			mBackground.recycle()
		}
	}

	fun start() {
		if (!isStarted) {
			isStarted = true
			updateBackground()
			attachToRootView()
			getAnimator().start()
		}
	}

	fun setDuration(duration: Long): RippleAnimation {
		mDuration = duration
		return this
	}

	private fun updateBackground() {
		mBackground = getBitmapFromView(mRootView)
	}

	private fun getBitmapFromView(view: View): Bitmap {
		view.measure(
			MeasureSpec.makeMeasureSpec(view.layoutParams.width, MeasureSpec.EXACTLY),
			MeasureSpec.makeMeasureSpec(view.layoutParams.height, MeasureSpec.EXACTLY)
		)
		val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
		val canvas = Canvas(bitmap)
		view.draw(canvas)
		return bitmap
	}

	private fun attachToRootView() {
		layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
		mRootView.addView(this)
	}

	private fun getAnimator(): ValueAnimator {
		val valueAnimator = ValueAnimator.ofFloat(0f, mMaxRadius).setDuration(mDuration)
		valueAnimator.addUpdateListener(mAnimatorUpdateListener)
		valueAnimator.addListener(mAnimationListener)
		return valueAnimator
	}

	override fun onDraw(canvas: Canvas?) {
		var layer = 0
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			layer = canvas?.saveLayer(0f, 0f, width.toFloat(), height.toFloat(), null)!!.toInt()
		}
		canvas?.drawBitmap(mBackground, 0f, 0f, null)
		canvas?.drawCircle(mStartX, mStartY, mCurrentRadius, mPaint)
		canvas?.restoreToCount(layer)
	}

	// 消费触摸事件
	@SuppressLint("ClickableViewAccessibility")
	override fun onTouchEvent(event: MotionEvent?): Boolean {
		return true
	}

	fun setOnAnimationEndListener(listener: OnAnimationEndListener): RippleAnimation {
		mOnAnimationEndListener = listener
		return this
	}

	interface OnAnimationEndListener {
		fun onAnimationEnd()
	}

}