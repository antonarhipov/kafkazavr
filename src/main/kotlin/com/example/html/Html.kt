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
            script(ScriptType.textJavaScript) {
                src = "https://api.mapbox.com/mapbox-gl-js/v2.1.1/mapbox-gl.js"
            }
            script(ScriptType.textJavaScript) {
                src = "https://api.mapbox.com/mapbox-gl-js/plugins/mapbox-gl-directions/v4.1.0/mapbox-gl-directions.js"
            }
            script(ScriptType.textJavaScript) {
                src = "https://api.tiles.mapbox.com/mapbox.js/plugins/turf/v2.0.0/turf.min.js"
            }
            script(ScriptType.textJavaScript) {
                src = "/webjars/vue/2.1.3/vue.js"
            }
            script(ScriptType.textJavaScript) {
                src = "/webjars/google-polyline/1.0.0/lib/decode.js"
            }
            link("/webjars/ionicons/2.0.1/css/ionicons.min.css", LinkRel.stylesheet)
            link("https://api.mapbox.com/mapbox-gl-js/v2.1.1/mapbox-gl.css", LinkRel.stylesheet)
            link(
                "https://api.mapbox.com/mapbox-gl-js/plugins/mapbox-gl-directions/v4.1.0/mapbox-gl-directions.css",
                LinkRel.stylesheet
            )
            script(ScriptType.textJavaScript) {
                src = "/assets/common.js"
            }
            script(ScriptType.textJavaScript) {
                src = "/assets/driver.js"
            }
            link("/assets/main.css", LinkRel.stylesheet)
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

    var driver: Document = createHTMLDocument().html(block = driverHTML)
}