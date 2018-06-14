package org.wordpress.android.modules

import android.content.Context

import com.android.volley.RequestQueue
import com.bumptech.glide.Glide
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.integration.volley.VolleyUrlLoader
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.module.AppGlideModule

import org.wordpress.android.WordPress
import org.wordpress.android.fluxc.network.HTTPAuthManager
import org.wordpress.android.fluxc.network.UserAgent
import org.wordpress.android.fluxc.network.rest.wpcom.auth.AccessToken
import org.wordpress.android.networking.GlideHeaderLoaderFactory

import java.io.InputStream

import javax.inject.Inject
import javax.inject.Named

/**
 * Custom [AppGlideModule] that replaces Glide's default [RequestQueue] with FluxC's and sets custom GlideHeaderLoader
 * which adds support for custom authorization headers.
 */
@GlideModule
class WordPressGlideModule : AppGlideModule() {
    @Inject @field:Named("custom-ssl") lateinit var mRequestQueue: RequestQueue
    @Inject lateinit var mAccessToken: AccessToken
    @Inject lateinit var mHttpAuthManager: HTTPAuthManager
    @Inject lateinit var mUserAgent: UserAgent

    override fun applyOptions(context: Context, builder: GlideBuilder) {}

    override fun isManifestParsingEnabled(): Boolean {
        return false
    }

    override fun registerComponents(context: Context, glide: Glide, registry: Registry) {
        (context as WordPress).component().inject(this)
        registry.replace(GlideUrl::class.java, InputStream::class.java, VolleyUrlLoader.Factory(mRequestQueue))
        registry.append(GlideUrl::class.java, InputStream::class.java,
                GlideHeaderLoaderFactory(mAccessToken, mHttpAuthManager, mUserAgent))
    }
}
