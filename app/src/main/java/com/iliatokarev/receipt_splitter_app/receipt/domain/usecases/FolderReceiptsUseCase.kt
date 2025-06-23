package com.iliatokarev.receipt_splitter_app.receipt.domain.usecases

import com.iliatokarev.receipt_splitter_app.receipt.data.room.receipt.ReceiptDbRepository
import com.iliatokarev.receipt_splitter_app.receipt.domain.FolderReceiptsReportCreator
import com.iliatokarev.receipt_splitter_app.receipt.domain.OrderDataSplitService
import com.iliatokarev.receipt_splitter_app.receipt.presentation.ReceiptData
import com.iliatokarev.receipt_splitter_app.receipt.presentation.ReceiptUiMessage
import com.iliatokarev.receipt_splitter_app.receipt.presentation.ReceiptWithOrdersDataSplit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext

class FolderReceiptsUseCase(
    private val receiptDbRepository: ReceiptDbRepository,
    private val orderDataSplitService: OrderDataSplitService,
    private val folderReceiptsReportCreator: FolderReceiptsReportCreator,
) : FolderReceiptsUseCaseInterface {

    override suspend fun createAllReports(
        allReceiptsList: List<ReceiptData>
    ): ReportsUseCaseResponse =
        withContext(Dispatchers.Default) {
            val receiptWithOrdersDataSplitList = createDataForReport(allReceiptsList)
            if (receiptWithOrdersDataSplitList == null)
                return@withContext ReportsUseCaseResponse.Error(
                    message = ReceiptUiMessage.INTERNAL_ERROR.msg
                )
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
                    return@withContext ReportsUseCaseResponse.Error(
                        message = ReceiptUiMessage.INTERNAL_ERROR.msg
                    )
                else
                    return@withContext ReportsUseCaseResponse.Reports(
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

interface ReportsUseCaseResponse {
    class Reports(
        val shortReport: String,
        val longReport: String,
        val basicReport: String,
    ) : ReportsUseCaseResponse

    class Error(val message: String) : ReportsUseCaseResponse
}

interface FolderReceiptsUseCaseInterface {
    suspend fun createAllReports(allReceiptsList: List<ReceiptData>): ReportsUseCaseResponse
}