package light.pay.api.transactions;

import light.pay.api.errors.Response;
import light.pay.api.transactions.request.InitiateTransactionRequest;
import light.pay.api.transactions.response.TransactionDTO;

public interface TransactionService {
    Response<TransactionDTO> initiateTransaction(InitiateTransactionRequest request);
    Response<TransactionDTO> completeTransaction(String transactionId);
    Response<TransactionDTO> findTransaction(String transactionId);
}
