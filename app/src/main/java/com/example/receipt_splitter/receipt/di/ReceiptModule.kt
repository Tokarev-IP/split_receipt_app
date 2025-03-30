package com.example.receipt_splitter.receipt.di

import com.example.receipt_splitter.receipt.data.ImageConverter
import com.example.receipt_splitter.receipt.data.ImageConverterInterface
import com.example.receipt_splitter.receipt.data.ImageLabelingKit
import com.example.receipt_splitter.receipt.data.ImageLabelingKitInterface
import com.example.receipt_splitter.receipt.data.ReceiptRepository
import com.example.receipt_splitter.receipt.data.ReceiptRepositoryInterface
import com.example.receipt_splitter.receipt.domain.ImageReceiptConverterUseCase
import com.example.receipt_splitter.receipt.domain.ImageReceiptConverterUseCaseInterface
import com.example.receipt_splitter.receipt.domain.OrderReportCreatorUseCase
import com.example.receipt_splitter.receipt.domain.OrderReportCreatorUseCaseInterface
import com.example.receipt_splitter.receipt.domain.ReceiptDataConverterUseCase
import com.example.receipt_splitter.receipt.domain.ReceiptDataConverterUseCaseInterface
import com.example.receipt_splitter.receipt.domain.RoomReceiptUseCase
import com.example.receipt_splitter.receipt.domain.RoomReceiptUseCaseInterface
import com.example.receipt_splitter.receipt.presentation.ReceiptViewModel
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val receiptModule = module {
    // View Model
    viewModelOf(::ReceiptViewModel)

    // Services / Utilities
    factoryOf(::ImageConverter) { bind<ImageConverterInterface>() }
    factoryOf(::ImageLabelingKit) { bind<ImageLabelingKitInterface>() }
    factoryOf(::ReceiptRepository) { bind<ReceiptRepositoryInterface>() }

    // Use Cases
    factoryOf(::ImageReceiptConverterUseCase) { bind<ImageReceiptConverterUseCaseInterface>() }
    factoryOf(::ReceiptDataConverterUseCase) { bind<ReceiptDataConverterUseCaseInterface>() }
    factoryOf(::OrderReportCreatorUseCase) { bind<OrderReportCreatorUseCaseInterface>() }
    factoryOf(::RoomReceiptUseCase) { bind<RoomReceiptUseCaseInterface>() }
}