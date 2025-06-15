package com.iliatokarev.receipt_splitter_app.receipt.data.services

import com.google.firebase.ai.type.Schema

object DataConstantsReceipt {

    val receiptSchemaObjectTranslated = Schema.obj(
        mapOf(
            "receipt_name" to Schema.string(),
            "translated_receipt_name" to Schema.string(),
            "date" to Schema.string(),
            "total_sum" to Schema.float(),
            "tax_in_percent" to Schema.float(),
            "discount_in_percent" to Schema.float(),
            "tip_in_percent" to Schema.float(),
            "orders" to Schema.array(
                Schema.obj(
                    mapOf(
                        "name" to Schema.string(),
                        "translated_name" to Schema.string(),
                        "quantity" to Schema.integer(),
                        "price" to Schema.float(),
                    )
                )
            )
        ),
        optionalProperties = listOf(
            "receipt_name",
            "translated_receipt_name",
            "date",
            "total_sum",
            "tax_in_percent",
            "discount_in_percent",
            "tip_in_percent",
            "orders",
        )
    )

    val receiptSchemaObjectNotTranslated = Schema.obj(
        mapOf(
            "receipt_name" to Schema.string(),
            "translated_receipt_name" to Schema.string(),
            "date" to Schema.string(),
            "total_sum" to Schema.float(),
            "tax_in_percent" to Schema.float(),
            "discount_in_percent" to Schema.float(),
            "tip_in_percent" to Schema.float(),
            "orders" to Schema.array(
                Schema.obj(
                    mapOf(
                        "name" to Schema.string(),
                        "translated_name" to Schema.string(),
                        "quantity" to Schema.integer(),
                        "price" to Schema.float(),
                    ),
                    optionalProperties = listOf(
                        "translated_name",
                    )
                ),
            )
        ),
        optionalProperties = listOf(
            "receipt_name",
            "translated_receipt_name",
            "date",
            "total_sum",
            "tax_in_percent",
            "discount_in_percent",
            "tip_in_percent",
            "orders",
        )
    )

    const val RESPONSE_MIME_TYPE = "application/json"
    const val USER_ATTEMPTS_COLLECTION = "amount_of_using"
    const val MAIN_CONSTANTS_COLLECTION = "main_constants"
    const val MAIN_CONSTANTS_DOCUMENT = "receipt_constants"

    const val MAXIMUM_AMOUNT_OF_DISHES = 300
    const val MAXIMUM_AMOUNT_OF_DISH_QUANTITY = 99
    const val MAXIMUM_AMOUNT_OF_RECEIPTS = 10_000_000
    const val MAXIMUM_TEXT_LENGTH = 80
    const val MAXIMUM_SUM = 999_999
    const val MAXIMUM_PERCENT = 100

    const val LANGUAGE_TEXT = "Translate to:"

    const val CONSUMER_NAME_DIVIDER = "%"
    const val ORDER_CONSUMER_NAME_DIVIDER = "#"
    const val MAXIMUM_AMOUNT_OF_CONSUMER_NAMES = 20
    const val MAXIMUM_CONSUMER_NAME_TEXT_LENGTH = 60

    // Labels for images are the following:
    // https://developers.google.com/ml-kit/vision/image-labeling/label-map
    // 135 Menu 240 Receipt 273 Paper 93 Poster
    val APPROPRIATE_LABELS: List<Int> = listOf(273, 135, 240, 93)

    const val ONE_ATTEMPT = 1
    const val MAXIMUM_AMOUNT_OF_IMAGES = 2
}