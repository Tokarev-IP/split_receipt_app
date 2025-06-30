package com.iliatokarev.receipt_splitter_app.receipt.di

import com.iliatokarev.receipt_splitter_app.receipt.data.services.ImageConverter
import com.iliatokarev.receipt_splitter_app.receipt.data.services.ImageConverterInterface
import com.iliatokarev.receipt_splitter_app.receipt.data.services.ImageLabelingKit
import com.iliatokarev.receipt_splitter_app.receipt.data.services.ImageLabelingKitInterface
import com.iliatokarev.receipt_splitter_app.receipt.data.services.ReceiptJsonService
import com.iliatokarev.receipt_splitter_app.receipt.data.services.ReceiptJsonServiceInterface
import com.iliatokarev.receipt_splitter_app.receipt.data.store.FireStoreRepository
import com.iliatokarev.receipt_splitter_app.receipt.data.store.FireStoreRepositoryInterface
import com.iliatokarev.receipt_splitter_app.receipt.data.store.FirebaseUserId
import com.iliatokarev.receipt_splitter_app.receipt.data.store.FirebaseUserIdInterface
import com.iliatokarev.receipt_splitter_app.receipt.domain.reports.FolderReceiptsReportCreator
import com.iliatokarev.receipt_splitter_app.receipt.domain.reports.FolderReceiptsReportCreatorInterface
import com.iliatokarev.receipt_splitter_app.receipt.domain.OrderDataSplitService
import com.iliatokarev.receipt_splitter_app.receipt.domain.OrderDataSplitServiceInterface
import com.iliatokarev.receipt_splitter_app.receipt.domain.OrderDataService
import com.iliatokarev.receipt_splitter_app.receipt.domain.OrderDataServiceInterface
import com.iliatokarev.receipt_splitter_app.receipt.domain.reports.OrderReportCreator
import com.iliatokarev.receipt_splitter_app.receipt.domain.reports.OrderReportCreatorInterface
import com.iliatokarev.receipt_splitter_app.receipt.domain.ReceiptDataService
import com.iliatokarev.receipt_splitter_app.receipt.domain.ReceiptDataServiceInterface
import com.iliatokarev.receipt_splitter_app.receipt.domain.usecases.AllReceiptsUseCase
import com.iliatokarev.receipt_splitter_app.receipt.domain.usecases.AllReceiptsUseCaseInterface
import com.iliatokarev.receipt_splitter_app.receipt.domain.usecases.CreateReceiptUseCase
import com.iliatokarev.receipt_splitter_app.receipt.domain.usecases.CreateReceiptUseCaseInterface
import com.iliatokarev.receipt_splitter_app.receipt.domain.usecases.EditReceiptUseCase
import com.iliatokarev.receipt_splitter_app.receipt.domain.usecases.EditReceiptUseCaseInterface
import com.iliatokarev.receipt_splitter_app.receipt.domain.usecases.AllFoldersUseCase
import com.iliatokarev.receipt_splitter_app.receipt.domain.usecases.AllFoldersUseCaseInterface
import com.iliatokarev.receipt_splitter_app.receipt.domain.usecases.FolderReceiptsUseCase
import com.iliatokarev.receipt_splitter_app.receipt.domain.usecases.FolderReceiptsUseCaseInterface
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
    factoryOf(::ReceiptJsonService) { bind<ReceiptJsonServiceInterface>() }
    factoryOf(::FireStoreRepository) { bind<FireStoreRepositoryInterface>() }
    factoryOf(::FirebaseUserId) { bind<FirebaseUserIdInterface>() }
    factoryOf(:: FolderReceiptsReportCreator) { bind<FolderReceiptsReportCreatorInterface>()}

    // Use Cases
    factoryOf(::SplitReceiptUseCase) { bind<SplitReceiptUseCaseInterface>() }
    factoryOf(::AllReceiptsUseCase) { bind<AllReceiptsUseCaseInterface>() }
    factoryOf(::EditReceiptUseCase) { bind<EditReceiptUseCaseInterface>() }
    factoryOf(::CreateReceiptUseCase) { bind<CreateReceiptUseCaseInterface>() }
    factoryOf(::AllFoldersUseCase) { bind<AllFoldersUseCaseInterface>() }
    factoryOf(::FolderReceiptsUseCase) { bind<FolderReceiptsUseCaseInterface>() }

    // Business Logic
    factoryOf(::OrderReportCreator) { bind<OrderReportCreatorInterface>() }
    factoryOf(::OrderDataService) { bind<OrderDataServiceInterface>() }
    factoryOf(::OrderDataSplitService) { bind<OrderDataSplitServiceInterface>() }
    factoryOf(::ReceiptDataService) { bind<ReceiptDataServiceInterface>() }
}