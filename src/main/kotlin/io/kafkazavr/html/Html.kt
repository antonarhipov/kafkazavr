package io.kafkazavr.html

import kotlinx.html.*
import kotlinx.html.dom.createHTMLDocument
import kotlinx.html.dom.serialize
import org.w3c.dom.Document

class Html(private val mapBoxAccessToken: String, private val wsUrl: String) {

    val driverHTML: HTML.() -> Unit = {
        common {
            title {
                +"Driver"
            }
            js("/assets/driver.js")
        }
    }

    val riderHTML: HTML.() -> Unit = {
        common {
            title {
                +"Rider"
            }
            js("/assets/rider.js")
        }
    }

    val options = mapOf(
        "rider" to riderHTML,
        "driver" to driverHTML
    )

    operator fun get(key: String): HTML.() -> Unit = options[key]!!

    private fun HTML.common(actor: HEAD.() -> Unit) {
        head {
            script {
                unsafe {
                    raw(
                        """
                            var module = {};
                            var wsUrl = "$wsUrl";
                        """.trimIndent()
                    )
                }
            }
            js("https://api.mapbox.com/mapbox-gl-js/v2.1.1/mapbox-gl.js")
            js("https://api.mapbox.com/mapbox-gl-js/plugins/mapbox-gl-directions/v4.1.0/mapbox-gl-directions.js")
            js("https://api.tiles.mapbox.com/mapbox.js/plugins/turf/v2.0.0/turf.min.js")
            js("/webjars/vue/2.1.3/vue.js")
            js("/webjars/google-polyline/1.0.0/lib/decode.js")
            js("/assets/common.js")
            css("/webjars/ionicons/2.0.1/css/ionicons.min.css")
            css("https://api.mapbox.com/mapbox-gl-js/v2.1.1/mapbox-gl.css")
            css("https://api.mapbox.com/mapbox-gl-js/plugins/mapbox-gl-directions/v4.1.0/mapbox-gl-directions.css")
            actor()
            css("/assets/main.css")
        }
        body {
            div {
                id = "app"
                +"Your ID: {{ uuid }}"
            }
            div { id = "map" }
            script {
                unsafe {
                    raw("""
                        mapboxgl.accessToken = "$mapBoxAccessToken";
                    """.trimIndent()
                    )
                }
            }
        }
    }

    private fun HEAD.css(source: String) {
        link(source, LinkRel.stylesheet)
    }

    private fun HEAD.js(source: String) {
        script(ScriptType.textJavaScript) {
            src = source
        }
    }

    var driver: Document = createHTMLDocument().html(block = driverHTML)
    var rider: Document = createHTMLDocument().html(block = riderHTML)
}

fun main() {
    println(Html("driver-key", "http://localhost:8080").driver.serialize())
    println("--------------------------------")
    println(Html("rider-key", "http://localhost:8080").rider.serialize())
}