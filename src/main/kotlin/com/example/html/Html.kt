package com.example.html

import kotlinx.html.*
import kotlinx.html.dom.createHTMLDocument
import org.w3c.dom.Document

class Html(mapBoxAccessToken: String) {

    val driverHTML: HTML.() -> Unit = {
        head {
            title {
                +"Driver"
            }
            script {
                unsafe {
                    raw(
                        """
                        var module = {};
                    """.trimIndent()
                    )
                }
            }
            js("https://api.mapbox.com/mapbox-gl-js/v2.1.1/mapbox-gl.js")
            js("https://api.mapbox.com/mapbox-gl-js/plugins/mapbox-gl-directions/v4.1.0/mapbox-gl-directions.js")
            js("https://api.tiles.mapbox.com/mapbox.js/plugins/turf/v2.0.0/turf.min.js")
            js("/webjars/vue/2.1.3/vue.js")
            js("/webjars/google-polyline/1.0.0/lib/decode.js")
            css("/webjars/ionicons/2.0.1/css/ionicons.min.css")
            css("https://api.mapbox.com/mapbox-gl-js/v2.1.1/mapbox-gl.css")
            css("https://api.mapbox.com/mapbox-gl-js/plugins/mapbox-gl-directions/v4.1.0/mapbox-gl-directions.css")
            js("/assets/common.js")
            js("/assets/driver.js")
            css("/assets/main.css")
        }
        body {
            div {
                id = "app"
                +"Your Driver ID: {{ uuid }}"
            }
            div { id = "map" }
            script {
                unsafe {
                    raw(
                        """
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
}