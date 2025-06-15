package com.iliatokarev.receipt_splitter_app.receipt.di

import com.iliatokarev.receipt_splitter_app.receipt.data.services.ImageConverter
import com.iliatokarev.receipt_splitter_app.receipt.data.services.ImageConverterInterface
import com.iliatokarev.receipt_splitter_app.receipt.data.services.ImageLabelingKit
import com.iliatokarev.receipt_splitter_app.receipt.data.services.ImageLabelingKitInterface
import com.iliatokarev.receipt_splitter_app.receipt.data.services.ReceiptService
import com.iliatokarev.receipt_splitter_app.receipt.data.services.ReceiptServiceInterface
import com.iliatokarev.receipt_splitter_app.receipt.data.store.FireStoreRepository
import com.iliatokarev.receipt_splitter_app.receipt.data.store.FireStoreRepositoryInterface
import com.iliatokarev.receipt_splitter_app.receipt.data.store.FirebaseUserId
import com.iliatokarev.receipt_splitter_app.receipt.data.store.FirebaseUserIdInterface
import com.iliatokarev.receipt_splitter_app.receipt.domain.OrderDataSplitService
import com.iliatokarev.receipt_splitter_app.receipt.domain.OrderDataSplitServiceInterface
import com.iliatokarev.receipt_splitter_app.receipt.domain.OrderDataService
import com.iliatokarev.receipt_splitter_app.receipt.domain.OrderDataServiceInterface
import com.iliatokarev.receipt_splitter_app.receipt.domain.OrderReportCreator
import com.iliatokarev.receipt_splitter_app.receipt.domain.OrderReportCreatorInterface
import com.iliatokarev.receipt_splitter_app.receipt.domain.usecases.AllReceiptsUseCase
import com.iliatokarev.receipt_splitter_app.receipt.domain.usecases.AllReceiptsUseCaseInterface
import com.iliatokarev.receipt_splitter_app.receipt.domain.usecases.CreateReceiptUseCase
import com.iliatokarev.receipt_splitter_app.receipt.domain.usecases.CreateReceiptUseCaseInterface
import com.iliatokarev.receipt_splitter_app.receipt.domain.usecases.EditReceiptUseCase
import com.iliatokarev.receipt_splitter_app.receipt.domain.usecases.EditReceiptUseCaseInterface
import com.iliatokarev.receipt_splitter_app.receipt.domain.usecases.AllFoldersUseCase
import com.iliatokarev.receipt_splitter_app.receipt.domain.usecases.AllFoldersUseCaseInterface
import com.iliatokarev.receipt_splitter_app.receipt.domain.usecases.SplitReceiptUseCase
import com.iliatokarev.receipt_splitter_app.receipt.domain.usecases.SplitReceiptUseCaseInterface
import com.iliatokarev.receipt_splitter_app.receipt.presentation.ReceiptViewModel
import com.iliatokarev.receipt_splitter_app.receipt.presentation.viewmodels.AllReceiptsViewModel
import com.iliatokarev.receipt_splitter_app.receipt.presentation.viewmodels.CreateReceiptViewModel
import com.iliatokarev.receipt_splitter_app.receipt.presentation.viewmodels.EditReceiptViewModel
import com.iliatokarev.receipt_splitter_app.receipt.presentation.viewmodels.FolderReceiptsViewModel
import com.iliatokarev.receipt_splitter_app.receipt.presentation.viewmodels.SplitReceiptForAllViewModel
import com.iliatokarev.receipt_splitter_app.receipt.presentation.viewmodels.SplitReceiptForOneViewModel
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val receiptSplitterModule = module {
    // View Model
    viewModelOf(::ReceiptViewModel)
    viewModelOf(::AllReceiptsViewModel)
    viewModelOf(::EditReceiptViewModel)
    viewModelOf(::SplitReceiptForOneViewModel)
    viewModelOf(::SplitReceiptForAllViewModel)
    viewModelOf(::CreateReceiptViewModel)
    viewModelOf(::FolderReceiptsViewModel)

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
    factoryOf(::AllFoldersUseCase) { bind<AllFoldersUseCaseInterface>() }

    // Business Logic
    factoryOf(::OrderReportCreator) { bind<OrderReportCreatorInterface>() }
    factoryOf(::OrderDataService) { bind<OrderDataServiceInterface>() }
    factoryOf(::OrderDataSplitService) { bind<OrderDataSplitServiceInterface>() }
}