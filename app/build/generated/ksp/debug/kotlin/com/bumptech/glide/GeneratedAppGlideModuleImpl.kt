package com.bumptech.glide

import android.content.Context
import com.example.deepfake.utils.GlideModule
import kotlin.Boolean
import kotlin.Suppress
import kotlin.Unit

internal class GeneratedAppGlideModuleImpl(
  @Suppress("UNUSED_PARAMETER")
  context: Context,
) : GeneratedAppGlideModule() {
  private val appGlideModule: GlideModule
  init {
    appGlideModule = GlideModule()
  }

  public override fun registerComponents(
    context: Context,
    glide: Glide,
    registry: Registry,
  ): Unit {
    appGlideModule.registerComponents(context, glide, registry)
  }

  public override fun applyOptions(context: Context, builder: GlideBuilder): Unit {
    appGlideModule.applyOptions(context, builder)
  }

  public override fun isManifestParsingEnabled(): Boolean = false
}
