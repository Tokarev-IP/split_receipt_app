package com.iliatokarev.receipt_splitter_app.receipt.domain.usecases

import com.iliatokarev.receipt_splitter_app.receipt.data.room.receipt.ReceiptDbRepositoryInterface
import com.iliatokarev.receipt_splitter_app.receipt.domain.OrderDataSplitServiceInterface
import com.iliatokarev.receipt_splitter_app.receipt.domain.reports.FolderReceiptsReportCreatorInterface
import com.iliatokarev.receipt_splitter_app.receipt.presentation.ReceiptData
import com.iliatokarev.receipt_splitter_app.receipt.presentation.ReceiptWithOrdersDataSplit
import com.iliatokarev.receipt_splitter_app.receipt.presentation.viewmodels.ReceiptReports
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext

class FolderReceiptsUseCase(
    private val receiptDbRepository: ReceiptDbRepositoryInterface,
    private val orderDataSplitService: OrderDataSplitServiceInterface,
    private val folderReceiptsReportCreator: FolderReceiptsReportCreatorInterface,
) : FolderReceiptsUseCaseInterface {

    override suspend fun createAllReports(
        allReceiptsList: List<ReceiptData>
    ): ReceiptReports? =
        withContext(Dispatchers.Default) {
            val receiptWithOrdersDataSplitList = createDataForReport(allReceiptsList)
            if (receiptWithOrdersDataSplitList == null)
                return@withContext null
            else {
                val shortReportAsync = async {
                    folderReceiptsReportCreator.createShortReport(
                        consumerNamesList = receiptWithOrdersDataSplitList.consumerNamesList,
                        receiptWithOrdersDataSplitList = receiptWithOrdersDataSplitList.receiptWithOrdersList,
                    )
                }
                val longReportAsync = async {
                    folderReceiptsReportCreator.createLongReport(
                        consumerNamesList = receiptWithOrdersDataSplitList.consumerNamesList,
                        receiptWithOrdersDataSplitList = receiptWithOrdersDataSplitList.receiptWithOrdersList,
                    )
                }
                val basicReportAsync = async {
                    folderReceiptsReportCreator.createBasicReport(
                        consumerNamesList = receiptWithOrdersDataSplitList.consumerNamesList,
                        receiptWithOrdersDataSplitList = receiptWithOrdersDataSplitList.receiptWithOrdersList,
                    )
                }

                val shortReport = shortReportAsync.await()
                val longReport = longReportAsync.await()
                val basicReport = basicReportAsync.await()

                if (shortReport == null || longReport == null || basicReport == null)
                    return@withContext null
                else
                    return@withContext ReceiptReports(
                        shortReport = shortReport,
                        longReport = longReport,
                        basicReport = basicReport,
                    )
            }
        }

    private suspend fun createDataForReport(
        allReceiptsList: List<ReceiptData>,
    ): DataForReport? = withContext(Dispatchers.IO) {
        runCatching {
            val receiptsList = allReceiptsList.filter { it.isChecked == true }

            val consumerNamesSet = mutableSetOf<String>()
            val receiptWithOrdersDataSplit = mutableListOf<ReceiptWithOrdersDataSplit>()

            for (receipt in receiptsList) {
                val orderDataList = receiptDbRepository.getOrdersByReceiptId(receiptId = receipt.id)
                val orderDataSplitList =
                    orderDataSplitService.convertOrderDataListToOrderDataSplitList(
                        orderDataList = orderDataList,
                    )
                receiptWithOrdersDataSplit.add(
                    ReceiptWithOrdersDataSplit(
                        receipt = receipt,
                        orders = orderDataSplitList,
                    )
                )
                for (orderDataSplit in orderDataSplitList) {
                    consumerNamesSet.addAll(orderDataSplit.consumerNamesList)
                }
            }
            return@withContext DataForReport(
                receiptWithOrdersList = receiptWithOrdersDataSplit,
                consumerNamesList = consumerNamesSet.toList(),
            )
        }.getOrElse { e: Throwable ->
            return@withContext null
        }
    }

}

class DataForReport(
    val receiptWithOrdersList: List<ReceiptWithOrdersDataSplit>,
    val consumerNamesList: List<String>,
)

interface FolderReceiptsUseCaseInterface {
    suspend fun createAllReports(allReceiptsList: List<ReceiptData>): ReceiptReports?
}