package light.pay.domain.transactions;

import light.pay.api.response.Response;
import light.pay.domain.transactions.entity.Transaction;

public interface TransactionRepository {
    Response<Long> insert(Transaction transaction);
    Response<Transaction> getByTransactionID(String transactionID);
    Response<Transaction> getByReferenceID(String referenceId);
    Response<Void> updateStatus(Transaction transaction);
}
