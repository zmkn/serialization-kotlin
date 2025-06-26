package com.zmkn.serialization.jackson

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.KotlinFeature
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.zmkn.jackson.module.datetime.DatetimeJacksonModule
import com.zmkn.jackson.module.time.TimeJacksonModule

class Jackson(
    mapper: ObjectMapper,
    initializer: ObjectMapper.() -> Unit = {},
    kotlinModuleInitializer: KotlinModule.Builder.() -> Unit = {},
) {
    constructor(initializer: ObjectMapper.() -> Unit = {}, kotlinModuleInitializer: KotlinModule.Builder.() -> Unit = {}) : this(
        Companion.objectMapper, initializer, kotlinModuleInitializer
    )

    constructor() : this(Companion.objectMapper)

    val objectMapper: ObjectMapper = mapper.copy().apply(initializer).registerModule(
        defaultKotlinModuleBuilder.apply(kotlinModuleInitializer).build()
    )

    companion object {
        val defaultKotlinModuleBuilder = KotlinModule.Builder().apply {
            enable(KotlinFeature.SingletonSupport) // 在反序列化时确保单例对象的唯一性
            enable(KotlinFeature.StrictNullChecks) // 确保反序列化后的集合中没有 null 成员，如果发现 null 则抛出异常
            enable(KotlinFeature.KotlinPropertyNameAsImplicitName) // 使用属性名进行序列化，确保与 Kotlin 的命名规则一致，避免因 getter 名称不同导致的问题
            enable(KotlinFeature.UseJavaDurationConversion) // 允许使用 JavaTimeModule 处理 Kotlin 的 Duration 类型，并需要在 getter 或 field 上声明 @JsonFormat 注解
        }
        val objectMapper = ObjectMapper().apply {
            // 注册 Kotlin 模块以支持 Kotlin 数据类
            setSerializationInclusion(JsonInclude.Include.NON_NULL) // 忽略所有值为 null 的属性
            // 配置序列化特性
            enable(SerializationFeature.INDENT_OUTPUT) // 在输出时美化打印JSON格式
            enable(SerializationFeature.CLOSE_CLOSEABLE) // 使实现了 Closeable 接口的对象（例如文件流或网络连接）都会被自动关闭
            enable(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS) // 使 Map 类型的对象在序列化为 JSON 时，条目会根据键进行排序
            enable(SerializationFeature.USE_EQUALITY_FOR_OBJECT_ID) // 使 Jackson 在处理对象 ID 引用时使用 equals() 方法来比较对象
            disable(SerializationFeature.FAIL_ON_EMPTY_BEANS) // 使没有可序列化的属性时不抛出异常
            disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS) // 使日期类型不会被序列化为时间戳
            // 配置反序列化特性
            enable(DeserializationFeature.FAIL_ON_NUMBERS_FOR_ENUMS) // 如果尝试将数字作为枚举类型的值，则抛出异常
            enable(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES) // 如果尝试将 JSON 中的 null 值赋给 Java 的基本数据类型（如 int、boolean），则抛出异常
            disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES) // 使 JSON 中包含目标类中不存在的字段时不抛出异常
            // 配置解析器 (JsonParser) 和生成器 (JsonGenerator) 的行为
            configure(JsonParser.Feature.INCLUDE_SOURCE_IN_LOCATION, true) // 在错误报告中包含原始输入源的信息
            configure(JsonGenerator.Feature.COMBINE_UNICODE_SURROGATES_IN_UTF8, true) // 在 UTF-8 编码输出中，将 Unicode 代理对（surrogate pairs）组合成单个字符
        }.also {
            it.registerModule(defaultKotlinModuleBuilder.build()).registerModule(DatetimeJacksonModule.all).registerModule(TimeJacksonModule.all)
        }
    }
}
