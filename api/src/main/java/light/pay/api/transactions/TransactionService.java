package light.pay.api.transactions;

import light.pay.api.errors.Response;
import light.pay.api.transactions.request.CreateTransactionRequest;
import light.pay.api.transactions.response.TransactionDTO;

public interface TransactionService {
    Response<TransactionDTO> createTransaction(CreateTransactionRequest request);
    Response<TransactionDTO> findTransaction(String transactionId);
}
