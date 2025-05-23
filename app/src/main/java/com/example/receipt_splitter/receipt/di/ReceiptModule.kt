package com.example.receipt_splitter.receipt.di

import com.example.receipt_splitter.receipt.data.services.ImageConverter
import com.example.receipt_splitter.receipt.data.services.ImageConverterInterface
import com.example.receipt_splitter.receipt.data.services.ImageLabelingKit
import com.example.receipt_splitter.receipt.data.services.ImageLabelingKitInterface
import com.example.receipt_splitter.receipt.data.services.ReceiptService
import com.example.receipt_splitter.receipt.data.services.ReceiptServiceInterface
import com.example.receipt_splitter.receipt.data.store.FireStoreRepository
import com.example.receipt_splitter.receipt.data.store.FireStoreRepositoryInterface
import com.example.receipt_splitter.receipt.data.store.FirebaseUserId
import com.example.receipt_splitter.receipt.data.store.FirebaseUserIdInterface
import com.example.receipt_splitter.receipt.domain.OrderDataSplitter
import com.example.receipt_splitter.receipt.domain.OrderDataSplitterInterface
import com.example.receipt_splitter.receipt.domain.OrderReportCreator
import com.example.receipt_splitter.receipt.domain.OrderReportCreatorInterface
import com.example.receipt_splitter.receipt.domain.usecases.AllReceiptsUseCase
import com.example.receipt_splitter.receipt.domain.usecases.AllReceiptsUseCaseInterface
import com.example.receipt_splitter.receipt.domain.usecases.CreateReceiptUseCase
import com.example.receipt_splitter.receipt.domain.usecases.CreateReceiptUseCaseInterface
import com.example.receipt_splitter.receipt.domain.usecases.EditReceiptUseCase
import com.example.receipt_splitter.receipt.domain.usecases.EditReceiptUseCaseInterface
import com.example.receipt_splitter.receipt.domain.usecases.SplitReceiptUseCase
import com.example.receipt_splitter.receipt.domain.usecases.SplitReceiptUseCaseInterface
import com.example.receipt_splitter.receipt.presentation.ReceiptViewModel
import com.example.receipt_splitter.receipt.presentation.viewmodels.AllReceiptsViewModel
import com.example.receipt_splitter.receipt.presentation.viewmodels.CreateReceiptViewModel
import com.example.receipt_splitter.receipt.presentation.viewmodels.EditReceiptViewModel
import com.example.receipt_splitter.receipt.presentation.viewmodels.SplitReceiptViewModel
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val receiptModule = module {
    // View Model
    viewModelOf(::ReceiptViewModel)
    viewModelOf(::AllReceiptsViewModel)
    viewModelOf(::EditReceiptViewModel)
    viewModelOf(::SplitReceiptViewModel)
    viewModelOf(::CreateReceiptViewModel)

    // Services & Utilities
    factoryOf(::ImageConverter) { bind<ImageConverterInterface>() }
    factoryOf(::ImageLabelingKit) { bind<ImageLabelingKitInterface>() }
    factoryOf(::ReceiptService) { bind<ReceiptServiceInterface>() }
    factoryOf(::FireStoreRepository) { bind<FireStoreRepositoryInterface>() }
    factoryOf(::FirebaseUserId) { bind<FirebaseUserIdInterface>() }

    // Use Cases
    factoryOf(::SplitReceiptUseCase) { bind<SplitReceiptUseCaseInterface>() }
    factoryOf(::AllReceiptsUseCase) { bind<AllReceiptsUseCaseInterface>() }
    factoryOf(::EditReceiptUseCase) { bind<EditReceiptUseCaseInterface>() }
    factoryOf(::CreateReceiptUseCase) { bind<CreateReceiptUseCaseInterface>() }

    // Business Logic
    factoryOf(::OrderReportCreator) { bind<OrderReportCreatorInterface>() }
    factoryOf(::OrderDataSplitter) { bind<OrderDataSplitterInterface>() }
}