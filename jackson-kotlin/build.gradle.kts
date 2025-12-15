dependencies {
    api(libs.kotlin.time.jackson.module) // Kotlin time module for jackson
    api(libs.kotlinx.datetime.jackson.module) // Kotlinx datetime module for jackson
    api(platform(libs.tools.jackson.bom)) // Jackson Bom 物料库
    api(libs.tools.jackson.databind) // JSON 序列化库
    api(libs.tools.jackson.module.kotlin) // Jackson Kotlin 支持库
}
