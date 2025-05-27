package com.iliatokarev.receipt_splitter.receipt.di

import com.iliatokarev.receipt_splitter.receipt.data.services.ImageConverter
import com.iliatokarev.receipt_splitter.receipt.data.services.ImageConverterInterface
import com.iliatokarev.receipt_splitter.receipt.data.services.ImageLabelingKit
import com.iliatokarev.receipt_splitter.receipt.data.services.ImageLabelingKitInterface
import com.iliatokarev.receipt_splitter.receipt.data.services.ReceiptService
import com.iliatokarev.receipt_splitter.receipt.data.services.ReceiptServiceInterface
import com.iliatokarev.receipt_splitter.receipt.data.store.FireStoreRepository
import com.iliatokarev.receipt_splitter.receipt.data.store.FireStoreRepositoryInterface
import com.iliatokarev.receipt_splitter.receipt.data.store.FirebaseUserId
import com.iliatokarev.receipt_splitter.receipt.data.store.FirebaseUserIdInterface
import com.iliatokarev.receipt_splitter.receipt.domain.OrderDataSplitter
import com.iliatokarev.receipt_splitter.receipt.domain.OrderDataSplitterInterface
import com.iliatokarev.receipt_splitter.receipt.domain.OrderReportCreator
import com.iliatokarev.receipt_splitter.receipt.domain.OrderReportCreatorInterface
import com.iliatokarev.receipt_splitter.receipt.domain.usecases.AllReceiptsUseCase
import com.iliatokarev.receipt_splitter.receipt.domain.usecases.AllReceiptsUseCaseInterface
import com.iliatokarev.receipt_splitter.receipt.domain.usecases.CreateReceiptUseCase
import com.iliatokarev.receipt_splitter.receipt.domain.usecases.CreateReceiptUseCaseInterface
import com.iliatokarev.receipt_splitter.receipt.domain.usecases.EditReceiptUseCase
import com.iliatokarev.receipt_splitter.receipt.domain.usecases.EditReceiptUseCaseInterface
import com.iliatokarev.receipt_splitter.receipt.domain.usecases.SplitReceiptUseCase
import com.iliatokarev.receipt_splitter.receipt.domain.usecases.SplitReceiptUseCaseInterface
import com.iliatokarev.receipt_splitter.receipt.presentation.ReceiptViewModel
import com.iliatokarev.receipt_splitter.receipt.presentation.viewmodels.AllReceiptsViewModel
import com.iliatokarev.receipt_splitter.receipt.presentation.viewmodels.CreateReceiptViewModel
import com.iliatokarev.receipt_splitter.receipt.presentation.viewmodels.EditReceiptViewModel
import com.iliatokarev.receipt_splitter.receipt.presentation.viewmodels.SplitReceiptViewModel
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