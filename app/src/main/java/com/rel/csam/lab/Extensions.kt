/**
 * Designed and developed by Aidan Follestad (@afollestad)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.rel.csam.lab

import android.content.Context
import androidx.annotation.DimenRes
import android.text.SpannableString
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.widget.TintContextWrapper
import androidx.recyclerview.widget.RecyclerView

internal fun Context.dimen(@DimenRes res: Int): Int {
  return resources.getDimensionPixelSize(res)
}

internal typealias ListAdapter<T> = RecyclerView.Adapter<T>

internal fun ListAdapter<*>.isEmpty(): Boolean {
  return itemCount == 0
}

internal fun RecyclerView.getItemPosition(e: MotionEvent): Int {
  val v = findChildViewUnder(e.x, e.y) ?: return RecyclerView.NO_POSITION
  return getChildAdapterPosition(v)
}

internal fun SpannableString.spanWith(target: String, apply: SpannableBuilder.() -> Unit) {
  val builder = SpannableBuilder()
  apply(builder)

  val start = this.indexOf(target)
  val end =  start + target.length

  setSpan(builder.what, start, end, builder.flags)
}

internal fun View.getSafeContext(): Context {
  val ctx = context
  if (ctx is TintContextWrapper) {
    return ctx.baseContext
  } else {
    return ctx
  }
}

class SpannableBuilder {
  lateinit var what: Any
  var flags: Int = 0
}
